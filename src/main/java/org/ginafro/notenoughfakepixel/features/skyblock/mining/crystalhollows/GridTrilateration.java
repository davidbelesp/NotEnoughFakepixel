package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import lombok.var;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.*;

/**
 * Grid-aware trilateration with noisy distances.
 * - Inputs: multiple (origin, distance) samples. Distances have +-epsilon noise.
 * - Output: best integer (x,y,z) block estimate.
 *
 * <p>Thanks to koban4ik for Grid trilateration functions<p/>
 */
public final class GridTrilateration {

    /** One reading: origin (ox,oy,oz) and measured distance d. */
    public static final class Sample {
        public final double ox, oy, oz, d;
        public Sample(double ox, double oy, double oz, double d) {
            this.ox = ox; this.oy = oy; this.oz = oz; this.d = d;
        }
    }

    /** Integer block position. */
    public static final class Int3 {
        public final int x, y, z;
        public Int3(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
        @Override public String toString() { return "Int3(" + x + "," + y + "," + z + ")"; }
        @Override public boolean equals(Object o){
            if(!(o instanceof Int3)) return false;
            Int3 i = (Int3) o;
            return x==i.x && y==i.y && z==i.z;
        }
        @Override public int hashCode(){ return (x*73856093) ^ (y*19349663) ^ (z*83492791); }
    }

    /** Result with a tiny confidence hint. */
    public static final class Result {
        public final Int3 best;
        public final double score;       // sum of squared normalized residuals at best
        public final double gapToNext;   // score(nextBest) - score(best); larger gap => higher confidence
        public Result(Int3 best, double score, double gapToNext) {
            this.best = best; this.score = score; this.gapToNext = gapToNext;
        }
        @Override public String toString(){
            return best + "  score=" + String.format(Locale.US,"%.3f",score)
                    + "  gapToNext=" + String.format(Locale.US,"%.3f",gapToNext);
        }
    }

    /** Main entry. epsilon is your +- distance precision in blocks (e.g. 5). */
    public static Result estimate(List<Sample> samples, double epsilon) {
        return estimate(samples, epsilon, /*extraPad*/ (int)ceil(max(2, epsilon)), /*maxVolume*/ 2_000_000);
    }

    /**
     * Full control version.
     * @param extraPad   expand the AABB by this many blocks after intersecting (safety margin)
     * @param maxVolume  hard cap on candidates; if exceeded, we fallback to a ball around LSQ solution
     */
    public static Result estimate(List<Sample> samples, double epsilon, int extraPad, int maxVolume) {
        if (samples == null || samples.size() < 3)
            throw new IllegalArgumentException("Need at least 3 samples; 4+ non-coplanar is recommended.");

        // 1) Continuous least-squares via linearization (subtract eqn 0).
        double[] p0 = lsqGuess(samples);

        // 2) Build tight AABB from distance bands and intersect them.
        int[] box = intersectedAABB(samples, epsilon);
        expandBox(box, extraPad);

        long volume = (long)(box[3]-box[0]+1)*(box[4]-box[1]+1)*(box[5]-box[2]+1);

        Iterable<Int3> candidates;
        if (volume <= maxVolume) {
            candidates = () -> new Iterator<Int3>() {
                int x = box[0], y = box[1], z = box[2];
                @Override public boolean hasNext(){ return x <= box[3]; }
                @Override public Int3 next(){
                    Int3 out = new Int3(x,y,z);
                    // advance z,y,x
                    if (++z > box[5]) { z = box[2]; if (++y > box[4]) { y = box[1]; ++x; } }
                    return out;
                }
            };
        } else {
            // 3) Fallback: scan a sphere around the LSQ point with radius ~ (2*epsilon + worst residual).
            int cx = (int)round(p0[0]), cy = (int)round(p0[1]), cz = (int)round(p0[2]);
            double worstResidual = worstResidual(samples, p0);
            int r = (int)ceil(max(2*epsilon, worstResidual + epsilon));
            candidates = () -> new Iterator() {
                final int minX = max(box[0], cx - r), maxX = min(box[3], cx + r);
                final int minY = max(box[1], cy - r), maxY = min(box[4], cy + r);
                final int minZ = max(box[2], cz - r), maxZ = min(box[5], cz + r);
                int x = minX, y = minY, z = minZ;
                @Override public boolean hasNext(){ return x <= maxX; }
                @Override public Int3 next() {
                    while (true) {
                        Int3 out = new Int3(x,y,z);
                        // advance
                        if (++z > maxZ) { z = minZ; if (++y > maxY) { y = minY; ++x; if (x>maxX) return out; } }
                        // keep out if outside sphere
                        double dx = out.x - p0[0], dy = out.y - p0[1], dz = out.z - p0[2];
                        if (dx*dx + dy*dy + dz*dz <= (double)r*r) return out;
                    }
                }
            };
        }

        // 4) Score candidates: sum_i ((| |p-oi| - di |) / epsilon)^2. Lower is better.
        Int3 best = null, second = null;
        double bestScore = Double.POSITIVE_INFINITY, secondScore = Double.POSITIVE_INFINITY;

        for (Int3 c : candidates) {
            double s = 0.0;
            for (Sample smp : samples) {
                double dx = c.x - smp.ox, dy = c.y - smp.oy, dz = c.z - smp.oz;
                double resid = abs(sqrt(dx*dx + dy*dy + dz*dz) - smp.d);
                // soft-robust: inside epsilon counts ~0, outside grows quadratically
                double n = resid / epsilon;
                s += n*n;
            }
            if (s < bestScore) {
                secondScore = bestScore; second = best;
                bestScore = s; best = c;
            } else if (s < secondScore) {
                secondScore = s; second = c;
            }
        }

        double gap = (second == null) ? Double.POSITIVE_INFINITY : (secondScore - bestScore);
        return new Result(best, bestScore, gap);
    }

    // ---------- Internals ----------

    private static double[] lsqGuess(List<Sample> samples) {
        // Use sample 0 as reference, derive linear system A p = b
        var r = samples.get(0);
        int m = samples.size() - 1;
        double a00=0,a01=0,a02=0,a11=0,a12=0,a22=0; // AtA symmetric
        double v0=0,v1=0,v2=0;                      // Atb

        for (int i=1;i<samples.size();i++) {
            var s = samples.get(i);
            double rx = s.ox - r.ox, ry = s.oy - r.oy, rz = s.oz - r.oz;
            double bi = (s.ox*s.ox + s.oy*s.oy + s.oz*s.oz - s.d*s.d)
                    - (r.ox*r.ox + r.oy*r.oy + r.oz*r.oz - r.d*r.d);
            // 2*(oi - o0) · p = bi  => row is [2*rx, 2*ry, 2*rz]
            double ax = 2*rx, ay = 2*ry, az = 2*rz;

            // accumulate AtA and Atb
            a00 += ax*ax; a01 += ax*ay; a02 += ax*az;
            a11 += ay*ay; a12 += ay*az; a22 += az*az;
            v0  += ax*bi; v1  += ay*bi; v2  += az*bi;
        }

        // Solve (AtA) p = Atb for p (3x3 symmetric). Gaussian elimination for stability.
        double[][] A = {
                {a00, a01, a02},
                {a01, a11, a12},
                {a02, a12, a22}
        };
        double[] b = {v0, v1, v2};
        return solve3x3(A, b);
    }

    private static double[] solve3x3(double[][] A, double[] b) {
        // Simple Gaussian elimination with partial pivoting
        int[] piv = {0,1,2};
        for (int k=0;k<3;k++){
            // pivot
            int max=k; double maxVal = abs(A[piv[k]][k]);
            for (int i=k+1;i<3;i++){ double v=abs(A[piv[i]][k]); if(v>maxVal){max=i;maxVal=v;} }
            int tmp=piv[k]; piv[k]=piv[max]; piv[max]=tmp;

            int r = piv[k];
            double pivot = A[r][k];
            if (abs(pivot) < 1e-12) return new double[]{0,0,0}; // degenerate; shrug and return origin
            // normalize row
            for (int j=k;j<3;j++) A[r][j] /= pivot;
            b[r] /= pivot;
            // eliminate
            for (int i=0;i<3;i++){
                if (i==r) continue;
                double f = A[i][k];
                if (f == 0) continue;
                for (int j=k;j<3;j++) A[i][j] -= f*A[r][j];
                b[i] -= f*b[r];
            }
        }
        // Now A is I (modulo row perm), b holds solution in permuted order
        double[] x = new double[3];
        for (int i=0;i<3;i++) x[i] = b[i];
        return x;
    }

    private static int[] intersectedAABB(List<Sample> samples, double epsilon) {
        // box = [minX, minY, minZ, maxX, maxY, maxZ]
        int minX = Integer.MIN_VALUE/4, minY = Integer.MIN_VALUE/4, minZ = Integer.MIN_VALUE/4;
        int maxX = Integer.MAX_VALUE/4, maxY = Integer.MAX_VALUE/4, maxZ = Integer.MAX_VALUE/4;

        for (Sample s : samples) {
            double R = s.d + epsilon;
            int sxMin = (int)floor(s.ox - R), sxMax = (int)ceil(s.ox + R);
            int syMin = (int)floor(s.oy - R), syMax = (int)ceil(s.oy + R);
            int szMin = (int)floor(s.oz - R), szMax = (int)ceil(s.oz + R);
            minX = max(minX, sxMin); maxX = min(maxX, sxMax);
            minY = max(minY, syMin); maxY = min(maxY, syMax);
            minZ = max(minZ, szMin); maxZ = min(maxZ, szMax);
        }
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            // No intersection (overly optimistic epsilon). Fall back to union box of all bands.
            minX = minY = minZ = Integer.MAX_VALUE/4;
            maxX = maxY = maxZ = Integer.MIN_VALUE/4;
            for (Sample s : samples) {
                double R = s.d + epsilon;
                minX = min(minX, (int)floor(s.ox - R)); maxX = max(maxX, (int)ceil(s.ox + R));
                minY = min(minY, (int)floor(s.oy - R)); maxY = max(maxY, (int)ceil(s.oy + R));
                minZ = min(minZ, (int)floor(s.oz - R)); maxZ = max(maxZ, (int)ceil(s.oz + R));
            }
        }
        return new int[]{minX,minY,minZ,maxX,maxY,maxZ};
    }

    private static void expandBox(int[] box, int pad){
        box[0]-=pad; box[1]-=pad; box[2]-=pad;
        box[3]+=pad; box[4]+=pad; box[5]+=pad;
    }

    private static double worstResidual(List<Sample> samples, double[] p){
        double w = 0.0;
        for (Sample s : samples) {
            double dx = p[0]-s.ox, dy = p[1]-s.oy, dz = p[2]-s.oz;
            double resid = abs(sqrt(dx*dx + dy*dy + dz*dz) - s.d);
            if (resid > w) w = resid;
        }
        return w;
    }

    // ---------- Helpers for common inputs ----------

    public static Sample fromBlockPos(BlockPos pos, double d){
        return new Sample(pos.getX(), pos.getY(), pos.getZ(), d);
    }

    public static Sample fromVector(Vec3 v, double d){
        return new Sample(v.xCoord, v.yCoord, v.zCoord, d);
    }
}

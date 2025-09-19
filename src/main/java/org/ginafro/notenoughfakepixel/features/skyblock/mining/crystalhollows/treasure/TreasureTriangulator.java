package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.treasure;

import org.ginafro.notenoughfakepixel.utils.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class TreasureTriangulator {

    // ---------- Adjustable params ----------
    private volatile double epsilon = 4.0;
    private volatile int maxSamples = 24;
    private volatile int minSamples = 4;
    private volatile double minPlayerDelta = 0.9; // bocks
    private volatile long estimateCooldownMs = 250; // 4hz
    private volatile double pruneK = 2.5;

    private volatile int maxVolume = 200_000;

    // ---------- State ----------
    private final RingBuffer samples;
    private final ReentrantLock samplesLock = new ReentrantLock();

    private volatile GridTrilateration.Result lastResult;
    private volatile long lastEstimateAt = 0L;

    private double lastX = Double.NaN, lastY = Double.NaN, lastZ = Double.NaN, lastD = Double.NaN;

    // ---------- Worker thread ----------
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduledTask;

    // ---------- Singleton thread-safe ----------
    private static final TreasureTriangulator INSTANCE = new TreasureTriangulator();

    public static TreasureTriangulator getInstance() {
        return INSTANCE;
    }

    private TreasureTriangulator() {
        this.samples = new RingBuffer(64);
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "NEF-TreasureWorker");
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY - 1);
            return t;
        });
        // background calculation (4 Hz)
        startWorker();
    }

    private void startWorker() {
        stopWorker();
        long period = Math.max(estimateCooldownMs, 50L);
        scheduledTask = executor.scheduleAtFixedRate(this::tryEstimateOnWorker, period, period, TimeUnit.MILLISECONDS);
    }

    // Call in unload/disable of module
    public void shutdown() {
        stopWorker();
        executor.shutdownNow();
    }

    private void stopWorker() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            scheduledTask = null;
        }
    }

    // ---------- Data thread (game thread) ----------
    public void handleData(double playerX, double playerY, double playerZ, double distance) {
        if (!shouldAccept(playerX, playerY, playerZ, distance)) return;

        samplesLock.lock();
        try {
            samples.add(new GridTrilateration.Sample(playerX, playerY, playerZ, distance));
            while (samples.size() > maxSamples) samples.poll(); // drop oldest
        } finally {
            samplesLock.unlock();
        }
        // Do not calculate here, do it in worker thread
    }

    // ---------- Worker: executes in thread ----------
    private void tryEstimateOnWorker() {
        // fast snapshot for tests
        GridTrilateration.Sample[] snap;
        samplesLock.lock();
        try {
            int n = samples.size();
            if (n < minSamples) return;
            snap = samples.toArray(new GridTrilateration.Sample[n]);
        } finally {
            samplesLock.unlock();
        }

        long now = System.currentTimeMillis();
        if (now - lastEstimateAt < estimateCooldownMs) return;
        lastEstimateAt = now;

        // mutable List for pruning
        ArrayList<GridTrilateration.Sample> buf = new ArrayList<>(snap.length);
        Collections.addAll(buf, snap);

        GridTrilateration.Result r = safeEstimateWithCap(buf, epsilon, maxVolume);

        if (r != null && buf.size() > minSamples) {
            final GridTrilateration.Int3 p = r.best;
            final double thr = pruneK * epsilon;
            boolean removed = false;
            for (Iterator<GridTrilateration.Sample> it = buf.iterator(); it.hasNext(); ) {
                GridTrilateration.Sample s = it.next();
                double resid = residual(p.x, p.y, p.z, s);
                if (resid > thr) {
                    it.remove();
                    removed = true;
                }
            }
            if (removed && buf.size() >= minSamples) {
                GridTrilateration.Result r2 = safeEstimateWithCap(buf, epsilon, maxVolume);
                if (r2 != null) r = r2;
            }
        }

        if (r != null) lastResult = r; // no lock
    }

    // ---------- API to render/UI ----------
    public GridTrilateration.Int3 getBestGuess() {
        return (lastResult == null) ? null : lastResult.best;
    }

    public double getConfidence() {
        if (lastResult == null) return 0.0;
        double c = lastResult.gapToNext / (1.0 + lastResult.score);
        if (c < 0) c = 0;
        else if (c > 1) c = 1;
        return c;
    }

    public String getDebugString() {
        if (lastResult == null) return "Triangulation: getting samples...";
        return "Triangulation: " + lastResult.toString() + "  conf=" + String.format(Locale.US, "%.2f", getConfidence());
    }

    public void reset() {
        samplesLock.lock();
        try {
            samples.clear();
        } finally {
            samplesLock.unlock();
        }
        lastResult = null;
        lastX = lastY = lastZ = lastD = Double.NaN;
        lastEstimateAt = 0L;
    }

    // ---------- Utils ----------
    private static GridTrilateration.Result safeEstimateWithCap(List<GridTrilateration.Sample> list, double eps, int cap) {
        try {
            // For full overload
            return GridTrilateration.estimate(list, eps, (int) Math.ceil(Math.max(2, eps)), cap);
            // If you don't have it, use the normal one and lower epsilon/extraPad to narrow the AABB.
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static double residual(int x, int y, int z, GridTrilateration.Sample s) {
        double dx = x - s.ox, dy = y - s.oy, dz = z - s.oz;
        return Math.abs(Math.sqrt(dx * dx + dy * dy + dz * dz) - s.d);
    }

    private boolean shouldAccept(double x, double y, double z, double d) {
        if (Double.isNaN(lastX)) {
            lastX = x;
            lastY = y;
            lastZ = z;
            lastD = d;
            return true;
        }
        double dx = x - lastX, dy = y - lastY, dz = z - lastZ;
        double move2 = dx * dx + dy * dy + dz * dz;
        double distDelta = Math.abs(d - lastD);
        if (move2 >= (minPlayerDelta * minPlayerDelta) || distDelta >= 0.75) {
            lastX = x;
            lastY = y;
            lastZ = z;
            lastD = d;
            return true;
        }
        return false;
    }

    // ---------- RingBuffer simple (no allocs) ----------
    private static final class RingBuffer {
        private GridTrilateration.Sample[] a;
        private int head = 0, tail = 0, size = 0;

        RingBuffer(int cap) {
            a = new GridTrilateration.Sample[cap];
        }

        int size() {
            return size;
        }

        void clear() {
            head = tail = size = 0;
        }

        void add(GridTrilateration.Sample s) {
            if (size == a.length) grow();
            a[tail] = s;
            tail = (tail + 1) % a.length;
            size++;
        }

        GridTrilateration.Sample poll() {
            if (size == 0) return null;
            GridTrilateration.Sample s = a[head];
            a[head] = null;
            head = (head + 1) % a.length;
            size--;
            return s;
        }

        GridTrilateration.Sample[] toArray(GridTrilateration.Sample[] out) {
            int n = size, i = 0, idx = head;
            while (i < n) {
                out[i++] = a[idx];
                idx = (idx + 1) % a.length;
            }
            return out;
        }

        GridTrilateration.Sample[] toArray() {
            GridTrilateration.Sample[] out = new GridTrilateration.Sample[size];
            return toArray(out);
        }

        private void grow() {
            int n = a.length, m = n << 1;
            GridTrilateration.Sample[] b = new GridTrilateration.Sample[m];
            toArray(b);
            a = b;
            head = 0;
            tail = size;
        }
    }
}


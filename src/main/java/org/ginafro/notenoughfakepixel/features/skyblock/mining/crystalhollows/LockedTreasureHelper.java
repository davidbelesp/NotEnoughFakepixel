package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.ParticlePacketEvent;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class LockedTreasureHelper {

    private static final Minecraft MC = Minecraft.getMinecraft();

    private static final double CHEST_RANGE = 0.5D;
    private static final float  SQUARE_SIZE = 0.18f;
    private static final long   MARKER_TTL_MS = 300;

    private final List<AxisAlignedBB> chestBoxes = new ArrayList<>(64);
    private final List<Marker> markers = new ArrayList<>(64);

    private static class Marker {
        double x, y, z;
        long   dieAt;
        void set(double x, double y, double z, long dieAt) {
            this.x = x; this.y = y; this.z = z; this.dieAt = dieAt;
        }
    }

    private Marker obtainMarker() {
        for (Marker marker : markers) {
            if (marker.dieAt <= 0) return marker;
        }
        Marker m = new Marker();
        markers.add(m);
        return m;
    }

    @SubscribeEvent
    public void onParticlePacket(ParticlePacketEvent e) {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!Config.feature.mining.lockedTreasureChest) return;
        if (MC.theWorld == null) return;

        final S2APacketParticles p = e.getPacket();
        if (p.getParticleType() != EnumParticleTypes.CRIT) return;

        final double x = p.getXCoordinate();
        final double y = p.getYCoordinate();
        final double z = p.getZCoordinate();

        collectNearbyChestAABBs();

        if (chestBoxes.isEmpty()) return;

        for (AxisAlignedBB box : chestBoxes) {
            if (pointWithinExpandedAABB(x, y, z, box, CHEST_RANGE)) {
                e.setCanceled(true);

                Marker m = obtainMarker();
                m.set(x, y, z, System.currentTimeMillis() + MARKER_TTL_MS);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent evt) {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!Config.feature.mining.lockedTreasureChest) return;
        if (MC.theWorld == null || MC.thePlayer == null) return;

        final long now = System.currentTimeMillis();
        boolean anyAlive = false;
        for (Marker marker : markers) {
            if (marker.dieAt > now) {
                anyAlive = true;
                break;
            }
        }
        if (!anyAlive) return;

        final double camX = MC.getRenderManager().viewerPosX;
        final double camY = MC.getRenderManager().viewerPosY;
        final double camZ = MC.getRenderManager().viewerPosZ;

        final float half = SQUARE_SIZE * 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.color(0f, 1f, 0f, 0.85f);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION); // GL_QUADS

        for (Marker m : markers) {
            if (m.dieAt <= now) {
                m.dieAt = 0;
                continue;
            }

            double cx = m.x - camX;
            double cy = m.y - camY;
            double cz = m.z - camZ;

            // 1) horizontal vector
            double vx = cx;
            double vz = cz;
            double vLen = Math.sqrt(vx * vx + vz * vz);
            if (vLen < 1e-6) {
                vx = 0.0;
                vz = 1.0;
                vLen = 1.0;
            }
            vx /= vLen;
            vz /= vLen;

            // 2) up = (0,1,0); right = up × viewHoriz = (vz, 0, -vx)
            double rdx = vz * half;
            double rdz = -vx * half;

            // 3) up * half
            double udx = 0.0;
            double udy = half;
            double udz = 0.0;

            wr.pos(cx - rdx - udx, cy - udy, cz - rdz - udz).endVertex();
            wr.pos(cx - rdx + udx, cy + udy, cz - rdz + udz).endVertex();
            wr.pos(cx + rdx + udx, cy + udy, cz + rdz + udz).endVertex();
            wr.pos(cx + rdx - udx, cy - udy, cz + rdz - udz).endVertex();
        }

        tess.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    // ===== Helpers =====

    private static boolean pointWithinExpandedAABB(double x, double y, double z, AxisAlignedBB box, double expand) {
        return x >= box.minX - expand && x <= box.maxX + expand
                && y >= box.minY - expand && y <= box.maxY + expand
                && z >= box.minZ - expand && z <= box.maxZ + expand;
    }

    private void collectNearbyChestAABBs() {
        chestBoxes.clear();
        if (MC.theWorld == null || MC.thePlayer == null) return;

        final int view = MC.gameSettings.renderDistanceChunks * 16 + 16;
        final double px = MC.thePlayer.posX;
        final double py = MC.thePlayer.posY;
        final double pz = MC.thePlayer.posZ;
        final double maxDistSq = (double) view * (double) view;

        @SuppressWarnings("unchecked")
        List<TileEntity> tiles = MC.theWorld.loadedTileEntityList;

        for (TileEntity te : tiles) {
            if (!(te instanceof TileEntityChest)) continue;

            BlockPos bp = te.getPos();
            double cx = bp.getX() + 0.5 - px;
            double cy = bp.getY() + 0.5 - py;
            double cz = bp.getZ() + 0.5 - pz;
            if (cx * cx + cy * cy + cz * cz > maxDistSq) continue;

            chestBoxes.add(new AxisAlignedBB(
                    bp.getX(), bp.getY(), bp.getZ(),
                    bp.getX() + 1, bp.getY() + 1, bp.getZ() + 1
            ));
        }
    }

}

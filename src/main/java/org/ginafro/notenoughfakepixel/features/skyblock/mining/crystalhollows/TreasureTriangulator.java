package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.*;
import org.ginafro.notenoughfakepixel.variables.Area;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Double.isNaN;

@RegisterEvents
public class TreasureTriangulator {

    // ---------- Adjustable params ----------
    private final double epsilon = 4.0;
    private final int maxSamples = 24;
    private final int minSamples = 4;
    private final double minPlayerDelta = 0.9; // blocks
    private final long estimateCooldownMs = 250L;
    private final double pruneK = 2.5; // delete residuals > pruneK * epsilon

    private final Deque<GridTrilateration.Sample> samples = new ArrayDeque<>(32);
    private GridTrilateration.Result lastResult = null;
    private long lastEstimateAt = 0L;

    private double lastX = Double.NaN, lastY = Double.NaN, lastZ = Double.NaN;
    private double lastD = Double.NaN;

    // Singleton
    private TreasureTriangulator INSTANCE = null;
    public TreasureTriangulator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TreasureTriangulator();
        }
        return INSTANCE;
    }

    public void handleData(double playerX, double playerY, double playerZ, double distance) {
        // 0) Filter almost indentical readings to not dirty with noises
        if (!shouldAccept(playerX, playerY, playerZ, distance)) return;

        samples.addLast(new GridTrilateration.Sample(playerX, playerY, playerZ, distance));
        if (samples.size() > maxSamples) samples.removeFirst();

        // 1) Try estimation if enough samples and cooldown
        if (samples.size() >= minSamples) {
            long now = System.currentTimeMillis();
            if (now - lastEstimateAt >= estimateCooldownMs) {
                estimateRobust();
                lastEstimateAt = now;
            }
        }
    }

    public GridTrilateration.Int3 getBestGuess() {
        return (lastResult == null) ? null : lastResult.best;
    }

    public double getConfidence() {
        if (lastResult == null) return 0.0;
        // Heuristics: relative gap (1 + score). [0,1]
        double c = lastResult.gapToNext / (1.0 + lastResult.score);
        if (c < 0) c = 0;
        if (c > 1) c = 1;
        return c;
    }

    /** Debug text for GUI and Overlay */
    public String getDebugString() {
        if (lastResult == null) return "Triangulation: getting samples...";
        return "Triangulation: " + lastResult.toString() + "  conf="
                + String.format(Locale.US, "%.2f", getConfidence());
    }

    /** Resets data completely */
    public void reset() {
        Logger.log("Triangulation reset");
        samples.clear();
        lastResult = null;
        lastX = lastY = lastZ = lastD = Double.NaN;
        lastEstimateAt = 0L;
    }


    private void estimateRobust() {
        // Copy the list for estimator (try not to modify the deque while itering)
        final ArrayList<GridTrilateration.Sample> buf = new ArrayList<>(samples);

        GridTrilateration.Result r = safeEstimate(buf, epsilon);

        // Cutting outliers by residue in the best point
        if (r != null && buf.size() > minSamples) {
            final GridTrilateration.Int3 p = r.best;
            final double thr = pruneK * epsilon;
            boolean removed = false;
            for (Iterator<GridTrilateration.Sample> it = buf.iterator(); it.hasNext(); ) {
                GridTrilateration.Sample s = it.next();
                double resid = residual(p.x, p.y, p.z, s);
                if (resid > thr) { it.remove(); removed = true; }
            }
            if (removed && buf.size() >= minSamples) {
                // Re-estima sin outliers
                GridTrilateration.Result r2 = safeEstimate(buf, epsilon);
                if (r2 != null) r = r2;
            }
        }

        if (r != null) lastResult = r;
    }

    private static GridTrilateration.Result safeEstimate(List<GridTrilateration.Sample> list, double eps) {
        try {
            return GridTrilateration.estimate(list, eps);
        } catch (Throwable t) {
            // If fails (i.e. 3 points almost coplaners/colinears) don't break the tick.
            return null;
        }
    }

    private static double residual(int x, int y, int z, GridTrilateration.Sample s) {
        double dx = x - s.ox, dy = y - s.oy, dz = z - s.oz;
        return Math.abs(Math.sqrt(dx*dx + dy*dy + dz*dz) - s.d);
    }

    @SubscribeEvent
    public void onChatReceived(net.minecraftforge.client.event.ClientChatReceivedEvent e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        if (ScoreboardUtils.currentArea != Area.CH_MINES_OF_DIVAN) return;
        String msg = e.message.getFormattedText();
        if (msg.contains("TREASURE: ")) {
            try {
                String distanceStr = msg.split("TREASURE: ")[1].split("m")[0].replaceAll("[^0-9.]", "");
                double distance = Double.parseDouble(distanceStr);
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                double playerX = player.posX;
                double playerY = player.posY;
                double playerZ = player.posZ;

                handleDataEvent(playerX, playerY, playerZ, distance);

            } catch (Exception ex) {
                Logger.log("§cMetal Detector: Failed to parse treasure distance from chat message");
            }
        }
    }

    public void handleDataEvent(double playerX, double playerY, double playerZ, double distance) {
        this.getInstance().handleData(playerX, playerY, playerZ, distance);
    }

    private boolean shouldAccept(double x, double y, double z, double d) {
        if (isNaN(lastX)) {
            lastX = x; lastY = y; lastZ = z; lastD = d;
            return true;
        }
        double dx = x - lastX, dy = y - lastY, dz = z - lastZ;
        double move2 = dx*dx + dy*dy + dz*dz;
        double distDelta = Math.abs(d - lastD);

        // Accept if player moved enough
        if (move2 >= (minPlayerDelta*minPlayerDelta) || distDelta >= 0.75) {
            lastX = x; lastY = y; lastZ = z; lastD = d;
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        getInstance().reset();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        getInstance().reset();
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        // if player right clicks a chest(finds it) reset the data
        if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) && e.world.getBlockState(e.pos).getBlock() instanceof BlockChest) {
            getInstance().reset();
        }

        // if player right clicks item with skyblock id DWARVEN_METAL_DETECTOR reset it
//        if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR) || e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
//            if (e.entity instanceof EntityPlayerSP){
//                EntityPlayerSP player = (EntityPlayerSP)e.entity;
//                if (ItemUtils.getInternalName(player.getHeldItem()).equals("DWARVEN_METAL_DETECTOR")){
//                    getInstance().reset();
//                    Logger.log("Resetting Triangulation list");
//                }
//            }
//        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        if (ScoreboardUtils.currentArea != Area.CH_MINES_OF_DIVAN) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        GridTrilateration.Int3 guess = this.getInstance().getBestGuess();

        if (guess != null ) {
            Color waypointColor = ColorUtils.getColor(Config.feature.mining.crystalDivanWaypointColor);
            RenderUtils.renderBeaconBeam(
                    new BlockPos(guess.x, player.posY, guess.z), waypointColor.getRGB(), 1, event.partialTicks
            );

            RenderUtils.renderWaypointText("Guess" ,new BlockPos(guess.x, player.posY + 3, guess.z), event.partialTicks);
        }
    }


}

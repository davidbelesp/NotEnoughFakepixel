package com.nef.notenoughfakepixel.features.skyblock.fishing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.ParticlePacketEvent;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.utils.SoundUtils;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class FishingCountdown {
    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityFishHook playerBobber;
    private long fishBiteTime;
    private String countdownText = null;
    private long countdownEndTime = 0;
    private final List<ParticleData> particleHistory = new ArrayList<>();
    private static final int MIN_PARTICLES_FOR_PATH = 3;
    private static final long MAX_PARTICLE_AGE = 1000;
    private static final double MAX_INTER_PARTICLE_DISTANCE = 1.0;
    private boolean biteTriggered = false;

    private static class ParticleData {
        Vec3 position;
        long timestamp;

        ParticleData(Vec3 position, long timestamp) {
            this.position = position;
            this.timestamp = timestamp;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.theWorld == null || mc.thePlayer == null) return;
        if (!Config.feature.fishing.fishingCountdown) return;

        updatePlayerBobber();
        mc.addScheduledTask(() -> {
            checkFishBiteStatus();
        });
        mc.addScheduledTask(() -> {
            cleanupParticleHistory();
        });
    }

    private void updatePlayerBobber() {
        playerBobber = mc.thePlayer.fishEntity;
    }

    private void checkFishBiteStatus() {
        if (mc.thePlayer == null) return;

        if (playerBobber == null) {
            countdownText = null;
            particleHistory.clear();
            biteTriggered = false;
            return;
        }

        if (!biteTriggered && System.currentTimeMillis() >= fishBiteTime && countdownText != null) {
            countdownText = "§c§l!!!";
            SoundUtils.playSound(
                    mc.thePlayer.getPosition().getX(),
                    mc.thePlayer.getPosition().getY(),
                    mc.thePlayer.getPosition().getZ(),
                    "note.pling",
                    1.0f,
                    1.0f
            );
            countdownEndTime = System.currentTimeMillis() + 2000;
            biteTriggered = true; // prevent spam
        }

        if (biteTriggered && System.currentTimeMillis() > countdownEndTime) {
            countdownText = null;
            biteTriggered = false;
        }
    }

    @SubscribeEvent
    public void onParticlePacketReceived(ParticlePacketEvent event) {
        S2APacketParticles packet = event.getPacket();
        if (playerBobber == null) return;

        EnumParticleTypes[] fishingParticles = {
                EnumParticleTypes.WATER_WAKE,
                EnumParticleTypes.LAVA
        };

        for (EnumParticleTypes particleType : fishingParticles) {
            if (particleType == packet.getParticleType()) {
                Vec3 particlePos = new Vec3(
                        packet.getXCoordinate(),
                        packet.getYCoordinate(),
                        packet.getZCoordinate()
                );
                processFishingParticle(particlePos);
                break;
            }
        }
    }

    private void processFishingParticle(Vec3 particlePos) {
        if (playerBobber == null) return;

        double dx = particlePos.xCoord - playerBobber.posX;
        double dz = particlePos.zCoord - playerBobber.posZ;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        if (horizontalDist > 3) return;

        long currentTime = System.currentTimeMillis();
        particleHistory.add(new ParticleData(particlePos, currentTime));

        if (particleHistory.size() >= MIN_PARTICLES_FOR_PATH) {
            if (isValidParticlePath(new Vec3(playerBobber.posX, 0, playerBobber.posZ))) {
                ParticleData firstParticle = particleHistory.get(0);
                ParticleData lastParticle = particleHistory.get(particleHistory.size() - 1);

                double firstDistance = new Vec3(firstParticle.position.xCoord, 0, firstParticle.position.zCoord)
                        .distanceTo(new Vec3(playerBobber.posX, 0, playerBobber.posZ));
                double lastDistance = new Vec3(lastParticle.position.xCoord, 0, lastParticle.position.zCoord)
                        .distanceTo(new Vec3(playerBobber.posX, 0, playerBobber.posZ));
                long totalTimeDiff = lastParticle.timestamp - firstParticle.timestamp;

                if (totalTimeDiff > 0 && firstDistance > lastDistance) {
                    double distanceDecrease = firstDistance - lastDistance;
                    double speed = distanceDecrease / totalTimeDiff; // blocks per ms

                    if (speed > 0) {
                        double currentDistance = lastDistance;
                        double timeToReach = currentDistance / speed; // ms
                        fishBiteTime = currentTime + (long) timeToReach;

                        if (timeToReach > 100) {
                            double countdownVal = MathHelper.clamp_double(timeToReach / 1000.0, 0.1, 5.0);
                            countdownText = "§e§l" + formatCountdownNumber(countdownVal);
                            countdownEndTime = currentTime + 1000;
                        }
                    }
                }
            }
        }
    }

    private boolean isValidParticlePath(Vec3 bobberPos) {
        if (particleHistory.size() < MIN_PARTICLES_FOR_PATH) return false;

        for (int i = 1; i < particleHistory.size(); i++) {
            double interParticleDistance = particleHistory.get(i).position.distanceTo(particleHistory.get(i - 1).position);
            if (interParticleDistance > MAX_INTER_PARTICLE_DISTANCE) {
                return false;
            }
        }

        double firstDistance = particleHistory.get(0).position.distanceTo(bobberPos);
        double lastDistance = particleHistory.get(particleHistory.size() - 1).position.distanceTo(bobberPos);

        return lastDistance < firstDistance;
    }

    private void cleanupParticleHistory() {
        long currentTime = System.currentTimeMillis();
        particleHistory.removeIf(particle -> currentTime - particle.timestamp > MAX_PARTICLE_AGE);
    }

    private String formatCountdownNumber(double value) {
        return String.format("%.1f", Math.round(value * 10) / 10.0);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!Config.feature.fishing.fishingCountdown) return;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || countdownText == null) return;
        if (System.currentTimeMillis() > countdownEndTime) {
            countdownText = null;
            return;
        }

        FontRenderer fr = mc.fontRendererObj;
        ScaledResolution res = new ScaledResolution(mc);

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);

        int textWidth = fr.getStringWidth(countdownText);
        int x = (res.getScaledWidth() / 4) - (textWidth / 2);
        int y = (res.getScaledHeight() / 4) + 10;

        fr.drawStringWithShadow(countdownText, x, y, 0xFFFFFF);
        GlStateManager.popMatrix();
    }
}
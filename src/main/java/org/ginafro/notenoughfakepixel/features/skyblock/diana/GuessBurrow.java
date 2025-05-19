package org.ginafro.notenoughfakepixel.features.skyblock.diana;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.PacketReadEvent;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

@RegisterEvents
public class GuessBurrow {

    int dingIndex = 0;
    boolean hasDinged = false;
    Float firstPitch = 0f;
    Float lastDingPitch = 0f;
    Vec3 lastSoundPoint = null;
    Vec3 guessPoint = null;
    ArrayList<Float> dingSlope = new ArrayList<>();
    Float distance = null;
    private final Queue<S2APacketParticles> particleDripLavaQueue = new LinkedList<>();
    private final ArrayList<Vec3> particleLocations = new ArrayList<>();

    private String displayText = null;
    private String warpCommand = null;
    private long cooldownEndTime = 0;
    private boolean wasKeyPressed = false;

    private static final WarpLocation[] WARP_LOCATIONS = {
            new WarpLocation("Crypts", new Vec3(-187, 74, -86), "/warp crypts"),
            new WarpLocation("Hub", new Vec3(-2, 70, -68), "/warp hub"),
            new WarpLocation("Castle", new Vec3(-248, 130, 44), "/warp castle"),
            new WarpLocation("Dark Auction", new Vec3(91, 74, 173), "/warp da"),
            new WarpLocation("Museum", new Vec3(-69, 76, 80), "/warp museum")
    };

    @SubscribeEvent
    public void onPacketRead(PacketReadEvent event) {
        if (!ScoreboardUtils.currentLocation.isHub()) return;

        if (event.packet instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundPacket = (S29PacketSoundEffect) event.packet;
            if (!"note.harp".equals(soundPacket.getSoundName())) return;

            float pitch = soundPacket.getPitch();
            Vec3 pos = new Vec3(soundPacket.getX(), soundPacket.getY(), soundPacket.getZ());

            if (!hasDinged) {
                firstPitch = pitch;
            }
            hasDinged = true;

            if (pitch < lastDingPitch) {
                firstPitch = pitch;
                dingIndex = 0;
                dingSlope.clear();
                lastDingPitch = pitch;
                lastSoundPoint = null;
                distance = null;
                particleLocations.clear();
            }

            if (lastDingPitch == 0f) {
                lastDingPitch = pitch;
                distance = null;
                lastSoundPoint = null;
                particleLocations.clear();
                return;
            }

            dingIndex++;

            if (dingIndex > 1) {
                dingSlope.add(pitch - lastDingPitch);
            }
            if (dingSlope.size() > 20) {
                dingSlope.remove(0);
            }

            Float slope = dingSlope.isEmpty() ? 0f
                    : dingSlope.stream().reduce(0f, Float::sum) / dingSlope.size();

            lastSoundPoint = pos;
            lastDingPitch = pitch;

            distance = slope == 0f ? 0f : (float) (Math.E / slope / 2.8);

            if (pitch > 0) {
                distance *= Math.max(0.1f, 2.0f - pitch);
            }

            if (distance < 0) {
                distance = 0f;
                guessPoint = null;
            }
        } else if (event.packet instanceof S2APacketParticles) {
            S2APacketParticles particlePacket = (S2APacketParticles) event.packet;
            addParticle(particlePacket);
        }
    }

    public void addParticle(S2APacketParticles particle) {
        switch (particle.getParticleType().getParticleName()) {
            case "dripLava":
                particleDripLavaQueue.add(particle);
                break;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !ScoreboardUtils.currentLocation.isHub()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null || lastSoundPoint == null) return;

        while (!particleDripLavaQueue.isEmpty()) {
            S2APacketParticles particle = particleDripLavaQueue.poll();
            Vec3 currLoc = new Vec3(particle.getXCoordinate(), particle.getYCoordinate(), particle.getZCoordinate());

            if (Math.abs(currLoc.xCoord - lastSoundPoint.xCoord) < 2 &&
                    Math.abs(currLoc.yCoord - lastSoundPoint.yCoord) < 0.5 &&
                    Math.abs(currLoc.zCoord - lastSoundPoint.zCoord) < 2) {

                if (particleLocations.size() < 100 && (particleLocations.isEmpty() || distance(particleLocations.get(particleLocations.size() - 1), currLoc) != 0.0)) {
                    double distMultiplier = 1.0;
                    if (particleLocations.size() > 2) {
                        double predictedDist = 0.06507 * particleLocations.size() + 0.259;
                        Vec3 lastPos = particleLocations.get(particleLocations.size() - 1);
                        double actualDist = distance(currLoc, lastPos);
                        distMultiplier = actualDist / predictedDist;
                    }
                    particleLocations.add(currLoc);

                    if (particleLocations.size() > 5 && distance != null) {
                        ArrayList<Double> slopeThing = new ArrayList<>();
                        for (int i = 0; i < particleLocations.size() - 1; i++) {
                            Vec3 a = particleLocations.get(i);
                            Vec3 b = particleLocations.get(i + 1);
                            slopeThing.add(Math.atan2(b.zCoord - a.zCoord, b.xCoord - a.xCoord));
                        }

                        Vec3 abc = solveEquationThing(
                                new Vec3(slopeThing.size() - 5, slopeThing.size() - 3, slopeThing.size() - 1),
                                new Vec3(
                                        slopeThing.get(slopeThing.size() - 5),
                                        slopeThing.get(slopeThing.size() - 3),
                                        slopeThing.get(slopeThing.size() - 1)
                                )
                        );
                        double a = abc.xCoord;
                        double b = abc.yCoord;
                        double c = abc.zCoord;

                        Vec3 lastParticle = particleLocations.get(particleLocations.size() - 1);
                        Vec3 secondLastParticle = particleLocations.get(particleLocations.size() - 2);
                        double dx = lastParticle.xCoord - secondLastParticle.xCoord;
                        double dz = lastParticle.zCoord - secondLastParticle.zCoord;
                        double magnitude = Math.hypot(dx, dz);
                        dx /= magnitude;
                        dz /= magnitude;

                        double[] lastPos = {lastParticle.xCoord, lastParticle.yCoord, lastParticle.zCoord};
                        double distCovered = 0.0;
                        int i = slopeThing.size() - 1;

                        while (distCovered < distance && i < 10000) {
                            double distStep = distMultiplier * (0.06507 * i + 0.259);
                            lastPos[0] += dx * distStep;
                            lastPos[1] = lastSoundPoint.yCoord;
                            lastPos[2] += dz * distStep;
                            distCovered = Math.hypot(lastPos[0] - lastSoundPoint.xCoord, lastPos[2] - lastSoundPoint.zCoord);
                            i++;
                        }

                        Vec3 predictedPoint = new Vec3(lastPos[0], lastPos[1], lastPos[2]);
                        guessPoint = findNearestDirtWithAirAbove(mc, predictedPoint);
                    }
                }
            }
        }

        if (guessPoint != null && Config.feature.diana.dianaWarpHelper
                && ScoreboardUtils.currentLocation.isHub()
                && mc.theWorld != null
                && mc.thePlayer != null) {
            Vec3 playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            double playerDist = distance(playerPos, guessPoint);

            WarpLocation bestWarp = null;
            double minWarpDist = Double.MAX_VALUE;

            for (WarpLocation warp : WARP_LOCATIONS) {
                boolean isEnabled = true;
                if (warp.command.equals("/warp da")) {
                    isEnabled = Config.feature.diana.dianaWarpDa;
                } else if (warp.command.equals("/warp museum")) {
                    isEnabled = Config.feature.diana.dianaWarpMuseum;
                } else if (warp.command.equals("/warp crypts")) {
                    isEnabled = Config.feature.diana.dianaWarpCrypts;
                } else if (warp.command.equals("/warp castle")) {
                    isEnabled = Config.feature.diana.dianaWarpCastle;
                }

                if (!isEnabled) continue;

                double warpDist = distance(warp.pos, guessPoint);
                if (warpDist < minWarpDist) {
                    minWarpDist = warpDist;
                    bestWarp = warp;
                }
            }

            if (bestWarp != null && minWarpDist < playerDist) {
                String keyName = Keyboard.getKeyName(Config.feature.diana.warpKeybind);
                displayText = "Warp to " + bestWarp.name + " (" + (keyName != null ? keyName : "None") + ")";
                warpCommand = bestWarp.command;
            } else {
                displayText = null;
                warpCommand = null;
            }
        } else {
            displayText = null;
            warpCommand = null;
        }

        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            return;
        }

        if (Config.feature.diana.dianaWarpHelper && warpCommand != null) {
            boolean keyPressed = Keyboard.isKeyDown(Config.feature.diana.warpKeybind);
            if (keyPressed && !wasKeyPressed && System.currentTimeMillis() > cooldownEndTime) {
                mc.thePlayer.sendChatMessage(warpCommand);
                cooldownEndTime = System.currentTimeMillis() + 5000;
                System.out.println("Executed warp: " + warpCommand);
                resetState();
            }
            wasKeyPressed = keyPressed;
        }
    }

    private Vec3 solveEquationThing(Vec3 x, Vec3 y) {
        double a = (-y.xCoord * x.yCoord * x.xCoord - y.yCoord * x.yCoord * x.zCoord +
                y.yCoord * x.yCoord * x.xCoord + x.yCoord * x.zCoord * y.zCoord +
                x.xCoord * x.zCoord * y.xCoord - x.xCoord * x.zCoord * y.zCoord) /
                (x.yCoord * y.xCoord - x.yCoord * y.zCoord + x.xCoord * y.zCoord -
                        y.xCoord * x.zCoord + y.yCoord * x.zCoord - y.yCoord * x.xCoord);
        double b = (y.xCoord - y.yCoord) * (x.xCoord + a) * (x.yCoord + a) / (x.yCoord - x.xCoord);
        double c = y.xCoord - b / (x.xCoord + a);
        return new Vec3(a, b, c);
    }

    private Vec3 findNearestDirtWithAirAbove(Minecraft mc, Vec3 point) {
        int x = (int) Math.floor(point.xCoord);
        int z = (int) Math.floor(point.zCoord);
        int yStart = Math.max(1, Math.min(255, (int) Math.floor(point.yCoord)));

        for (int y = yStart; y > 0; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            Block blockAbove = mc.theWorld.getBlockState(pos.up()).getBlock();
            if ((block == Blocks.dirt || block == Blocks.grass) && blockAbove == Blocks.air) {
                return new Vec3(x + 0.5, y, z + 0.5);
            }
        }

        for (int y = yStart + 1; y < 255; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            Block blockAbove = mc.theWorld.getBlockState(pos.up()).getBlock();
            if ((block == Blocks.dirt || block == Blocks.grass) && blockAbove == Blocks.air) {
                return new Vec3(x + 0.5, y, z + 0.5);
            }
        }

        double playerY = mc.thePlayer.posY;
        int yFallback = Math.max(1, Math.min(255, (int) Math.floor(playerY)));
        BlockPos pos = new BlockPos(x, yFallback, z);
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        Block blockAbove = mc.theWorld.getBlockState(pos.up()).getBlock();
        if ((block == Blocks.dirt || block == Blocks.grass) && blockAbove == Blocks.air) {
            return new Vec3(x + 0.5, yFallback, z + 0.5);
        }

        return new Vec3(x + 0.5, yFallback, z + 0.5);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (guessPoint != null && Config.feature.diana.dianaBurrowGuess) {
            BlockPos loc = new BlockPos(guessPoint.xCoord, guessPoint.yCoord + 1, guessPoint.zCoord);
            AxisAlignedBB aabb = new AxisAlignedBB(
                    loc.getX(), loc.getY(), loc.getZ(),
                    loc.getX() + 1, loc.getY() + 1, loc.getZ() + 1
            );

            renderWaypointText("Burrow Guess", loc, event.partialTicks);
            drawOutlinedBoundingBox(aabb, Color.GREEN, 2.0f, event.partialTicks);
        }
    }

    public static void renderWaypointText(String str, BlockPos loc, float partialTicks) {
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.pushMatrix();

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

        double x = loc.getX() + 0.5 - viewerX;
        double y = loc.getY() + 0.5 - viewerY - viewer.getEyeHeight();
        double z = loc.getZ() + 0.5 - viewerZ;

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);
        if (distSq > 144) {
            x *= 12 / dist;
            y *= 12 / dist;
            z *= 12 / dist;
        }
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0, viewer.getEyeHeight(), 0);

        float scale = 2.0F;
        GlStateManager.scale(scale, scale, scale);

        drawNametag(str);

        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, -0.25f, 0);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

        drawNametag(EnumChatFormatting.YELLOW.toString() + Math.round(dist) + "m");

        GlStateManager.popMatrix();
        GlStateManager.disableLighting();
    }

    public static void drawNametag(String str) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.pushMatrix();
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 0;

        int j = fontrenderer.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-j - 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(-j - 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(j + 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(j + 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
        GlStateManager.depthMask(true);
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aabb, Color color, float width, float partialTicks) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(width);

        RenderGlobal.drawOutlinedBoundingBox(aabb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static double distance(Vec3 d1, Vec3 d2) {
        double dx = d2.xCoord - d1.xCoord;
        double dy = d2.yCoord - d1.yCoord;
        double dz = d2.zCoord - d1.zCoord;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        guessPoint = null;
        resetState();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || displayText == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);

        int textWidth = mc.fontRendererObj.getStringWidth(displayText);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;

        int x = Config.feature.diana.warpHelperPos.getAbsX(sr, textWidth);
        int y = Config.feature.diana.warpHelperPos.getAbsY(sr, textHeight);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(Config.feature.diana.warpHelperScale,
                Config.feature.diana.warpHelperScale, 1f);
        mc.fontRendererObj.drawStringWithShadow(displayText, 0, 0, 0xFFFFFF);
        GL11.glPopMatrix();
    }

    private void resetState() {
        displayText = null;
        warpCommand = null;
        wasKeyPressed = false;
    }

    public static class WarpLocation {
        public String name;
        public Vec3 pos;
        public String command;

        public WarpLocation(String name, Vec3 pos, String command) {
            this.name = name;
            this.pos = pos;
            this.command = command;
        }
    }
}
package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.util.StringUtils;
import com.nef.notenoughfakepixel.config.gui.utils.Utils;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class EtherwarpZoom {

    private boolean wasUsingEtherwarp = false;
    private boolean usingEtherwarp = false;
    private RaycastResult etherwarpRaycast = null;
    private int lastEtherwarpUse = 0;
    private String denyTpReason = null;
    public int aoteTeleportationMillis = 0;
    public Vector3f aoteTeleportationCurr = null;

    private final Pattern etherwarpDistancePattern = Pattern.compile("(?<distance>\\d{2}) blocks away\\.");


    private static class RaycastResult {
        IBlockState state;
        BlockPos pos;

        public RaycastResult(IBlockState state, BlockPos pos) {
            this.state = state;
            this.pos = pos;
        }
    }

    private RaycastResult raycast(EntityPlayerSP player, float partialTicks, float dist, float step) {
        Vector3f pos = new Vector3f((float) player.posX, (float) player.posY + player.getEyeHeight(), (float) player.posZ);

        Vec3 lookVec3 = player.getLook(partialTicks);

        Vector3f look = new Vector3f((float) lookVec3.xCoord, (float) lookVec3.yCoord, (float) lookVec3.zCoord);
        look.scale(step / look.length());

        int stepCount = (int) Math.ceil(dist / step);

        for (int i = 0; i < stepCount; i++) {
            Vector3f.add(pos, look, pos);

            WorldClient world = Minecraft.getMinecraft().theWorld;
            BlockPos position = new BlockPos(pos.x, pos.y, pos.z);
            IBlockState state = world.getBlockState(position);

            if (state.getBlock() != Blocks.air) {
                //Back-step
                Vector3f.sub(pos, look, pos);
                look.scale(0.1f);

                for (int j = 0; j < 10; j++) {
                    Vector3f.add(pos, look, pos);

                    BlockPos position2 = new BlockPos(pos.x, pos.y, pos.z);
                    IBlockState state2 = world.getBlockState(position2);

                    if (state2.getBlock() != Blocks.air) {
                        return new RaycastResult(state2, position2);
                    }
                }

                return new RaycastResult(state, position);
            }
        }

        return null;
    }

    @SubscribeEvent
    public void renderBlockOverlay(DrawBlockHighlightEvent event) {
        if (aoteTeleportationCurr != null && aoteTeleportationMillis > 0) {
            event.setCanceled(true);
        }

        usingEtherwarp = false;
        etherwarpRaycast = null;
        float lastFOVMult = this.targetFOVMult;
        this.targetFOVMult = 1;
        this.targetSensMult = 1;

        ItemStack held = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (held == null) {
            return;
        }
        String heldInternal = ItemUtils.getInternalName(held);
        if (heldInternal == null) {
            return;
        }

        EntityPlayer player = event.player;
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.partialTicks;

        if (tick - lastEtherwarpUse > 10 || !Config.feature.qol.qolEtherwarpZoom)
            if (onRenderBlockEtherwarp(
                    heldInternal,
                    held,
                    d0,
                    d1,
                    d2,
                    lastFOVMult
            )) {
                return;
            }


    }

    private boolean onRenderBlockEtherwarp(
            String heldInternal,
            ItemStack held,
            double d0,
            double d1,
            double d2,
            float lastFOVMult
    ) {
        boolean aotv = Minecraft.getMinecraft().thePlayer.isSneaking() &&
                (heldInternal.equals("ASPECT_OF_THE_VOID") || heldInternal.equals("ASPECT_OF_THE_END"));
        if (!aotv && !heldInternal.equals("ETHERWARP_CONDUIT")) {
            return false;
        }

        usingEtherwarp = !aotv;
        if (aotv) {
            NBTTagCompound tag = held.getTagCompound();
            if (tag != null && tag.hasKey("ExtraAttributes", 10)) {
                NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");
                if (ea.hasKey("conduit")) {
                    if (ea.getInteger("conduit") == 1) {
                        usingEtherwarp = true;
                    }
                }
            }
        }

        if (!usingEtherwarp) {
            return false;
        }

        int dist = 0;
        for (String line : ItemUtils.getLoreFromNBT(held.getTagCompound())) {
            String cleaned = StringUtils.cleanColour(line);
            Matcher matcher = etherwarpDistancePattern.matcher(cleaned);
            if (matcher.matches()) {
                dist = Integer.parseInt(matcher.group("distance"));
                break;
            }
        }

        if(dist == 0) return false;
        etherwarpRaycast = raycast(Minecraft.getMinecraft().thePlayer, 1f, dist, 0.1f);

        if (etherwarpRaycast != null && Config.feature.qol.qolEtherwarpOverlay) {
            if (denyTpReason == null) {
                AxisAlignedBB box = etherwarpRaycast.state.getBlock().getSelectedBoundingBox(
                        Minecraft.getMinecraft().theWorld,
                        etherwarpRaycast.pos
                );
                String color;
                if (denyTpReason != null) {
                    color = Config.feature.qol.qolEtherwarpFailedOverlayColor;
                } else color = Config.feature.qol.qolEtherwarpOverlayColor;

                AxisAlignedBB bb = box.expand(0.01D, 0.01D, 0.01D).offset(-d0, -d1, -d2);
                drawFilledBoundingBox(
                        bb,
                        1f,
                        ColorUtils.getColor(color)
                );

                GlStateManager.disableDepth();
                drawOutlineBoundingBox(
                        bb,
                        2f,
                        ColorUtils.getColor(color)
                );
                GlStateManager.enableDepth();

                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
            }

            if (Config.feature.qol.qolEtherwarpZoom) {
                float distFactor = 1 -
                        (float) Math.sqrt(etherwarpRaycast.pos.distanceSq(Minecraft.getMinecraft().thePlayer.getPosition())) /
                                60;

                targetFOVMult = distFactor * distFactor * distFactor * 0.75f + 0.25f;
                if (targetFOVMult < 0.25f) targetFOVMult = 0.25f;

                targetSensMult = distFactor * 0.76f + 0.25f;
            }
        } else if (Config.feature.qol.qolEtherwarpZoom) {
            targetFOVMult = lastFOVMult;
        }
        return true;
    }

    @SubscribeEvent
    public void onOverlayDrawn(RenderGameOverlayEvent.Post event) {
        if (((event.type == null && Loader.isModLoaded("labymod")) || event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS)) {

            WorldClient world = Minecraft.getMinecraft().theWorld;
            if (usingEtherwarp) {
                denyTpReason = null;

                if (etherwarpRaycast == null) {
                    denyTpReason = "Too far!";
                } else {
                    BlockPos pos = etherwarpRaycast.pos;

                    Block block = etherwarpRaycast.state.getBlock();
                    if (!block.isCollidable() ||
                            //Don't allow teleport at this block
                            block == Blocks.carpet || block == Blocks.skull ||
                            block.getCollisionBoundingBox(world, etherwarpRaycast.pos, etherwarpRaycast.state) == null &&
                                    //Allow teleport at this block
                                    block != Blocks.wall_sign && block != Blocks.standing_sign) {
                        denyTpReason = "Not solid!";
                    } else {
                        BlockPos blockPosAbove = pos.add(0, 1, 0);
                        Block blockAbove = world.getBlockState(blockPosAbove).getBlock();

                        Block twoBlockAbove = world.getBlockState(pos.add(0, 2, 0)).getBlock();
                        if (blockAbove != Blocks.air &&
                                //Allow teleport to the block below this block
                                blockAbove != Blocks.carpet && blockAbove != Blocks.skull && blockAbove.isCollidable() &&
                                blockAbove.getCollisionBoundingBox(world, blockPosAbove, world.getBlockState(blockPosAbove)) != null ||
                                //Don't allow teleport to the block below this block
                                blockAbove == Blocks.wall_sign || block == Blocks.standing_sign ||
                                //Allow teleport to the block 2 blocks below this block
                                twoBlockAbove != Blocks.air && twoBlockAbove != Blocks.double_plant && twoBlockAbove != Blocks.carpet &&
                                        blockAbove != Blocks.skull) {
                            denyTpReason = "No air above!";
                        }
                    }
                }

                if (Config.feature.qol.qolEtherwarpText) {
                    if (denyTpReason != null) {
                        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
                        Utils.drawStringCentered(EnumChatFormatting.RED + "Can't TP: " + denyTpReason,
                                Minecraft.getMinecraft().fontRendererObj,
                                scaledResolution.getScaledWidth() / 2f, scaledResolution.getScaledHeight() / 2f + 10, true, 0
                        );
                        GlStateManager.color(1, 1, 1, 1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGameTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        if (!usingEtherwarp && wasUsingEtherwarp) {
            if (player.rotationYaw > 0) {
                player.rotationYaw -= 0.000001F;
            } else {
                player.rotationYaw += 0.000001F;
            }
        }
        wasUsingEtherwarp = usingEtherwarp;

        tick++;
        if (tick > Integer.MAX_VALUE / 2) tick = 0;

    }

    private static float lastPartialTicks = 0;
    private static float currentFOVMult = 1;
    private static float lastPartialDelta = 0;
    private static float currentSensMult = 1;
    private static float targetSensMult = 1;
    private static int tick;
    private static float targetFOVMult = 1;

    public static float getSensMultiplier() {
        if (targetSensMult < 0) {
            currentSensMult = 1;
        } else {
            float deltaSens = targetSensMult - currentSensMult;

            currentSensMult += deltaSens * lastPartialDelta * 0.1;// (0.05 * );
            if (currentSensMult < 0.25f) currentSensMult = 0.25f;
            if (currentSensMult > 1) currentSensMult = 1;
        }
        return currentSensMult;
    }

    public static float getFovMultiplier(float partialTicks) {
        float partialDelta = partialTicks + tick - lastPartialTicks;
        if (partialDelta < 0) partialDelta++;

        if (partialDelta > 0) lastPartialDelta = partialDelta;

        if (targetFOVMult < 0) {
            currentFOVMult = 1;
        } else {
            float deltaFOV = targetFOVMult - currentFOVMult;

            currentFOVMult += deltaFOV * lastPartialDelta * 0.2;
            if (currentFOVMult < 0.15f) currentFOVMult = 0.15f;
            if (currentFOVMult > 1) currentFOVMult = 1;
        }
        lastPartialTicks = partialTicks + tick;
        return currentFOVMult;
    }

    public static void drawOutlineBoundingBox(AxisAlignedBB p_181561_0_, float alpha, Color c) {
        float newAlpha = c.getAlpha() / 255f * alpha;
        if (newAlpha > 1) newAlpha = 1;
        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, newAlpha);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        GL11.glLineWidth(3);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();

        GL11.glLineWidth(1);
    }

    public static void drawFilledBoundingBox(AxisAlignedBB p_181561_0_, float alpha, Color c) {

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f * alpha);

        //vertical
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();

        GlStateManager.color(
                c.getRed() / 255f * 0.8f,
                c.getGreen() / 255f * 0.8f,
                c.getBlue() / 255f * 0.8f,
                c.getAlpha() / 255f * alpha
        );

        //x
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();

        GlStateManager.color(
                c.getRed() / 255f * 0.9f,
                c.getGreen() / 255f * 0.9f,
                c.getBlue() / 255f * 0.9f,
                c.getAlpha() / 255f * alpha
        );
        //z
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
    }

}

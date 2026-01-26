package com.nef.notenoughfakepixel.features.skyblock.dungeons.puzzles;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RegisterEvents
public class CreeperSolver {

    static final int[] CREEPER_COLOURS = {
            0xFF50EF39,  // Green
            0xFFC51111,  // Red
            0xFF132ED1,  // Blue
            0xFF117F2D,  // Dark Green
            0xFFED54BA,  // Pink
            0xFFEF7D0D,  // Orange
            0xFFF5F557,  // Yellow
            0xFFD6E0F0,  // Light Gray
            0xFF6B2FBB,  // Purple
            0xFF39FEDC   // Cyan
    };
    static boolean drawCreeperLines = false;
    static Vec3 creeperLocation = new Vec3(0, 0, 0);
    static List<Vec3[]> creeperLines = new ArrayList<>();
    private static int ticks = 0;
    private static final Set<BlockPos> usedEndBlocks = new HashSet<>(); // Track used endpoints

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        EntityPlayerSP player = mc.thePlayer;
        ticks++;
        if (ticks % 20 == 0) {
            if (Config.feature.dungeons.dungeonsCreeper && SkyblockData.getCurrentLocation().isDungeon() && world != null && player != null) {
                double x = player.posX;
                double y = player.posY;
                double z = player.posZ;
                AxisAlignedBB creeperScan = new AxisAlignedBB(x - 14, y - 8, z - 13, x + 14, y + 8, z + 13);
                List<EntityCreeper> creepers = world.getEntitiesWithinAABB(EntityCreeper.class, creeperScan);

                if (!creepers.isEmpty() && !creepers.get(0).isInvisible()) {
                    EntityCreeper creeper = creepers.get(0);
                    creeperLines.clear();
                    usedEndBlocks.clear();
                    if (!drawCreeperLines) creeperLocation = new Vec3(creeper.posX, creeper.posY + 1, creeper.posZ);
                    drawCreeperLines = true;

                    BlockPos point1 = new BlockPos(creeper.posX - 14, creeper.posY - 7, creeper.posZ - 13);
                    BlockPos point2 = new BlockPos(creeper.posX + 14, creeper.posY + 10, creeper.posZ + 13);
                    Iterable<BlockPos> blocks = BlockPos.getAllInBox(point1, point2);

                    for (BlockPos blockPos : blocks) {
                        Block block = world.getBlockState(blockPos).getBlock();
                        if (block == Blocks.sea_lantern || block == Blocks.prismarine) {
                            Vec3 startBlock = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
                            BlockPos oppositeBlock = getFirstBlockPosAfterVectors(mc, startBlock, creeperLocation, 10, 20);
                            BlockPos endBlock = getUniqueNearbyBlock(mc, oppositeBlock, Blocks.sea_lantern, Blocks.prismarine);

                            if (endBlock != null && startBlock.yCoord > 68 && endBlock.getY() > 68) {
                                Vec3[] insertArray = {startBlock, new Vec3(endBlock.getX() + 0.5, endBlock.getY() + 0.5, endBlock.getZ() + 0.5)};
                                creeperLines.add(insertArray);
                                usedEndBlocks.add(endBlock);
                            }
                        }
                    }
                } else {
                    drawCreeperLines = false;
                    usedEndBlocks.clear();
                }
            }
            ticks = 0;
        }
    }

    public static BlockPos getFirstBlockPosAfterVectors(Minecraft mc, Vec3 pos1, Vec3 pos2, int strength, int distance) {
        double x = pos2.xCoord - pos1.xCoord;
        double y = pos2.yCoord - pos1.yCoord;
        double z = pos2.zCoord - pos1.zCoord;

        for (int i = strength; i < distance * strength; i++) {
            double newX = pos1.xCoord + ((x / strength) * i);
            double newY = pos1.yCoord + ((y / strength) * i);
            double newZ = pos1.zCoord + ((z / strength) * i);

            BlockPos newBlock = new BlockPos(newX, newY, newZ);
            if (mc.theWorld.getBlockState(newBlock).getBlock() != Blocks.air) {
                return newBlock;
            }
        }
        return null;
    }

    public static BlockPos getUniqueNearbyBlock(Minecraft mc, BlockPos pos, Block... blockTypes) {
        if (pos == null) return null;
        BlockPos pos1 = new BlockPos(pos.getX() - 2, pos.getY() - 3, pos.getZ() - 2);
        BlockPos pos2 = new BlockPos(pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);

        BlockPos closestBlock = null;
        double closestBlockDistance = 99;
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(pos1, pos2);

        for (BlockPos block : blocks) {
            for (Block blockType : blockTypes) {
                if (mc.theWorld.getBlockState(block).getBlock() == blockType &&
                        !usedEndBlocks.contains(block) && // Skip if already used
                        block.distanceSq(pos) < closestBlockDistance) {
                    closestBlock = block;
                    closestBlockDistance = block.distanceSq(pos);
                }
            }
        }
        return closestBlock;
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (Config.feature.dungeons.dungeonsCreeper && drawCreeperLines && !creeperLines.isEmpty()) {
            for (int i = 0; i < creeperLines.size(); i++) {
                Vec3[] line = creeperLines.get(i);
                if (line == null || line.length < 2 || line[0] == null || line[1] == null) {
                    continue;
                }
                Vec3 pos1 = line[0];
                Vec3 pos2 = line[1];
                int colour = CREEPER_COLOURS[i % CREEPER_COLOURS.length];

                drawFilled3DBox(
                        new AxisAlignedBB(pos1.xCoord - 0.51, pos1.yCoord - 0.51, pos1.zCoord - 0.51,
                                pos1.xCoord + 0.51, pos1.yCoord + 0.51, pos1.zCoord + 0.51),
                        colour, true, true, event.partialTicks
                );
                drawFilled3DBox(
                        new AxisAlignedBB(pos2.xCoord - 0.51, pos2.yCoord - 0.51, pos2.zCoord - 0.51,
                                pos2.xCoord + 0.51, pos2.yCoord + 0.51, pos2.zCoord + 0.51),
                        colour, true, true, event.partialTicks
                );
            }
        }
    }

    public static void drawFilled3DBox(AxisAlignedBB aabb, int colourInt, boolean translucent, boolean depth, float partialTicks) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        Color colour = new Color(colourInt);

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.tryBlendFuncSeparate(770, translucent ? 1 : 771, 1, 0);
        if (!depth) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GlStateManager.depthMask(false);
        }

        GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, 0.5f);

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        if (!depth) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
}
package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.puzzles;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

@RegisterEvents
public class BoulderSolver {

    public static BlockPos boulderChest = null;
    public static EnumFacing boulderFacing = null;
    public static BoulderState[][] grid = new BoulderState[7][6];
    public static int roomVariant = -1;
    public static ArrayList<ArrayList<BoulderPush>> variantSteps = new ArrayList<>();
    public static ArrayList<ArrayList<BoulderState>> expectedBoulders = new ArrayList<>();

    private static int ticks = 0;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public BoulderSolver() {
        // Variant 0
        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY));
        variantSteps.add(Lists.newArrayList(new BoulderPush(2, 4, Direction.RIGHT), new BoulderPush(2, 3, Direction.FORWARD), new BoulderPush(3, 3, Direction.RIGHT), new BoulderPush(4, 3, Direction.RIGHT), new BoulderPush(4, 1, Direction.FORWARD), new BoulderPush(5, 1, Direction.RIGHT)));

        // Variant 1
        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(3, 4, Direction.FORWARD), new BoulderPush(2, 4, Direction.LEFT), new BoulderPush(3, 3, Direction.RIGHT), new BoulderPush(3, 2, Direction.FORWARD), new BoulderPush(2, 2, Direction.LEFT), new BoulderPush(4, 2, Direction.RIGHT), new BoulderPush(2, 1, Direction.FORWARD), new BoulderPush(4, 1, Direction.FORWARD), new BoulderPush(3, 1, Direction.RIGHT)));

        // Variant 2
        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(1, 1, Direction.RIGHT)));

        // Variant 3
        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(4, 3, Direction.FORWARD), new BoulderPush(3, 3, Direction.LEFT), new BoulderPush(3, 1, Direction.FORWARD), new BoulderPush(2, 1, Direction.LEFT)));

        // Variant 4
        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(3, 4, Direction.FORWARD), new BoulderPush(3, 3, Direction.FORWARD), new BoulderPush(2, 1, Direction.FORWARD), new BoulderPush(1, 1, Direction.LEFT)));

        // Variant 5
        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY));
        variantSteps.add(Lists.newArrayList(new BoulderPush(1, 4, Direction.FORWARD), new BoulderPush(1, 1, Direction.RIGHT)));

        // Variant 6
        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(6, 4, Direction.FORWARD), new BoulderPush(6, 3, Direction.FORWARD), new BoulderPush(4, 1, Direction.FORWARD), new BoulderPush(5, 1, Direction.RIGHT)));

        // Variant 7
        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(0, 1, Direction.FORWARD)));

        // New Variant (Variant 8) with only 2 moves
        ArrayList<BoulderState> newVariantInitial = Lists.newArrayList(
                // row=0 (top, near chest)
                BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED,
                // row=1
                BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY,
                // row=2
                BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY,
                // row=3
                BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED,
                // row=4
                BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED,
                // row=5 (bottom)
                BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY
        );
        expectedBoulders.add(newVariantInitial);

        ArrayList<BoulderPush> newVariantSteps = Lists.newArrayList(
                new BoulderPush(1, 5, Direction.RIGHT),
                new BoulderPush(2, 1, Direction.RIGHT)
        );
        variantSteps.add(newVariantSteps);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        ticks++;
        if (ticks % 20 == 0) {
            ticks = 0;
            update();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsBoulderSolver) return;
        if (boulderChest == null) return;
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks;

        if (roomVariant >= 0) {
            ArrayList<BoulderPush> steps = variantSteps.get(roomVariant);
            for (BoulderPush step : steps) {
                if (grid[step.x][step.y] != BoulderState.EMPTY) {
                    EnumFacing downRow = boulderFacing.getOpposite();
                    EnumFacing rightColumn = boulderFacing.rotateY();
                    BlockPos farLeftPos = boulderChest.offset(downRow, 5).offset(rightColumn.getOpposite(), 9);

                    BlockPos boulderPos = farLeftPos.offset(rightColumn, 3 * step.x).offset(downRow, 3 * step.y);

                    EnumFacing actualDirection = null;

                    switch (step.direction) {
                        case FORWARD:
                            actualDirection = boulderFacing;
                            break;
                        case BACKWARD:
                            actualDirection = boulderFacing.getOpposite();
                            break;
                        case LEFT:
                            actualDirection = boulderFacing.rotateYCCW();
                            break;
                        case RIGHT:
                            actualDirection = boulderFacing.rotateY();
                            break;
                    }

                    BlockPos buttonPos = boulderPos.offset(actualDirection.getOpposite(), 2).down();
                    double x = buttonPos.getX() - viewerX;
                    double y = buttonPos.getY() - viewerY;
                    double z = buttonPos.getZ() - viewerZ;
                    GlStateManager.disableCull();
                    drawFilledBoundingBox(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), new Color(255, 0, 0), 0.6f);
                    GlStateManager.enableCull();
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.pos == boulderChest) {
            reset();
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        reset();
    }

    public static void update() {
        if (!Config.feature.dungeons.dungeonsBoulderSolver) return;
        EntityPlayerSP player = mc.thePlayer;
        World world = mc.theWorld;
        if (TablistParser.currentLocation.isDungeon() && world != null && player != null) {
            new Thread(() -> {
                int quartzBlocksFound = 0;
                int barriersFound = 0;
                BlockPos plusPlusQuartz = null;
                BlockPos minusMinusQuartz = null;
                Iterable<BlockPos> blocks = BlockPos.getAllInBox(
                        new BlockPos(player.posX - 25, 68, player.posZ - 25),
                        new BlockPos(player.posX + 25, 68, player.posZ + 25)
                );

                // Detect boulder room
                for (BlockPos blockPos : blocks) {
                    if (world.getBlockState(blockPos).getBlock() == Blocks.quartz_block) {
                        quartzBlocksFound++;
                        if (plusPlusQuartz == null ||
                                (blockPos.getX() >= plusPlusQuartz.getX() && blockPos.getZ() >= plusPlusQuartz.getZ())) {
                            plusPlusQuartz = blockPos;
                        }
                        if (minusMinusQuartz == null ||
                                (blockPos.getX() <= minusMinusQuartz.getX() && blockPos.getZ() <= minusMinusQuartz.getZ())) {
                            minusMinusQuartz = blockPos;
                        }
                        if (quartzBlocksFound == 8) break;
                    } else if (world.getBlockState(blockPos).getBlock() == Blocks.barrier) {
                        barriersFound++;
                    }
                }

                if (quartzBlocksFound == 8 && barriersFound >= 10) {
                    if (boulderChest == null || boulderFacing == null) {
                        // Detect rotation of room
                        BlockPos northChest = minusMinusQuartz.add(11, +1, 0);
                        BlockPos eastChest = plusPlusQuartz.add(0, +1, -11);
                        BlockPos southChest = plusPlusQuartz.add(-11, +1, 0);
                        BlockPos westChest = minusMinusQuartz.add(0, +1, 11);

                        if (world.getBlockState(northChest).getBlock() == Blocks.log) {
                            boulderFacing = EnumFacing.NORTH;
                            boulderChest = northChest.add(0, -3, -2);
                        } else if (world.getBlockState(eastChest).getBlock() == Blocks.log) {
                            boulderFacing = EnumFacing.EAST;
                            boulderChest = eastChest.add(2, -3, 0);
                        } else if (world.getBlockState(southChest).getBlock() == Blocks.log) {
                            boulderFacing = EnumFacing.SOUTH;
                            boulderChest = southChest.add(0, -3, 2);
                        } else if (world.getBlockState(westChest).getBlock() == Blocks.log) {
                            boulderFacing = EnumFacing.WEST;
                            boulderChest = westChest.add(-2, -3, 0);
                        } else {
                            mc.thePlayer.addChatMessage(new ChatComponentText(
                                    EnumChatFormatting.RED + "Could not determine orientation of boulder room."
                            ));
                            return;
                        }
                        System.out.println("Boulder chest is at " + boulderChest);
                        System.out.println("Boulder room is facing " + boulderFacing);
                    }

                    // Update grid state
                    EnumFacing downRow = boulderFacing.getOpposite();
                    EnumFacing rightColumn = boulderFacing.rotateY();
                    BlockPos farLeftPos = boulderChest.offset(downRow, 5).offset(rightColumn.getOpposite(), 9);
                    for (int row = 0; row < 6; row++) {
                        for (int column = 0; column < 7; column++) {
                            BlockPos current = farLeftPos.offset(rightColumn, 3 * column).offset(downRow, 3 * row);
                            IBlockState state = world.getBlockState(current);
                            grid[column][row] = state.getBlock() == Blocks.air ? BoulderState.EMPTY : BoulderState.FILLED;
                        }
                    }

                    // Detect variant
                    if (roomVariant == -1) {
                        roomVariant = -2;
                        for (int i = 0; i < expectedBoulders.size(); i++) {
                            ArrayList<BoulderState> expected = expectedBoulders.get(i);
                            boolean isRight = true;
                            for (int j = 0; j < expected.size(); j++) {
                                int column = j % 7;
                                int row = (int) Math.floor(j / 7);
                                BoulderState state = expected.get(j);
                                if (grid[column][row] != state && state != BoulderState.PLACEHOLDER) {
                                    isRight = false;
                                    break;
                                }
                            }
                            if (isRight) {
                                roomVariant = i;
                                mc.thePlayer.addChatMessage(new ChatComponentText(
                                        EnumChatFormatting.GREEN + "NEF detected boulder variant " + (roomVariant + 1) + "."
                                ));
                                break;
                            }
                        }
                        if (roomVariant == -2) {
                            mc.thePlayer.addChatMessage(new ChatComponentText(
                                    EnumChatFormatting.RED + "NEF couldn't detect the boulder variant."
                            ));
                        }
                    }
                }
            }).start();
        }
    }

    public static void reset() {
        boulderChest = null;
        boulderFacing = null;
        grid = new BoulderState[7][6];
        roomVariant = -1;
    }

    public static class BoulderPush {
        int x, y;
        Direction direction;

        public BoulderPush(int x, int y, Direction direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    public enum BoulderState {
        EMPTY,
        FILLED,
        PLACEHOLDER
    }

    public static void drawFilledBoundingBox(AxisAlignedBB aabb, Color c, float alphaMultiplier) {
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f * alphaMultiplier);

        // Vertical
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        tessellator.draw();

        GlStateManager.color(c.getRed() / 255f * 0.8f, c.getGreen() / 255f * 0.8f, c.getBlue() / 255f * 0.8f, c.getAlpha() / 255f * alphaMultiplier);

        // X-axis
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();

        GlStateManager.color(c.getRed() / 255f * 0.9f, c.getGreen() / 255f * 0.9f, c.getBlue() / 255f * 0.9f, c.getAlpha() / 255f * alphaMultiplier);

        // Z-axis
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
    }
}
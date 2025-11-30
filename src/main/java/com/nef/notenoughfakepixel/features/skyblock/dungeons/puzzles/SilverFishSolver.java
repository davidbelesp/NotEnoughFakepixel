package com.nef.notenoughfakepixel.features.skyblock.dungeons.puzzles;

import com.google.common.collect.Lists;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RegisterEvents
public class SilverFishSolver {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final List<Point> steps = new ArrayList<>();
    private static BlockPos silverfishChestPos;
    private static EnumFacing roomFacing;
    private static int[][] grid = null;
    private static EntitySilverfish silverfish = null;
    private static Point silverfishPos = null;
    private int ticks = 0;
    private boolean prevInSilverfishRoom = false;
    private boolean inSilverfishRoom = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !SkyblockData.getCurrentLocation().isDungeon() || mc.thePlayer == null || mc.theWorld == null)
            return;

        if (!Config.feature.dungeons.dungeonsSilverfishSolver) return;

        List<EntitySilverfish> silverfishes = mc.theWorld.getEntities(EntitySilverfish.class, s -> mc.thePlayer.getDistanceToEntity(s) < 20);
        if (!silverfishes.isEmpty()) {
            SilverFishSolver.silverfish = silverfishes.get(0);
            if (silverfishChestPos == null || roomFacing == null) {
                if (ticks % 20 == 0) {
                    new Thread(() -> {
                        prevInSilverfishRoom = inSilverfishRoom;
                        double x = mc.thePlayer.posX;
                        double z = mc.thePlayer.posZ;
                        AxisAlignedBB entityScan = new AxisAlignedBB(x - 25, 67, z - 25, x + 25, 68, z + 25);
                        List<EntitySilverfish> silverfishList = mc.theWorld.getEntitiesWithinAABB(EntitySilverfish.class, entityScan);
                        List<EntityItem> items = mc.theWorld.getEntitiesWithinAABB(EntityItem.class, entityScan);
                        if (!silverfishList.isEmpty() && !items.isEmpty()) {
                            double silverfishX = silverfishList.get(0).posX;
                            double silverfishZ = silverfishList.get(0).posZ;
                            for (EntityItem item : items) {
                                if (Item.getIdFromItem(item.getEntityItem().getItem()) == 46 &&
                                        Math.abs(item.posX - silverfishX) < 1 &&
                                        Math.abs(item.posZ - silverfishZ) < 1) {
                                    Iterable<BlockPos> blocks = BlockPos.getAllInBox(
                                            new BlockPos(mc.thePlayer.posX - 27, 66, mc.thePlayer.posZ - 27),
                                            new BlockPos(mc.thePlayer.posX + 27, 66, mc.thePlayer.posZ + 27)
                                    );
                                    for (BlockPos blockPos : blocks) {
                                        if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.packed_ice &&
                                                mc.theWorld.getBlockState(blockPos.add(0, 3, 0)).getBlock() == Blocks.hopper) {
                                            inSilverfishRoom = true;
                                            if (!prevInSilverfishRoom) {
                                                silverfishChestPos = blockPos.add(0, 1, 0);
                                                TileEntity hopper = mc.theWorld.getTileEntity(blockPos.add(0, 3, 0));
                                                roomFacing = BlockHopper.getFacing(hopper.getBlockMetadata());
                                                System.out.printf("Silverfish chest is at %s and is facing %s%n", silverfishChestPos, roomFacing);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }, "Ice-Path-Detection").start();
                    ticks = 0;
                }
            } else if (grid == null) {
                grid = getLayout();
                silverfishPos = getGridPointFromPos(SilverFishSolver.silverfish.getPosition());
                steps.clear();
                if (silverfishPos != null) {
                    steps.addAll(solve(grid, silverfishPos.x, silverfishPos.y, 9, 0));
                }
            }
        }

        if (SilverFishSolver.silverfish != null && grid != null) {
            Point silverfishGridPos = getGridPointFromPos(SilverFishSolver.silverfish.getPosition());
            if (SilverFishSolver.silverfish.isEntityAlive() && !Objects.equals(silverfishGridPos, silverfishPos)) {
                silverfishPos = silverfishGridPos;
                if (silverfishPos != null) {
                    steps.clear();
                    steps.addAll(solve(grid, silverfishPos.x, silverfishPos.y, 9, 0));
                }
            }
        }

        ticks++;
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsSilverfishSolver) return;

        if (silverfishChestPos != null && roomFacing != null && grid != null && SilverFishSolver.silverfish.isEntityAlive()) {
            for (int i = 0; i < steps.size() - 1; i++) {
                Point point = steps.get(i);
                Point point2 = steps.get(i + 1);
                Vec3 pos = getVec3RelativeToGrid(point.x, point.y);
                Vec3 pos2 = getVec3RelativeToGrid(point2.x, point2.y);
                GlStateManager.disableCull();
                assert pos != null;
                assert pos2 != null;
                draw3DLine(pos.addVector(0.5, 0.5, 0.5), pos2.addVector(0.5, 0.5, 0.5), 5, new Color(255, 0, 0), event.partialTicks);
                GlStateManager.enableCull();
            }
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        silverfishChestPos = null;
        roomFacing = null;
        grid = null;
        steps.clear();
        silverfish = null;
        silverfishPos = null;
    }

    public static void draw3DLine(Vec3 pos1, Vec3 pos2, int width, Color color, float partialTicks) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(width);
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

        worldRenderer.pos(pos1.xCoord, pos1.yCoord, pos1.zCoord).endVertex();
        worldRenderer.pos(pos2.xCoord, pos2.yCoord, pos2.zCoord).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static Iterable<BlockPos> getBlocksWithinRangeAtSameY(BlockPos center, int radius, int y) {
        BlockPos corner1 = new BlockPos(center.getX() - radius, y, center.getZ() - radius);
        BlockPos corner2 = new BlockPos(center.getX() + radius, y, center.getZ() + radius);
        return BlockPos.getAllInBox(corner1, corner2);
    }

    private Vec3 getVec3RelativeToGrid(int column, int row) {
        if (silverfishChestPos == null || roomFacing == null) return null;

        return new Vec3(silverfishChestPos
                .offset(roomFacing.getOpposite(), 4)
                .offset(roomFacing.rotateYCCW(), 8)
                .offset(roomFacing.rotateY(), column)
                .offset(roomFacing.getOpposite(), row));
    }

    private Point getGridPointFromPos(BlockPos pos) {
        if (silverfishChestPos == null || roomFacing == null) return null;
        for (int row = 0; row < 17; row++) {
            for (int column = 0; column < 17; column++) {
                Vec3 vec = getVec3RelativeToGrid(column, row);
                if (vec == null) continue;
                if (new BlockPos(vec).equals(pos)) {
                    return new Point(column, row);
                }
            }
        }
        return null;
    }

    private int[][] getLayout() {
        if (silverfishChestPos == null || roomFacing == null) return null;
        int[][] grid = new int[17][17];
        for (int row = 0; row < 17; row++) {
            for (int column = 0; column < 17; column++) {
                Vec3 vec = getVec3RelativeToGrid(column, row);
                if (vec == null) continue;
                grid[row][column] = mc.theWorld.getBlockState(new BlockPos(vec)).getBlock() != Blocks.air ? 1 : 0;
            }
            if (row == 16) return grid;
        }
        return null;
    }

    private ArrayList<Point> solve(int[][] iceCave, int startX, int startY, int endX, int endY) {
        Point startPoint = new Point(startX, startY);

        LinkedList<Point> queue = new LinkedList<>();
        Point[][] iceCaveColors = new Point[iceCave.length][iceCave[0].length];

        queue.addLast(new Point(startX, startY));
        iceCaveColors[startY][startX] = startPoint;

        while (!queue.isEmpty()) {
            Point currPos = queue.pollFirst();
            // traverse adjacent nodes while sliding on the ice
            for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                Point nextPos = move(iceCave, iceCaveColors, currPos, dir);
                if (nextPos != null) {
                    queue.addLast(nextPos);
                    iceCaveColors[(int) nextPos.getY()][(int) nextPos.getX()] = new Point((int) currPos.getX(), (int) currPos.getY());
                    if (nextPos.getY() == endY && nextPos.getX() == endX) {
                        ArrayList<Point> steps = new ArrayList<>();
                        // we found the end point
                        Point tmp = currPos;  // if we start from nextPos we will count one too many edges
                        int count = 0;
                        steps.add(nextPos);
                        steps.add(currPos);
                        while (tmp != startPoint) {
                            count++;
                            tmp = iceCaveColors[(int) tmp.getY()][(int) tmp.getX()];
                            steps.add(tmp);
                        }
                        //System.out.println("Silverfish solved in " + count + " moves.");
                        return steps;
                    }
                }
            }
        }
        return Lists.newArrayList();
    }


    private Point move(int[][] iceCave, Point[][] iceCaveColors, Point currPos, EnumFacing dir) {
        int x = (int) currPos.getX();
        int y = (int) currPos.getY();

        int diffX = dir.getDirectionVec().getX();
        int diffY = dir.getDirectionVec().getZ();

        int i = 1;
        while (x + i * diffX >= 0
                && x + i * diffX < iceCave[0].length
                && y + i * diffY >= 0
                && y + i * diffY < iceCave.length
                && iceCave[y + i * diffY][x + i * diffX] != 1) {
            i++;
        }

        i--;  // reverse the last step

        if (iceCaveColors[y + i * diffY][x + i * diffX] != null) {
            // we've already seen this point
            return null;
        }

        return new Point(x + i * diffX, y + i * diffY);
    }
}


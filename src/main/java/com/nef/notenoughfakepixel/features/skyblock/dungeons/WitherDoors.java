package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

@RegisterEvents
public class WitherDoors {
    public enum ROOMSIZE {
        ONE_ONE("1x1"),
        ONE_TWO("2x1"),
        ONE_THREE("3x1"),
        ONE_FOUR("4x1"),
        TWO_TWO("2x2"),
        L_SHAPE("L"),
        NONE("");

        public final String name;
        ROOMSIZE(String s) {
            name = s;
        }

        public static ROOMSIZE get(String name) {
            for (ROOMSIZE size : values()) {
                if (size.name.equals(name)) return size;
            }
            return NONE;
        }
    }

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Set<BlockPos> activeDoors = new HashSet<>();
    private final Map<String, List<BlockPos>> roomDoors = new HashMap<>();
    private final List<BlockPos> persistentDoors = new ArrayList<>();
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 1000;
    private int yLvl = -1;
    private int left = 0, right = 0, north = 0, south = 0;
    private int width = 0, height = 0;
    private String currentRoomId = "";
    private final List<BlockPos> foundCoalBlocks = new ArrayList<>();
    private List<BlockPos> currentDoors = new ArrayList<>();
    private ROOMSIZE size = ROOMSIZE.NONE;
    private boolean doorOpenedInEmptyRoom = false;

    private void checkYLVL() {
        EntityPlayerSP p = mc.thePlayer;
        if (p == null) return;
        World w = p.worldObj;
        if (w == null) return;
        for (int i = 200; i > 65; i--) {
            BlockPos pos = new BlockPos(p.posX, i, p.posZ);
            Block b = w.getBlockState(pos).getBlock();
            if (b != Blocks.air) {
                yLvl = i;
                break;
            }
        }
    }

    private void detectRoomBoundaries() {
        if (yLvl == -1) return;
        EntityPlayerSP p = mc.thePlayer;
        if (p == null || p.worldObj == null) return;
        World w = p.worldObj;

        BlockPos pos = new BlockPos(p.posX, yLvl, p.posZ);
        if (w.getBlockState(pos).getBlock() == Blocks.air) return;

        for (int x = 0; x < 136; x++) {
            BlockPos check = new BlockPos(p.posX - x, yLvl, p.posZ);
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                left = check.getX();
                break;
            }
        }

        for (int x = 0; x < 136; x++) {
            BlockPos check = new BlockPos(p.posX + x, yLvl, p.posZ);
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                right = check.getX();
                break;
            }
        }

        for (int z = 0; z < 136; z++) {
            BlockPos check = new BlockPos(p.posX, yLvl, p.posZ - z);
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                north = check.getZ();
                break;
            }
        }

        for (int z = 0; z < 136; z++) {
            BlockPos check = new BlockPos(p.posX, yLvl, p.posZ + z);
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                south = check.getZ();
                break;
            }
        }

        width = right - left;
        height = south - north;
        currentRoomId = left + ":" + right + ":" + north + ":" + south;

        String name = width / 32 + "x" + height / 32;
        if (height > width) {
            name = height / 32 + "x" + width / 32;
        }

        size = isLShaped(w, p) ? ROOMSIZE.L_SHAPE : ROOMSIZE.get(name);
    }

    private boolean isLShaped(World w, EntityPlayerSP sp) {
        if (width <= 32 && height <= 32) return false;
        if (width == height) {
            BlockPos center = new BlockPos(left + width / 2, yLvl, north + height / 2);
            return w.getBlockState(center).getBlock() == Blocks.air;
        }
        if (width > 64 || height > 64) return false;

        int centerX = left + width / 2;
        int centerZ = north + height / 2;

        if (width > height) {
            boolean closerToLeft = Math.abs(sp.posX - left) <= Math.abs(sp.posX - right);
            BlockPos pos = closerToLeft ? new BlockPos(right - 16, yLvl, centerZ) : new BlockPos(left + 16, yLvl, centerZ);
            ROOMSIZE rsize = getRoomSize(pos, w);
            return rsize == ROOMSIZE.TWO_TWO;
        } else {
            boolean closerToNorth = Math.abs(sp.posZ - north) <= Math.abs(sp.posZ - south);
            BlockPos pos = closerToNorth ? new BlockPos(centerX, yLvl, south - 16) : new BlockPos(centerX, yLvl, north + 16);
            ROOMSIZE rsize = getRoomSize(pos, w);
            return rsize == ROOMSIZE.TWO_TWO;
        }
    }

    private ROOMSIZE getRoomSize(BlockPos pos, World w) {
        int l = 0, r = 0, u = 0, d = 0;
        for (int x = 0; x < 128; x++) {
            BlockPos check = new BlockPos(pos.getX() - x, yLvl, pos.getZ());
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                l = check.getX();
                break;
            }
        }
        for (int x = 0; x < 128; x++) {
            BlockPos check = new BlockPos(pos.getX() + x, yLvl, pos.getZ());
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                r = check.getX();
                break;
            }
        }
        for (int z = 0; z < 128; z++) {
            BlockPos check = new BlockPos(pos.getX(), yLvl, pos.getZ() - z);
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                u = check.getZ();
                break;
            }
        }
        for (int z = 0; z < 128; z++) {
            BlockPos check = new BlockPos(pos.getX(), yLvl, pos.getZ() + z);
            if (w.getBlockState(check).getBlock() == Blocks.air) {
                d = check.getZ();
                break;
            }
        }
        int wi = r - l;
        int h = d - u;
        String name = wi / 32 + "x" + h / 32;
        if (h > wi) {
            name = h / 32 + "x" + wi / 32;
        }
        return ROOMSIZE.get(name);
    }

    private List<BlockPos> findWitherDoors() {
        Set<BlockPos> doorBases = new HashSet<>();
        foundCoalBlocks.clear();
        if (yLvl == -1 || !SkyblockData.getCurrentLocation().isDungeon()) return new ArrayList<>();

        World w = mc.theWorld;
        if (w == null) return new ArrayList<>();

        for (int x = left + 1; x < right; x++) {
            for (int z = north + 1; z < south; z++) {
                BlockPos pos = new BlockPos(x, 69, z);
                IBlockState state = w.getBlockState(pos);
                Block block = state.getBlock();
                if (block == Blocks.coal_block ||
                        (block == Blocks.stained_hardened_clay &&
                                state.getValue(BlockColored.COLOR) == EnumDyeColor.RED)) {
                    foundCoalBlocks.add(pos);
                    // Check standard orientation (positive x, positive z)
                    if (isWitherDoorStructure(w, pos)) {
                        doorBases.add(pos);
                    }
                    // Check west-facing orientation (negative x)
                    BlockPos westBase = pos.add(-2, 0, 0);
                    if (isWitherDoorStructure(w, westBase)) {
                        doorBases.add(westBase);
                    }
                    // Check south-facing orientation (negative z)
                    BlockPos southBase = pos.add(0, 0, -2);
                    if (isWitherDoorStructure(w, southBase)) {
                        doorBases.add(southBase);
                    }
                }
            }
        }
        return new ArrayList<>(doorBases);
    }

    private boolean isWitherDoorStructure(World w, BlockPos base) {
        for (int dx = 0; dx < 3; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                for (int dz = 0; dz < 3; dz++) {
                    BlockPos checkPos = base.add(dx, dy, dz);
                    IBlockState state = w.getBlockState(checkPos);
                    Block block = state.getBlock();
                    if (block != Blocks.coal_block &&
                            !(block == Blocks.stained_hardened_clay &&
                                    state.getValue(BlockColored.COLOR) == EnumDyeColor.RED)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Config.feature.dungeons.dungeonsWitherDoors) return;
        if (event.phase != TickEvent.Phase.END) return;

        if (System.currentTimeMillis() - lastUpdateTime > UPDATE_INTERVAL) {
            if (SkyblockData.getCurrentLocation().isDungeon()) {
                checkYLVL();
                detectRoomBoundaries();
                currentDoors = findWitherDoors();
                roomDoors.put(currentRoomId, currentDoors);
                for (BlockPos door : currentDoors) {
                    if (!persistentDoors.contains(door)) {
                        persistentDoors.add(door);
                        if (doorOpenedInEmptyRoom && persistentDoors.size() == 1) {
                            activeDoors.add(door);
                            doorOpenedInEmptyRoom = false;
                        }
                    }
                }
            }
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!Config.feature.dungeons.dungeonsWitherDoors) return;
        activeDoors.clear();
        roomDoors.clear();
        foundCoalBlocks.clear();
        persistentDoors.clear();
        doorOpenedInEmptyRoom = false;
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (!Config.feature.dungeons.dungeonsWitherDoors) return;
        if (!SkyblockData.getCurrentLocation().isDungeon()) return;

        String msg = event.message.getUnformattedText();
        if (msg.startsWith("RIGHT CLICK on the WITHER DOOR to open it") ||
                msg.startsWith("RIGHT CLICK on the BLOOD DOOR to open it")) {
            if (persistentDoors.isEmpty()) {
                doorOpenedInEmptyRoom = true;
            } else {
                activeDoors.addAll(persistentDoors);
            }
        } else if (msg.endsWith("opened a WITHER door!") ||
                msg.endsWith("The BLOOD DOOR has been opened!")) {
            activeDoors.clear();
            persistentDoors.clear();
            foundCoalBlocks.clear();
            doorOpenedInEmptyRoom = false;
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsWitherDoors) return;
        if (!SkyblockData.getCurrentLocation().isDungeon()) return;

        if (persistentDoors.isEmpty()) return;

        double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
        double playerY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
        double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(2.0F);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos door : persistentDoors) {
            boolean isActive = activeDoors.contains(door);
            Color color = isActive ?
                    ColorUtils.getColor(Config.feature.dungeons.dungeonsWitherDoorsActive) :
                    ColorUtils.getColor(Config.feature.dungeons.dungeonsWitherDoorsInactive);
            renderDoor(worldRenderer, door, color, playerX, playerY, playerZ);
        }

        tessellator.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private void renderDoor(WorldRenderer worldRenderer, BlockPos door, Color color,
                            double playerX, double playerY, double playerZ) {
        double minX = door.getX() - playerX;
        double minY = door.getY() - playerY;
        double minZ = door.getZ() - playerZ;
        double maxX = minX + 3;
        double maxY = minY + 4;
        double maxZ = minZ + 3;

        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        float a = color.getAlpha() / 255.0f;

        worldRenderer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();

        worldRenderer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();

        worldRenderer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
    }
}
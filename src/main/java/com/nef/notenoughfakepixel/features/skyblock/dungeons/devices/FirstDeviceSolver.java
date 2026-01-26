package com.nef.notenoughfakepixel.features.skyblock.dungeons.devices;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.PacketWriteEvent;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockButtonStone;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Objects;

@RegisterEvents
public class FirstDeviceSolver {

    private BlockPos[] positionsToSolve;
    private int[] positionsIndexSolved = new int[]{-1, -1, -1, -1, -1};
    private boolean startMemorising = false;
    private boolean resolving = false;
    private int positionInRound = 0;
    private int round = 1;

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsFirstDeviceSolver) return;

        // Check for sea lanterns
        if (startMemorising) {
            if (positionsToSolve == null) return;
            for (int i = 0; i < positionsToSolve.length; i++) {
                Block targetBlock = Minecraft.getMinecraft().theWorld.getBlockState(positionsToSolve[i]).getBlock();
                if (targetBlock == Blocks.sea_lantern && positionsIndexSolved[round - 1] == -1 && !checkIfAdded(positionsIndexSolved, i)) {
                    positionsIndexSolved[round - 1] = i;
                    resolving = true;
                    startMemorising = false;
                }
            }
        }
        // Show highlighted button and next buttons
        if (resolving) {
            for (int i = 0; i < round; i++) {
                if (positionsIndexSolved[i] == -1) {
                    reset();
                    return;
                }
                Color baseColor = ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor);
                if (i == positionInRound) {
                    RenderUtils.highlightBlock(positionsToSolve[positionsIndexSolved[i]], ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor), false, true, event.partialTicks);
                } else if (i == positionInRound + 1) {
                    RenderUtils.highlightBlock(positionsToSolve[positionsIndexSolved[i]], ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor), false, true, event.partialTicks);
                } else if (i == positionInRound + 2) {
                    RenderUtils.highlightBlock(positionsToSolve[positionsIndexSolved[i]], new Color(
                            baseColor.getRed(),
                            baseColor.getGreen(),
                            baseColor.getBlue(),
                            150
                    ), false, true, event.partialTicks);
                }
            }
        }
    }

    // Check for initial button interact
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (!Config.feature.dungeons.dungeonsFirstDeviceSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (Minecraft.getMinecraft().thePlayer != event.entityPlayer) return;
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            //lastTimeClicked = System.currentTimeMillis(); // Update last clicked time
            Block buttonBlock = Minecraft.getMinecraft().theWorld.getBlockState(event.pos).getBlock();
            if (buttonBlock instanceof BlockButtonStone) {
                EnumFacing enumfacing = Minecraft.getMinecraft().theWorld.getBlockState(event.pos).getValue(BlockButton.FACING);
                Block blockClicked = Minecraft.getMinecraft().theWorld.getBlockState(getBlockUnderButton(event.pos, enumfacing)).getBlock();
                // Check if button is pressed over an obsidian block
                if (Objects.equals(blockClicked.getUnlocalizedName(), Blocks.obsidian.getUnlocalizedName())) {
                    if (!resolving) return;
                    if (!Objects.equals(getBlockUnderButton(event.pos, enumfacing), positionsToSolve[positionsIndexSolved[positionInRound]])) {
                        event.setCanceled(true);
                        return;
                    }
                    positionInRound++;
                    if (positionInRound == round) {
                        positionInRound = 0;
                        round++;
                        startMemorising = true;
                        resolving = false;
                    }
                    if (round == 6) {
                        reset();
                    }
                    // Else if over an emerald block
                } else if (Objects.equals(blockClicked.getUnlocalizedName(), Blocks.emerald_block.getUnlocalizedName())) {//&& !startMemorising) {
                    BlockPos pos = getBlockUnderButton(event.pos, enumfacing);
                    positionsToSolve = getSurroundingBlocks(pos, enumfacing);
                    reset();
                    startMemorising = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onDiggingPacket(PacketWriteEvent event) {
        if (!Config.feature.dungeons.dungeonsFirstDeviceSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        Packet packet = event.packet;
        if (packet instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packetPlayerDigging = (C07PacketPlayerDigging) packet;
            if (packetPlayerDigging.getStatus() != C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) return;

            Block buttonBlock = Minecraft.getMinecraft().theWorld.getBlockState(((C07PacketPlayerDigging) packet).getPosition()).getBlock();
            if (buttonBlock instanceof BlockButtonStone) {
                EnumFacing enumfacing = Minecraft.getMinecraft().theWorld.getBlockState(((C07PacketPlayerDigging) packet).getPosition()).getValue(BlockButton.FACING);
                Block blockClicked = Minecraft.getMinecraft().theWorld.getBlockState(getBlockUnderButton(((C07PacketPlayerDigging) packet).getPosition(), enumfacing)).getBlock();

                // Check if button is pressed over an obsidian block
                if (Objects.equals(blockClicked.getUnlocalizedName(), Blocks.obsidian.getUnlocalizedName())) {
                    if (!resolving) return;
                    if (!Objects.equals(getBlockUnderButton(((C07PacketPlayerDigging) packet).getPosition(), enumfacing), positionsToSolve[positionsIndexSolved[positionInRound]])) {
                        // TODO TO TEST
                        event.setCanceled(true);
                        //reset();
                        return;
                    }
                    positionInRound++;
                    if (positionInRound == round) {
                        positionInRound = 0;
                        round++;
                        startMemorising = true;
                        resolving = false;
                    }
                    if (round == 6) {
                        reset();
                    }
                    // Else if over an emerald block
                } else if (Objects.equals(blockClicked.getUnlocalizedName(), Blocks.emerald_block.getUnlocalizedName()) && !startMemorising) {
                    BlockPos pos = getBlockUnderButton(((C07PacketPlayerDigging) packet).getPosition(), enumfacing);
                    positionsToSolve = getSurroundingBlocks(pos, enumfacing);
                    reset();
                    startMemorising = true;
                }
            }
        }
    }

    @SubscribeEvent()
    public void onWorldUnload(WorldEvent.Unload event) {
        if (Config.feature.dungeons.dungeonsFirstDeviceSolver) reset();
    }

    private static BlockPos getBlockUnderButton(BlockPos pos, EnumFacing facing) {
        //System.out.println(facing);
        switch (facing) {
            case NORTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
            case EAST:
                return new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
            case SOUTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
            case WEST:
                return new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
            default:
                return null;
        }
    }

    public static BlockPos[] getSurroundingBlocks(BlockPos startPos, EnumFacing enumFacing) {
        BlockPos[] surroundingBlocks = new BlockPos[16];

        int index = 0;
        for (int yOffset = -1; yOffset <= 2; yOffset++) { // Y goes from P.y - 1 to P.y + 2
            for (int hOffset = 1; hOffset <= 4; hOffset++) { // Moves horizontally
                int dx = 0, dz = 0, dy = yOffset;

                switch (enumFacing) {
                    case NORTH: // Moves along X, Z is constant
                        dx = -hOffset;
                        break;
                    case SOUTH: // Moves along X, Z is constant
                        dx = hOffset;
                        break;
                    case WEST: // Moves along Z, X is constant
                        dz = hOffset;
                        break;
                    case EAST: // Moves along Z, X is constant
                        dz = -hOffset;
                        break;
                }

                surroundingBlocks[index++] = new BlockPos(startPos.getX() + dx, startPos.getY() + dy, startPos.getZ() + dz);
            }
        }

        return surroundingBlocks;
    }

    private boolean checkIfAdded(int[] array, int index) {
        for (int j : array) {
            if (j == index) return true;
        }
        return false;
    }

    private void reset() {
        positionsIndexSolved = new int[]{-1, -1, -1, -1, -1};
        startMemorising = false;
        resolving = false;
        positionInRound = 0;
        round = 1;
    }
}

package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.puzzles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;

import java.awt.*;
import java.util.*;
import java.util.List;


public class WaterSolver {

    private Minecraft mc = Minecraft.getMinecraft();
    private EntityPlayerSP player = mc.thePlayer;
    private World world = mc.theWorld;

    private static final List<EnumDyeColor> WOOL_ORDER = Arrays.asList(
            EnumDyeColor.LIME, EnumDyeColor.BLUE, EnumDyeColor.RED, EnumDyeColor.PURPLE, EnumDyeColor.ORANGE
    );
    private boolean inWaterRoom = false;
    private int tickCounter = 0;

    private Set<EnumDyeColor> woolBlocks = new LinkedHashSet<>();
    private Map<String, BlockPos> leversPositions = new HashMap<>();

    private ArrayList<boolean[]> correctLevers = new ArrayList<>();
    private BlockPos waterLeverPos;
    private boolean waterLeverPowered = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Configuration.dungeonsWaterSolver) return;
        if (!DungeonManager.checkEssentials()) return;
        if (event.phase != TickEvent.Phase.START) return;

        tickCounter++;
        if (tickCounter % 20 != 0) return;

        mc = Minecraft.getMinecraft();
        player = mc.thePlayer;
        world = mc.theWorld;
        if (player == null || world == null) return;

        if (waterLeverPos != null && world.getBlockState(waterLeverPos).getBlock() == Blocks.lever) waterLeverPowered = world.getBlockState(waterLeverPos).getValue(BlockLever.POWERED);
        new Thread(this::detectWaterRoom).start();
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!Configuration.dungeonsWaterSolver) return;
        if (!DungeonManager.checkEssentials()) return;
        if (!inWaterRoom || woolBlocks == null || woolBlocks.isEmpty()) return;
        if (!woolBlocks.iterator().hasNext()) return;

        List<EnumDyeColor> woolList = new ArrayList<>(woolBlocks);
        for (int i = 0; i < woolList.size(); i++) {
            int[] colorSolutions = WOOL_SOLUTIONS.getArraySolutions(woolList.get(i).getName());
            drawFeatures(colorSolutions, i, event.partialTicks);
        }
    }

    private void drawFeatures (int[] colorSolutions, float offsetY, float partialTicks) {
        if (correctLevers == null || correctLevers.isEmpty()) return;
        int solutionNumber = (int)(offsetY);
        List<BlockPos> boundingBoxPositions = new ArrayList<>();
        for (int i = 0; i < colorSolutions.length; i++) {
            int solution = colorSolutions[i];
            correctLevers.get(solutionNumber)[i] = true;
            BlockPos position = null;
            switch (i) {
                case 0: position = leversPositions.get("minecraft:quartz_block"); break;
                case 1: position = leversPositions.get("minecraft:diamond_block"); break;
                case 2: position = leversPositions.get("minecraft:gold_block"); break;
                case 3: position = leversPositions.get("minecraft:emerald_block"); break;
                case 4: position = leversPositions.get("minecraft:coal_block"); break;
                case 5: position = leversPositions.get("minecraft:hardened_clay"); break;
            }
            if (position == null) continue;
            if (world.getBlockState(position).getBlock() != Blocks.lever) continue;

            boolean isLeverActive = world.getBlockState(position).getValue(BlockLever.POWERED);
            if (!(solution == -1 || (solution == 0 && !isLeverActive) || (solution == 1 && isLeverActive))) {
                if (offsetY == 0) RenderUtils.draw3DLine(
                        new Vec3(position.getX(), position.getY(), position.getZ()),
                        player.getPositionEyes(partialTicks),
                        Color.green,
                        4,
                        true,
                        partialTicks,
                        false,
                        true,
                        world.getBlockState(position).getValue(BlockLever.FACING)
                );
                correctLevers.get(solutionNumber)[i] = false;
                boundingBoxPositions.add(position);
            }
        }

        // Draw final water lever tracer
        if (offsetY == 0 && allTrue(correctLevers.get(solutionNumber)) && waterLeverPos != null) {
            if (world.getBlockState(waterLeverPos).getBlock() != null && world.getBlockState(waterLeverPos).getBlock() == Blocks.lever) {
                RenderUtils.draw3DLine(
                        new Vec3(waterLeverPos.getX(), waterLeverPos.getY(), waterLeverPos.getZ()),
                        player.getPositionEyes(partialTicks),
                        Color.green,
                        4,
                        true,
                        partialTicks,
                        false,
                        true,
                        world.getBlockState(waterLeverPos).getValue(BlockLever.FACING)
                );
            } else {
                Logger.log("Crash avoided: waterLeverPos is null");
            }
        }

        Color color = Color.green;
        if (offsetY != 0) color = Color.yellow;
        // Now draw all bounding boxes AFTER all 3D lines
        for (BlockPos position : boundingBoxPositions) {
            if (world.getBlockState(position).getBlock() != Blocks.lever) continue;
            RenderUtils.drawLeverBoundingBox(
                    new BlockPos(position.getX(), position.getY()+offsetY, position.getZ()),
                    world.getBlockState(position).getValue(BlockLever.FACING).getFacing(),
                    color,
                    partialTicks
            );
        }
    }

    @SubscribeEvent()
    public void onWorldUnload(WorldEvent.Load event) {
        if (!Configuration.dungeonsWaterSolver) return;
        if (!DungeonManager.checkEssentials()) return;
        waterLeverPos = null;
        //correctLevers = new boolean[]{true,true,true,true,true,true};
        correctLevers = new ArrayList<>();
    }


    private void detectWaterRoom() {
        if (checkForBlock(Blocks.sticky_piston, 20, 57) == null) {
            woolBlocks = null;
            correctLevers = new ArrayList<>();
            return;
        }
        BlockPos foundPistonHead = checkForBlock(Blocks.piston_head, 18, 57);
        if (foundPistonHead != null) {
            //System.out.println("Found piston head");
            inWaterRoom = true;
            woolBlocks = detectWoolBlocks(foundPistonHead);
            for (int i = 0; i < woolBlocks.size(); i++) {
                correctLevers.add(new boolean[]{true,true,true,true,true,true});
            }
            leversPositions = detectLeverPositions(new BlockPos(foundPistonHead.getX(), foundPistonHead.getY()+4, foundPistonHead.getZ()));
        }
    }

    private Set<EnumDyeColor> detectWoolBlocks(BlockPos posOrigin) {
        Set<EnumDyeColor> woolBlocks = new LinkedHashSet<>();
        BlockPos scan1 = new BlockPos(posOrigin.getX() + 5, posOrigin.getY(), posOrigin.getZ() + 5);
        BlockPos scan2 = new BlockPos(posOrigin.getX() - 5, posOrigin.getY(), posOrigin.getZ() - 5);

        for (BlockPos pos : BlockPos.getAllInBox(scan1, scan2)) {
            IBlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block == Blocks.piston_head) {
                EnumFacing enumfacing = blockState.getValue(BlockPistonExtension.FACING);
                BlockPos woolPos = getBlockPistonHead(pos, enumfacing);
                woolBlocks.add(world.getBlockState(woolPos).getValue(BlockColored.COLOR));
            }
        }
        return sortWoolBlocks(woolBlocks);
    }

    private Set<EnumDyeColor> sortWoolBlocks(Set<EnumDyeColor> woolBlocks) {
        Set<EnumDyeColor> sortedSet = new LinkedHashSet<>();
        for (EnumDyeColor color : WOOL_ORDER) {
            if (woolBlocks.contains(color)) {
                sortedSet.add(color);
            }
        }
        return sortedSet;
    }

    private Map<String, BlockPos> detectLeverPositions(BlockPos posOrigin) {
        Map<String, BlockPos> leversPositions = new HashMap<>();

        BlockPos scan1 = new BlockPos(posOrigin.getX() + 16, posOrigin.getY(), posOrigin.getZ() + 16);
        BlockPos scan2 = new BlockPos(posOrigin.getX() - 16, posOrigin.getY()-1, posOrigin.getZ() - 16);

        for (BlockPos pos : BlockPos.getAllInBox(scan1, scan2)) {
            IBlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (block == Blocks.lever) {
                EnumFacing facing = blockState.getValue(BlockLever.FACING).getFacing();
                BlockPos behindLever = getBlockBehindLever(pos, facing);
                if (behindLever == null) continue;
                // Get the block behind the lever
                Block behindBlock = world.getBlockState(behindLever).getBlock();
                String blockName = Block.blockRegistry.getNameForObject(behindBlock).toString();

                // If it's one of the predefined blocks, store its position
                if (isTargetBlock(blockName)) {
                    leversPositions.put(blockName, pos);
                } else if (Objects.equals(blockName, "minecraft:stone")) {
                    waterLeverPos = pos;
                }
            }
        }
        return leversPositions;
    }

    private BlockPos checkForBlock(Block targetBlock, int radius, int yLevel) {
        for (int x = (int) (player.posX - radius); x <= player.posX + radius; x++) {
            for (int z = (int) (player.posZ - radius); z <= player.posZ + radius; z++) {
                if (world.getBlockState(new BlockPos(x, yLevel, z)).getBlock() == targetBlock) {
                    return new BlockPos(x, yLevel, z);
                }
            }
        }
        return null;
    }

    private static BlockPos getBlockPistonHead(BlockPos pos, EnumFacing facing) {
        //System.out.println(facing);
        switch (facing) {
            case NORTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
            case EAST:
                return new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
            case SOUTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
            case WEST:
                return new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
            default:
                return null;
        }
    }

    public static BlockPos getBlockBehindLever(BlockPos pos, EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
            case EAST:
                return new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
            case SOUTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
            case WEST:
                return new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
            case UP:
                return new BlockPos(pos.getX(), pos.getY()-1, pos.getZ());
            case DOWN:
                return new BlockPos(pos.getX(), pos.getY()+1, pos.getZ());
            default:
                return null;
        }
    }

    // Helper method to check if a block is in the predefined set
    private boolean isTargetBlock(String blockName) {
        return blockName.equals("minecraft:quartz_block") ||
                blockName.equals("minecraft:gold_block") ||
                blockName.equals("minecraft:coal_block") ||
                blockName.equals("minecraft:diamond_block") ||
                blockName.equals("minecraft:emerald_block") ||
                blockName.equals("minecraft:hardened_clay");
    }

    public static boolean allTrue(boolean[] array) {
        for (boolean value : array) {
            if (!value) {
                return false;
            }
        }
        return true;
    }

    public enum WOOL_SOLUTIONS {
        LIME("lime",0,0,0,0,0,0),
        BLUE("blue",0,0,0,0,-1,1),
        RED("red",0,0,1,-1,1,-1),
        PURPLE("purple",1,1,1,0,-1,-1),
        ORANGE("orange",1,1,0,1,-1,-1);

        private final String color;
        private final int quartzBlock;
        private final int diamondBlock;
        private final int goldBlock;
        private final int emeraldBlock;
        private final int coalBlock;
        private final int hardenedClay;


        WOOL_SOLUTIONS(String color, int quartz, int diamondBlock, int goldBlock, int emeraldBlock, int coalBlock, int hardenedClay) {
            this.color = color;
            this.quartzBlock = quartz;
            this.diamondBlock = diamondBlock;
            this.goldBlock = goldBlock;
            this.emeraldBlock = emeraldBlock;
            this.coalBlock = coalBlock;
            this.hardenedClay = hardenedClay;
        }

        public static int[] getArraySolutions(String color) {
            for (WOOL_SOLUTIONS solution : WOOL_SOLUTIONS.values()){
                if (solution.color.equals(color)){
                    return new int[] {solution.quartzBlock, solution.diamondBlock, solution.goldBlock, solution.emeraldBlock, solution.coalBlock, solution.hardenedClay};
                }
            }
            return new int[]{-1,-1,-1,-1,-1,-1};
        }
    }

}

package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.mixin.accesors.BlockAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockCrops.class)
public abstract class MixinBlockCropsHeight extends MixinBlockHitbox {

    @Unique
    private static final AxisAlignedBB[] CARROT_POTATO_BOX = {
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
    };

    @Unique
    private static final AxisAlignedBB[] WHEAT_BOX = {
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
    };

    @Override
    public void getSelectedBoundingBox(World worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir) {
        if (Config.feature.qol.qolCropsHeight) {
            notEnoughFakepixel$updateCropsMaxY(worldIn, pos, worldIn.getBlockState(pos).getBlock());
        }
    }

    @Override
    public void collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end, CallbackInfoReturnable<MovingObjectPosition> cir) {
        if (Config.feature.qol.qolCropsHeight) {
            notEnoughFakepixel$updateCropsMaxY(worldIn, pos, worldIn.getBlockState(pos).getBlock());
        }
    }

    @Unique
    private static void notEnoughFakepixel$updateCropsMaxY(World world, BlockPos pos, Block block) {
        final IBlockState state = world.getBlockState(pos);
        final Integer age = state.getValue(BlockCrops.AGE);
        BlockAccessor blockAccessor = (BlockAccessor) block;
        blockAccessor.setMaxY(
                state.getBlock() instanceof BlockPotato ||
                        state.getBlock() instanceof BlockCarrot ? CARROT_POTATO_BOX[age].maxY : WHEAT_BOX[age].maxY
        );

    }

}

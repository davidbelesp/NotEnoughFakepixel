package com.nef.notenoughfakepixel.mixin;

import net.minecraft.block.BlockLever;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import com.nef.notenoughfakepixel.config.gui.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLever.class)
public abstract class MixinBlockLever {

    @Inject(method = "setBlockBoundsBasedOnState", at = @At("HEAD"), cancellable = true)
    private void modifyLeverBoundingBox(IBlockAccess worldIn, BlockPos pos, CallbackInfo ci) {
        if (Config.feature.qol.qolFullBlockLever) {
            BlockLever lever = (BlockLever) (Object) this;
            IBlockState state = worldIn.getBlockState(pos);
            EnumFacing facing = state.getValue(BlockLever.FACING).getFacing();

            if (facing == EnumFacing.DOWN) {
                lever.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F); // Lever on bottom face
            } else if (facing == EnumFacing.UP) {
                lever.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F); // Lever on top face
            } else if (facing == EnumFacing.NORTH) {
                lever.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F); // Lever on north face
            } else if (facing == EnumFacing.SOUTH) {
                lever.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F); // Lever on south face
            } else if (facing == EnumFacing.WEST) {
                lever.setBlockBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F); // Lever on west face
            } else if (facing == EnumFacing.EAST) {
                lever.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F); // Lever on east face
            }
            ci.cancel();
        }
    }
}
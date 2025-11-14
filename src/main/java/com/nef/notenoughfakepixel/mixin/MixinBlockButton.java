package com.nef.notenoughfakepixel.mixin;

import net.minecraft.block.BlockButton;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import com.nef.notenoughfakepixel.config.gui.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockButton.class)
public abstract class MixinBlockButton {

    @Inject(method = "setBlockBoundsBasedOnState", at = @At("HEAD"), cancellable = true)
    private void modifyButtonBoundingBox(IBlockAccess worldIn, BlockPos pos, CallbackInfo ci) {
        if (Config.feature.qol.qolFullBlockButton) {
            BlockButton button = (BlockButton) (Object) this;
            IBlockState state = worldIn.getBlockState(pos);
            EnumFacing facing = state.getValue(BlockButton.FACING);

            if (facing == EnumFacing.DOWN) {
                button.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F); // Button on bottom face
            } else if (facing == EnumFacing.UP) {
                button.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F); // Button on top face
            } else if (facing == EnumFacing.NORTH) {
                button.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F); // Button on north face
            } else if (facing == EnumFacing.SOUTH) {
                button.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F); // Button on south face
            } else if (facing == EnumFacing.WEST) {
                button.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F); // Button on west face
            } else if (facing == EnumFacing.EAST) {
                button.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F); // Button on east face
            }
            ci.cancel();
        }
    }
}
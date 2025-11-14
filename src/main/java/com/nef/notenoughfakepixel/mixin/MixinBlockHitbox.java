package com.nef.notenoughfakepixel.mixin;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlockHitbox {

    @Inject(method = "getSelectedBoundingBox", at = @At("HEAD"))
    public void getSelectedBoundingBox(World worldin, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir) {

    }

    @Inject(method = "collisionRayTrace", at = @At("HEAD"))
    public void collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end, CallbackInfoReturnable<MovingObjectPosition> cir) {

    }

}

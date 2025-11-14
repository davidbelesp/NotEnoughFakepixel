package com.nef.notenoughfakepixel.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import com.nef.notenoughfakepixel.config.gui.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderer.class)
public abstract class MixinEffectRenderer {

    /**
     * Cancels block-breaking particles if qolHideBlockBreakingParticles is enabled.
     */
    @Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
    public void onAddBlockDestroyEffects(BlockPos pos, IBlockState state, CallbackInfo ci) {
        if (Config.feature.qol.qolHideBlockBreakingParticles) {
            ci.cancel();
        }
    }

    /**
     * Cancels block-hit particles if qolHideBlockBreakingParticles is enabled.
     */
    @Inject(method = "addBlockHitEffects", at = @At("HEAD"), cancellable = true)
    public void onAddBlockHitEffects(BlockPos pos, EnumFacing side, CallbackInfo ci) {
        if (Config.feature.qol.qolHideBlockBreakingParticles) {
            ci.cancel();
        }
    }
}
package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import com.nef.notenoughfakepixel.config.gui.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void onHurtCam(float partialTicks, CallbackInfo ci) {
        if (Config.feature.qol.qolNoHurtCam) ci.cancel();
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void disableRainRendering(CallbackInfo ci) {
        if (Config.feature.qol.qolDisableRain) {
            ci.cancel();
        }
    }

}

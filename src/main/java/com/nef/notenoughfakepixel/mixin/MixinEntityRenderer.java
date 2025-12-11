package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.features.skyblock.qol.EtherwarpZoom;
import jdk.internal.org.objectweb.asm.Opcodes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Shadow private Minecraft mc;

    @Redirect(method = "updateCameraAndRender", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/settings/GameSettings;mouseSensitivity:F",
            opcode = Opcodes.GETFIELD
    ))
    public float updateCameraAndRender_mouseSensitivity(GameSettings gameSettings) {
        return gameSettings.mouseSensitivity * EtherwarpZoom.getSensMultiplier();
    }

    @Inject(
            method = "getFOVModifier",
            at = @At("RETURN"),
            cancellable = true
    )
    public void getFOVModifier_override(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir) {
        float currentFov = cir.getReturnValueF();
        cir.setReturnValue(currentFov * EtherwarpZoom.getFovMultiplier(partialTicks));
    }

}

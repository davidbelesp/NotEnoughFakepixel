package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.features.skyblock.qol.EtherwarpZoom;
import com.nef.notenoughfakepixel.features.skyblock.qol.SmoothAote;
import com.nef.notenoughfakepixel.utils.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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

    @Redirect(method = "renderWorldPass", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V",
            remap = false)
    )
    public void renderWorldPass_dispatchRenderLast(RenderGlobal context, float partialTicks) {
        SmoothAote smoothAote = SmoothAote.getInstance();
        if (smoothAote == null) {
            ForgeHooksClient.dispatchRenderLast(context, partialTicks);
            return;
        }
        Vector3f currentPosition = smoothAote.getCurrentPosition();
        if (currentPosition == null) {
            ForgeHooksClient.dispatchRenderLast(context, partialTicks);
            return;
        }
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        GlStateManager.translate(-currentPosition.x + d0, -currentPosition.y + d1, -currentPosition.z + d2);
        ForgeHooksClient.dispatchRenderLast(context, partialTicks);
        GlStateManager.translate(currentPosition.x - d0, currentPosition.y - d1, currentPosition.z - d2);
    }


    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE"), ordinal = 0)
    public double orientCamera_d0(double d0) {
        if (SmoothAote.getInstance() == null) return d0;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) return currentPosition.x;
        return d0;
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE"), ordinal = 1)
    public double orientCamera_d1(double d1) {
        if (SmoothAote.getInstance() == null) return d1;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) return currentPosition.y;
        return d1;
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE"), ordinal = 2)
    public double orientCamera_d2(double d2) {
        if (SmoothAote.getInstance() == null) return d2;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) return currentPosition.z;
        return d2;
    }

    //renderWorldPass
    @ModifyVariable(method = "renderWorldPass", at = @At(value = "STORE"), ordinal = 0)
    public double renderWorldPass_d0(double d0) {
        if (SmoothAote.getInstance() == null) return d0;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) return currentPosition.x;
        return d0;
    }

    @ModifyVariable(method = "renderWorldPass", at = @At(value = "STORE"), ordinal = 1)
    public double renderWorldPass_d1(double d1) {
        if (SmoothAote.getInstance() == null) return d1;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) return currentPosition.y;
        return d1;
    }

    @ModifyVariable(method = "renderWorldPass", at = @At(value = "STORE"), ordinal = 2)
    public double renderWorldPass_d2(double d2) {
        if (SmoothAote.getInstance() == null) return d2;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) return currentPosition.z;
        return d2;
    }

}

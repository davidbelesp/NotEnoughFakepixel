package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import com.nef.notenoughfakepixel.features.capes.CapeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LayerCape.class, priority = 1010)
public abstract class MixinLayerCape implements LayerRenderer<AbstractClientPlayer> {
    @Shadow
    @Final
    private RenderPlayer playerRenderer;

    @Inject(method = "doRenderLayer", at = @At("HEAD"))
    public void onRenderCape(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                             float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
                             float scale, CallbackInfo ci) {
        if (this.playerRenderer.getMainModel().isChild || player.isChild()) {
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
        }
        ResourceLocation currentCape = player.getLocationCape();

        if (CapeManager.hasCape()) {
            float customWidth = CapeManager.getCapeWidth();
            float customHeight = CapeManager.getCapeHeight();


            GlStateManager.pushMatrix();
        }
    }

    @Inject(method = "doRenderLayer", at = @At("RETURN"))
    public void afterRenderCape(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                                float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
                                float scale, CallbackInfo ci) {
        ResourceLocation currentCape = player.getLocationCape();

        if (CapeManager.hasCape()) {
            GlStateManager.popMatrix();
        }
    }

}
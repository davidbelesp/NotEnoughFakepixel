package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import com.nef.notenoughfakepixel.features.cosmetics.CosmeticsHandler;
import com.nef.notenoughfakepixel.features.cosmetics.CosmeticsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPlayer.class)
public class MixinModelPlayer extends ModelBiped {

    @Shadow
    public ModelRenderer bipedLeftLegwear;

    @Shadow
    public ModelRenderer bipedRightLegwear;

    @Shadow
    public ModelRenderer bipedLeftArmwear;

    @Shadow
    public ModelRenderer bipedRightArmwear;

    @Shadow
    public ModelRenderer bipedBodyWear;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
    private void onRender(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (entityIn.getName().equals(Minecraft.getMinecraft().getSession().getProfile().getName())) {
            for (CosmeticsHandler handler : CosmeticsManager.cosmetics) {
                if (handler.getType() == CosmeticsHandler.CosmeticsType.HAT) {
                    handler.render(entityIn, this.bipedHead);
                } else if (handler.getType() == CosmeticsHandler.CosmeticsType.BODY) {
                    handler.render(entityIn, this.bipedBody);
                }
            }
        }
    }
}

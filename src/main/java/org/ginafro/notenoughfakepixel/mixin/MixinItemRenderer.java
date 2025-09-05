package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.ItemAnimations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ItemRenderer.class}, priority = 1010)
public class MixinItemRenderer {

    @Shadow
    private ItemStack itemToRender;

    @Inject(method = "transformFirstPersonItem(FF)V", at = @At("HEAD"), cancellable = true)
    public void itemTransform(float equipProgress, float swingProgress, CallbackInfo ci) {
//        if (ItemAnimations.itemTransformHook(equipProgress, swingProgress)) ci.cancel();
    }

    @Inject(method = "doItemUsedTransformations", at = @At("HEAD"), cancellable = true)
    public void useTransform(float swingProgress, CallbackInfo ci) {
//        if (ItemAnimations.scaledSwing(swingProgress)) ci.cancel();
    }

    @Inject(method = "performDrinking", at = @At("HEAD"), cancellable = true)
    public void drinkTransform(AbstractClientPlayer clientPlayer, float partialTicks, CallbackInfo ci) {
//        if (ItemAnimations.rotationlessDrink(clientPlayer, partialTicks)) ci.cancel();
//        if (ItemAnimations.scaledDrinking(clientPlayer, partialTicks, itemToRender)) ci.cancel();
    }
}
package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.features.skyblock.dungeons.ScaleDungeonSecrets;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem {
    @Inject(
            method = "doRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/renderer/GlStateManager.pushMatrix()V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            )
    )
    private void scaleItemDrop(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        ScaleDungeonSecrets.scaleItemDrop(entity, x, y, z, entityYaw, partialTicks, ci);
    }
}
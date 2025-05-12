package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.client.renderer.entity.RenderLightningBolt;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderLightningBolt.class})
public class MixinRenderLightningBolt {
    @Inject(method = "doRender(Lnet/minecraft/entity/effect/EntityLightningBolt;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private void cancelLightningBolt(CallbackInfo ci) {
        if (Config.feature.qol.qolDisableThunderlordBolt) {
            ci.cancel();
        }
    }
}

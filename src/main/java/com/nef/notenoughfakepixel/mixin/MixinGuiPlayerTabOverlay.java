package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
    private void hideConnectionIndicator(int width, int x, int y, NetworkPlayerInfo playerInfo, CallbackInfo ci) {
        ci.cancel();
    }
}

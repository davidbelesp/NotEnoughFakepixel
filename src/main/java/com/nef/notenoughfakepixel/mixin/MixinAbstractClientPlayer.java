package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.features.capes.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Shadow
    private NetworkPlayerInfo playerInfo;

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    public void onGetCape(CallbackInfoReturnable<ResourceLocation> cir) {

        if (CapeManager.hasCape() && playerInfo != null) {
            if (Minecraft.getMinecraft().thePlayer.getGameProfile().getName().equals(playerInfo.getGameProfile().getName())) {
                cir.setReturnValue(CapeManager.getCapeTexture(CapeManager.getCape()));
            }
        }
    }
}

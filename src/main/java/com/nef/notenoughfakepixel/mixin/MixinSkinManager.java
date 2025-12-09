package com.nef.notenoughfakepixel.mixin;

import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@Mixin(SkinManager.class)
public class MixinSkinManager {

    @Redirect(
            method = "loadSkinFromCache",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/cache/LoadingCache;getUnchecked(Ljava/lang/Object;)Ljava/lang/Object;",
                    remap = false
            )
    )
    private Object protectAgainstBadSkulls(LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> cache, Object key) {
        try {
            return cache.getUnchecked((GameProfile) key);
        } catch (UncheckedExecutionException | NullPointerException e) {
            return new HashMap<>();
        }
    }

}

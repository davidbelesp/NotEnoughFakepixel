package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.renderer.InventoryEffectRenderer;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({InventoryEffectRenderer.class})
public class MixinInventoryEffectRenderer {

    @ModifyVariable(method = "updateActivePotionEffects", at = @At(value = "STORE"))
    public boolean hasVisibleEffect_updateActivePotionEffects(boolean hasVisibleEffect) {
        if (Config.feature.qol.qolDisablePotionEffects && ScoreboardUtils.currentGamemode.isSkyblock()) {
            return false;
        } else {
            return hasVisibleEffect;
        }
    }
}

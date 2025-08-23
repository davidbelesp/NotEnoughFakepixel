package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.DisableHyperionExplosions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class MixinWorld {
    @Inject(method = "spawnParticle(IZDDDDDD[I)V", at = @At("HEAD"), cancellable = true)
    public void spawnParticle(
            int particleID, boolean p_175720_2_, double xCood, double yCoord, double zCoord,
            double xOffset, double yOffset, double zOffset, int[] p_175720_15_, CallbackInfo ci
    ) {
        if (Config.feature.qol.qolDisableHyperionExplosions &&
            System.currentTimeMillis() - DisableHyperionExplosions.lastClickedHyperion < 500) {

            if ( particleID == 1 ) ci.cancel();
        }
    }

    @Inject(method = "updateWeather", at = @At("HEAD"), cancellable = true)
    private void disableRain(CallbackInfo ci) {
        // Check if the custom configuration option to disable rain is enabled
        if (Config.feature.qol.qolDisableRain) {
            // Cast this mixin instance back to World
            World world = (World) (Object) this;

            // Access the world info to modify weather
            WorldInfo worldInfo = world.getWorldInfo();

            // Set weather to clear
            worldInfo.setRainTime(0);         // Reset rain timer
            worldInfo.setThunderTime(0);     // Reset thunder timer
            worldInfo.setRaining(false);     // Ensure it's not raining
            worldInfo.setThundering(false);  // Ensure it's not thundering

            // Cancel further execution of the weather update
            ci.cancel();
        }
    }
}

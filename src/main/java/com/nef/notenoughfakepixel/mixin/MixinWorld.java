package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.events.SpawnedParticleEvent;
import com.nef.notenoughfakepixel.features.skyblock.qol.DisableHyperionExplosions;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

    @Inject(
            method = "spawnParticle(Lnet/minecraft/util/EnumParticleTypes;ZDDDDDD[I)V",
            at = @At("HEAD"),
            remap = false
    )
    private void nef$onSpawnParticle_mcp(EnumParticleTypes type, boolean longDistance,
                                         double x, double y, double z,
                                         double xOff, double yOff, double zOff,
                                         int[] params, CallbackInfo ci) {
        nef$post(type, longDistance, x, y, z, xOff, yOff, zOff, params);
    }

    @Unique
    private static void nef$post(EnumParticleTypes type, boolean longDistance,
                                 double x, double y, double z,
                                 double xOff, double yOff, double zOff,
                                 int[] params) {
        SpawnedParticleEvent evt = new SpawnedParticleEvent(
                type, longDistance, x, y, z, xOff, yOff, zOff, params
        );
        MinecraftForge.EVENT_BUS.post(evt);
    }

    // Weather control
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

package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.events.ParticlePacketEvent;
import com.nef.notenoughfakepixel.features.skyblock.qol.SmoothAote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Shadow private Minecraft gameController;

    @Inject(
            method = "handleParticles(Lnet/minecraft/network/play/server/S2APacketParticles;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nef$onHandleParticles(S2APacketParticles packet, CallbackInfo ci) {
        ParticlePacketEvent evt = new ParticlePacketEvent(packet);
        boolean canceled = MinecraftForge.EVENT_BUS.post(evt);
        if (canceled) {
            ci.cancel();
        }
    }

    @Inject(
            method = "handleParticles(Lnet/minecraft/network/play/server/S2APacketParticles;)V",
            at = @At("HEAD")
    )
    private void nef$postPacketEvent(S2APacketParticles packet, CallbackInfo ci) {
        ParticlePacketEvent evt = new ParticlePacketEvent(packet);
        MinecraftForge.EVENT_BUS.post(evt);
    }

    @Redirect(method = "handlePlayerPosLook", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setPositionAndRotation(DDDFF)V"))
    public void handlePlayerPosLook_setPositionAndRotation(
            EntityPlayer player,
            double x,
            double y,
            double z,
            float yaw,
            float pitch
    ) {
        if (SmoothAote.getInstance() == null) {
            player.setPositionAndRotation(x, y, z, yaw, pitch);
            return;
        }

        if (SmoothAote.getInstance().teleportCurrentPos != null) {
            SmoothAote.getInstance().teleportMillis += Math.max(
                    0,
                    Math.min(300, SmoothAote.getInstance().teleportTime)
            );
        }

        player.setPositionAndRotation(x, y, z, yaw, pitch);
    }

}
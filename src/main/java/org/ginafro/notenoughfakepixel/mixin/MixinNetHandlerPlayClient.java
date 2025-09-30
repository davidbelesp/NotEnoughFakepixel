package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraftforge.common.MinecraftForge;
import org.ginafro.notenoughfakepixel.events.ParticlePacketEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

}
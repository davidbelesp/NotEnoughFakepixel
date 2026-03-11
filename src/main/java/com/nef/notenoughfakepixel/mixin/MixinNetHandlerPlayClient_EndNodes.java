package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.features.skyblock.qol.EnderNodesHighlighter;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.EnumParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_EndNodes {

    @Inject(
            method = "handleParticles(Lnet/minecraft/network/play/server/S2APacketParticles;)V",
            at = @At("HEAD")
    )
    private void nef$onHandleParticles_endNodes(S2APacketParticles packet, CallbackInfo ci) {
        if (packet.getParticleType() == EnumParticleTypes.PORTAL) {
            EnderNodesHighlighter.INSTANCE.onPortalParticle(
                    packet.getXCoordinate(),
                    packet.getYCoordinate(),
                    packet.getZCoordinate()
            );
        }
    }

}
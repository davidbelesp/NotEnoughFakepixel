package org.ginafro.notenoughfakepixel.events;

import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ParticlePacketEvent extends Event {
    private final S2APacketParticles packet;

    public ParticlePacketEvent(S2APacketParticles packet) {
        this.packet = packet;
    }

    public S2APacketParticles getPacket() {
        return packet;
    }
}
package com.nef.notenoughfakepixel.events;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.network.play.server.S23PacketBlockChange;

public class NefPacketBlockChange {

    @Getter
    S23PacketBlockChange packet;
    @Getter
    Block block;

    public NefPacketBlockChange(S23PacketBlockChange packetIn) {
        this.packet = packetIn;
        this.block = packetIn.getBlockState().getBlock();
    }

}

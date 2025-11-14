package com.nef.notenoughfakepixel.events;

import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class NEFClientConnectedToServerEvent extends FMLNetworkEvent.ClientConnectedToServerEvent {

    public NEFClientConnectedToServerEvent(NetworkManager manager, String connectionType) {
        super(manager, connectionType);
    }

    public static NEFClientConnectedToServerEvent create(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        return new NEFClientConnectedToServerEvent(event.manager, event.connectionType);
    }

    @Override
    public String toString() {
        return "NEFClientConnectedToServerEvent{" +
                "manager.remoteAddress=" + manager.getRemoteAddress().toString() +
                ", connectionType='" + connectionType + '\'' +
                '}';
    }
}

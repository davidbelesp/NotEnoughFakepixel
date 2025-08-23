package org.ginafro.notenoughfakepixel.events.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;

@RegisterEvents
public class ConnectionEvents {

    @SubscribeEvent
    public void onCLientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        RepoHandler.refreshOnJoinAsync();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // Optional
    }

}

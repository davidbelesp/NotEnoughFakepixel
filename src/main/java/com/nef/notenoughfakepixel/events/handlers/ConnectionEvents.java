package com.nef.notenoughfakepixel.events.handlers;

import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Collections;

@RegisterEvents
public class ConnectionEvents {

    @SubscribeEvent
    public void onCLientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        RepoHandler.refreshAsync(Collections.singleton("REPO"));
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // Optional
    }

}

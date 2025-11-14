package com.nef.notenoughfakepixel.events.handlers;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.NEFClientConnectedToServerEvent;
import com.nef.notenoughfakepixel.utils.Logger;

@RegisterEvents
public class ConnectionHandler {

    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        NEFClientConnectedToServerEvent nefEvent = NEFClientConnectedToServerEvent.create(event);
        nefEvent.manager.channel().pipeline().addBefore("packet_handler", "nef_packet_handler", new PacketHandler());
        Logger.logConsole("Added packet handler to channel pipeline.");
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        ApiHandler.init();
    }

}
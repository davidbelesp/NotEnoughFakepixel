package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class WelcomeMessage {

    public static boolean notified = false;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!notified && event.message.getUnformattedText().contains("You are playing on profile:")) {
            String profileName = event.message.getUnformattedText().replace("You are playing on profile: ", "").trim();
            SkyblockData.setCurrentProfile(profileName);
        }
    }
}

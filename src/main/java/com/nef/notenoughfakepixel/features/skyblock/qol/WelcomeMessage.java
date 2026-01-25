package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ListUtils;
import com.nef.notenoughfakepixel.utils.RandomUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

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

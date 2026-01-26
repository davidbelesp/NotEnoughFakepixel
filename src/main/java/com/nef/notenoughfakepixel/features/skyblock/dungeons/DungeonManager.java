package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class DungeonManager {
    private static boolean isBossStage = false; // This includes blood room
    private static boolean isFinalStage = false;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!DungeonManager.checkEssentials()) return;
        if (event.message.getUnformattedText().contains("> EXTRA STATS <")) {
            isFinalStage = true;
            isBossStage = false;
        } else if (event.message.getUnformattedText().contains("[BOSS]")) {
            isBossStage = true;
        }
    }

    @SubscribeEvent()
    public void onWorldLoad(WorldEvent.Load event) {
        if (!DungeonManager.checkEssentials()) return;
        isBossStage = false;
        isFinalStage = false;
    }

    public static boolean checkEssentials() {
        return SkyblockData.getCurrentGamemode().isSkyblock() && SkyblockData.getCurrentLocation().isDungeon();
    }

    public static boolean checkEssentialsF7() {
        return checkEssentials() && (SkyblockData.getCurrentFloor().name().equals("F7") || SkyblockData.getCurrentFloor().name().equals("M7"));
    }

    public static boolean isBossStage() {
        return isBossStage;
    }

    public static boolean isFinalStage() {
        return isFinalStage;
    }

}

package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;

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
        return ScoreboardUtils.currentGamemode.isSkyblock() && TablistParser.currentLocation.isDungeon();
    }

    public static boolean checkEssentialsF7() {
        return checkEssentials() && (ScoreboardUtils.currentFloor.name().equals("F7") || ScoreboardUtils.currentFloor.name().equals("M7"));
    }

    public static boolean isBossStage() {
        return isBossStage;
    }

    public static boolean isFinalStage() {
        return isFinalStage;
    }

}

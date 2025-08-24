package org.ginafro.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.TablistParser;

@RegisterEvents
public class MuteIrrelevantMessages {
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!TablistParser.currentLocation.isDungeon()) return;
        if (!Config.feature.dungeons.dungeonsMuteIrrelevantMessages) return;
        if (event.message.getUnformattedText().contains("[BOSS]") || event.message.getUnformattedText().contains("[CROWD]"))
            event.setCanceled(true);
    }
}

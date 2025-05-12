package org.ginafro.notenoughfakepixel.features.skyblock.fishing;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.Area;

public class TrophyFishNotifier {

    @SubscribeEvent
    public void onChatRecieve(ClientChatReceivedEvent e) {
        if (e.type != 1) return;
        if (!Config.feature.fishing.fishingTrophyFish) return;

        if (ScoreboardUtils.currentArea != Area.CRIMSON &&
                ScoreboardUtils.currentArea != Area.CRIMSON_FIELDS &&
                ScoreboardUtils.currentArea != Area.SCARELTON &&
                ScoreboardUtils.currentArea != Area.ASHFANG &&
                ScoreboardUtils.currentArea != Area.VOLCANO) {
            return;
        }

        if (e.message.getUnformattedText().toLowerCase().contains("trophy fish!")) {
            String fish = e.message.getUnformattedText().replace("TROPHY FISH!", "").replace("You caught a ", "");
            Minecraft.getMinecraft().ingameGUI.displayTitle("TROPHY FISH", fish, 1, 20, 1);
        }
    }
}

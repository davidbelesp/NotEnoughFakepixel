package com.nef.notenoughfakepixel.features.skyblock.fishing;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Area;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrophyFishNotifier {

    @SubscribeEvent
    public void onChatRecieve(ClientChatReceivedEvent e) {
        if (e.type != 1) return;
        if (!Config.feature.fishing.fishingTrophyFish) return;

        Area area = SkyblockData.getCurrentArea();

        if (area != Area.CRIMSON &&
                area != Area.CRIMSON_FIELDS &&
                area != Area.SCARELTON &&
                area != Area.ASHFANG &&
                area != Area.VOLCANO) {
            return;
        }

        if (e.message.getUnformattedText().toLowerCase().contains("trophy fish!")) {
            String fish = e.message.getUnformattedText().replace("TROPHY FISH!", "").replace("You caught a ", "");
            Minecraft.getMinecraft().ingameGUI.displayTitle("TROPHY FISH", fish, 1, 20, 1);
        }
    }
}

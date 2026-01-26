package com.nef.notenoughfakepixel.features.skyblock.mining;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Pattern;

@RegisterEvents
public class EventsMsgSupressor {

    // message start example §r§e[NPC] §r§bDon Espresso§r§f:
    private final Pattern donEspressoPattern = Pattern.compile("§r§e\\[NPC] §r§bDon Espresso§r§f:");
    //info message
    /*
        §b[PLAYER INFORMATION]
        §fWant to get the most out of the game?
        §bCheck out our shop §fand enjoy the best combination of
        §fprice and quality, get access to a variety of
        §bunique features §fand additional bonuses!
    */


    @SubscribeEvent
    public void onChatRecieve(ClientChatReceivedEvent e) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        checkMessageMatches(e);
    }

    private void checkMessageMatches(ClientChatReceivedEvent e) {
        checkDonEspressoMessage(e);
    }

    private void checkDonEspressoMessage(ClientChatReceivedEvent e) {
        if (!Config.feature.mining.miningDisableDonEspresso) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (SkyblockData.getCurrentLocation() != Location.DWARVEN) return;
        if (donEspressoPattern.matcher(e.message.getFormattedText()).find()) {
            e.setCanceled(true);
        }
    }


}

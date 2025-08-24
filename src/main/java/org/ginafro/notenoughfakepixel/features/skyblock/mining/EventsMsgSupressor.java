package org.ginafro.notenoughfakepixel.features.skyblock.mining;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;

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
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (TablistParser.currentLocation != Location.DWARVEN) return;
        if (donEspressoPattern.matcher(e.message.getFormattedText()).find()) {
            e.setCanceled(true);
        }
    }


}

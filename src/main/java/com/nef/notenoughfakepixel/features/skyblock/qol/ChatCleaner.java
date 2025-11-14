package com.nef.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ChatUtils;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;

import java.util.regex.Pattern;

@RegisterEvents
public class ChatCleaner {

    private final Pattern sellingRankPattern = Pattern.compile("(?<rank>\\[[A-Za-z0-9_+]+\\] )?(?<username>\\w+:) (?<message>.*\\bselling\\b.*\\brank(s)?\\b.*)");
    private final Pattern watchdogPattern = Pattern.compile("§4\\[WATCHDOG ANNOUNCEMENT]\n");
    private final Pattern infoPattern = Pattern.compile("§b\\[PLAYER INFORMATION]\n");
    private final Pattern friendJoinPattern = Pattern.compile("§aFriend > ");
    private final Pattern potatoDropPattern = Pattern.compile("§r§6§lRARE DROP! §r§fPotato§r§b");
    private final Pattern poisonousPotatoDropPattern = Pattern.compile("§r§6§lRARE DROP! §r§fPoisonous Potato§r§b");
    private final Pattern carrotDropPattern = Pattern.compile("§r§6§lRARE DROP! §r§fCarrot§r§b");

    @SubscribeEvent
    public void onChatRecieve(ClientChatReceivedEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (ChatUtils.middleBar.matcher(event.message.getFormattedText()).matches()) return;
        cancelMessage(Config.feature.qol.qolDisableSellingRanks, event, sellingRankPattern);
        cancelMessage(Config.feature.qol.qolDisableWatchdogInfo, event, watchdogPattern, true);
        cancelMessage(Config.feature.qol.qolDisableWatchdogInfo, event, infoPattern, true);
        cancelMessage(Config.feature.qol.qolDisableFriendJoin, event, friendJoinPattern, true);
        cancelMessage(Config.feature.qol.qolDisableZombieRareDrops, event, potatoDropPattern, true);
        cancelMessage(Config.feature.qol.qolDisableZombieRareDrops, event, poisonousPotatoDropPattern, true);
        cancelMessage(Config.feature.qol.qolDisableZombieRareDrops, event, carrotDropPattern, true);
    }

    private void cancelMessage(boolean option, ClientChatReceivedEvent e, Pattern pattern, boolean formatted) {
        if (!option) return;
        String message = e.message.getUnformattedText();
        if (formatted) message = e.message.getFormattedText();

        if (pattern.matcher(message).find() || pattern.matcher(message).matches()) {
            e.setCanceled(true);
        }
    }

    private void cancelMessage(boolean option, ClientChatReceivedEvent e, Pattern pattern) {
        cancelMessage(option, e, pattern, false);
    }

}

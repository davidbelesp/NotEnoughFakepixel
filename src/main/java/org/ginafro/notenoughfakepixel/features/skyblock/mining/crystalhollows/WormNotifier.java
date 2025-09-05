package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import net.minecraft.util.EnumChatFormatting;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.ChatNotifier;

@RegisterEvents
public class WormNotifier extends ChatNotifier {

    @Override
    public boolean shouldNotify() {
        return Config.feature.mining.crystalWormNotifier;
    }

    @Override
    public String getMessage() {
        return "§7§oYou hear the sound of something approaching...";
    }

    @Override
    public String notifyMessage() {
        return "Worm";
    }

    @Override
    public EnumChatFormatting getColor() {
        return EnumChatFormatting.RED;
    }

    @Override
    public void afterDetection() {
        WormSpawnTimer.setGoalEpochMs(System.currentTimeMillis() + 30000);
    }
}

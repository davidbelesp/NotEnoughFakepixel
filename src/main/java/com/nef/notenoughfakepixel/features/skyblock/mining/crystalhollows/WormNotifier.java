package com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.ChatNotifier;
import net.minecraft.util.EnumChatFormatting;

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
    public void afterDetection(String message) {
        WormSpawnTimer.setGoalEpochMs(System.currentTimeMillis() + 30000);
    }
}

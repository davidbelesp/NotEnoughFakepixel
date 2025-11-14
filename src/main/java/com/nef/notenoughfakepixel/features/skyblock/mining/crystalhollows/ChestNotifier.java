package com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.ChatNotifier;

@RegisterEvents
public class ChestNotifier extends ChatNotifier {

    @Override
    public boolean shouldNotify() {
        return Config.feature.mining.crystalPowderNotifier;
    }

    @Override
    public String notifyMessage() {
        return "Treasure found!";
    }

    @Override
    public String getMessage() {
        return "§aYou uncovered a treasure chest!";
    }

}

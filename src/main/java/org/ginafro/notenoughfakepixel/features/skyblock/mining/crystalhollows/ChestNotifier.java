package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.ChatNotifier;

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

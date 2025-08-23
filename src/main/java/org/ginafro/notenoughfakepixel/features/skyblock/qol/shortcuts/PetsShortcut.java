package org.ginafro.notenoughfakepixel.features.skyblock.qol.shortcuts;

import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;

@RegisterEvents
public class PetsShortcut extends KeyShortcut {

    @Override
    public boolean getConfigOption() {
        return Config.feature.qol.qolShortcutPets;
    }

    @Override
    public int getKeyBind() {
        return Config.feature.qol.qolPetsKey;
    }

    @Override
    public String getCommand() {
        return "/pets";
    }

    @Override
    public String getMenuTitle() {
        return "Pets";
    }


}
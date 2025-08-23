package org.ginafro.notenoughfakepixel.features.skyblock.qol.shortcuts;

import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;

@RegisterEvents
public class EquipmentShortcut extends KeyShortcut{

    @Override
    public boolean getConfigOption() {
        return Config.feature.qol.qolShortcutEq;
    }

    @Override
    public int getKeyBind() {
        return Config.feature.qol.qolEqKey;
    }

    @Override
    public String getCommand() {
        return "/equipment";
    }

    @Override
    public String getMenuTitle() {
        return "Your Equipment";
    }
}

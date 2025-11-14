package com.nef.notenoughfakepixel.features.skyblock.qol.slothighlight;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;

import java.awt.*;

@RegisterEvents
public class ShowNotOpenedChestsRed extends HightlightSlot {

    @Override
    public String getLoreLine() {
        return "No more chests to open!";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getContainerName() {
        return "Croesus";
    }

    @Override
    public boolean getConfigOption() {
        return Config.feature.dungeons.dungeonsShowOpenedChests;
    }

    @Override
    public Color getHighlightColor() {
        return new Color(255, 55, 55);
    }

}

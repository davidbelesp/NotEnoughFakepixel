package com.nef.notenoughfakepixel.features.skyblock.qol.slothighlight;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ColorUtils;

import java.awt.*;

@RegisterEvents
public class ShowCurrentPet extends HightlightSlot {

    @Override
    public String getLoreLine() {
        return "Click to despawn!";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getContainerName() {
        return "Pets";
    }

    @Override
    public boolean getConfigOption() {
        return Config.feature.qol.qolShowPetEquipped;
    }

    @Override
    public Color getHighlightColor() {
        return ColorUtils.getColor(Config.feature.qol.qolPetEquippedColor);
    }

    @Override
    public boolean highlightOnlyFirst() {
        return true;
    }

}

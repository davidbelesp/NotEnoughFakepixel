package com.nef.notenoughfakepixel.features.skyblock.qol.slothighlight;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;

import java.awt.*;

@RegisterEvents
public class JacobRewardsRed extends HightlightSlot {

    @Override
    public String getLoreLine() {
        return "Rewards claimed!";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getContainerName() {
        return "Your contests";
    }

    @Override
    public boolean getConfigOption() {
        return Config.feature.qol.qolShowJacobRewards;
    }

    @Override
    public Color getHighlightColor() {
        return new Color(255, 55, 55);
    }

}

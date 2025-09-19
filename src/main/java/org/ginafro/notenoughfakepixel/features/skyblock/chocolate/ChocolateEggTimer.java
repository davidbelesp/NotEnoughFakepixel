package org.ginafro.notenoughfakepixel.features.skyblock.chocolate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.Timer;
import org.ginafro.notenoughfakepixel.serverdata.SkyblockData;
import org.ginafro.notenoughfakepixel.variables.Resources;

@RegisterEvents
public class ChocolateEggTimer extends Timer {

    private static long goal = 0L;

    @Override
    public boolean shouldShow() {
        return Config.feature.chocolateFactory.chocolateEggTimer && SkyblockData.getSeason().equals(SkyblockData.Season.SPRING);
    }

    @Override
    public ResourceLocation getIcon() {
        return Resources.EGG_HUNT.getResource();
    }

    @Override
    public long getGoalEpochMs() {
        return goal;
    }

    public static void setGoalEpochMs(long goal) {
        ChocolateEggTimer.goal = goal;
    }

    @Override
    public boolean getTextShadow() {
        return true;
    }

    @Override
    public int getX() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int x = Config.feature.chocolateFactory.eggTimerPos.getAbsX(sr, this.getObjectWidth());
        return x - getObjectWidth()/2;
    }

    @Override
    public int getY() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int y = Config.feature.chocolateFactory.eggTimerPos.getAbsY(sr, this.getObjectHeight());
        return y - getObjectHeight()/2;
    }

    @Override
    public float getScale() {
        return Config.feature.chocolateFactory.eggTimerScale;
    }

    @Override
    public int getTextColor(long deltaMs) {
        if (deltaMs < 0) return 0xFFFFFF55;
        if (deltaMs < 60000L) return 0xFFFF5555;
        return 0xFFFFAA00;
    }

}

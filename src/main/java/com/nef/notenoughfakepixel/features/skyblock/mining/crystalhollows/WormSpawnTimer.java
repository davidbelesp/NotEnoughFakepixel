package com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.overlays.Timer;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.variables.Location;
import com.nef.notenoughfakepixel.variables.Resources;

@RegisterEvents
public class WormSpawnTimer extends Timer {

    private static long goal = 0L;

    @Override
    public boolean shouldShow() {
        return Config.feature.mining.wormTimerCooldown && ScoreboardUtils.currentGamemode.isSkyblock() && TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS);
    }

    @Override
    public ResourceLocation getIcon() {
        return Resources.SCATHA.getResource();
    }

    @Override
    public long getGoalEpochMs() {
        return goal;
    }

    @Override
    public int getTextColor(long deltaMs) {
        if (deltaMs < 0) return 0xFFFFFF55;
        if (deltaMs < 5000L) return 0xFF55FF55;
        return 0xFFFFFFFF;
    }

    @Override
    public float getScale() {
        return Config.feature.mining.wormTimerScale;
    }

    @Override
    public boolean getTextShadow() {
        return true;
    }

    @Override
    public int getX() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int x = Config.feature.mining.wormTimerPos.getAbsX(sr, this.getObjectWidth());
        return x - getObjectWidth()/2;
    }

    @Override
    public int getY() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int y = Config.feature.mining.wormTimerPos.getAbsY(sr, this.getObjectHeight());
        return y - getObjectHeight()/2;
    }

    public static void setGoalEpochMs(long goal) {
        WormSpawnTimer.goal = goal;
    }

}

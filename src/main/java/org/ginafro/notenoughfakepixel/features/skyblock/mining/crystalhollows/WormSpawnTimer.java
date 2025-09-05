package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.Timer;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.DarkAhTimer;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;
import org.ginafro.notenoughfakepixel.variables.Resources;

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

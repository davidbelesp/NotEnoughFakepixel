package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.Timer;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TimeUtils;
import org.ginafro.notenoughfakepixel.variables.Resources;

@RegisterEvents
public class DarkAhTimer extends Timer {

    private static long goal = 0L;

    @Override
    public boolean shouldShow() {
        return Config.feature.qol.qolDAtimer && ScoreboardUtils.currentGamemode.isSkyblock();
    }

    @Override
    public ResourceLocation getIcon() {
        return Resources.DARK_AH.getResource();
    }

    @Override
    public void preRender() {
        long now = System.currentTimeMillis();
        long goal = getGoalEpochMs();

        if (goal <= 0L || now > goal) {
            setGoalEpochMs(TimeUtils.nextXx55UtcEpochMs());
        }
        if (goal - now <= 60000L && goal - now > 59900L) {
            notifyDA();
        }
    }

    @Override
    protected int getTextColor(long deltaMs) {
        if (deltaMs <= 0) return 0xFF55FF55;
        long sec = deltaMs / 1000L;
        if (sec <= 180)  return 0xFFFF5555;
        return 0xFFffaa00;
    }

    @Override public int getX() { return Config.feature.qol.darkAhTimerPos.getAbsX(new ScaledResolution(Minecraft.getMinecraft()), getObjectWidth()) - getObjectWidth()/2; }
    @Override public int getY() { return Config.feature.qol.darkAhTimerPos.getAbsY(new ScaledResolution(Minecraft.getMinecraft()), getObjectHeight()) - getObjectHeight()/2; }
    @Override public float getScale() { return Config.feature.qol.darkAHTimerScale; }
    @Override public boolean getTextShadow() { return true; }
    @Override public long getGoalEpochMs() { return goal; }
    public static void setGoalEpochMs(long goal) {
        DarkAhTimer.goal = goal;
    }

    private static void notifyDA() {
        if (!Config.feature.qol.qolDarkAhNotifier) return;
        Minecraft.getMinecraft().ingameGUI.displayTitle(EnumChatFormatting.GOLD + "Mining Ability Ready", "", 2, 70, 2);
    }

}

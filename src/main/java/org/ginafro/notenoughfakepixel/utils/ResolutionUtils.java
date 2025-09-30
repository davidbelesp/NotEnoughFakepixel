package org.ginafro.notenoughfakepixel.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ResolutionUtils {
    private static ScaledResolution scaledResolution;

    public static int getHeight() {
        scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledResolution.getScaledHeight();
    }

    public static int getWidth() {
        scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledResolution.getScaledWidth();
    }

    public static int getFactor() {
        scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledResolution.getScaleFactor();
    }

    public static float getXRatio(int x) {
        return x / 1920f;
    }

    public static float getXStatic(int x){
        return getWidth() * getXRatio(x);
    }

    public static float getYStatic(int y){
        return getHeight() * getYRatio(y);
    }

    public static float getYRatio(int y) {
        return y / 1080f;
    }
}

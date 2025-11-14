package com.nef.notenoughfakepixel.features.skyblock.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public abstract class Overlay {

    public static final int LINE_HEIGHT = 11;
    public static final int MINIMUM_WIDTH = 20;
    public final Minecraft mc = Minecraft.getMinecraft();

    public void draw(float x, float y, float scale, String bgColorOption) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, scale);

        List<String> lines = getLines();

        // Parse background color
        String[] colorParts = bgColorOption.split(":");
        int alpha = Integer.parseInt(colorParts[1]);
        int red = Integer.parseInt(colorParts[2]);
        int green = Integer.parseInt(colorParts[3]);
        int blue = Integer.parseInt(colorParts[4]);
        int bgColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Draw background
        int width = (int) getWidth(scale, lines);
        int height = (int) getHeight(scale, lines);
        Gui.drawRect(0, 0, width, height, bgColor);

        // Draw text
        for (int i = 0; i < lines.size(); i++) {
            mc.fontRendererObj.drawString(lines.get(i), 2, (i * LINE_HEIGHT) + 2, -1);
        }

        GlStateManager.popMatrix();
    }

    public int getLongestLine(List<String> lines) {
        int longest = 0;
        for (String line : lines) {
            if (line.length() > longest) longest = line.length();
        }
        return Math.max(longest, MINIMUM_WIDTH);
    }

    public abstract boolean shouldShow();

    public abstract List<String> getLines();

    public abstract float getWidth(float scale, List<String> lines);

    public abstract float getHeight(float scale, List<String> lines);

}

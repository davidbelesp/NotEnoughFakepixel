package com.nef.notenoughfakepixel.utils;

import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RegisterEvents
public class TitleUtils {
    private static final List<Title> titles = new ArrayList<>();

    public static void showTitle(String text, Color color, int durationMillis) {
        showTitleInternal(text, color, durationMillis);
    }

    public static void showTitle(String text, Color color) {
        showTitleInternal(text, color, 1000); // default duration of 1000ms
    }

    public static void showTitle(String text) {
        showTitleInternal(text, null, 1000); // default duration & preserve formatting
    }

    public static void showTitle(String text, int durationMillis) {
        showTitleInternal(text, null, durationMillis); // preserve formatting & custom duration
    }

    private static void showTitleInternal(String text, Color color, int durationMillis) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            long endTime = System.currentTimeMillis() + durationMillis;
            titles.add(new Title(text, color, endTime));
        });
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;

        long currentTime = System.currentTimeMillis();
        Iterator<Title> iterator = titles.iterator();
        while (iterator.hasNext()) {
            Title title = iterator.next();
            if (currentTime > title.endTime) {
                iterator.remove();
                continue;
            }
            renderTitle(title, event);
        }
    }

    private void renderTitle(Title title, RenderGameOverlayEvent.Post event) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        String displayText = title.color == null ? title.text : StringUtils.stripControlCodes(title.text);
        int renderColor = title.color == null ? 0xFFFFFF : (title.color.getRGB() & 0xFFFFFF);
        int screenWidth = event.resolution.getScaledWidth();
        int screenHeight = event.resolution.getScaledHeight();

        GlStateManager.pushMatrix();
        GlStateManager.scale(4.0F, 4.0F, 4.0F);
        int textWidth = fr.getStringWidth(displayText);
        int x = (screenWidth / 8) - (textWidth / 2);
        int y = (screenHeight / 8) - 10;
        fr.drawStringWithShadow(displayText, x, y, renderColor);
        GlStateManager.popMatrix();
    }

    private static class Title {
        String text;
        Color color;
        long endTime;

        Title(String text, Color color, long endTime) {
            this.text = text;
            this.color = color;
            this.endTime = endTime;
        }
    }
}
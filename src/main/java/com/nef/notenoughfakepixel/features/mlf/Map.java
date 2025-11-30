package com.nef.notenoughfakepixel.features.mlf;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.variables.Gamemode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class Map {

    private String inc = "", bal = "", e1 = "", e2 = "";
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!Config.feature.mlf.mlfInfoHud) return;
        if (SkyblockData.getCurrentGamemode() != Gamemode.MLF) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int x = (int) Config.feature.mlf.mlfInfoOffsetX;
        int y = (int) Config.feature.mlf.mlfInfoOffsetY;

        // Parse background color (assuming A:R:G:B format)
        String[] colorParts = Config.feature.mlf.mlfInfoBackgroundColor.split(":");
        int alpha = Integer.parseInt(colorParts[1]); // Adjusted to match typical A:R:G:B
        int red = Integer.parseInt(colorParts[2]);
        int green = Integer.parseInt(colorParts[3]);
        int blue = Integer.parseInt(colorParts[4]);
        int bgColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Update text lines from scoreboard
        List<String> sideBarLines = ScoreboardUtils.getScoreboardLines();
        updateLines(sideBarLines);

        // Prepare text lines
        List<String> lines = new ArrayList<>();
        lines.add("Income: " + inc);
        lines.add("Balance: " + bal);
        lines.add("Events: ");
        if (!e1.isEmpty()) lines.add("- " + e1);
        if (!e2.isEmpty()) lines.add("- " + e2);

        // Calculate text dimensions
        int textWidth = 0;
        for (String line : lines) {
            int lineWidth = mc.fontRendererObj.getStringWidth(line);
            if (lineWidth > textWidth) textWidth = lineWidth;
        }
        textWidth += 4; // 2px padding on each side
        int textHeight = lines.size() * 9; // 8px font height + 1px spacing per line

        // Draw background matching text dimensions
        Gui.drawRect(x, y, x + textWidth, y + textHeight, bgColor);

        // Draw text
        for (int i = 0; i < lines.size(); i++) {
            mc.fontRendererObj.drawString(lines.get(i), x + 2, y + (i * 9), -1); // 2px left padding, 9px line spacing
        }
    }

    /**
     * Updates the lines from the scoreboard
     */
    private void updateLines(List<String> sideBarLines) {
        if (sideBarLines == null) return;
        e1 = "";
        e2 = "";
        for (String s : sideBarLines) {
            if (s.contains("- ")) {
                if (e1.isEmpty()) {
                    e1 = StringUtils.stripControlCodes(s.replace("- ", ""));
                } else {
                    e2 = StringUtils.stripControlCodes(s.replace("- ", ""));
                }
            }
            if (s.contains("Balance")) {
                bal = StringUtils.stripControlCodes(s.replace("Balance: ", ""));
            }
            if (s.contains("Income")) {
                inc = StringUtils.stripControlCodes(s.replace("Income: ", ""));
            }
        }
    }

    /**
     * Renders a dummy version for the position editor
     */
    public void renderDummy() {
        ScaledResolution sr = new ScaledResolution(mc);
        int x = (int) Config.feature.mlf.mlfInfoOffsetX;
        int y = (int) Config.feature.mlf.mlfInfoOffsetY;

        String[] colorParts = Config.feature.mlf.mlfInfoBackgroundColor.split(":");
        int alpha = Integer.parseInt(colorParts[1]); // Adjusted to match typical A:R:G:B
        int red = Integer.parseInt(colorParts[2]);
        int green = Integer.parseInt(colorParts[3]);
        int blue = Integer.parseInt(colorParts[4]);
        int bgColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Dummy text lines
        List<String> lines = new ArrayList<>();
        lines.add("Income: 100");
        lines.add("Balance: 500");
        lines.add("Events: ");
        lines.add("- Event 1");
        lines.add("- Event 2");

        // Calculate text dimensions
        int textWidth = 0;
        for (String line : lines) {
            int lineWidth = mc.fontRendererObj.getStringWidth(line);
            if (lineWidth > textWidth) textWidth = lineWidth;
        }
        textWidth += 4; // 2px padding on each side
        int textHeight = lines.size() * 9; // 8px font height + 1px spacing per line

        // Draw background matching text dimensions
        Gui.drawRect(x, y, x + textWidth, y + textHeight, bgColor);

        // Draw text
        for (int i = 0; i < lines.size(); i++) {
            mc.fontRendererObj.drawString(lines.get(i), x + 2, y + (i * 9), -1); // 2px left padding, 9px line spacing
        }
    }
}
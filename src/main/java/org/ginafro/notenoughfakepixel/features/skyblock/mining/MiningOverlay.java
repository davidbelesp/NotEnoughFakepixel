package org.ginafro.notenoughfakepixel.features.skyblock.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.features.Mining;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;

import java.util.ArrayList;
import java.util.List;

public class MiningOverlay {

    private static final int LINE_HEIGHT = 11;
    private static final int MINIMUM_WIDTH = 20;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!shouldShow()) return;

        draw(Mining.miningOverlayOffsetX, Mining.miningOverlayOffsetY, Mining.miningOverlayScale, false);
    }

    private boolean shouldShow() {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return false;
        if (!ScoreboardUtils.currentLocation.equals(Location.DWARVEN)) return false;
        return Mining.miningOverlay;
    }

    private void draw(float x, float y, float scale, boolean example) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, scale);

        List<String> lines = new ArrayList<>();
        getLines(lines, example);

        // Parse background color
        String[] colorParts = Mining.miningOverlayBackgroundColor.split(":");
        int alpha = Integer.parseInt(colorParts[0]);
        int red = Integer.parseInt(colorParts[1]);
        int green = Integer.parseInt(colorParts[2]);
        int blue = Integer.parseInt(colorParts[3]);
        int bgColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Draw background
        int width = (int) getWidth(scale, example);
        int height = (int) getHeight(scale, example);
        Gui.drawRect(0, 0, width, height, bgColor);

        // Draw text
        for (int i = 0; i < lines.size(); i++) {
            mc.fontRendererObj.drawString(lines.get(i), 2, i * LINE_HEIGHT, -1);
        }

        GlStateManager.popMatrix();
    }

    public void renderDummy() {
        draw(Mining.miningOverlayOffsetX, Mining.miningOverlayOffsetY, Mining.miningOverlayScale, true);
    }

    private void getLines(List<String> lines, boolean example) {
        if (example) {
            if (Mining.miningAbilityCooldown) lines.add("\u00a77Ability Cooldown: \u00a7r5s");
            if (Mining.miningMithrilPowder) lines.add("\u00a77Mithril Powder: \u00a721000");
            if (Mining.miningDrillFuel) lines.add("\u00a77Drill Fuel: \u00a7a50%");
            lines.add("\u00a77Titanium: \u00a7a80%");
            lines.add("\u00a77Mithril: \u00a7e50%");
        } else {
            if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
            if (!ScoreboardUtils.currentLocation.equals(Location.DWARVEN)) return;

            if (Mining.miningAbilityCooldown) lines.add("\u00a77Ability Cooldown: \u00a7r" + AbilityNotifier.cdSecondsRemaining());
            if (Mining.miningMithrilPowder) lines.add(formatMithrilPowder(TablistParser.mithrilPowder));
            if (Mining.miningDrillFuel) lines.add(DrillFuelParsing.getString());
            for (String commission : TablistParser.commissions) {
                lines.add(formatCommission(commission));
            }
        }
    }

    public float getWidth(float scale, boolean example) {
        float var = Math.max(getLongestCommission(example) * 6, MINIMUM_WIDTH);
        return var * scale;
    }

    public float getHeight(float scale, boolean example) {
        int variable = Mining.miningDrillFuel ? LINE_HEIGHT : 0;
        variable = Mining.miningMithrilPowder ? variable + LINE_HEIGHT : variable;
        variable = Mining.miningAbilityCooldown ? variable + LINE_HEIGHT : variable;
        int commissionCount = example ? 2 : TablistParser.commissions.size(); // 2 for dummy
        return (variable + (commissionCount * LINE_HEIGHT)) * scale;
    }

    private String formatMithrilPowder(long mithrilPowder) {
        return String.format("\u00a77Mithril Powder: \u00a72%d", mithrilPowder);
    }

    private String formatCommission(String commission) {
        Double percent = Double.parseDouble(commission.split(":")[1].replaceAll("[ %]", ""));
        String colorCode = percent <= 33 ? "\u00a7c" : percent <= 79 ? "\u00a7e" : "\u00a7a";
        return "\u00a77" + commission.split(":")[0] + ": " + colorCode + percent + "%";
    }

    private int getLongestCommission(boolean example) {
        if (example) return "Mithril Powder: 1000".length(); // Example longest line
        int longest = 0;
        for (String commission : TablistParser.commissions) {
            if (commission.length() > longest) {
                longest = commission.length();
            }
        }
        return longest < MINIMUM_WIDTH ? MINIMUM_WIDTH : longest;
    }
}
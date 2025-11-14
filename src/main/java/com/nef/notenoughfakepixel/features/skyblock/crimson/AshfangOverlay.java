package com.nef.notenoughfakepixel.features.skyblock.crimson;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class AshfangOverlay {

    private final Position position;

    public AshfangOverlay() {
        this.position = Config.feature.crimson.ashfangOverlayPos;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!shouldShow()) return;
        render();
    }

    /**
     * Renders the overlay at the configured position
     */
    public void render() {
        if (!shouldShow()) return;

        List<String> lines = new ArrayList<>();
        getLines(lines, false);

        // Get absolute x and y coordinates based on the position
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int x = position.getAbsX(sr, 100); // Width assumed as 100
        int y = position.getAbsY(sr, 20);  // Height assumed as 20

        // Render each line
        for (String line : lines) {
            Minecraft.getMinecraft().fontRendererObj.drawString(line, x, y, 0xFFFFFF); // White text
            y += 10;
        }
    }

    /**
     * Renders a dummy version for the position editor preview
     */
    public void renderDummy() {
        List<String> lines = new ArrayList<>();
        getLines(lines, true);

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int x = position.getAbsX(sr, 100);
        int y = position.getAbsY(sr, 20);

        for (int i = 0; i < lines.size(); i++) {
            Minecraft.getMinecraft().fontRendererObj.drawString(
                    lines.get(i), x, y + i * 10, 0xFFFFFF
            );
        }
    }

    /**
     * Populates the text lines to display
     */
    private void getLines(List<String> lines, boolean example) {
        if (Crimson.checkEssentials()) return; // Skip if essentials check fails
        if (Config.feature.crimson.crimsonAshfangOverlay) {
            lines.add("\u00a77Ashfang HP: \u00a7r" + formatAshfangHP(AshfangHelper.getAshfangHP()));
            lines.add("\u00a77Blazing souls: \u00a7r" + AshfangHelper.getBlazingSoulsCounter() + " / " + AshfangHelper.getHitsNeeded());
        }
    }

    /**
     * Determines if the overlay should be shown
     */
    private boolean shouldShow() {
        return Config.feature.crimson.crimsonAshfangOverlay &&
                Crimson.checkAshfangArea(new int[]{
                        Minecraft.getMinecraft().thePlayer.getPosition().getX(),
                        Minecraft.getMinecraft().thePlayer.getPosition().getY(),
                        Minecraft.getMinecraft().thePlayer.getPosition().getZ()
                });
    }

    /**
     * Formats Ashfang HP with color based on percentage
     */
    private static String formatAshfangHP(double hp) {
        double percentage = hp / 50_000_000; // Assuming max HP is 50M
        String prefix = percentage > 0.5 ? "§a" : percentage > 0.1 ? "§e" : "§c"; // Green, Yellow, Red
        return prefix + coolFormat(hp, 0);
    }

    /**
     * Formats numbers into a compact form (e.g., 1k, 1M)
     */
    private static final char[] SUFFIXES = {'k', 'M'};

    private static String coolFormat(double n, int iteration) {
        double d = ((double) (long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;
        return d < 1000 ?
                (isRound || d > 9.99 ? (int) d * 10 / 10 : d) + "" + SUFFIXES[iteration] :
                coolFormat(d, iteration + 1);
    }
}
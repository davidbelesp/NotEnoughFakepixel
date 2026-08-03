package com.nef.notenoughfakepixel.features.skyblock.garden;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.position.Position;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.overlays.Overlay;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RegisterEvents
public class GardenOverlay extends Overlay {

    @Override
    protected boolean useTextShadow() {
        return true;
    }

    @Override
    public boolean shouldShow() {
        return Config.feature != null && Config.feature.garden != null && Config.feature.garden.enable
                && SkyblockData.getCurrentLocation() == Location.GARDEN
                && !mc.gameSettings.keyBindPlayerList.isKeyDown()
                && !mc.gameSettings.showDebugInfo
                && !(mc.currentScreen instanceof GuiChat);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !shouldShow()) return;

        if (Config.feature.garden.pestsOverlay.enabled) {
            drawOverlay(getPestLines(), Config.feature.garden.pestsOverlay.pos,
                    Config.feature.garden.pestsOverlay.scale,
                    Config.feature.garden.pestsOverlay.backgroundColor);
        }
        if (Config.feature.garden.visitorsOverlay.enabled) {
            drawOverlay(getVisitorLines(), Config.feature.garden.visitorsOverlay.pos,
                    Config.feature.garden.visitorsOverlay.scale,
                    Config.feature.garden.visitorsOverlay.backgroundColor);
        }
        if (Config.feature.garden.cropMilestoneOverlay.enabled) {
            List<String> cropLines = getCropMilestoneLines();
            if (!cropLines.isEmpty()) {
                drawOverlay(cropLines, Config.feature.garden.cropMilestoneOverlay.pos,
                        Config.feature.garden.cropMilestoneOverlay.scale,
                        Config.feature.garden.cropMilestoneOverlay.backgroundColor);
            }
        }
    }

    @Override
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        lines.addAll(getPestLines());
        lines.addAll(getVisitorLines());
        lines.addAll(getCropMilestoneLines());
        return lines;
    }

    private void drawOverlay(List<String> lines, Position position, float scale, String backgroundColor) {
        ScaledResolution resolution = new ScaledResolution(mc);
        int width = (int) getWidth(scale, lines);
        int height = (int) getHeight(scale, lines);
        int x = position.getAbsX(resolution, width);
        int y = position.getAbsY(resolution, height);
        draw(lines, x, y, scale, backgroundColor);
    }

    private List<String> getPestLines() {
        List<String> lines = new ArrayList<>();
        int pestCount = 0;
        if (SkyblockData.getActivePests() != null) {
            for (Map.Entry<Integer, Integer> entry : SkyblockData.getActivePests().entrySet()) {
                if (entry.getValue() != null && entry.getValue() > 0) pestCount += entry.getValue();
            }
        }
        lines.add("\u00a7cPests: \u00a7f" + pestCount);
        return lines;
    }

    private List<String> getVisitorLines() {
        List<String> lines = new ArrayList<>();
        List<String> visitors = SkyblockData.getActiveVisitors() == null
                ? new ArrayList<>() : SkyblockData.getActiveVisitors();
        lines.add("\u00a7bVisitors \u00a7f(" + visitors.size() + ")");
        for (String visitor : visitors) {
            lines.add(" " + visitor);
        }
        return lines;
    }

    private List<String> getCropMilestoneLines() {
        return SkyblockData.getCropMilestone() == null
                ? new ArrayList<>() : new ArrayList<>(SkyblockData.getCropMilestone());
    }

    @Override
    public float getWidth(float scale, List<String> lines) {
        int longest = 0;
        for (String line : lines) {
            longest = Math.max(longest, net.minecraft.util.StringUtils.stripControlCodes(line).length());
        }
        return Math.max(longest * 5, MINIMUM_WIDTH);
    }

    @Override
    public float getHeight(float scale, List<String> lines) {
        return lines.size() * LINE_HEIGHT;
    }
}

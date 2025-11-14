package com.nef.notenoughfakepixel.features.skyblock.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.overlays.Overlay;
import com.nef.notenoughfakepixel.utils.ListUtils;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.variables.Location;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class MiningOverlay extends Overlay {

    private static final int LINE_HEIGHT = 11;
    private static final int MINIMUM_WIDTH = 20;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!shouldShow()) return;

        draw(
                Config.feature.mining.miningOverlayOffsetX,
                Config.feature.mining.miningOverlayOffsetY,
                Config.feature.mining.miningOverlayScale,
                Config.feature.mining.miningOverlayBackgroundColor
        );
    }

    @Override
    public boolean shouldShow() {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return false;
        if (!TablistParser.currentLocation.equals(Location.DWARVEN) && !TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS))  return false;
        if (Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isKeyDown()) return false;
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) return false;
        if (Config.feature.mining.miningOverlayHideOnChat && mc.currentScreen instanceof GuiChat) return false;
        if ( mc.gameSettings.showDebugInfo ) return false;
        return Config.feature.mining.miningOverlay;
    }

    @Override
    public List<String> getLines() {
        if (!shouldShow()) return ListUtils.of();
        List<String> lines = new ArrayList<>();

        if (Config.feature.mining.miningAbilityCooldown)
            lines.add("\u00a77Ability Cooldown: \u00a7r" + AbilityNotifier.cdSecondsRemaining());
        if (Config.feature.mining.miningMithrilPowder && TablistParser.currentLocation.equals(Location.DWARVEN)) lines.add(formatMithrilPowder(TablistParser.mithrilPowder));
        if (Config.feature.mining.miningGemstonePowder && TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS)) lines.add(formatGemstonePowder(TablistParser.gemstonePowder));
        if (Config.feature.mining.miningDrillFuel) lines.add(DrillFuelParsing.getString());
        if (Config.feature.mining.miningCommissions){
            for (String commission : TablistParser.commissions) {
                lines.add(formatCommission(commission));
            }
        }
        return lines;
    }

    @Override
    public float getWidth(float scale, List<String> lines) {
        float var = Math.max(getLongestLine(lines) * 5, MINIMUM_WIDTH);
        return var * scale;
    }

    @Override
    public float getHeight(float scale, List<String> lines) {
        return lines.size() * LINE_HEIGHT;
    }

    private String formatMithrilPowder(long mithrilPowder) {
        return String.format("\u00a77Mithril Powder: \u00a72%d", mithrilPowder);
    }

    private String formatGemstonePowder(long gemstonePowder) {
        return String.format("\u00a77Gemstone Powder: \u00a7d%d", gemstonePowder);
    }

    private String formatCommission(String commission) {

        String[] split = commission.split(":");
        try {
            if (split.length == 0) return commission;
            double percent = Double.parseDouble(split[1].replaceAll("[ %]", ""));
            String colorCode = percent <= 33 ? "\u00a7c" : percent <= 79 ? "\u00a7e" : "\u00a7a";
            return "\u00a77" + commission.split(":")[0] + ": " + colorCode + percent + "%";
        } catch (Exception e) {}
        return commission;
    }

}
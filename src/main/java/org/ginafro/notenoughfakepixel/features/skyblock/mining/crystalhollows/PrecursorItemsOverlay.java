package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.Overlay;
import org.ginafro.notenoughfakepixel.utils.InventoryUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.Area;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class PrecursorItemsOverlay extends Overlay {

    private static boolean hasSwitch = false;
    private static boolean hasSynthetic = false;
    private static boolean hasSuperlite = false;
    private static boolean hasRobotron = false;
    private static boolean hasElectron = false;
    private static boolean hasFTX = false;

    @Override
    public boolean shouldShow() {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return false;
        if (ScoreboardUtils.currentArea != Area.CH_LOST_PRECURSOR) return false;
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) return false;
        if (Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isKeyDown()) return false;
        if (Config.feature.mining.miningOverlayHideOnChat && mc.currentScreen instanceof GuiChat) return false;
        return Config.feature.mining.crystalShowAutomaton;
    }

    @Override
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();

        lines.add("\u00a79Synthetic Heart \u00a77- \u00a7" + (hasSynthetic? "a✔":"c✖"));
        lines.add("\u00a7bSuperlite Motor \u00a77- \u00a7" + (hasSuperlite? "a✔":"c✖"));
        lines.add("\u00a7eRobotron Reflector \u00a77- \u00a7" + (hasRobotron? "a✔":"c✖"));
        lines.add("\u00a7aElectron Transmitter \u00a77- \u00a7" + (hasElectron? "a✔":"c✖"));
        lines.add("\u00a7cFTX 3070 \u00a77- \u00a7" + (hasFTX? "a✔":"c✖"));
        lines.add("\u00a7dControl Switch \u00a77- \u00a7" + (hasSwitch? "a✔":"c✖"));

        return lines;
    }

    @Override
    public float getWidth(float scale, List<String> lines) {
        return MINIMUM_WIDTH + (25*6);
    }

    @Override
    public float getHeight(float scale, List<String> lines) {
        return LINE_HEIGHT * getLines().size();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!shouldShow()) return;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int width = (int) getWidth(Config.feature.mining.automatonOverlayScale, getLines());
        int height = (int) getHeight(Config.feature.mining.automatonOverlayScale, getLines());
        int x = Config.feature.mining.automatonOverlayPos.getAbsX(sr, width);
        int y = Config.feature.mining.automatonOverlayPos.getAbsY(sr, height);

        draw(
                x - ((float) width /2),
                y - ((float) height /2),
                Config.feature.mining.automatonOverlayScale,
                Config.feature.mining.miningOverlayBackgroundColor
        );
    }

    private int tickCounter = 0;
    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 330 == 0 && shouldShow()) {
            hasSynthetic = InventoryUtils.searchForItemInInventory("SYNTHETIC_HEART");
            hasElectron = InventoryUtils.searchForItemInInventory("ELECTRON_TRANSMITTER");
            hasSwitch = InventoryUtils.searchForItemInInventory("CONTROL_SWITCH");
            hasFTX = InventoryUtils.searchForItemInInventory("FTX_3070");
            hasRobotron = InventoryUtils.searchForItemInInventory("ROBOTRON_REFLECTOR");
            hasSuperlite = InventoryUtils.searchForItemInInventory("SUPERLITE_MOTOR");
            tickCounter = 0;
        }
    }
}

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
public class ScavengedToolsOverlay extends Overlay {

    private static boolean hasLapis = false;
    private static boolean hasGoldh = false;
    private static boolean hasEmera = false;
    private static boolean hasDiamo = false;

    private int tickCounter = 0;

    @Override
    public boolean shouldShow() {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return false;
        if (ScoreboardUtils.currentArea != Area.CH_MINES_OF_DIVAN) return false;
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) return false;
        if (Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isKeyDown()) return false;
        if (Config.feature.mining.miningOverlayHideOnChat && mc.currentScreen instanceof GuiChat) return false;
        return Config.feature.mining.scavengedOverlay;
    }

    @Override
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();

        lines.add("\u00a73Scavenged \u00a79Lapis Sword \u00a77-       \u00a7" + (hasLapis? "a✔":"c✖"));
        lines.add("\u00a73Scavenged \u00a76Gold Hammer \u00a77-       \u00a7" + (hasGoldh? "a✔":"c✖"));
        lines.add("\u00a73Scavenged \u00a7aEmerald Hammer \u00a77-  \u00a7" + (hasEmera? "a✔":"c✖"));
        lines.add("\u00a73Scavenged \u00a7bDiamond Axe \u00a77-       \u00a7" + (hasDiamo? "a✔":"c✖"));

        return lines;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 330 == 0 && shouldShow()) {
            hasLapis = InventoryUtils.searchForItemInInventory("DWARVEN_LAPIS_SWORD");
            hasGoldh = InventoryUtils.searchForItemInInventory("DWARVEN_GOLD_HAMMER");
            hasEmera = InventoryUtils.searchForItemInInventory("DWARVEN_EMERALD_HAMMER");
            hasDiamo = InventoryUtils.searchForItemInInventory("DWARVEN_DIAMOND_AXE");
            tickCounter = 0;
        }
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
        int width = (int) getWidth(Config.feature.mining.scavengedOverlayScale, getLines());
        int height = (int) getHeight(Config.feature.mining.scavengedOverlayScale, getLines());
        int x = Config.feature.mining.scavengedOverlayPos.getAbsX(sr, width);
        int y = Config.feature.mining.scavengedOverlayPos.getAbsY(sr, height);

        draw(
                x - ((float) width /2),
                y - ((float) height /2),
                Config.feature.mining.scavengedOverlayScale,
                Config.feature.mining.miningOverlayBackgroundColor
        );
    }


}

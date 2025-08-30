package org.ginafro.notenoughfakepixel;

import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import org.ginafro.notenoughfakepixel.alerts.AlertManagementGui;
import org.ginafro.notenoughfakepixel.config.features.*;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.config.gui.config.ConfigEditor;
import org.ginafro.notenoughfakepixel.config.gui.core.GuiElement;
import org.ginafro.notenoughfakepixel.config.gui.core.GuiScreenElementWrapper;
import org.ginafro.notenoughfakepixel.config.gui.core.config.Position;
import org.ginafro.notenoughfakepixel.config.gui.core.config.annotations.Category;
import org.ginafro.notenoughfakepixel.config.gui.core.config.gui.GuiPositionEditor;
import org.ginafro.notenoughfakepixel.events.handlers.RepoHandler;
import org.ginafro.notenoughfakepixel.features.capes.gui.CapeGui;
import org.ginafro.notenoughfakepixel.features.duels.Duels;
import org.ginafro.notenoughfakepixel.features.duels.KDCounter;
import org.ginafro.notenoughfakepixel.features.mlf.Info;
import org.ginafro.notenoughfakepixel.features.mlf.Map;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.terminals.TerminalSimulator;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.stats.PositionEditorScreen;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.customaliases.AliasManagementGui;
import org.ginafro.notenoughfakepixel.utils.ItemUtils;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;

import java.awt.*;
import java.awt.datatransfer.Clipboard;

public class Configuration {

    private void editOverlay(String activeConfig, int width, int height, Position position) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiPositionEditor(position, width, height, () -> {
        }, () -> {
        }, () -> Config.screenToOpen = new GuiScreenElementWrapper(new ConfigEditor(Config.feature, activeConfig))));
    }

    public void executeRunnable(String runnableId) {
        String activeConfigCategory = null;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiScreenElementWrapper) {
            GuiScreenElementWrapper wrapper = (GuiScreenElementWrapper) Minecraft.getMinecraft().currentScreen;
            GuiElement element = wrapper.element;
            if (element instanceof ConfigEditor) {
                activeConfigCategory = ((ConfigEditor) element).getSelectedCategoryName();
            }
        }
        if ("editAshfangPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 100, 20, Config.feature.crimson.ashfangOverlayPos);
        }
        if ("editDungeonsMapPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 128, 128, Config.feature.dungeons.dungeonsMapPos);
        }
        if ("editMlfInfoPosition".equals(runnableId)) {
            Position tempPosition = new Position((int) Config.feature.mlf.mlfInfoOffsetX, (int) Config.feature.mlf.mlfInfoOffsetY);
            Minecraft.getMinecraft().displayGuiScreen(
                    new GuiPositionEditor(
                            tempPosition,
                            35, 60,
                            () -> new Map().renderDummy(),
                            () -> {
                                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                                Config.feature.mlf.mlfInfoOffsetX = tempPosition.getAbsX(sr, 35);
                                Config.feature.mlf.mlfInfoOffsetY = tempPosition.getAbsY(sr, 60);
                            },
                            () -> {
                            }
                    )
            );
        }
        if ("editKdCounterPosition".equals(runnableId)) {
            Position tempPosition = new Position((int) Config.feature.duels.kdCounterOffsetX, (int) Config.feature.duels.kdCounterOffsetY);
            KDCounter kdCounter = new KDCounter();
            Minecraft.getMinecraft().displayGuiScreen(
                    new GuiPositionEditor(
                            tempPosition,
                            (int) kdCounter.getWidth(), (int) kdCounter.getHeight(),
                            kdCounter::renderDummy,
                            () -> {
                                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                                Config.feature.duels.kdCounterOffsetX = tempPosition.getAbsX(sr, (int) kdCounter.getWidth());
                                Config.feature.duels.kdCounterOffsetY = tempPosition.getAbsY(sr, (int) kdCounter.getHeight());
                            },
                            () -> {
                            }
                    )
            );
        }
        if ("editScoreOverlayPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 150, 115, Config.feature.dungeons.scoreOverlayPos);
        }
        if ("resetItemValues".equals(runnableId)) {
            Config.feature.qol.resetItemValues();
        }
        if ("editSlayerOverlayPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 100, 20, Config.feature.slayer.slayerBossHPPos);
        }
        if ("nefAlerts".equals(runnableId)) {
            Minecraft.getMinecraft().displayGuiScreen(new AlertManagementGui());
        }
        if ("nefAlias".equals(runnableId)) {
            Minecraft.getMinecraft().displayGuiScreen(new AliasManagementGui());
        }
        if ("termSim".equals(runnableId)) {
            Minecraft.getMinecraft().displayGuiScreen(new TerminalSimulator());
        }
        if ("slotReset".equals(runnableId)) {
            NotEnoughFakepixel.resetLockedSlots();
        }
        if ("nefCapes".equals(runnableId)) {
            Minecraft.getMinecraft().displayGuiScreen(new CapeGui());
        }
        if ("editTerminalTrackerPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 150, 60, Config.feature.dungeons.terminalTrackerPos);
        }
        if ("editWarpHelperPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 100, 30, Config.feature.diana.warpHelperPos);
        }
        if("nefButtons".equals(runnableId)){
            //Minecraft.getMinecraft().displayGuiScreen(new InventoryEditor());
        }
        // Debug runnables
        if ("logLocation".equals(runnableId)) {
            Logger.log(TablistParser.currentLocation);
        }
        if ("logScoreboard".equals(runnableId)) {
            ScoreboardUtils.getScoreboardLines().forEach(Logger::log);
        }
        if ("logIsInSkyblock".equals(runnableId)) {
            Logger.log("Current Gamemode: " + ScoreboardUtils.currentGamemode + " | Is in Skyblock: " + ScoreboardUtils.currentGamemode.isSkyblock());
        }
        if("statEditor".equals(runnableId)){
            Minecraft.getMinecraft().displayGuiScreen(new PositionEditorScreen());
        }
        if ("showAPI".equals(runnableId)) {
            String data = RepoHandler.getJson("fairysouls");
            if (data != null) {
                // copy to clipboard
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                clip.setContents(new java.awt.datatransfer.StringSelection(data), null);
                Logger.log("Copied API to clipboard! Length: " + data.length());
            }
        }
        if ("showSBID".equals(runnableId)) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {
                ItemStack item = player.getHeldItem();
                if (item != null) {
                    Logger.log(ItemUtils.getInternalName(item));
                } else {
                    Logger.logError("No Held Item");
                }
            } else {
                Logger.logError("No Player");
            }
        }
    }

    @Expose
    @Category(name = "Quality of Life", desc = "Quality of Life settings.")
    public QualityOfLife qol = new QualityOfLife();

    @Expose
    @Category(name = "Dungeons", desc = "Dungeons settings.")
    public Dungeons dungeons = new Dungeons();

    @Expose
    @Category(name = "Diana", desc = "Diana settings.")
    public DianaF diana = new DianaF();

    @Expose
    @Category(name = "Slayer", desc = "Slayer settings.")
    public Slayer slayer = new Slayer();

    @Expose
    @Category(name = "Experimentation Table", desc = "Experimentation Table settings.")
    public Experimentation experimentation = new Experimentation();

    @Expose
    @Category(name = "Chocolate Factory", desc = "Chocolate Factory settings.")
    public ChocolateFactory chocolateFactory = new ChocolateFactory();

    @Expose
    @Category(name = "Crimson", desc = "Crimson settings.")
    public Crimson crimson = new Crimson();

    @Expose
    @Category(name = "Mining", desc = "Mining settings.")
    public Mining mining = new Mining();

    @Expose
    @Category(name = "Fishing", desc = "Fishing settings.")
    public Fishing fishing = new Fishing();

    @Expose
    @Category(name = "My Little Farm", desc = "Mlf settings.")
    public Info mlf = new Info();

    @Expose
    @Category(name = "Slot Locking", desc = "Slot Locking Settings")
    public SlotLocking sl = new SlotLocking();

    @Expose
    @Category(name = "Duels", desc = "Duels settings.")
    public Duels duels = new Duels();

    @Expose
    @Category(name = "Misc", desc = "Misc features.")
    public Misc misc = new Misc();

    @Expose
    @Category(name = "Overlays", desc = "GUI Overlays")
    public Overlays overlays = new Overlays();

    @Expose
    @Category(name = "Debug", desc = "Debug settings.")
    public Debug debug = new Debug();

    public static boolean isPojav() {
        return Config.feature.debug.forcePojav || (System.getProperty("os.name").contains("Android") || System.getProperty("os.name").contains("Linux"));
    }
}
package com.nef.notenoughfakepixel;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.alerts.AlertManagementGui;
import com.nef.notenoughfakepixel.config.features.*;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.config.ConfigEditor;
import com.nef.notenoughfakepixel.config.gui.core.GuiElement;
import com.nef.notenoughfakepixel.config.gui.core.GuiScreenElementWrapper;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.Category;
import com.nef.notenoughfakepixel.config.gui.core.config.gui.GuiPositionEditor;
import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import com.nef.notenoughfakepixel.features.capes.gui.CapeGui;
import com.nef.notenoughfakepixel.features.duels.Duels;
import com.nef.notenoughfakepixel.features.duels.KDCounter;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals.TerminalSimulator;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.CrystalHollowsMap;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.PrecursorItemsOverlay;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.ScavengedToolsOverlay;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.WormSpawnTimer;
import com.nef.notenoughfakepixel.features.skyblock.overlays.stats.PositionEditorScreen;
import com.nef.notenoughfakepixel.features.skyblock.qol.customaliases.AliasManagementGui;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.Logger;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

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
        if ("editCrystalHollowsMapPos".equals(runnableId)) {
            int width = Config.feature.mining.miningCrystalMapWidth + ((CrystalHollowsMap.getMARGIN_PX()*2));
            editOverlay(activeConfigCategory, width, width , Config.feature.mining.crystalMapPos);
        }
        if ("editScavengedOverlayPos".equals(runnableId)) {
            int width = (int)Math.abs((ScavengedToolsOverlay.MINIMUM_WIDTH + (25*6)) * Config.feature.mining.scavengedOverlayScale);
            int height = (int)Math.abs((ScavengedToolsOverlay.LINE_HEIGHT * 4) * Config.feature.mining.scavengedOverlayScale);
            editOverlay(activeConfigCategory, width, height, Config.feature.mining.scavengedOverlayPos);
        }
        if ("editAutomatonOverlayPos".equals(runnableId)) {
            int width = (int)Math.abs((PrecursorItemsOverlay.MINIMUM_WIDTH + (25*6)) * Config.feature.mining.automatonOverlayScale);
            int height = (int)Math.abs((PrecursorItemsOverlay.LINE_HEIGHT * 6) * Config.feature.mining.automatonOverlayScale);
            editOverlay(activeConfigCategory, width, height, Config.feature.mining.automatonOverlayPos);
        }
        if ("editWormTimerPos".equals(runnableId)) {
            editOverlay(activeConfigCategory, (int) Math.abs(38 * Config.feature.mining.wormTimerScale), (int) Math.abs(9 * Config.feature.mining.wormTimerScale), Config.feature.mining.wormTimerPos);
        }
        if ("editEggTimerPos".equals(runnableId)) {
            editOverlay(activeConfigCategory, (int) Math.abs(38 * Config.feature.chocolateFactory.eggTimerScale), (int) Math.abs(9 * Config.feature.chocolateFactory.eggTimerScale), Config.feature.chocolateFactory.eggTimerPos);
        }
        if ("editDarkAHTimerPos".equals(runnableId)) {
            editOverlay(activeConfigCategory, (int) Math.abs(38 * Config.feature.qol.darkAHTimerScale), (int) Math.abs(9 * Config.feature.qol.darkAHTimerScale), Config.feature.qol.darkAhTimerPos);
        }
        // Debug runnables
        if ("logLocation".equals(runnableId)) {
            Logger.log(SkyblockData.getCurrentLocation());
        }
        if ("logScoreboard".equals(runnableId)) {
            ScoreboardUtils.getScoreboardLines().forEach(Logger::log);
        }
        if ("logIsInSkyblock".equals(runnableId)) {
            Logger.log("Current Gamemode: " + SkyblockData.getCurrentGamemode() + " | Is in Skyblock: " +  SkyblockData.getCurrentGamemode().isSkyblock());
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
        if ("triggerTimers".equals(runnableId)) {
            WormSpawnTimer.setGoalEpochMs(System.currentTimeMillis() + 30000);
            Logger.log("Set worm spawn to 30 seconds from now");
        }
        if ("logSbData".equals(runnableId)) {
            Logger.log("\u00a7c=============================");
            Logger.log("Gamemode: \u00a7f" + SkyblockData.getCurrentGamemode().toString());
            Logger.log("Using Profile: \u00a7f" + SkyblockData.getCurrentProfile());
            Logger.log("\u00a72Location\u00a7f:");
            Logger.log(" - Current Location: \u00a7f" + SkyblockData.getCurrentLocation());
            Logger.log(" - Current Area: \u00a7f" + SkyblockData.getCurrentArea());
            Logger.log("\u00a74Slayer\u00a7f:");
            Logger.log(" - Has Slayer Active: \u00a7f" + SkyblockData.isSlayerActive());
            Logger.log(" - Current Slayer: \u00a7f" + SkyblockData.getCurrentSlayer());
            Logger.log("\u00a73Mining\u00a7f:");
            Logger.log(" - Mithril Powder: \u00a7f" + SkyblockData.getMithrilPowder());
            Logger.log(" - Gemstone Powder: \u00a7f" + SkyblockData.getGemstonePowder());
            Logger.log(" - Heat: \u00a7f" + SkyblockData.getHeat());
            Logger.log("\u00a7dTime\u00a7f:");
            Logger.log(" - Skyblock Hour: \u00a7f" + SkyblockData.getSbHour());
            Logger.log(" - Skyblock Minutes: \u00a7f" + SkyblockData.getSbMinute());
            Logger.log(" - AM/PM: \u00a7f" + (SkyblockData.isAm() ? "AM" : "PM"));
            Logger.log(" - Current Season: \u00a7f" + SkyblockData.getSeason());
            Logger.log("\u00a7c=============================");

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
    @Category(name = "Slot Locking", desc = "Slot Locking Settings")
    public SlotLocking sl = new SlotLocking();

    @Expose
    @Category(name = "Duels", desc = "Duels settings.")
    public Duels duels = new Duels();

    @Expose
    @Category(name = "Misc", desc = "Misc features.")
    public Misc misc = new Misc();

    @Expose
    @Category(name = "Waypoints", desc = "Waypoints settings.")
    public Waypoints waypoints = new Waypoints();

    @Expose
    @Category(name = "Overlays", desc = "GUI Overlays")
    public Overlays overlays = new Overlays();

    @Expose
    @Category(name = "Accessories", desc = "Accessories settings.")
    public Accessories accessories = new Accessories();

    @Expose
    @Category(name = "Debug", desc = "Debug settings.")
    public Debug debug = new Debug();

    public static boolean isPojav() {
        return Config.feature.debug.forcePojav || (System.getProperty("os.name").contains("Android") || System.getProperty("os.name").contains("Linux"));
    }
}
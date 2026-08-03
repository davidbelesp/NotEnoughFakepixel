package com.nef.notenoughfakepixel;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.alerts.AlertManagementGui;
import com.nef.notenoughfakepixel.config.ConfigRunnables;
import com.nef.notenoughfakepixel.config.features.*;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.position.Position;
import com.nef.notenoughfakepixel.config.position.GuiPositionEditor;
import com.nef.notenoughfakepixel.features.capes.gui.CapeGui;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals.TerminalSimulator;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.CrystalHollowsMap;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.PrecursorItemsOverlay;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.ScavengedToolsOverlay;
import com.nef.notenoughfakepixel.features.skyblock.overlays.stats.PositionEditorScreen;
import com.nef.notenoughfakepixel.features.skyblock.qol.customaliases.AliasManagementGui;
import com.nef.notenoughfakepixel.features.skyblock.slayers.SlayerOverlay;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import net.minecraft.client.Minecraft;
import java.awt.*;
import java.net.URI;

public class Configuration extends io.github.notenoughupdates.moulconfig.Config {

    private void editOverlay(String activeConfig, int width, int height, Position position) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiPositionEditor(position, width, height, () -> {
        }, () -> {
        }, () -> Config.screenToOpen = Config.createMoulConfigScreen(activeConfig)));
    }

    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ignored) {
        }
    }

    public void executeRunnable(String runnableId) {
        String activeConfigCategory = null;

        if (runnableId.startsWith("debug_")) {
            ConfigRunnables.runDebugRunnable(runnableId);
        }

        if (runnableId.startsWith("exec_")) {
            ConfigRunnables.runExecutableRunnable(runnableId);
        }

        if ("editAshfangPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 100, 20, Config.feature.crimson.ashfangSettings.ashfangOverlayPos);
        }
        if ("editDungeonsMapPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 128, 128, Config.feature.dungeons.map.dungeonsMapPos);
        }
        if ("editScoreOverlayPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 150, 115, Config.feature.dungeons.scoreSecrets.scoreOverlayPos);
        }
        if ("resetItemValues".equals(runnableId)) {
            Config.feature.qol.resetItemValues();
        }
        if ("editSlayerOverlayPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 100, 20, Config.feature.slayer.bossSettings.slayerBossHPPos);
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
        if ("openNefDiscord".equals(runnableId)) {
            openUrl("https://discord.gg/xjFy4GQmbp");
        }
        if ("openNefGithub".equals(runnableId)) {
            openUrl("https://github.com/davidbelesp/NotEnoughFakepixel");
        }
        if ("openNefForge".equals(runnableId)) {
            openUrl("https://github.com/MinecraftForge/MinecraftForge");
        }
        if ("openNefMixin".equals(runnableId)) {
            openUrl("https://github.com/SpongePowered/Mixin/");
        }
        if ("openNefMoulConfig".equals(runnableId)) {
            openUrl("https://github.com/NotEnoughUpdates/MoulConfig");
        }
        if ("openNefLombok".equals(runnableId)) {
            openUrl("https://projectlombok.org/");
        }
        if ("openNefReflections".equals(runnableId)) {
            openUrl("https://github.com/ronmamo/reflections");
        }
        if ("openNefArchitecturyLoom".equals(runnableId)) {
            openUrl("https://moddev.nea.moe/");
        }
        if ("editTerminalTrackerPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 150, 60, Config.feature.dungeons.terminals.terminalTrackerPos);
        }
        if ("editWarpHelperPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 100, 30, Config.feature.diana.burrowSettings.warpHelperPos);
        }
        if("nefButtons".equals(runnableId)){
            //Minecraft.getMinecraft().displayGuiScreen(new InventoryEditor());
        }
        if ("editCrystalHollowsMapPos".equals(runnableId)) {
            int width = Config.feature.mining.crystalHollowsMap.miningCrystalMapWidth + ((CrystalHollowsMap.getMARGIN_PX()*2));
            editOverlay(activeConfigCategory, width, width , Config.feature.mining.crystalHollowsMap.crystalMapPos);
        }
        if ("editScavengedOverlayPos".equals(runnableId)) {
            int width = (int)Math.abs((ScavengedToolsOverlay.MINIMUM_WIDTH + (25*6)) * Config.feature.mining.scavengedOverlaySettings.scavengedOverlayScale);
            int height = (int)Math.abs((ScavengedToolsOverlay.LINE_HEIGHT * 4) * Config.feature.mining.scavengedOverlaySettings.scavengedOverlayScale);
            editOverlay(activeConfigCategory, width, height, Config.feature.mining.scavengedOverlaySettings.scavengedOverlayPos);
        }
        if ("editAutomatonOverlayPos".equals(runnableId)) {
            int width = (int)Math.abs((PrecursorItemsOverlay.MINIMUM_WIDTH + (25*6)) * Config.feature.mining.automatonOverlaySettings.automatonOverlayScale);
            int height = (int)Math.abs((PrecursorItemsOverlay.LINE_HEIGHT * 6) * Config.feature.mining.automatonOverlaySettings.automatonOverlayScale);
            editOverlay(activeConfigCategory, width, height, Config.feature.mining.automatonOverlaySettings.automatonOverlayPos);
        }
        if ("editWormTimerPos".equals(runnableId)) {
            editOverlay(activeConfigCategory, (int) Math.abs(38 * Config.feature.mining.wormTimerSettings.wormTimerScale), (int) Math.abs(9 * Config.feature.mining.wormTimerSettings.wormTimerScale), Config.feature.mining.wormTimerSettings.wormTimerPos);
        }
        if ("editEggTimerPos".equals(runnableId)) {
            editOverlay(activeConfigCategory, (int) Math.abs(38 * Config.feature.chocolateFactory.eggTimerScale), (int) Math.abs(9 * Config.feature.chocolateFactory.eggTimerScale), Config.feature.chocolateFactory.eggTimerPos);
        }
        if ("editDarkAHTimerPos".equals(runnableId)) {
            editOverlay(activeConfigCategory, (int) Math.abs(38 * Config.feature.qol.darkAuctionTimerSettings.darkAHTimerScale), (int) Math.abs(9 * Config.feature.qol.darkAuctionTimerSettings.darkAHTimerScale), Config.feature.qol.darkAuctionTimerSettings.darkAhTimerPos);
        }
        if ("editGardenPestsOverlayPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 100, 20, Config.feature.garden.pestsOverlay.pos);
        }
        if ("editGardenVisitorsOverlayPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 150, 70, Config.feature.garden.visitorsOverlay.pos);
        }
        if ("editGardenCropMilestoneOverlayPosition".equals(runnableId)) {
            editOverlay(activeConfigCategory, 170, 45, Config.feature.garden.cropMilestoneOverlay.pos);
        }
        if ("editSlayerOverlayPos".equals(runnableId)) {
            int width = (int)Math.abs((SlayerOverlay.MINIMUM_WIDTH + (15*8)) * Config.feature.slayer.slayerOverlaySettings.slayerOverlayScale);
            int height = (int)Math.abs((SlayerOverlay.LINE_HEIGHT * 6) * Config.feature.slayer.slayerOverlaySettings.slayerOverlayScale);
            editOverlay(activeConfigCategory, width, height, Config.feature.slayer.slayerOverlaySettings.slayerOverlayPos);
        }
        if("statEditor".equals(runnableId)){
            Minecraft.getMinecraft().displayGuiScreen(new PositionEditorScreen());
        }
    }

    @Override
    public void executeRunnable(int runnableId) {
        switch (runnableId) {
            case 1: executeRunnable("editDungeonsMapPosition"); break;
            case 2: executeRunnable("editScoreOverlayPosition"); break;
            case 3: executeRunnable("editTerminalTrackerPosition"); break;
            case 4: executeRunnable("exec_fairySoulsReset"); break;
            case 5: executeRunnable("slotReset"); break;
            case 6: executeRunnable("editAshfangPosition"); break;
            case 7: executeRunnable("editSlayerOverlayPos"); break;
            case 8: executeRunnable("editSlayerOverlayPosition"); break;
            case 9: executeRunnable("statEditor"); break;
            case 10: executeRunnable("editEggTimerPos"); break;
            case 11: executeRunnable("termSim"); break;
            case 12: executeRunnable("debug_logLocation"); break;
            case 13: executeRunnable("debug_logIsInSkyblock"); break;
            case 14: executeRunnable("debug_logScoreboard"); break;
            case 15: executeRunnable("debug_showAPI"); break;
            case 16: executeRunnable("debug_showSBID"); break;
            case 17: executeRunnable("debug_triggerTimers"); break;
            case 18: executeRunnable("debug_logSbData"); break;
            case 19: executeRunnable("editWarpHelperPosition"); break;
            case 20: executeRunnable("editCrystalHollowsMapPos"); break;
            case 21: executeRunnable("editScavengedOverlayPos"); break;
            case 22: executeRunnable("editAutomatonOverlayPos"); break;
            case 23: executeRunnable("editWormTimerPos"); break;
            case 24: executeRunnable("nefAlerts"); break;
            case 25: executeRunnable("nefAlias"); break;
            case 26: executeRunnable("resetItemValues"); break;
            case 27: executeRunnable("editDarkAHTimerPos"); break;
            case 28: executeRunnable("editGardenPestsOverlayPosition"); break;
            case 29: executeRunnable("editGardenVisitorsOverlayPosition"); break;
            case 30: executeRunnable("editGardenCropMilestoneOverlayPosition"); break;
            case 32: executeRunnable("openNefDiscord"); break;
            case 33: executeRunnable("openNefGithub"); break;
            case 34: executeRunnable("openNefForge"); break;
            case 35: executeRunnable("openNefMixin"); break;
            case 36: executeRunnable("openNefMoulConfig"); break;
            case 37: executeRunnable("openNefLombok"); break;
            case 38: executeRunnable("openNefReflections"); break;
            case 39: executeRunnable("openNefArchitecturyLoom"); break;
            default: break;
        }
    }

    @Expose
    @Category(name = "About", desc = "")
    public About about = new About();

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
    @Category(name = "Garden", desc = "Garden settings.")
    public Garden garden = new Garden();

    @Expose
    @Category(name = "Fishing", desc = "Fishing settings.")
    public Fishing fishing = new Fishing();

    @Expose
    @Category(name = "Slot Locking", desc = "Slot Locking Settings")
    public SlotLocking sl = new SlotLocking();

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
    @Category(name = "Pets", desc = "Pets settings.")
    public Pets pets = new Pets();

    @Expose
    @Category(name = "Debug", desc = "Debug settings.")
    public Debug debug = new Debug();

    public static boolean isPojav() {
        return Config.feature.debug.forcePojav || (System.getProperty("os.name").contains("Android") || System.getProperty("os.name").contains("Linux"));
    }

    @Override
    public io.github.notenoughupdates.moulconfig.common.text.StructuredText getTitle() {
        return io.github.notenoughupdates.moulconfig.common.text.StructuredText.of("NotEnoughFakepixel");
    }

    @Override
    public void saveNow() {
        Config.saveConfig();
    }
}


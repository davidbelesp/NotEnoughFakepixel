package com.nef.notenoughfakepixel.config.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nef.notenoughfakepixel.Configuration;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.env.registers.RegisterKeybind;
import com.nef.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import com.nef.notenoughfakepixel.config.gui.annotations.ConfigTitleDisplay;
import com.nef.notenoughfakepixel.config.gui.editors.GuiOptionEditorTitleDisplay;
import io.github.notenoughupdates.moulconfig.gui.GuiScreenElementWrapper;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@RegisterEvents
public class Config {

    public static Configuration feature;
    private static File configFile;
    public static File configDirectory = new File("config/Notenoughfakepixel");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public static void init() {
        configFile = new File(configDirectory, "config.json");
        loadConfig();
    }

    private static void loadConfig() {
        feature = null;
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8))) {
                JsonObject config = new JsonParser().parse(reader).getAsJsonObject();
                migrateDungeonsConfig(config);
                migrateAccordionCategoryConfig(config);
                feature = gson.fromJson(config, Configuration.class);
            } catch (Exception exception) {
                System.err.println("[NotEnoughFakepixel] Failed to load config " + configFile.getAbsolutePath());
                exception.printStackTrace();
                backupCorruptedConfig();
            }
        }
        if (feature == null) {
            feature = new Configuration();
        }
        feature.fishing.migrateLegacyOptions();
        saveMainConfig();
    }

    private static void migrateDungeonsConfig(JsonObject config) {
        JsonElement dungeonsElement = config.get("dungeons");
        if (dungeonsElement == null || !dungeonsElement.isJsonObject()) return;

        JsonObject dungeons = dungeonsElement.getAsJsonObject();
        moveOptions(dungeons, "general",
                "dungeonsIsPaul",
                "dungeonsAutoCloseChests",
                "dungeonsKeyTracers",
                "dungeonsMuteIrrelevantMessages",
                "dungeonsSalvageItemsPrevention",
                "dungeonsLividFinder",
                "dungeonsLividFinderRender",
                "dungeonsShowOpenedChests",
                "dungeonsSpiritLeapGUI",
                "dungeonsLeapAnnounce",
                "dungeonsSpiritBow",
                "dungeonsBloodReady");
        moveOptions(dungeons, "map",
                "dungeonsMap",
                "dungeonsMapBorderColor",
                "dungeonsMapScale",
                "dungeonsRotateMap",
                "dungeonsMapPos",
                "editDungeonsMapPositionButton");
        moveOptions(dungeons, "puzzles",
                "dungeonsThreeWeirdos",
                "dungeonsCreeper",
                "dungeonsBoulderSolver",
                "dungeonsSilverfishSolver",
                "dungeonsWaterSolver",
                "dungeonsTeleportMaze");
        moveOptions(dungeons, "mobs",
                "dungeonsFelMob",
                "dungeonsFelColor",
                "dungeonsBatMobs",
                "dungeonsBatColor",
                "dungeonsStarredMobs",
                "dungeonsStarredBoxColor",
                "dungeonsStarredMobsEsp",
                "dungeonsWithermancerColor",
                "dungeonsZombieCommanderColor",
                "dungeonsSkeletonMasterColor",
                "dungeonsStormyColor");
        moveOptions(dungeons, "terminals",
                "dungeonsTerminalTracker",
                "dungeonsTerminalTrackerScale",
                "dungeonsTerminalTrackerColor",
                "terminalTrackerPos",
                "editTerminalTrackerPositionButton",
                "dungeonsCustomGuiClickIn",
                "dungeonsCustomGuiColors",
                "dungeonsCustomGuiMaze",
                "dungeonsCustomGuiStartsWith",
                "dungeonsCustomGuiPanes",
                "dungeonsTerminalsScale",
                "dungeonsTerminalStartsWithSolver",
                "dungeonsTerminalSelectColorsSolver",
                "dungeonsTerminalClickInOrderSolver",
                "dungeonsTerminalMazeSolver",
                "dungeonsTerminalCorrectPanesSolver",
                "dungeonsTerminalHideIncorrect",
                "dungeonsPreventMissclicks",
                "dungeonsHideTooltips",
                "dungeonsFirstDeviceSolver",
                "dungeonsThirdDeviceSolver",
                "dungeonsCorrectColor",
                "dungeonsAlternativeColor",
                "dungeonsTerminalWaypoints");
        moveOptions(dungeons, "scoreSecrets",
                "dungeonsSPlusNotifier",
                "dungeonsScoreOverlay",
                "dungeonsScoreSimple",
                "scoreOverlayScale",
                "scoreOverlayBackgroundColor",
                "scoreOverlayPos",
                "editScoreOverlayPositionButton",
                "dungeonsSNotifier",
                "dungeonsSPlusMessage",
                "dungeonsSPlusCustom",
                "dungeonsItemSecretsDisplay",
                "dungeonsItemSecretsColor",
                "dungeonsItemSecretsBig",
                "dungeonsScaleItemDrop");
        moveOptions(dungeons, "masterMode7",
                "m7Relics",
                "distBox",
                "dragOutline");
    }

    private static void migrateAccordionCategoryConfig(JsonObject config) {
        JsonObject crimson = getObject(config, "crimson");
        if (crimson != null) {
            moveOptions(crimson, "bossNotifiers",
                    "crimsonBladesoulNotifier", "crimsonMageOutlawNotifier", "crimsonAshfangNotifier", "crimsonBarbarianDukeXNotifier");
            moveOptions(crimson, "ashfangSettings",
                    "crimsonAshfangWaypoint", "crimsonAshfangWaypointColor", "crimsonAshfangHitboxes", "crimsonAshfangMuteChat",
                    "crimsonAshfangMuteSound", "crimsonAshfangHurtSound", "editAshOverlayPositionButton", "crimsonAshfangOverlay",
                    "ashfangOverlayPos", "crimsonGravityOrbWaypoint", "crimsonBlazingSoulWaypointColor");
        }

        JsonObject debug = getObject(config, "debug");
        if (debug != null) {
            moveOptions(debug, "loggers",
                    "logLocationButton", "logIsInSkyblock", "logScoreboardButton", "showAPIButton",
                    "showSBIDButton", "triggerTimers", "logSkyblockData");
        }

        JsonObject diana = getObject(config, "diana");
        if (diana != null) {
            moveOptions(diana, "burrowSettings",
                    "dianaBurrowGuess", "dianaTracerBurrowGuess", "dianaGuessBurrowTracerColor", "dianaWarpHelper",
                    "warpKeybind", "warpHelperScale", "dianaWarpDa", "dianaWarpMuseum", "dianaWarpCrypts",
                    "dianaWarpCastle", "editWarpHelperPositionButton", "dianaShowWaypointsBurrows", "dianaEmptyBurrowColor",
                    "dianaShowLabelsWaypoints", "dianaMobBurrowColor", "dianaShowTracersWaypoints", "dianaTreasureBurrowColor",
                    "warpHelperPos");
            moveOptions(diana, "mobSettings",
                    "dianaGaiaConstruct", "dianaGaiaHittableColor", "dianaSiamese",
                    "dianaGaiaUnhittableColor", "dianaSiameseHittableColor");
        }

        JsonObject mining = getObject(config, "mining");
        if (mining != null) {
            moveOptions(mining, "dwarvenMines", "miningPuzzlerSolver", "miningShowGhosts", "miningDisableDonEspresso");
            moveOptions(mining, "miningOverlaySettings",
                    "miningOverlayOffsetX", "miningOverlayOffsetY", "miningOverlayScale", "miningOverlayBackgroundColor",
                    "miningAbilityCooldown", "miningMithrilPowder", "miningGemstonePowder", "miningDrillFuel", "miningCommissions");
            moveOptions(mining, "crystalHollows",
                    "scavengedOverlay", "miningCrystalMap", "crystalMetalDetector", "crystalFullBlockPane", "crystalWaypoints",
                    "crystalShowAutomaton", "crystalPowderNotifier", "crystalWormNotifier", "lockedTreasureChest",
                    "wormTimerCooldown", "crystalHeatNotifier", "crystalHeatLevel", "chestTracker", "chestTrackerClearKey",
                    "chWaypointsGUI", "chNewWaypointGUI", "miningCrystalMapType", "editCrystalHollowsMapPosition",
                    "crystalMapPos", "miningCrystalMapWidth", "editScavengedPosition", "scavengedOverlayPos",
                    "scavengedOverlayScale", "crystalDivanWaypointColor", "crystalWaypointColor", "crystalWaypointsBeacons",
                    "crystalWaypointsNames", "editAutomatonPosition", "automatonOverlayPos", "automatonOverlayScale",
                    "automatonColor", "wormTimerScale", "editWormTimerPos", "wormTimerPos");
            JsonObject crystalHollows = getObject(mining, "crystalHollows");
            if (crystalHollows != null) {
                moveOptions(crystalHollows, "crystalHollowsMap",
                        "chWaypointsGUI", "chNewWaypointGUI", "miningCrystalMapType", "editCrystalHollowsMapPosition",
                        "crystalMapPos", "miningCrystalMapWidth");
                moveOptions(crystalHollows, "scavengedOverlaySettings", "editScavengedPosition", "scavengedOverlayPos", "scavengedOverlayScale");
                moveOptions(crystalHollows, "metalDetector", "crystalDivanWaypointColor");
                moveOptions(crystalHollows, "waypointSettings", "crystalWaypointColor", "crystalWaypointsBeacons", "crystalWaypointsNames");
                moveOptions(crystalHollows, "automatonOverlaySettings", "editAutomatonPosition", "automatonOverlayPos", "automatonOverlayScale", "automatonColor");
                moveOptions(crystalHollows, "wormTimerSettings", "wormTimerScale", "editWormTimerPos", "wormTimerPos");
                promoteCategory(crystalHollows, mining, "crystalHollowsMap");
                promoteCategory(crystalHollows, mining, "scavengedOverlaySettings");
                promoteCategory(crystalHollows, mining, "metalDetector");
                promoteCategory(crystalHollows, mining, "waypointSettings");
                promoteCategory(crystalHollows, mining, "automatonOverlaySettings");
                promoteCategory(crystalHollows, mining, "wormTimerSettings");
            }
        }

        JsonObject overlays = getObject(config, "overlays");
        if (overlays != null) {
            moveOptions(overlays, "statBars",
                    "statOverlay", "disableActionBar", "disableIcons", "statEditor",
                    "healthBar", "barLengthH", "posHealth", "manaBar", "barLengthM", "posMana",
                    "expBar", "barLengthE", "posExp", "speedBar", "barLengthS", "posSpeed",
                    "defenceBar", "barLengthD", "posDefense");
            JsonObject statBars = getObject(overlays, "statBars");
            if (statBars != null) {
                moveOptions(statBars, "healthBarSettings", "healthBar", "barLengthH", "posHealth");
                moveOptions(statBars, "manaBarSettings", "manaBar", "barLengthM", "posMana");
                moveOptions(statBars, "expBarSettings", "expBar", "barLengthE", "posExp");
                moveOptions(statBars, "speedBarSettings", "speedBar", "barLengthS", "posSpeed");
                moveOptions(statBars, "defenceBarSettings", "defenceBar", "barLengthD", "posDefense");
                promoteCategory(statBars, overlays, "healthBarSettings");
                promoteCategory(statBars, overlays, "manaBarSettings");
                promoteCategory(statBars, overlays, "expBarSettings");
                promoteCategory(statBars, overlays, "speedBarSettings");
                promoteCategory(statBars, overlays, "defenceBarSettings");
            }
        }

        JsonObject waypoints = getObject(config, "waypoints");
        if (waypoints != null) {
            moveOptions(waypoints, "fairySoulWaypointsSettings", "fairySoulWaypoints", "fairySoulWaypointsColor", "fairySoulsReset");
            moveOptions(waypoints, "giftWaypointsSettings", "giftWaypoints", "giftWaypointsColor", "stJerryLocation");
            moveOptions(waypoints, "dwarvenWaypointsSettings", "miningDwarvenWaypoints", "miningDwarvenBeacons", "miningDwarvenBeaconsColor");
            moveOptions(waypoints, "crimsonWaypointsSettings", "crimsonWaypoints", "crimsonBeacons", "crimsonBeaconsColor");
        }

        JsonObject slayer = getObject(config, "slayer");
        if (slayer != null) {
            moveOptions(slayer, "slayerOverlaySettings",
                    "slayerOverlay", "editSlayerOverlayPos", "slayerOverlayPos", "slayerOverlayHideOnTab",
                    "slayerOverlayHideOnChat", "slayerOverlayScale", "slayerOverlayBackgroundColor");
            moveOptions(slayer, "minibossSettings", "slayerMinibosses", "slayerMinibossTitle", "slayerMinibossSound", "slayerColor");
            moveOptions(slayer, "bossSettings",
                    "slayerBosses", "slayerBossesOutline", "slayerBossHP", "slayerBossHPPos",
                    "editSlayerOverlayPositionButton", "slayerBossColor", "slayerBossTimer");
            moveOptions(slayer, "beaconSettings", "slayerShowBeaconPath", "slayerBeaconColor", "showTracerToBeacon", "notifyBeaconInScreen");
            moveOptions(slayer, "infernoDemonlord",
                    "slayerFirePillarDisplay", "slayerHellionShieldFilter", "slayerBlazeAttunementOutline", "slayerBlazeAttunementFill");
            JsonObject inferno = getObject(slayer, "infernoDemonlord");
            if (inferno != null) {
                moveOptions(inferno, "blazeAttunementSettings", "slayerBlazeAttunementOutline", "slayerBlazeAttunementFill");
                promoteCategory(inferno, slayer, "blazeAttunementSettings");
            }
        }

        JsonObject qol = getObject(config, "qol");
        if (qol != null) {
            moveOptions(qol, "visualTweaks",
                    "qolFullbright", "qolDisableRain", "qolItemRarity", "qolItemRarityOpacity", "qolGoldenEnchants",
                    "qolHideFallingBlocks", "qolHidePlayerArmor", "qolHidePlayerNearNpcs");
            moveOptions(qol, "etherwarpSettings",
                    "qolEtherwarpZoom", "qolEtherwarpOverlay", "qolEtherwarpText", "qolEtherwarpOverlayColor",
                    "qolEtherwarpFailedOverlayColor", "qolEtherwarpSound");
            moveOptions(qol, "shortcuts",
                    "qolShortcutWardrobe", "qolShortcutSlotsWardrobe", "qolWardrobeKey", "qolWardrobeKey1", "qolWardrobeKey2",
                    "qolWardrobeKey3", "qolWardrobeKey4", "qolWardrobeKey5", "qolWardrobeKey6", "qolWardrobeKey7",
                    "qolWardrobeKey8", "qolWardrobeKey9", "qolShortcutPets", "qolPetsKey", "qolShortcutEq",
                    "qolEqKey", "qolShortcutWarps", "qolShortcutWarpIs", "qolShortcutWarpHub", "qolShortcutWarpDh");
            moveOptions(qol, "soundTweaks",
                    "qolDisableJerryChineGunSounds", "qolDisableAoteSounds", "qolDisableHyperionExplosions",
                    "qolDisableThunderlordBolt", "qolDisableMidaStaffAnimation");
            moveOptions(qol, "itemAnimation",
                    "customAnimations", "customSize", "doesScaleSwing", "customX", "customY", "customZ",
                    "customYaw", "customPitch", "customRoll", "customSpeed", "ignoreHaste", "drinkingSelector", "resetItemValuesButton");
            moveOptions(qol, "itemSalvaging", "salvageMythicPrevention", "salvageLegendaryPrevention", "salvageEpicPrevention");
            moveOptions(qol, "playerSizeSettings", "smolPeople");
            moveOptions(qol, "itemTooltipSettings", "qolShowAdminTag", "qolShowStackingCounter");
            moveOptions(qol, "endNodesSettings", "qolEndNodeHighlighter", "endNodeColor");
            moveOptions(qol, "darkAuctionSettings",
                    "qolDAtimer", "qolDarkAhNotifier", "darkAHTimerScale", "editDarkAHTimerPos", "darkAhTimerPos");
            JsonObject darkAuction = getObject(qol, "darkAuctionSettings");
            if (darkAuction != null) {
                moveOptions(darkAuction, "darkAuctionTimerSettings", "darkAHTimerScale", "editDarkAHTimerPos", "darkAhTimerPos");
                promoteCategory(darkAuction, qol, "darkAuctionTimerSettings");
            }

            JsonObject pets = getObject(config, "pets");
            if (pets == null) {
                pets = new JsonObject();
                config.add("pets", pets);
            }
            moveAcross(qol, pets, "qolShowPetEquipped", "qolPetEquippedColor", "qolShortcutPets", "qolPetsKey");
            JsonObject shortcuts = getObject(qol, "shortcuts");
            if (shortcuts != null) {
                moveAcross(shortcuts, pets, "qolShortcutPets", "qolPetsKey");
            }
        }
    }

    private static void moveAcross(JsonObject from, JsonObject to, String... optionNames) {
        for (String optionName : optionNames) {
            if (from.has(optionName) && !to.has(optionName)) {
                to.add(optionName, from.remove(optionName));
            } else if (from.has(optionName)) {
                from.remove(optionName);
            }
        }
    }

    private static JsonObject getObject(JsonObject source, String name) {
        JsonElement element = source.get(name);
        if (element == null || !element.isJsonObject()) return null;
        return element.getAsJsonObject();
    }

    private static void promoteCategory(JsonObject source, JsonObject target, String name) {
        JsonElement element = source.get(name);
        if (element != null && element.isJsonObject() && !target.has(name)) {
            target.add(name, element);
        }
    }

    private static void moveOptions(JsonObject source, String targetName, String... optionNames) {
        JsonObject target = source.has(targetName) && source.get(targetName).isJsonObject()
                ? source.getAsJsonObject(targetName)
                : new JsonObject();
        source.add(targetName, target);

        for (String optionName : optionNames) {
            if (source.has(optionName) && !target.has(optionName)) {
                target.add(optionName, source.get(optionName));
            }
        }
    }

    public static void saveConfig() {
        if (configFile == null || feature == null) return;

        saveMainConfig();

        try {
            SlotLocking.getInstance().saveConfig();
        } catch (Exception exception) {
            System.err.println("[NotEnoughFakepixel] Failed to save slot-locking config");
            exception.printStackTrace();
        }
    }

    private static void saveMainConfig() {
        if (configFile == null || feature == null) return;

        File temporaryFile = new File(configFile.getParentFile(), configFile.getName() + ".tmp");
        try {
            File parent = configFile.getParentFile();
            if (parent != null) {
                Files.createDirectories(parent.toPath());
            }

            String json = gson.toJson(feature);
            new JsonParser().parse(json).getAsJsonObject();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(temporaryFile.toPath(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE),
                    StandardCharsets.UTF_8))) {
                writer.write(json);
                writer.flush();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Files.newInputStream(temporaryFile.toPath()), StandardCharsets.UTF_8))) {
                gson.fromJson(reader, Configuration.class);
            }

            try {
                Files.move(temporaryFile.toPath(), configFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception exception) {
            System.err.println("[NotEnoughFakepixel] Failed to save config " + configFile.getAbsolutePath());
            exception.printStackTrace();
            try {
                Files.deleteIfExists(temporaryFile.toPath());
            } catch (IOException cleanupException) {
                cleanupException.printStackTrace();
            }
        }
    }

    private static void backupCorruptedConfig() {
        if (configFile == null || !configFile.exists()) return;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        File backup = new File(configFile.getParentFile(), configFile.getName() + "." + timestamp + ".corrupted");
        try {
            Files.copy(configFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.err.println("[NotEnoughFakepixel] Backed up corrupted config to " + backup.getAbsolutePath());
        } catch (Exception exception) {
            System.err.println("[NotEnoughFakepixel] Failed to back up corrupted config");
            exception.printStackTrace();
        }
    }

    @RegisterKeybind
    public static final KeyBinding openGuiKey = new KeyBinding(
            "Open GUI",
            Keyboard.KEY_P,
            "NotEnoughFakepixel"
    );

    public static GuiScreen screenToOpen = null;
    private static int screenTicks = 0;

    public static GuiScreen createMoulConfigScreen(String searchQuery) {
        MoulConfigProcessor<Configuration> processor = MoulConfigProcessor.withDefaults(feature);
        processor.registerConfigEditor(ConfigTitleDisplay.class,
                (option, annotation) -> new GuiOptionEditorTitleDisplay(option, annotation));
        new ConfigProcessorDriver(processor).processConfig(feature);
        MoulConfigEditor<Configuration> editor = new MoulConfigEditor<>(processor);
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            editor.search(searchQuery);
        }
        return new NefGuiScreenElementWrapper(editor);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;

        if (screenToOpen != null) {
            screenTicks++;
            if (screenTicks == 5) {
                Minecraft.getMinecraft().displayGuiScreen(screenToOpen);
                screenTicks = 0;
                screenToOpen = null;
            }
        }

        if (openGuiKey.isPressed() && Minecraft.getMinecraft().currentScreen == null) {
            screenToOpen = createMoulConfigScreen(null);
        }
    }

}


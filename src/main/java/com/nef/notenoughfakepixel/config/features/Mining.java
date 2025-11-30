package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;
import org.lwjgl.input.Keyboard;

public class Mining {

    /* General Mining Settings */

    @Expose
    @ConfigOption(name = "Enable Mining Ability Notifier", desc = "Notify when mining ability is ready.")
    @ConfigEditorBoolean
    public boolean miningAbilityNotifier = true;

    @Expose
    @ConfigOption(name = "Fix Drill Animation Reset", desc = "Fix drill animation resetting on fuel update.")
    @ConfigEditorBoolean
    public boolean miningDrillFix = true;

    @Expose
    @ConfigOption(name = "Hide Overlay on Chat open", desc = "Hides overlays on when chat is opened.")
    @ConfigEditorBoolean
    public boolean miningOverlayHideOnChat = true;

    @Expose
    @ConfigOption(name = "Mining Overlay", desc = "Enable the mining overlay in Dwarven Mines & Crystal Hollows.")
    @ConfigEditorBoolean
    public boolean miningOverlay = true;

    /* Dwarven Mines */

    @Expose
    @ConfigOption(name = "Dwarven Mines", desc = "Settings for Dwarven Mines.")
    @ConfigEditorAccordion(id = 1)
    public boolean dwarvenSubcategory = false;

    @Expose
    @ConfigOption(name = "Puzzler Solver", desc = "Enable Puzzler block solver.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean miningPuzzlerSolver = true;

    @Expose
    @ConfigOption(name = "Remove Ghosts Invisibility", desc = "Remove invisibility from ghosts.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean miningShowGhosts = true;

    @Expose
    @ConfigOption(name = "Disable Don Espresso Messages", desc = "Disable Don Espresso event messages.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean miningDisableDonEspresso = true;

    @Expose
    @ConfigOption(name = "Dwarven Waypoints Settings", desc = "Settings for dwarven waypoints.")
    @ConfigAccordionId(id = 1)
    @ConfigEditorAccordion(id = 1_1)
    public boolean waypointsSubcategory = false;

    @Expose
    @ConfigOption(name = "Enable Dwarven Waypoints", desc = "Enable Area Waypoints in the Dwarven Mines.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1_1)
    public boolean miningDwarvenWaypoints = true;

    @Expose
    @ConfigOption(name = "Enable Dwarven Waypoint beacons", desc = "Enable beacons on every waypoint (waypoint must be enabled).")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1_1)
    public boolean miningDwarvenBeacons = true;

    @Expose
    @ConfigOption(name = "Beacons Color", desc = "Color of waypoint beacons.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1_1)
    public String miningDwarvenBeaconsColor = "0:190:0:255:0";

    @Expose
    @ConfigOption(name = "Mining Overlay Settings", desc = "Settings for the mining overlay.")
    @ConfigEditorAccordion(id = 3)
    public boolean overlayAccordion = false;

    @Expose
    @ConfigOption(name = "Mining Overlay Offset X", desc = "Horizontal offset of the mining overlay.")
    @ConfigEditorSlider(minValue = 0.0f, maxValue = 1800.0f, minStep = 1.0f)
    @ConfigAccordionId(id = 3)
    public float miningOverlayOffsetX = 10.0f;

    @Expose
    @ConfigOption(name = "Mining Overlay Offset Y", desc = "Vertical offset of the mining overlay.")
    @ConfigEditorSlider(minValue = 0.0f, maxValue = 1250.0f, minStep = 1.0f)
    @ConfigAccordionId(id = 3)
    public float miningOverlayOffsetY = 10.0f;

    @Expose
    @ConfigOption(name = "Mining Overlay Scale", desc = "Scale of the mining overlay text.")
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    @ConfigAccordionId(id = 3)
    public float miningOverlayScale = 1.0f;

    @Expose
    @ConfigOption(name = "Mining Overlay Background Color", desc = "Background color of the mining overlay.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 3)
    public String miningOverlayBackgroundColor = "0:150:0:0:0";

    @Expose
    @ConfigOption(name = "Show Ability Cooldown", desc = "Show the mining ability cooldown in the overlay.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean miningAbilityCooldown = true;

    @Expose
    @ConfigOption(name = "Show Mithril Powder", desc = "Show mithril powder in the overlay.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean miningMithrilPowder = true;

    @Expose
    @ConfigOption(name = "Show Gemstone Powder", desc = "Show gemstone powder in the overlay.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean miningGemstonePowder = true;

    @Expose
    @ConfigOption(name = "Show Drill Fuel", desc = "Show drill fuel in the overlay.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean miningDrillFuel = true;

    @Expose
    @ConfigOption(name = "Show Commissions", desc = "Show commissions in the overlay.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean miningCommissions = true;

    /* Crystal Hollows */

    @Expose
    @ConfigOption(name = "Crystal Hollows", desc = "Settings for Crystal Hollows.")
    @ConfigEditorAccordion(id = 2)
    public boolean crystalSubcategory = false;

    @Expose
    @ConfigOption(name = "Show Scavenged owned", desc = "Shows the scavenged owned in your inventory at Mines of Divan")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean scavengedOverlay = true;

    @Expose
    @ConfigOption(name = "Crystal Hollows Map", desc = "Crystal Hollows map settings.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean miningCrystalMap = true;

    @Expose
    @ConfigOption(name = "Metal Detector Waypoint", desc = "Triangulates the possible position for the treasure in Mines of Divan.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean crystalMetalDetector = true;

    @Expose
    @ConfigOption(name = "Full block Crystal Panes", desc = "Modifies the crystal pane hitbox to be a full block instead of a thin pane.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean crystalFullBlockPane = true;

    @Expose
    @ConfigOption(name = "Enables CH Waypoints", desc = "Enables render of saved Crystal Hollows waypoints.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean crystalWaypoints = true;

    @Expose
    @ConfigOption(name = "Automaton Display", desc = "Shows Automatons close to you.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean crystalShowAutomaton = true;

    @Expose
    @ConfigOption(name = "Powder Mining Notifier", desc = "Notifies in your screen when you found a powder chest.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean crystalPowderNotifier = true;

    @Expose
    @ConfigOption(name = "Worm Notifier", desc = "Notifies in your screen when a worm spawned.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean crystalWormNotifier = false;

    @Expose
    @ConfigOption(name = "Treasure Chest Helper", desc = "Shows a square where to aim when a treasure chest spawns.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean lockedTreasureChest = true;

    @Expose
    @ConfigOption(name = "Worm Timer", desc = "Shows a timer for the worm cooldown.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean wormTimerCooldown = true;

    @Expose
    @ConfigOption(name = "Heat Notifier", desc = "Notifies you when you are over given heat.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean crystalHeatNotifier = true;

    // slider to get heat level 50 to 100
    @Expose
    @ConfigOption(name = "Heat Notifier Level", desc = "Set the heat level to get notified.")
    @ConfigEditorSlider(minValue = 50.0f, maxValue = 100.0f, minStep = 1.0f)
    @ConfigAccordionId(id = 2)
    public int crystalHeatLevel = 80;

    @Expose
    @ConfigOption(name = "Crystal Hollows Map Settings", desc = "Settings for the Crystal Hollows map.")
    @ConfigAccordionId(id = 2)
    @ConfigEditorAccordion(id = 2_1)
    public boolean crystalMapAccordion = false;

    @Expose
    @ConfigOption(name = "Open Waypoints GUI Key", desc = "Keybind to open Waypoints GUI")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_M)
    @ConfigAccordionId(id = 2_1)
    public int chWaypointsGUI = Keyboard.KEY_M;

    @Expose
    @ConfigOption(name = "Create new Waypoint Key", desc = "Keybind to open GUI to create a new Waypoint")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_B)
    @ConfigAccordionId(id = 2_1)
    public int chNewWaypointGUI = Keyboard.KEY_B;

    @Expose
    @ConfigOption(name = "Crystal Hollows Map Type", desc = "Choose the type of Crystal Hollows map.")
    @ConfigEditorDropdown(
            values = {"Gemstones", "Zones"}
    )
    @ConfigAccordionId(id = 2_1)
    public String miningCrystalMapType = "Gemstones";

    @Expose
    @ConfigOption(name = "Edit Crystal Hollows Map Pos", desc = "Adjust the CH map position visually")
    @ConfigEditorButton(runnableId = "editCrystalHollowsMapPos", buttonText = "Edit")
    @ConfigAccordionId(id = 2_1)
    public String editCrystalHollowsMapPosition = "";

    @Expose
    public Position crystalMapPos = new Position(10, 10, false, false);

    @Expose
    @ConfigOption(name = "Crystal Hollows Map Width", desc = "Width of the Crystal Hollows map in pixels.")
    @ConfigEditorSlider(minValue = 32.0f, maxValue = 160.0f, minStep = 1.0f)
    @ConfigAccordionId(id = 2_1)
    public int miningCrystalMapWidth = 64;

    @Expose
    @ConfigOption(name = "Scavenged Overlay Settings", desc = "Settings for Scavenged Overlay.")
    @ConfigAccordionId(id = 2)
    @ConfigEditorAccordion(id = 2_2)
    public boolean scavengerOverlayAccordion = false;

    @Expose
    @ConfigOption(name = "Edit Scavenged Overlay Pos", desc = "Adjust the Scavenged overlay position visually")
    @ConfigEditorButton(runnableId = "editScavengedOverlayPos", buttonText = "Edit")
    @ConfigAccordionId(id = 2_2)
    public String editScavengedPosition = "";

    @Expose
    public Position scavengedOverlayPos = new Position(0, 0, true, true);

    @Expose
    @ConfigOption(name = "Scavenged Overlay Scale", desc = "Scale of the Scavenged Overlay text.")
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    @ConfigAccordionId(id = 2_2)
    public float scavengedOverlayScale = 1.0f;

    @Expose
    @ConfigOption(name = "Metal Detector Settings", desc = "Settings for the metal detector triangulator.")
    @ConfigAccordionId(id = 2)
    @ConfigEditorAccordion(id = 2_3)
    public boolean crystalMetalDetectorAccordion = false;

    @Expose
    @ConfigOption(name = "Treasure Waypoint color", desc = "Color of the waypoint approximation.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 2_3)
    public String crystalDivanWaypointColor = "0:190:0:255:0";

    @Expose
    @ConfigOption(name = "Waypoint Settings", desc = "Settings for the custom waypoints.")
    @ConfigAccordionId(id = 2)
    @ConfigEditorAccordion(id = 2_4)
    public boolean crystalWaypointAccordion = false;

    @Expose
    @ConfigOption(name = "Waypoint color", desc = "Default waypoint color.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 2_4)
    public String crystalWaypointColor = "0:255:0:0:0";

    @Expose
    @ConfigOption(name = "Show Beacons", desc = "Enables beacons in the waypoints.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2_4)
    public boolean crystalWaypointsBeacons = false;

    @Expose
    @ConfigOption(name = "Show Names", desc = "Enables names in the waypoints.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2_4)
    public boolean crystalWaypointsNames = true;

    @Expose
    @ConfigOption(name = "Automaton Overlay", desc = "Settings for Automaton Overlay.")
    @ConfigAccordionId(id = 2)
    @ConfigEditorAccordion(id = 2_5)
    public boolean automatonOverlayAccordion = false;

    @Expose
    @ConfigOption(name = "Edit Automaton Overlay Pos", desc = "Adjust the Automaton overlay position visually")
    @ConfigEditorButton(runnableId = "editAutomatonOverlayPos", buttonText = "Edit")
    @ConfigAccordionId(id = 2_5)
    public String editAutomatonPosition = "";

    @Expose
    public Position automatonOverlayPos = new Position(0, 0, true, true);

    @Expose
    @ConfigOption(name = "Automaton Overlay Scale", desc = "Scale of the Automaton Overlay text.")
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    @ConfigAccordionId(id = 2_5)
    public float automatonOverlayScale = 1.0f;

    @Expose
    @ConfigOption(name = "Automaton Box Color", desc = "Color of the Automaton box.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 2_5)
    public String automatonColor = "0:190:0:255:0";

    @Expose
    @ConfigOption(name = "Worm Timer", desc = "Settings for Worm Timer.")
    @ConfigAccordionId(id = 2)
    @ConfigEditorAccordion(id = 2_6)
    public boolean wormTimerAccordion = false;

    @Expose
    @ConfigOption(name = "Scale", desc = "Scale of the Worm Timer.")
    @ConfigAccordionId(id = 2_6)
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    public float wormTimerScale = 1.0f;

    @Expose
    @ConfigOption(name = "Position", desc = "Position of the Worm Timer.")
    @ConfigEditorButton(runnableId = "editWormTimerPos", buttonText = "Edit")
    @ConfigAccordionId(id = 2_6)
    public String editWormTimerPos = "";

    @Expose
    public Position wormTimerPos = new Position(0, 0, true, true);

}
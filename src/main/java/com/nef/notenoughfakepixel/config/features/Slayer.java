package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;

public class Slayer {

    // Minibosses Subcategory
    @Expose
    @ConfigOption(name = "Miniboss Settings", desc = "Settings for slayer minibosses.")
    @ConfigEditorAccordion(id = 0)
    public boolean minibossAccordion = false;

    @Expose
    @ConfigOption(name = "Slayer Minibosses Display", desc = "Draw a box around slayer minibosses.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean slayerMinibosses = true;

    @Expose
    @ConfigOption(name = "Miniboss Spawn Title", desc = "Show a title when a miniboss spawns.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean slayerMinibossTitle = true;

    @Expose
    @ConfigOption(name = "Miniboss Sound Notification", desc = "Play a sound when a miniboss spawns.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean slayerMinibossSound = true;

    @Expose
    @ConfigOption(name = "Slayer Minibosses Color", desc = "Color of slayer minibosses.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 0)
    public String slayerColor = "0:92:154:255:255";

    // Bosses Subcategory
    @Expose
    @ConfigOption(name = "Slayer Boss Settings", desc = "Settings for slayer bosses.")
    @ConfigEditorAccordion(id = 1)
    public boolean bossAccordion = false;

    @Expose
    @ConfigOption(name = "Slayer Bosses Display", desc = "Draw a box around slayer bosses.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean slayerBosses = true;

    @Expose
    @ConfigOption(name = "Slayer Bosses Outline", desc = "Draw an outline around slayer bosses.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean slayerBossesOutline = false;

    @Expose
    @ConfigOption(name = "Slayer Boss Health Display", desc = "Displays the slayer health on screen.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean slayerBossHP = true;

    @Expose
    public Position slayerBossHPPos = new Position(10, 10, false, true);

    @Expose
    @ConfigOption(name = "Edit Slayer HP Overlay Position", desc = "Adjust the slayer hp overlay position visually")
    @ConfigEditorButton(runnableId = "editSlayerOverlayPosition", buttonText = "Edit Position")
    @ConfigAccordionId(id = 1)
    public String editSlayerOverlayPositionButton = "";

    @Expose
    @ConfigOption(name = "Slayer Bosses Color", desc = "Color of slayer bosses.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String slayerBossColor = "0:92:154:255:255";

    @Expose
    @ConfigOption(name = "Slayer Boss Time", desc = "Show slayer boss time.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean slayerBossTimer = true;

    // Beacons Subcategory
    @Expose
    @ConfigOption(name = "Voidgloom Beacon Settings", desc = "Settings for beacon waypoints.")
    @ConfigEditorAccordion(id = 2)
    public boolean beaconAccordion = false;

    @Expose
    @ConfigOption(name = "Show Beacon Waypoint", desc = "Show waypoint for beacon.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean slayerShowBeaconPath = true;

    @Expose
    @ConfigOption(name = "Beacon Color", desc = "Color of beacon waypoint.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 2)
    public String slayerBeaconColor = "0:128:0:128:255";

    @Expose
    @ConfigOption(name = "Show Beacon Tracer", desc = "Traces a line to the beacon.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean showTracerToBeacon = true;

    @Expose
    @ConfigOption(name = "Beacon notifier", desc = "Shows a message middle screen when beacon is detected.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean notifyBeaconInScreen = true;

    // Top-Level Options
    @Expose
    @ConfigOption(name = "Faster Maddox Calling", desc = "Method for faster Maddox calling.")
    @ConfigEditorDropdown(values = {"Auto Open", "Semi Auto", "Disabled"})
    public int slayerMaddoxCalling = 1;

    @Expose
    @ConfigOption(name = "Display Blaze Pillar Title", desc = "Display title when blaze pillar is nearby.")
    @ConfigEditorBoolean
    public boolean slayerFirePillarDisplay = true;

    @Expose
    @ConfigOption(name = "Blaze Attunements Display", desc = "Display blaze attunements.")
    @ConfigEditorBoolean
    public boolean slayerBlazeAttunements = true;

    @Expose
    @ConfigOption(name = "Slayer Overlay", desc = "Slayer Overlay Settings")
    @ConfigEditorAccordion(id = 3)
    @ConfigEditorBoolean
    public boolean slayerAccordion = true;

    @Expose
    @ConfigOption(name = "Slayer Overlay", desc = "Enable/Disable slayers overlay")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean slayerOverlay = true;

    @Expose
    @ConfigOption(name = "Position", desc = "Edit slayer overlay position.")
    @ConfigEditorButton(runnableId = "editSlayerOverlayPos", buttonText = "Edit")
    @ConfigAccordionId(id = 3)
    public String editSlayerOverlayPos = "";

    @Expose
    public Position slayerOverlayPos = new Position(0, 0, false, false);

    @Expose
    @ConfigOption(name = "Slayer Overlay Scale", desc = "Scale of the slayer overlay text.")
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    @ConfigAccordionId(id = 3)
    public float slayerOverlayScale = 1.0f;

    @Expose
    @ConfigOption(name = "Slayer Overlay Background Color", desc = "Background color of the slayer overlay.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 3)
    public String slayerOverlayBackgroundColor = "0:150:0:0:0";
}
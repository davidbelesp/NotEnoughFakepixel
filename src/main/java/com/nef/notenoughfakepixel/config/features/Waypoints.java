package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;

public class Waypoints {

    @Expose
    @ConfigOption(name = "Enable Waypoints", desc = "Enable/Disable Waypoints")
    @ConfigEditorBoolean
    public boolean generalWaypointToggle = true;

    // Fairy Soul Subcategory
    @Expose
    @ConfigOption(name = "Fairy Soul Waypoints", desc = "Settings for fairy soul waypoints.")
    @ConfigEditorAccordion(id = 1)
    public boolean fairySoulAccordion = false;

    @Expose
    @ConfigOption(name = "Enable Fairy Soul Waypoints", desc = "Enable fairy soul waypoints.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean fairySoulWaypoints = false;

    @Expose
    @ConfigOption(name = "Fairy Soul Waypoints Color", desc = "Color of fairy soul waypoints.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String fairySoulWaypointsColor = "0:100:255:255:255";

    @Expose
    @ConfigOption(name = "Fairy Souls Reset", desc = "Resets fairy soul waypoints.")
    @ConfigEditorButton(runnableId = "exec_fairySoulsReset", buttonText = "Reset")
    @ConfigAccordionId(id = 1)
    public String fairySoulsReset = "";

    // Gift Waypoints Subcategory

    @Expose
    @ConfigOption(name = "Gift Waypoints", desc = "Enable gifts waypoints in Jerry Island.")
    @ConfigEditorBoolean
    @ConfigEditorAccordion(id = 2)
    public boolean giftWaypointsAccordion = false;

    @Expose
    @ConfigOption(name = "Enable Gift Waypoints", desc = "Enable gift waypoints in Jerry Island.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean giftWaypoints = false;

    @Expose
    @ConfigOption(name = "Gift Waypoints Color", desc = "Color of gift waypoints.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 2)
    public String giftWaypointsColor = "0:255:0:255:255";

    @Expose
    @ConfigOption(name = "Show St. Jerry", desc = "Shows St. Jerry location.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean stJerryLocation = false;

    // Dwarven Waypoints Subcategory

    @Expose
    @ConfigOption(name = "Dwarven Waypoints Settings", desc = "Settings for dwarven waypoints.")
    @ConfigEditorAccordion(id = 3)
    public boolean dwarvenSubcategory = false;

    @Expose
    @ConfigOption(name = "Enable Dwarven Waypoints", desc = "Enable Area Waypoints in the Dwarven Mines.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean miningDwarvenWaypoints = true;

    @Expose
    @ConfigOption(name = "Enable Dwarven Waypoint beacons", desc = "Enable beacons on every waypoint (waypoint must be enabled).")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean miningDwarvenBeacons = true;

    @Expose
    @ConfigOption(name = "Beacons Color", desc = "Color of waypoint beacons.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 3)
    public String miningDwarvenBeaconsColor = "0:190:0:255:0";

    // Crimson Isle Waypoints Subcategory

    @Expose
    @ConfigOption(name = "Crimson Waypoints Settings", desc = "Settings for crimson waypoints.")
    @ConfigEditorAccordion(id = 4)
    public boolean crimsonSubcategory = false;

    @Expose
    @ConfigOption(name = "Enable Crimson Waypoints", desc = "Enable Area Waypoints in the Crimson Isle.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean crimsonWaypoints = true;

    @Expose
    @ConfigOption(name = "Enable Crimson Waypoint beacons", desc = "Enable beacons on every waypoint (waypoint must be enabled).")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean crimsonBeacons = true;

    @Expose
    @ConfigOption(name = "Beacons Color", desc = "Color of waypoint beacons.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 4)
    public String crimsonBeaconsColor = "0:190:0:255:0";

}

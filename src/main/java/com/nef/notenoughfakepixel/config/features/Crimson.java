package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;

public class Crimson {

    // Notifiers Subcategory
    @Expose
    @ConfigOption(name = "Boss Notifiers", desc = "Notifications for boss spawns.")
    @ConfigEditorAccordion(id = 0)
    public boolean notifiersAccordion = false;

    @Expose
    @ConfigOption(name = "Bladesoul Notifier", desc = "Notify when Bladesoul boss spawns.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean crimsonBladesoulNotifier = true;

    @Expose
    @ConfigOption(name = "Mage Outlaw Notifier", desc = "Notify when Mage Outlaw boss spawns.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean crimsonMageOutlawNotifier = true;

    @Expose
    @ConfigOption(name = "Ashfang Notifier", desc = "Notify when Ashfang boss spawns.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean crimsonAshfangNotifier = true;

    @Expose
    @ConfigOption(name = "Barbarian Duke X Notifier", desc = "Notify when Barbarian Duke X boss spawns.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean crimsonBarbarianDukeXNotifier = true;

    // Ashfang Subcategory
    @Expose
    @ConfigOption(name = "Ashfang Settings", desc = "Settings related to Ashfang.")
    @ConfigEditorAccordion(id = 1)
    public boolean ashfangAccordion = false;

    @Expose
    @ConfigOption(name = "Ashfang Waypoint", desc = "Show waypoint on Ashfang.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean crimsonAshfangWaypoint = true;

    @Expose
    @ConfigOption(name = "Ashfang Waypoint Color", desc = "Color of Ashfang waypoint.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String crimsonAshfangWaypointColor = "0:100:255:0:255";

    @Expose
    @ConfigOption(name = "Ashfang Hitboxes", desc = "Show hitboxes for Ashfang minions.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean crimsonAshfangHitboxes = true;

    @Expose
    @ConfigOption(name = "Ashfang Mute Chat", desc = "Mute chat messages from Ashfang minions.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean crimsonAshfangMuteChat = true;

    @Expose
    @ConfigOption(name = "Ashfang Mute Sound", desc = "Mute sounds from Ashfang minions.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean crimsonAshfangMuteSound = true;

    @Expose
    @ConfigOption(name = "Ashfang Hurt Sound", desc = "Play sound when Ashfang is hit by Blazing Soul.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean crimsonAshfangHurtSound = true;

    @Expose
    @ConfigOption(name = "Edit Ashfang Overlay Position", desc = "Adjust the overlay position visually")
    @ConfigEditorButton(runnableId = "editAshfangPosition", buttonText = "Edit Position")
    @ConfigAccordionId(id = 1)
    public String editAshOverlayPositionButton = "";

    @Expose
    @ConfigOption(name = "Ashfang Overlay", desc = "Show overlay for Ashfang HP and Blazing Souls.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean crimsonAshfangOverlay = true;

    @Expose
    public Position ashfangOverlayPos = new Position(10, 10, false, true);

    @Expose
    @ConfigOption(name = "Gravity Orb Waypoint", desc = "Show waypoint on Gravity Orb.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean crimsonGravityOrbWaypoint = true;

    @Expose
    @ConfigOption(name = "Gravity Orb Waypoint Color", desc = "Color of Gravity Orb waypoint.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String crimsonBlazingSoulWaypointColor = "0:255:255:0:255";
}
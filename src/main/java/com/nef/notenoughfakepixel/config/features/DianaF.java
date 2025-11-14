package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;
import org.lwjgl.input.Keyboard;

public class DianaF {

    // Burrows Subcategory
    @Expose
    @ConfigOption(name = "Burrow Settings", desc = "Settings for burrow waypoints and visuals.")
    @ConfigEditorAccordion(id = 0)
    public boolean burrowAccordion = false;

    @Expose
    @ConfigOption(name = "Show Burrow Guess", desc = "Show a guess to the burrow.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaBurrowGuess = true;

    @Expose
    @ConfigOption(name = "Warp helper", desc = "Show the closest warp to the guess.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaWarpHelper = true;

    @Expose
    @ConfigOption(name = "Warp Keybbind", desc = "Keybind to quickly warp to the nearest guess burrow.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_H)
    @ConfigAccordionId(id = 0)
    public int warpKeybind = Keyboard.KEY_H;

    @Expose
    @ConfigOption(name = "Warp helper scale", desc = "Scale of the warp text.")
    @ConfigEditorSlider(minValue = 1.0f, maxValue = 5.0f, minStep = 0.1f)
    @ConfigAccordionId(id = 0)
    public float warpHelperScale = 1.0f;

    @Expose
    @ConfigOption(name = "Dark Auction warp", desc = "")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaWarpDa = true;

    @Expose
    @ConfigOption(name = "Museum warp", desc = "")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaWarpMuseum = true;

    @Expose
    @ConfigOption(name = "Crypts warp", desc = "")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaWarpCrypts = true;

    @Expose
    @ConfigOption(name = "Castle warp", desc = "")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaWarpCastle = true;

    @Expose
    @ConfigOption(name = "Edit Warp helper Position", desc = "Adjust the Warp helper position visually")
    @ConfigEditorButton(runnableId = "editWarpHelperPosition", buttonText = "Edit Position")
    @ConfigAccordionId(id = 0)
    public String editWarpHelperPositionButton = "";


    @Expose
    @ConfigOption(name = "Show Waypoints on Burrows", desc = "Show waypoints on burrows.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaShowWaypointsBurrows = true;

    @Expose
    @ConfigOption(name = "Empty Burrow Color", desc = "Color of empty burrows.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 0)
    public String dianaEmptyBurrowColor = "0:100:0:255:255";

    @Expose
    @ConfigOption(name = "Show Labels on Waypoints", desc = "Show labels on burrow waypoints.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaShowLabelsWaypoints = true;

    @Expose
    @ConfigOption(name = "Mob Burrow Color", desc = "Color of mob burrows.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 0)
    public String dianaMobBurrowColor = "0:255:255:255:255";

    @Expose
    @ConfigOption(name = "Show Tracers on Waypoints", desc = "Show tracers on burrow waypoints.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean dianaShowTracersWaypoints = true;

    @Expose
    @ConfigOption(name = "Treasure Burrow Color", desc = "Color of treasure burrows.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 0)
    public String dianaTreasureBurrowColor = "0:255:0:0:255";

    // Gaia/Siamese Subcategory
    @Expose
    @ConfigOption(name = "Mob Settings", desc = "Settings for Gaia Construct and Siamese.")
    @ConfigEditorAccordion(id = 1)
    public boolean mobAccordion = false;

    @Expose
    @ConfigOption(name = "Track Gaia Hits", desc = "Track when Gaia Construct can be damaged.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean dianaGaiaConstruct = true;

    @Expose
    @ConfigOption(name = "Gaia Hittable Color", desc = "Color when Gaia is hittable.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String dianaGaiaHittableColor = "0:250:255:0:255";

    @Expose
    @ConfigOption(name = "Show Hittable Siamese", desc = "Show when Siamese can be damaged.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean dianaSiamese = true;

    @Expose
    @ConfigOption(name = "Gaia Un-hittable Color", desc = "Color when Gaia is not hittable.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String dianaGaiaUnhittableColor = "0:255:0:0:255";

    @Expose
    @ConfigOption(name = "Siamese Hittable Color", desc = "Color when Siamese is hittable.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String dianaSiameseHittableColor = "0:250:255:0:255";

    @Expose
    @ConfigOption(name = "Minos Inquisitor Alert", desc = "Alert when Minos Inquisitor is dug.")
    @ConfigEditorBoolean
    public boolean dianaMinosInquisitorAlert = true;

    @Expose
    @ConfigOption(name = "Minos Inquisitor Outline", desc = "Outline Minos Inquisitors (the outline color is linked to starred mobs color).")
    @ConfigEditorBoolean
    public boolean dianaMinosInquisitorOutline = true;

    @Expose
    @ConfigOption(name = "Inq outline Color", desc = "Color of the inquisitor outline.")
    @ConfigEditorColour
    public String dianaInqOutlineColor = "0:100:0:255:255";

    @Expose
    @ConfigOption(name = "Waypoint Sounds", desc = "Enable sounds for waypoint creation.")
    @ConfigEditorBoolean
    public boolean dianaWaypointSounds = true;

    @Expose
    @ConfigOption(name = "Disable Explosion Sounds", desc = "Disable burrow digging explosion sounds.")
    @ConfigEditorBoolean
    public boolean dianaDisableDianaExplosionSounds = false;

    @Expose
    @ConfigOption(name = "Disable Ancestral Spade Cooldown Message", desc = "Mute Ancestral Spade cooldown message.")
    @ConfigEditorBoolean
    public boolean dianaCancelCooldownSpadeMessage = true;

    @Expose
    public Position warpHelperPos = new Position(10, 10, false, true);
}
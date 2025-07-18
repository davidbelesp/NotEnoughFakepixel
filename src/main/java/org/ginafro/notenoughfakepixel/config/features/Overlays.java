package org.ginafro.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import org.ginafro.notenoughfakepixel.config.gui.core.config.annotations.*;

public class Overlays {

    @Expose
    @ConfigOption(name = "Storage", desc = "Storage Overlay Settings")
    @ConfigEditorAccordion(id = 0)
    public boolean storage = false;

    @Expose
    @ConfigOption(name = "Storage Overlay", desc = "Redesign of Storage GUI")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean storageOverlay = false;

    @Expose
    @ConfigOption(name = "Storage Search", desc = "Add a search bar to storage gui(Requires Storage Overlay)")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean storageSearch = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Color of the storage overlay")
    @ConfigEditorColour
    @ConfigAccordionId(id = 0)
    public String storageColor = "0:102:32:32:32";

    @Expose
    @ConfigOption(name = "Chest Color", desc = "Color of the storage Chest Background")
    @ConfigEditorColour
    @ConfigAccordionId(id = 0)
    public String chestColor = "0:102:32:32:32";

    @Expose
    @ConfigOption(name = "Search Highlight Color", desc = "Color to highlight search item with")
    @ConfigEditorColour
    @ConfigAccordionId(id = 0)
    public String searchColor = "0:255:0:255:0";

//    @Expose
//    @ConfigOption(name = "Inventory Buttons", desc = "Settings related to inv buttons")
//    @ConfigEditorAccordion(id = 1)
    public boolean invbutton = false;

//    @Expose
//    @ConfigOption(name = "Inventory Buttons", desc = "Enable/Disable ")
//    @ConfigEditorBoolean
//    @ConfigAccordionId(id = 1)
    public boolean invButtons = false;

//    @Expose
//    @ConfigOption(name = "Snap to Grid", desc = "Whether or not inv buttons will be snapped to a grid")
//    @ConfigEditorBoolean
//    @ConfigAccordionId(id = 1)
    public boolean snapGrid = false;

//    @Expose
//    @ConfigOption(name = "Button Editor", desc = "Change Position or edit/add any inventory button")
//    @ConfigEditorButton(buttonText = "EDIT", runnableId = "nefButtons")
//    @ConfigAccordionId(id = 1)
    public String editor = "";

    @Expose
    @ConfigOption(name = "Equipment Overlay", desc = "Shows what equipment u are wearing")
    @ConfigEditorBoolean
    public boolean equipment = true;

    @Expose
    @ConfigOption(name = "Stat Bars", desc = "Setting for Stat Bars")
    @ConfigEditorAccordion(id = 1)
    public boolean statBars = false;

    @Expose
    @ConfigOption(name = "Enable Stat Bars", desc = "Enable/Disable this feature")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean statOverlay = false;

    @Expose
    @ConfigOption(name = "Disable Action Bar", desc = "Enable/Disable whether or not it should show ur stats in the action bar")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean disableActionBar = false;

    @Expose
    @ConfigOption(name = "Disable Default Render", desc = "Enable/Disable whether or not it should render vanilla minecraft's bars")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean disableIcons = false;

    @Expose
    @ConfigOption(name = "Stat Bar Position", desc = "Edit position of stat bars")
    @ConfigEditorButton(runnableId = "statEditor",buttonText = "Edit Positions")
    @ConfigAccordionId(id = 1)
    public String statEditor = "";


    @Expose
    @ConfigOption(name = "Health Bar", desc = "Settings for the health bar")
    @ConfigEditorAccordion(id = 2)
    @ConfigAccordionId(id = 1)
    public boolean health = false;

    @Expose
    @ConfigOption(name = "Health Bar", desc = "Enable/Disable health bar")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean healthBar = true;

    @Expose
    @ConfigOption(name = "Bar Length", desc = "How long the health bar is")
    @ConfigEditorDropdown(values = {"Tiny","Small","Medium","Large"},initialIndex = 2)
    @ConfigAccordionId(id = 2)
    public int barLengthH = 2;


    @Expose
    @ConfigOption(name = "Mana Bar", desc = "Settings for the mana bar")
    @ConfigEditorAccordion(id =3)
    @ConfigAccordionId(id = 1)
    public boolean mana = false;

    @Expose
    @ConfigOption(name = "Mana Bar", desc = "Enable/Disable mana bar")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean manaBar = true;

    @Expose
    @ConfigOption(name = "Bar Length", desc = "How long the mana bar is")
    @ConfigEditorDropdown(values = {"Tiny","Small","Medium","Large"},initialIndex = 2)
    @ConfigAccordionId(id = 3)
    public int barLengthM = 2;


    @Expose
    @ConfigOption(name = "Exp Bar", desc = "Settings for the exp bar")
    @ConfigEditorAccordion(id =4)
    @ConfigAccordionId(id = 1)
    public boolean exp = false;

    @Expose
    @ConfigOption(name = "Exp Bar", desc = "Enable/Disable exp bar")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean expBar = true;

    @Expose
    @ConfigOption(name = "Bar Length", desc = "How long the exp bar is")
    @ConfigEditorDropdown(values = {"Tiny","Small","Medium","Large"},initialIndex = 3)
    @ConfigAccordionId(id = 4)
    public int barLengthE = 3;


    @Expose
    @ConfigOption(name = "Speed Bar", desc = "Settings for the speed bar")
    @ConfigEditorAccordion(id =5)
    @ConfigAccordionId(id = 1)
    public boolean speed = false;

    @Expose
    @ConfigOption(name = "Speed Bar", desc = "Enable/Disable speed bar")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 5)
    public boolean speedBar = true;

    @Expose
    @ConfigOption(name = "Bar Length", desc = "How long the speed bar is")
    @ConfigEditorDropdown(values = {"Tiny","Small","Medium"},initialIndex = 2)
    @ConfigAccordionId(id = 5)
    public int barLengthS = 2;


    @Expose
    @ConfigOption(name = "Defence Bar", desc = "Settings for the defence bar")
    @ConfigEditorAccordion(id =5)
    @ConfigAccordionId(id = 1)
    public boolean defence = false;

    @Expose
    @ConfigOption(name = "Defence Bar", desc = "Enable/Disable defence bar")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 5)
    public boolean defenceBar = true;

    @Expose
    @ConfigOption(name = "Bar Length", desc = "How long the defence bar is")
    @ConfigEditorDropdown(values = {"Tiny","Small","Medium"},initialIndex = 2)
    @ConfigAccordionId(id = 5)
    public int barLengthD = 2;

    @Expose
    public Position posHealth = new Position(100,100);
    @Expose
    public Position posMana = new Position(200,100);
    @Expose
    public Position posExp = new Position(200,200);
    @Expose
    public Position posSpeed = new Position(300,100);
    @Expose
    public Position posDefense = new Position(300,200);

}

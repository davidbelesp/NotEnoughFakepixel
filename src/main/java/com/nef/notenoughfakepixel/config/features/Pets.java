package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import org.lwjgl.input.Keyboard;

public class Pets {

    @Expose
    @ConfigOption(name = "Pet Tracker", desc = "Show pet score, missing pets, and upgradeable pets in the Pets menu.")
    @ConfigEditorBoolean
    public boolean petTracker = true;

    @Expose
    @ConfigOption(name = "Pets Shortcut", desc = "Enable a keybind that runs /pets.")
    @ConfigEditorBoolean
    public boolean qolShortcutPets = false;

    @Expose
    @ConfigOption(name = "Pets Shortcut Key", desc = "Keybind for opening the Pets menu.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_NONE)
    public int qolPetsKey = Keyboard.KEY_NONE;

    @Expose
    @ConfigOption(name = "Highlight Equipped Pet", desc = "Highlight the currently equipped pet in the Pets menu.")
    @ConfigEditorBoolean
    public boolean qolShowPetEquipped = true;

    @Expose
    @ConfigOption(name = "Equipped Pet Highlight Color", desc = "Color used to highlight the equipped pet slot.")
    @ConfigEditorColour
    public String qolPetEquippedColor = "0:100:0:255:0";
}

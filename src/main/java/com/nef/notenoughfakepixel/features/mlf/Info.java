package com.nef.notenoughfakepixel.features.mlf;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;

public class Info {

    @Expose
    @ConfigOption(name = "MLF Info HUD", desc = "Enable the MLF info HUD.", subcategoryId = 1)
    @ConfigEditorBoolean
    public boolean mlfInfoHud = true;

    @Expose
    @ConfigOption(name = "MLF Info Offset X", desc = "Horizontal offset of the MLF info HUD.", subcategoryId = 1)
    @ConfigEditorSlider(minValue = 0.0f, maxValue = 1800.0f, minStep = 1.0f)
    public float mlfInfoOffsetX = 5.0f;

    @Expose
    @ConfigOption(name = "MLF Info Offset Y", desc = "Vertical offset of the MLF info HUD.", subcategoryId = 1)
    @ConfigEditorSlider(minValue = 0.0f, maxValue = 1250.0f, minStep = 1.0f)
    public float mlfInfoOffsetY = 0.0f;

    @Expose
    @ConfigOption(name = "MLF Info Background Color", desc = "Background color of the MLF info HUD.", subcategoryId = 1)
    @ConfigEditorColour
    public String mlfInfoBackgroundColor = "0:102:0:0:0"; // Default: semi-transparent black

    @Expose
    @ConfigOption(name = "Edit MLF Info Position", desc = "Adjust the MLF info HUD position visually", subcategoryId = 1)
    @ConfigEditorButton(runnableId = "editMlfInfoPosition", buttonText = "Edit Position")
    public String editMlfInfoPositionButton = "";

}
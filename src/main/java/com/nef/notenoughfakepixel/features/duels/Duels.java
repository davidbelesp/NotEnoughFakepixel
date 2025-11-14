package com.nef.notenoughfakepixel.features.duels;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigEditorBoolean;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigEditorButton;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigEditorSlider;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigOption;

public class Duels {

    // Duels K/D Counter (subcategoryId = 1)
    @Expose
    @ConfigOption(name = "K/D Counter", desc = "Enable the K/D counter in Duels.", subcategoryId = 1)
    @ConfigEditorBoolean
    public boolean kdCounterEnabled = true;

    @Expose
    @ConfigOption(name = "K/D Counter Offset X", desc = "Horizontal offset of the K/D counter.", subcategoryId = 1)
    @ConfigEditorSlider(minValue = 0.0f, maxValue = 1800.0f, minStep = 1.0f)
    public float kdCounterOffsetX = 10.0f;

    @Expose
    @ConfigOption(name = "K/D Counter Offset Y", desc = "Vertical offset of the K/D counter.", subcategoryId = 1)
    @ConfigEditorSlider(minValue = 0.0f, maxValue = 1250.0f, minStep = 1.0f)
    public float kdCounterOffsetY = 10.0f;

    @Expose
    @ConfigOption(name = "K/D Counter Scale", desc = "Scale of the K/D counter text.", subcategoryId = 1)
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    public float kdCounterScale = 1.0f;

    @Expose
    @ConfigOption(name = "Edit K/D Counter Position", desc = "Adjust the K/D counter position visually", subcategoryId = 1)
    @ConfigEditorButton(runnableId = "editKdCounterPosition", buttonText = "Edit Position")
    public String editKdCounterPositionButton = "";


}


package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigEditorBoolean;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigOption;

public class Experimentation {

    @Expose
    @ConfigOption(name = "Chronomatron Solver", desc = "Enable Chronomatron solver.")
    @ConfigEditorBoolean
    public boolean experimentationChronomatronSolver = true;

    @Expose
    @ConfigOption(name = "Ultrasequencer Solver", desc = "Enable Ultrasequencer solver.")
    @ConfigEditorBoolean
    public boolean experimentationUltraSequencerSolver = true;

    @Expose
    @ConfigOption(name = "Hide Tooltips", desc = "Hide tooltips during experiments.")
    @ConfigEditorBoolean
    public boolean experimentationHideTooltips = true;

    @Expose
    @ConfigOption(name = "Prevent Missclicks", desc = "Prevent missclicks during experiments.")
    @ConfigEditorBoolean
    public boolean experimentationPreventMissclicks = true;
}
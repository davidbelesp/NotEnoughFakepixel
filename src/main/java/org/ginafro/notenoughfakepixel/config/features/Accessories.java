package org.ginafro.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import org.ginafro.notenoughfakepixel.config.gui.core.config.annotations.ConfigEditorBoolean;
import org.ginafro.notenoughfakepixel.config.gui.core.config.annotations.ConfigOption;

public class Accessories {

    @Expose
    @ConfigOption(name = "Enable Missing Accessories", desc = "Enable/Disable Missing accessories GUI")
    @ConfigEditorBoolean
    public boolean enable = true;

    @Expose
    @ConfigOption(name = "Show MP Estimation", desc = "Shows MP estimation in accessories GUI")
    @ConfigEditorBoolean
    public boolean showMpEstimation = true;

    @Expose
    @ConfigOption(name = "Show Missing Accessories List", desc = "Shows a list of missing accessories in accessories GUI")
    @ConfigEditorBoolean
    public boolean showMissingAccessoriesList = true;

}

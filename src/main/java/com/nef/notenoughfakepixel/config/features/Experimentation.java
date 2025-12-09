package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigEditorBoolean;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigEditorDropdown;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.ConfigOption;

public class Experimentation {

    @Expose
    @ConfigOption(name = "Enable Solvers", desc = "Enable experimentation Solvers.")
    @ConfigEditorBoolean
    public boolean experimentationSolvers = true;

    @Expose
    @ConfigOption(name = "Chronomatron Solver", desc = "Enable Chronomatron solver.")
    @ConfigEditorBoolean
    public boolean experimentationChronomatronSolver = true;

    @Expose
    @ConfigOption(name = "Ultrasequencer Solver", desc = "Enable Ultrasequencer solver.")
    @ConfigEditorBoolean
    public boolean experimentationUltraSequencerSolver = true;

    @Expose
    @ConfigOption(name = "Superpairs Solver", desc = "Enable Superpairs solver.")
    @ConfigEditorBoolean
    public boolean experimentationSuperpairsSolver = true;

    @Expose
    @ConfigOption(name = "Hide Tooltips", desc = "Hide tooltips during experiments.")
    @ConfigEditorBoolean
    public boolean experimentationHideTooltips = true;

    @Expose
    @ConfigOption(name = "Prevent Missclicks", desc = "Prevent missclicks during experiments.")
    @ConfigEditorBoolean
    public boolean experimentationPreventMissclicks = true;

    @Expose
    @ConfigOption(
            name = "Ultrasequencer Upcoming",
            desc = "Set the colour of the glass pane shown behind the element in the ultrasequencer which is coming after \"next\""
    )
    @ConfigEditorDropdown(
            values = {
                    "None", "White", "Orange", "Light Purple", "Light Blue", "Yellow", "Light Green", "Pink",
                    "Gray", "Light Gray", "Cyan", "Dark Purple", "Dark Blue", "Brown", "Dark Green", "Red", "Black"
            }
    )
    public int seqUpcoming = 5;

    @Expose
    @ConfigOption(
            name = "Superpairs Matched",
            desc = "Set the colour of the glass pane shown behind successfully matched pairs"
    )
    @ConfigEditorDropdown(
            values = {
                    "None", "White", "Orange", "Light Purple", "Light Blue", "Yellow", "Light Green", "Pink",
                    "Gray", "Light Gray", "Cyan", "Dark Purple", "Dark Blue", "Brown", "Dark Green", "Red", "Black"
            }
    )
    public int supMatched = 6;

    @Expose
    @ConfigOption(
            name = "Superpairs Possible",
            desc = "Set the colour of the glass pane shown behind pairs which can be matched, but have not yet"
    )
    @ConfigEditorDropdown(
            values = {
                    "None", "White", "Orange", "Light Purple", "Light Blue", "Yellow", "Light Green", "Pink",
                    "Gray", "Light Gray", "Cyan", "Dark Purple", "Dark Blue", "Brown", "Dark Green", "Red", "Black"
            }
    )
    public int supPossible = 2;

    @Expose
    @ConfigOption(
            name = "Superpairs Unmatched",
            desc = "Set the colour of the glass pane shown behind pairs which have been previously uncovered"
    )
    @ConfigEditorDropdown(
            values = {
                    "None", "White", "Orange", "Light Purple", "Light Blue", "Yellow", "Light Green", "Pink",
                    "Gray", "Light Gray", "Cyan", "Dark Purple", "Dark Blue", "Brown", "Dark Green", "Red", "Black"
            }
    )
    public int supUnmatched = 5;

    @Expose
    @ConfigOption(
            name = "Superpairs Powerups",
            desc = "Set the colour of the glass pane shown behind powerups"
    )
    @ConfigEditorDropdown(
            values = {
                    "None", "White", "Orange", "Light Purple", "Light Blue", "Yellow", "Light Green", "Pink",
                    "Gray", "Light Gray", "Cyan", "Dark Purple", "Dark Blue", "Brown", "Dark Green", "Red", "Black"
            }
    )
    public int supPower = 11;
}
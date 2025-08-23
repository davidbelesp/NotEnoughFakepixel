package org.ginafro.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import org.ginafro.notenoughfakepixel.config.gui.core.config.annotations.*;


public class Debug {

    @Expose
    @ConfigOption(name = "Debug Logs", desc = "Enable debug mode for logging.")
    @ConfigEditorBoolean
    public boolean debug = false;

    @Expose
    @ConfigOption(name = "Sound Debug", desc = "Show Sounds debug.")
    @ConfigEditorBoolean
    public boolean showSounds = false;

    @Expose
    @ConfigOption(name = "Force Pojav", desc = "Force Pojav detection in the mod.")
    @ConfigEditorBoolean
    public boolean forcePojav = false;

    @Expose
    @ConfigOption(name = "Enable out of fakepixel", desc = "Enables mod out of fakepixel server.")
    @ConfigEditorBoolean
    public boolean enableOutOfFakepixel = false;

    @Expose
    @ConfigOption(name = "Loggers", desc = "Logs some stored variables.")
    @ConfigEditorAccordion(id = 0)
    public boolean loggersAccordion = false;

    @Expose
    @ConfigOption(name = "Log location", desc = "Log current location.")
    @ConfigEditorButton(buttonText = "Log", runnableId = "logLocation")
    @ConfigAccordionId(id = 0)
    public String logLocationButton = "";

    @Expose
    @ConfigOption(name = "Log SKYBLOCK", desc = "Log if player is in Skyblock gamemode.")
    @ConfigEditorButton(buttonText = "Log", runnableId = "logIsInSkyblock")
    @ConfigAccordionId(id = 0)
    public String logIsInSkyblock = "";

    @Expose
    @ConfigOption(name = "Log Scoreboard", desc = "Log current scoreboard data.")
    @ConfigEditorButton(buttonText = "Log", runnableId = "logScoreboard")
    @ConfigAccordionId(id = 0)
    public String logScoreboardButton = "";

    @Expose
    @ConfigOption(name = "Show data loaded", desc = "Shows data loaded on API calls.")
    @ConfigEditorButton(runnableId = "showAPI", buttonText = "Show API")
    @ConfigAccordionId(id = 0)
    public String showAPIButton = "";
}
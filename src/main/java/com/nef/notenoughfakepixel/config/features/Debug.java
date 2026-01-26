package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;

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
    @ConfigEditorButton(buttonText = "Log", runnableId = "debug_logLocation")
    @ConfigAccordionId(id = 0)
    public String logLocationButton = "";

    @Expose
    @ConfigOption(name = "Log SKYBLOCK", desc = "Log if player is in Skyblock gamemode.")
    @ConfigEditorButton(buttonText = "Log", runnableId = "debug_logIsInSkyblock")
    @ConfigAccordionId(id = 0)
    public String logIsInSkyblock = "";

    @Expose
    @ConfigOption(name = "Log Scoreboard", desc = "Log current scoreboard data.")
    @ConfigEditorButton(buttonText = "Log", runnableId = "debug_logScoreboard")
    @ConfigAccordionId(id = 0)
    public String logScoreboardButton = "";

    @Expose
    @ConfigOption(name = "Copy API data", desc = "Copy API data to clipboard.")
    @ConfigEditorButton(runnableId = "debug_showAPI", buttonText = "Copy")
    @ConfigAccordionId(id = 0)
    public String showAPIButton = "";

    @Expose
    @ConfigOption(name = "Show held item SBID", desc = "Shows Skyblock ID of the item held.")
    @ConfigEditorButton(runnableId = "debug_showSBID", buttonText = "Show")
    @ConfigAccordionId(id = 0)
    public String showSBIDButton = "";

    @Expose
    @ConfigOption(name = "Trigger all timers", desc = "trigger all timers event.")
    @ConfigEditorButton(runnableId = "debug_triggerTimers", buttonText = "Show")
    @ConfigAccordionId(id = 0)
    public String triggerTimers = "";

    @Expose
    @ConfigOption(name = "Log Skyblock Data", desc = "Logs NEF saved skyblock data.")
    @ConfigEditorButton(runnableId = "debug_logSbData", buttonText = "Show")
    @ConfigAccordionId(id = 0)
    public String logSkyblockData = "";

    @Expose
    @ConfigOption(name = "Copy NBT", desc = "Allows copying mouse item NBT into clipboard \u00a7c[R CTRL]\u00a77.")
    @ConfigEditorBoolean
    public boolean debugCopyNbt = false;
}
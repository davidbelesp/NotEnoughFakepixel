package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.annotations.ConfigTitleDisplay;
import io.github.notenoughupdates.moulconfig.annotations.*;

public class About {

    @ConfigOption(name = "Title", desc = "Title")
    @ConfigTitleDisplay(type = ConfigTitleDisplay.Type.TEXT, text = "Welcome to NotEnoughFakepixel!", size = ConfigTitleDisplay.Size.MEDIUM)
    public transient Void maintitle = null;

    @ConfigOption(name = "Current Version", desc = "The NotEnoughFakepixel version you are running")
    @ConfigTitleDisplay(type = ConfigTitleDisplay.Type.VERSION, color = 0x87ff54)
    public transient Void currentVersion = null;

    @Expose
    @ConfigOption(name = "Discord", desc = "Join the NotEnoughFakepixel Discord server.")
    @ConfigEditorButton(runnableId = 32, buttonText = "Open")
    public String discordButton = "";

    @Expose
    @ConfigOption(name = "GitHub", desc = "View the NotEnoughFakepixel source code.")
    @ConfigEditorButton(runnableId = 33, buttonText = "Open")
    public String githubButton = "";

    @Expose
    @ConfigOption(name = "Used Software / Libraries", desc = "Software and libraries used by NotEnoughFakepixel.")
    @ConfigEditorAccordion(id = 0)
    public boolean librariesAccordion = false;

    @Expose
    @ConfigOption(name = "Forge", desc = "Minecraft Forge modding API.")
    @ConfigEditorButton(runnableId = 34, buttonText = "Source")
    @ConfigAccordionId(id = 0)
    public String forgeButton = "";

    @Expose
    @ConfigOption(name = "Mixin", desc = "Mixin bytecode manipulation framework.")
    @ConfigEditorButton(runnableId = 35, buttonText = "Source")
    @ConfigAccordionId(id = 0)
    public String mixinButton = "";

    @Expose
    @ConfigOption(name = "MoulConfig", desc = "Configuration GUI library by NotEnoughUpdates.")
    @ConfigEditorButton(runnableId = 36, buttonText = "Source")
    @ConfigAccordionId(id = 0)
    public String moulConfigButton = "";

    @Expose
    @ConfigOption(name = "Lombok", desc = "Java compiler annotations and code generation library.")
    @ConfigEditorButton(runnableId = 37, buttonText = "Website")
    @ConfigAccordionId(id = 0)
    public String lombokButton = "";

    @Expose
    @ConfigOption(name = "Reflections", desc = "Runtime classpath scanning library.")
    @ConfigEditorButton(runnableId = 38, buttonText = "Source")
    @ConfigAccordionId(id = 0)
    public String reflectionsButton = "";

    @Expose
    @ConfigOption(name = "Architectury Loom", desc = "Gradle tooling used to develop and build the mod.")
    @ConfigEditorButton(runnableId = 39, buttonText = "Website")
    @ConfigAccordionId(id = 0)
    public String architecturyLoomButton = "";
}

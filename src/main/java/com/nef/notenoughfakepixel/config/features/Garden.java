package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.position.Position;
import io.github.notenoughupdates.moulconfig.annotations.*;

public class Garden {

    @Expose
    @ConfigOption(name = "Enable Garden Features", desc = "Master toggle for garden-related features.")
    @ConfigEditorBoolean
    public boolean enable = true;

    @Expose
    @Category(name = "Pests Overlay", desc = "Settings for the active pests overlay.")
    public PestOverlaySettings pestsOverlay = new PestOverlaySettings();

    @Expose
    @Category(name = "Visitors Overlay", desc = "Settings for the active visitors overlay.")
    public VisitorOverlaySettings visitorsOverlay = new VisitorOverlaySettings();

    @Expose
    @Category(name = "Crop Milestone Overlay", desc = "Settings for the current crop milestone overlay.")
    public CropMilestoneOverlaySettings cropMilestoneOverlay = new CropMilestoneOverlaySettings();

    @Expose
    @Category(name = "Pest Plot Grill", desc = "Highlight plots containing active pests.")
    public PestPlotGrillSettings pestPlotGrill = new PestPlotGrillSettings();

    @Expose
    @Category(name = "Pest Finder", desc = "Show boxes and tracers for active garden pests.")
    public PestEspSettings pestEsp = new PestEspSettings();

    @Expose
    @Category(name = "Vacuum Pests Bag", desc = "Show the pests stored in vacuum bags as a gold stack count.")
    public VacuumPestsBagSettings vacuumPestsBag = new VacuumPestsBagSettings();

    public static class PestOverlaySettings {
        @Expose
        @ConfigOption(name = "Pests Overlay", desc = "Show the active pest count.")
        @ConfigEditorBoolean
        public boolean enabled = true;

        @Expose
        @ConfigOption(name = "Position", desc = "Edit the pests overlay position.")
        @ConfigEditorButton(runnableId = 28, buttonText = "Edit")
        public String editPosition = "";

        @Expose
        @ConfigOption(name = "Scale", desc = "Scale of the pests overlay text.")
        @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
        public float scale = 1.0f;

        @Expose
        @ConfigOption(name = "Background Color", desc = "Background color of the pests overlay.")
        @ConfigEditorColour
        public String backgroundColor = "0:0:0:0:0";

        @Expose
        public Position pos = new Position(5, 5, false, false);
    }

    public static class VisitorOverlaySettings {
        @Expose
        @ConfigOption(name = "Visitors Overlay", desc = "Show active garden visitors.")
        @ConfigEditorBoolean
        public boolean enabled = true;

        @Expose
        @ConfigOption(name = "Position", desc = "Edit the visitors overlay position.")
        @ConfigEditorButton(runnableId = 29, buttonText = "Edit")
        public String editPosition = "";

        @Expose
        @ConfigOption(name = "Scale", desc = "Scale of the visitors overlay text.")
        @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
        public float scale = 1.0f;

        @Expose
        @ConfigOption(name = "Background Color", desc = "Background color of the visitors overlay.")
        @ConfigEditorColour
        public String backgroundColor = "0:0:0:0:0";

        @Expose
        public Position pos = new Position(5, 25, false, false);
    }

    public static class CropMilestoneOverlaySettings {
        @Expose
        @ConfigOption(name = "Crop Milestone Overlay", desc = "Show the current crop milestone and progress.")
        @ConfigEditorBoolean
        public boolean enabled = true;

        @Expose
        @ConfigOption(name = "Position", desc = "Edit the crop milestone overlay position.")
        @ConfigEditorButton(runnableId = 30, buttonText = "Edit")
        public String editPosition = "";

        @Expose
        @ConfigOption(name = "Scale", desc = "Scale of the crop milestone overlay text.")
        @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
        public float scale = 1.0f;

        @Expose
        @ConfigOption(name = "Background Color", desc = "Background color of the crop milestone overlay.")
        @ConfigEditorColour
        public String backgroundColor = "0:0:0:0:0";

        @Expose
        public Position pos = new Position(5, 65, false, false);
    }

    public static class PestPlotGrillSettings {
        @Expose
        @ConfigOption(name = "Pest Plot Grill", desc = "Show a grill around plots that contain pests.")
        @ConfigEditorBoolean
        public boolean enabled = true;

        @Expose
        @ConfigOption(name = "Grill Color", desc = "Color of the plot grill.")
        @ConfigEditorColour
        public String color = "0:255:255:170:0";

        @Expose
        @ConfigOption(name = "Line Width", desc = "Width of the plot grill lines.")
        @ConfigEditorSlider(minValue = 1.0f, maxValue = 5.0f, minStep = 1.0f)
        public float lineWidth = 2.0f;

        @Expose
        @ConfigOption(name = "Grid Spacing", desc = "Spacing between the grill bars in blocks.")
        @ConfigEditorSlider(minValue = 2.0f, maxValue = 32.0f, minStep = 1.0f)
        public float gridSpacing = 8.0f;
    }

    public static class PestEspSettings {
        @Expose
        @ConfigOption(name = "Pest Finder", desc = "Show a one-block box around every detected pest.")
        @ConfigEditorBoolean
        public boolean enabled = true;

        @Expose
        @ConfigOption(name = "Box Color", desc = "Color of pest boxes.")
        @ConfigEditorColour
        public String boxColor = "0:0:255:0:0";

        @Expose
        @ConfigOption(name = "Box Line Width", desc = "Width of pest boxes.")
        @ConfigEditorSlider(minValue = 1.0f, maxValue = 5.0f, minStep = 1.0f)
        public float boxLineWidth = 2.0f;

        @Expose
        @ConfigOption(name = "Box Through Walls", desc = "Render pest boxes through walls.")
        @ConfigEditorBoolean
        public boolean boxThroughWalls = true;

        @Expose
        @ConfigOption(name = "Tracers", desc = "Draw a tracer from the player to each pest.")
        @ConfigEditorBoolean
        public boolean tracers = true;

        @Expose
        @ConfigOption(name = "Tracer Color", desc = "Color of pest tracers.")
        @ConfigEditorColour
        public String tracerColor = "0:0:255:0:0";

        @Expose
        @ConfigOption(name = "Tracer Line Width", desc = "Width of pest tracers.")
        @ConfigEditorSlider(minValue = 1.0f, maxValue = 8.0f, minStep = 1.0f)
        public float tracerLineWidth = 2.0f;

        @Expose
        @ConfigOption(name = "Tracers Through Walls", desc = "Render pest tracers through walls.")
        @ConfigEditorBoolean
        public boolean tracerThroughWalls = true;
    }

    public static class VacuumPestsBagSettings {
        @Expose
        @ConfigOption(name = "Vacuum Pests Bag", desc = "Show the pests stored in vacuum bags as a gold stack count.")
        @ConfigEditorBoolean
        public boolean enabled = true;

        @Expose
        @ConfigOption(name = "Count Color", desc = "Color used for the vacuum bag count.")
        @ConfigEditorColour
        public String color = "0:255:255:170:0";
    }
}

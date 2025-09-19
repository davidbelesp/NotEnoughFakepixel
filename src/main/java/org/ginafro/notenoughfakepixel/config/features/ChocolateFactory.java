package org.ginafro.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import org.ginafro.notenoughfakepixel.config.gui.core.config.Position;
import org.ginafro.notenoughfakepixel.config.gui.core.config.annotations.*;

public class ChocolateFactory {

    @Expose
    @ConfigOption(name = "Show Waypoints on Chocolate Eggs", desc = "Show waypoints on chocolate eggs.")
    @ConfigEditorBoolean
    public boolean chocolateChocolateEggWaypoints = true;

    @Expose
    @ConfigOption(name = "Show best upgrade on Chocolate factory", desc = "Show the best upgrade available.")
    @ConfigEditorBoolean
    public boolean chocolateChocolateShowBestUpgrade = true;

    @Expose
    @ConfigOption(name = "Chocolate Eggs Waypoints Color", desc = "Color of chocolate eggs waypoints.")
    @ConfigEditorColour
    public String chocolateChocolateEggWaypointsColor = "0:210:105:30:255";

    @Expose
    @ConfigOption(name = "Chocolate Egg Timer", desc = "Show a timer for the next chocolate egg spawn.")
    @ConfigEditorBoolean
    public boolean chocolateEggTimer = false;

    @Expose
    @ConfigOption(name = "Notify on new Egg Hunt day", desc = "Notifies in the screen when new egg hunt day starts.")
    @ConfigEditorBoolean
    public boolean huntDayNotifier = false;

    @Expose
    @ConfigOption(name = "Scale", desc = "Scale of the Egg Timer.")
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    public float eggTimerScale = 1.0f;

    @Expose
    @ConfigOption(name = "Position", desc = "Position of the Egg Timer.")
    @ConfigEditorButton(runnableId = "editEggTimerPos", buttonText = "Edit")
    public String editEggTimerPos = "";

    @Expose
    public Position eggTimerPos = new Position(0, 0, true, true);

}
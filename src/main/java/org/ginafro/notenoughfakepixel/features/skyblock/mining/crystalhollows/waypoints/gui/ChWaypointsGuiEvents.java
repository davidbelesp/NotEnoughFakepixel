package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterKeybind;
import org.lwjgl.input.Keyboard;

@RegisterEvents
public class ChWaypointsGuiEvents {

    @RegisterKeybind
    public static final KeyBinding CHW_OPEN_WAYPOINTS =
            new KeyBinding("Open Waypoints GUI", Keyboard.KEY_M, "NotEnoughFakepixel");

    @RegisterKeybind
    public static final KeyBinding CHW_NEW_WAYPOINT =
            new KeyBinding("Create new Waypoint", Keyboard.KEY_B, "NotEnoughFakepixel");

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (mc.currentScreen == null && CHW_OPEN_WAYPOINTS.isPressed()) {
            mc.displayGuiScreen(new GuiWaypointManager());
        }
        if (mc.currentScreen == null && CHW_NEW_WAYPOINT.isPressed()) {
            mc.displayGuiScreen(new GuiWaypointCreate());
        }
    }

}

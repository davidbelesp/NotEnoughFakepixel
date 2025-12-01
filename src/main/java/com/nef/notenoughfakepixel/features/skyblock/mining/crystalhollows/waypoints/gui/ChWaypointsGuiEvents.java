package com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.gui;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.config.KeybindHelper;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@RegisterEvents
public class ChWaypointsGuiEvents {

    private final Minecraft mc = Minecraft.getMinecraft();

    private static long lastExecuted = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        handleWaypointGUI(Config.feature.mining.chWaypointsGUI, new GuiWaypointManager());
        handleWaypointGUI(Config.feature.mining.chNewWaypointGUI, new GuiWaypointCreate());
    }

    private void handleWaypointGUI(int key, GuiScreen guiScreen) {
        // ignore if Chat is open
        if (lastExecuted == 0) lastExecuted = System.currentTimeMillis();
        if (System.currentTimeMillis() - lastExecuted < 200) return;

        if (mc.currentScreen != null && mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) {
            return;
        }
        if (KeybindHelper.isKeyDown(key)) {
            if (!SkyblockData.getCurrentLocation().equals(Location.CRYSTAL_HOLLOWS)) {
                mc.thePlayer.addChatMessage(new ChatComponentText("§cYou can only use waypoints in Crystal Hollows"));
                lastExecuted = System.currentTimeMillis();
                return;
            }
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(guiScreen);
            }
        }
    }

}

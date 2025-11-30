package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class AutoCloseChests {

    private static final String TARGET_CHEST_NAME = "Secret Chest";

    @SubscribeEvent
    public void onGuiBackgroundRender(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!Config.feature.dungeons.dungeonsAutoCloseChests) return; // Check if the feature is enabled
        if (!SkyblockData.getCurrentLocation().isDungeon()) return; // Check if the player is in a dungeon
        if (event.gui == null) return;

        // Check if the GUI is a chest
        if (event.gui instanceof GuiChest) {
            GuiChest chestGui = (GuiChest) event.gui;
            ContainerChest chestContainer = (ContainerChest) chestGui.inventorySlots;
            // Get the name of the chest
            String chestName = chestContainer.getLowerChestInventory().getDisplayName().getUnformattedText();
            // Check if the chest name matches the target and cancel the event
            if (TARGET_CHEST_NAME.equals(chestName)) {
                Minecraft.getMinecraft().thePlayer.closeScreen(); // Close the chest
            }
        }
    }

}

package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.List;

@RegisterEvents
public class MiddleClickEvent {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final List<String> menuWhitelist = Arrays.asList(
            "Skyblock Menu", "Chronomatron", "Experiment Over", "Ultrasequencer", "Superpairs",
            "Coop Bank Account", "Booster Cookie", "Pets", "Storage", "Your Museum", "Museum",
            "Settings", "Autopet", "Quest Log", "Trades", "Profiles"

    );

    private static final List<String> itemWhitelist = Arrays.asList(
            "Go Back", "Close", "« First Page", "Last Page »", "Next Page →", "← Previous Page", "Enable Favorites",
            "Chocolate", "Rabbit", "CLICK ME"
    );

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (!Config.feature.qol.qolMiddleClickChests) return;
        if (Mouse.getEventButton() != 0 || !Mouse.getEventButtonState()) return;
        if (!(mc.currentScreen instanceof GuiChest)) return;


        GuiChest chestGui = (GuiChest) mc.currentScreen;
        ContainerChest container = (ContainerChest) chestGui.inventorySlots;
        String chestName = container.getLowerChestInventory().getDisplayName().getUnformattedText();

        if (!isValidClick(chestGui)) return;

        // Exclusion checks
        if (menuWhitelist.stream().anyMatch(name -> name.contains(chestName))) {
            registerMiddleClickEvent(event, chestGui);
            return;
        }

        ItemStack item = container.getSlot(chestGui.getSlotUnderMouse().slotNumber).getStack();
        if (ItemUtils.isMenuItem(item) || itemWhitelist.stream().anyMatch(itemName -> item.getDisplayName().contains(itemName))) {
            registerMiddleClickEvent(event, chestGui);
        }
    }

    private boolean isValidClick(GuiChest chestGui) {
        ContainerChest container = (ContainerChest) chestGui.inventorySlots;
        if (chestGui.getSlotUnderMouse() == null || container == null || container.getSlot(chestGui.getSlotUnderMouse().slotNumber).getStack() == null)
            return false;
        return container.getSlot(chestGui.getSlotUnderMouse().slotNumber).getStack().getDisplayName() != null;
    }


    private void registerMiddleClickEvent(GuiScreenEvent.MouseInputEvent.Pre event, GuiChest chestGui) {
        ContainerChest container = (ContainerChest) chestGui.inventorySlots;

        event.setCanceled(true);

        int slot = chestGui.getSlotUnderMouse() != null ? chestGui.getSlotUnderMouse().slotNumber : -1;

        if (slot >= 0) {
            mc.playerController.windowClick(
                    container.windowId,
                    slot,
                    2,
                    0,
                    mc.thePlayer
            );
        }
    }
}

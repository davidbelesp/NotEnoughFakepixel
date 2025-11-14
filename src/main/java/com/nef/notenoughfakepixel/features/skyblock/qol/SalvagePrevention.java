package com.nef.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.Logger;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.variables.Rarity;
import org.lwjgl.input.Mouse;

@RegisterEvents
public class SalvagePrevention {

    private final Minecraft mc = Minecraft.getMinecraft();

    private static final int[] salvageSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
    };

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Mouse.getEventButton() < 0
                || !ScoreboardUtils.currentGamemode.isSkyblock()
                || !(mc.currentScreen instanceof GuiChest)
                || (!Config.feature.qol.salvageLegendaryPrevention && !Config.feature.qol.salvageEpicPrevention)) return;

        GuiChest chestGui = (GuiChest) mc.currentScreen;
        ContainerChest container = (ContainerChest) chestGui.inventorySlots;

        if (!isValidClick(chestGui)) return;

        for (int slot : salvageSlots) {
            ItemStack item = container.getSlot(slot).getStack();
            if (item == null) continue;

            Rarity rarity = ItemUtils.getRarity(item);
            if ((rarity == Rarity.LEGENDARY && Config.feature.qol.salvageLegendaryPrevention)
                    || (rarity == Rarity.EPIC && Config.feature.qol.salvageEpicPrevention)) {
                cancelEvents(event, (rarity == Rarity.LEGENDARY ? " Legendary item, " : "n Epic item, ") + item.getDisplayName());
                return;
            }
        }
    }

    private void cancelEvents(GuiScreenEvent.MouseInputEvent.Pre event, String itemName) {
        event.setCanceled(true);
        Logger.logOnlyOnce("\u00a7cNEF prevented you from salvaging a" + itemName);
    }

    private boolean isValidClick(GuiChest chestGui) {
        ContainerChest container = (ContainerChest) chestGui.inventorySlots;
        if (container == null) return false;

        String chestName = container.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (chestName == null || !chestName.contains("Salvage Item")) return false;

        Slot slotClicked = chestGui.getSlotUnderMouse();
        if (slotClicked == null || slotClicked.getSlotIndex() != 40) return false;

        ItemStack item = container.getSlot(slotClicked.getSlotIndex()).getStack();
        return item != null
                && item.getDisplayName().contains("Salvage Item")
                && Block.getBlockFromItem(item.getItem()) == Blocks.beacon;
    }

}

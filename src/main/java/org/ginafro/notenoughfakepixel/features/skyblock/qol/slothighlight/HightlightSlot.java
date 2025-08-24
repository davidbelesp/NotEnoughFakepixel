package org.ginafro.notenoughfakepixel.features.skyblock.qol.slothighlight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.utils.InventoryUtils;
import org.ginafro.notenoughfakepixel.utils.ItemUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.Gamemode;

import java.awt.*;

public abstract class HightlightSlot {

    // The lore line that this feature applies to
    public abstract String getLoreLine();

    // The name of the item that this feature applies to
    public abstract String getName();

    // The name of the container (chest) that this feature applies to
    public abstract String getContainerName();

    // The config option (or boolean expression) that enables this feature
    public abstract boolean getConfigOption();

    // This is used to check if the feature should only be enabled in Skyblock
    public boolean onlyInSkyblock() {
        return true;
    }

    // This is the color the slot is highlighted with
    public Color getHighlightColor() {
        return new Color(55, 255, 55);
    }

    // This is used to check if the feature should only highlight the first slot found
    public boolean highlightOnlyFirst() {
        return false;
    }

    @SubscribeEvent
    public void onOpen(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!getConfigOption()
                || (onlyInSkyblock() && ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK)
                || !(e.gui instanceof GuiChest)
                || !checkForEssentials()) return;

        GuiChest chest = (GuiChest) e.gui;
        Container container = chest.inventorySlots;

        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.startsWith(getContainerName())) return;

        for (Slot slot : container.inventorySlots) {
            if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;

            ItemStack item = slot.getStack();
            if (item == null || item.getItem() == null) continue;

            boolean matchesLore = getLoreLine() != null && !getLoreLine().isEmpty() && ItemUtils.getLoreLine(item, getLoreLine()) != null;
            boolean matchesName = getName() != null && !getName().isEmpty() && item.getDisplayName().contains(getName());

            if (matchesLore || matchesName) {
                highlightSlot(slot, chest);
                if (highlightOnlyFirst()) return;
            }
        }
    }

    // Check if the lore line, name, and container name are not null or empty
    private boolean checkForEssentials() {
        return (getLoreLine() != null && !getLoreLine().isEmpty())
                || (getName() != null && !getName().isEmpty())
                && getContainerName() != null && !getContainerName().isEmpty();
    }

    // Highlight the slot with the specified color
    private void highlightSlot(Slot slot, GuiChest chest) {
        InventoryUtils.highlightSlot(slot, chest, getHighlightColor());
    }
}

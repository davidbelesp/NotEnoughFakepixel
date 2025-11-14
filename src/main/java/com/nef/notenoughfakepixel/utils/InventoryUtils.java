package com.nef.notenoughfakepixel.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class InventoryUtils {

    public static int getCurrentSlot() {
        return Minecraft.getMinecraft().thePlayer.inventory.currentItem;
    }

    public static void goToSlot(int targetSlot) {
        int currentSlot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
        if (targetSlot > currentSlot) {
            for (int i = 0; i < targetSlot - currentSlot; i++) {
                Minecraft.getMinecraft().thePlayer.inventory.changeCurrentItem(-1);
            }
        } else {
            for (int i = 0; i < currentSlot - targetSlot; i++) {
                Minecraft.getMinecraft().thePlayer.inventory.changeCurrentItem(1);
            }
        }
    }

    public static ItemStack getHeldItem() {
        return Minecraft.getMinecraft().thePlayer.getHeldItem();
    }

    public static int getSlot(String name) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        for (int i = 0; i < 7; i++) {
            if (p.inventory.mainInventory[i] != null) {
                if (p.inventory.mainInventory[i].getDisplayName().contains(name)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void highlightSlot(Slot slot, GuiContainer container, Color color) {
        RenderUtils.drawOnSlot(container.inventorySlots.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, color.getRGB());
    }

    public static boolean searchForItemInInventory(String skyblockID) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return false;
        InventoryPlayer inventory = player.inventory;
        if (inventory == null) return false;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) continue;
            if (ItemUtils.getInternalName(stack).equals(skyblockID)) {
                return true;
            }
        }
        return false;
    }
}

package com.nef.notenoughfakepixel.events;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ReplaceItemEvent extends Event {

    public boolean post() {
        MinecraftForge.EVENT_BUS.post(this);
        return isCancelable() && isCanceled();
    }

    public void cancel() {
        setCanceled(true);
    }

    final ItemStack original;
    final IInventory inventory;
    final int slotNumber;
    ItemStack replaceWith;

    public ReplaceItemEvent(ItemStack original, IInventory inventory, int slotNumber) {
        this.original = original;
        this.inventory = inventory;
        this.slotNumber = slotNumber;
        this.replaceWith = original;
    }

    public ItemStack getOriginal() {
        return original;
    }

    public IInventory getInventory() {
        return inventory;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public ItemStack getReplacement() {
        return replaceWith;
    }

    public void replaceWith(ItemStack is) {
        this.replaceWith = is;
    }
}

package com.nef.notenoughfakepixel.features.skyblock.overlays.inventory.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class EquipmentData {

    public Map<Integer, String> items;

    public EquipmentData(Map<Integer, ItemStack> map) {
        this.items = new HashMap<>();
        int in = 0;
        for (Integer i : map.keySet()) {
            ItemStack stack = map.get(i);
            if (stack != null) {
                items.put(in, itemStackToJson(stack));
            }
            in++;
        }
    }

    public static String itemStackToJson(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        stack.writeToNBT(nbt);
        return nbt.toString();
    }

    public static ItemStack jsonToItemStack(String json) throws NBTException {
        NBTTagCompound nbt = net.minecraft.nbt.JsonToNBT.getTagFromJson(json);
        return ItemStack.loadItemStackFromNBT(nbt);
    }

    public Map<Integer, ItemStack> getItemStacks() throws NBTException {
        Map<Integer, ItemStack> itemStacks = new HashMap<>();
        for (Map.Entry<Integer, String> entry : items.entrySet()) {
            itemStacks.put(entry.getKey(), jsonToItemStack(entry.getValue())); // Convert JSON back to ItemStack
        }
        return itemStacks;
    }

}

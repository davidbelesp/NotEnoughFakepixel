package org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

//public class StorageData {
//    public String chestName;
//    public Map<Integer, String> items;
//
//    public StorageData(ContainerChest chest) {
//        String s = chest.getLowerChestInventory().getDisplayName().getUnformattedText();
//        if (s.contains("Backpack")) {
//            String[] a = s.split(" ");
//            String bagNumber = a[a.length - 1].split("/")[0];
//            chestName = "Backpack" + bagNumber;
//        } else {
//            chestName = s;
//        }
//        this.items = new HashMap<>();
//        for (int i = 9; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
//            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);
//            if (stack != null) {
//                items.put((i - 9), itemStackToJson(stack)); // Convert ItemStack to JSON
//            }
//        }
//    }
//
//    public static String itemStackToJson(ItemStack stack) {
//        NBTTagCompound nbt = new NBTTagCompound();
//        stack.writeToNBT(nbt);
//
//        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//             DataOutputStream dataOut = new DataOutputStream(byteOut)) {
//            CompressedStreamTools.write(nbt, dataOut);
//            return Base64.getEncoder().encodeToString(byteOut.toByteArray());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to convert ItemStack to Base64 JSON", e);
//        }
//    }
//
//    public static ItemStack jsonToItemStack(String base64) throws NBTException {
//        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
//             DataInputStream dataIn = new DataInputStream(byteIn)) {
//            NBTTagCompound nbt = CompressedStreamTools.read(dataIn);
//            return ItemStack.loadItemStackFromNBT(nbt);
//        } catch (Exception e) {
//            throw new NBTException("Failed to read ItemStack from Base64 JSON: " + e.getMessage());
//        }
//    }
//
//    public Map<Integer, ItemStack> getItemStacks() throws NBTException {
//        Map<Integer, ItemStack> itemStacks = new HashMap<>();
//        for (Map.Entry<Integer, String> entry : items.entrySet()) {
//            itemStacks.put(entry.getKey(), jsonToItemStack(entry.getValue()));
//        }
//        return itemStacks;
//    }
//}
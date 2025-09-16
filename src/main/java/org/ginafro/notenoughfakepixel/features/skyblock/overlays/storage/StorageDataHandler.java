package org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.SlotClickEvent;
import org.ginafro.notenoughfakepixel.utils.ConfigHandler;
//
//@RegisterEvents
//public class StorageDataHandler {
//
//    @SubscribeEvent
//    public void onOpen(GuiOpenEvent e) {
//        if (e.gui instanceof GuiContainer) {
//            if (((GuiContainer) e.gui).inventorySlots instanceof ContainerChest) {
//                ContainerChest chest = (ContainerChest) ((GuiContainer) e.gui).inventorySlots;
//                if (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Ender Chest")) {
//                    StorageData data = new StorageData(chest);
//                    CustomConfigHandler.saveStorageData(data);
//                    return;
//                }
//                System.out.println(chest.getLowerChestInventory().getDisplayName().getUnformattedText());
//                if (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Backpack")) {
//                    StorageData data = new StorageData(chest);
//                    CustomConfigHandler.saveStorageData(data);
//                }
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public void onSlot(SlotClickEvent e) {
//        if (e.guiContainer.inventorySlots instanceof ContainerChest) {
//            ContainerChest chest = (ContainerChest) e.guiContainer.inventorySlots;
//            if (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Ender Chest")) {
//                StorageData data = new StorageData(chest);
//                CustomConfigHandler.saveStorageData(data);
//
//            }
//            if (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Backpack")) {
//                StorageData data = new StorageData(chest);
//                CustomConfigHandler.saveStorageData(data);
//
//            }
//        }
//    }
//}


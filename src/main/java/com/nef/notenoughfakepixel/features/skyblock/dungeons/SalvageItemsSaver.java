package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ChatUtils;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import com.nef.notenoughfakepixel.utils.StringUtils;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

@RegisterEvents
public class SalvageItemsSaver {

    String legendaryPattern = "LEGENDARY";
    String mythicPattern = "MYTHIC";
    String[] starredItems = new String[]{"Wither Cloak Sword",
            "Jerry-chine Gun",
            "Bonzo's Staff",
            "Bonzo's Mask",
            "Spirit Mask",
            "Juju Shortbow",
            "Shadow Assassin",
            "Tarantula"};

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (SkyblockData.getCurrentLocation() != Location.DUNGEON_HUB) return;
        if (!Config.feature.dungeons.dungeonsSalvageItemsPrevention) return;
        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            Container container = chest.inventorySlots;
            if (container instanceof ContainerChest) {
                String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
                if (!title.startsWith("Salvage Item")) return;
                if (container.getSlot(31) == null || container.getSlot(31).getStack() == null) return;
                if (Block.getBlockFromItem(container.getSlot(31).getStack().getItem()) == Blocks.beacon) {
                    if (ItemUtils.getLoreLine(container.getSlot(31).getStack(), "[NEF]") != null) return;
                    ItemUtils.addLoreLine(container.getSlot(31).getStack(), "");
                    ItemUtils.addLoreLine(container.getSlot(31).getStack(), "[NEF] Salvage prevention enabled");
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (SkyblockData.getCurrentLocation() != Location.DUNGEON_HUB) return;
        if (!Config.feature.dungeons.dungeonsSalvageItemsPrevention) return;
        if (!Mouse.getEventButtonState()) return;
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest))
            return; // Check if the current screen is a chest GUI
        GuiChest chestGui = (GuiChest) Minecraft.getMinecraft().currentScreen;
        Container container = chestGui.inventorySlots;
        if (container instanceof ContainerChest) {
            String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
            if (!title.equals("Salvage Item")) return;
            Slot slot = chestGui.getSlotUnderMouse();
            if (slot == null || slot.getStack() == null || slot.inventory == Minecraft.getMinecraft().thePlayer.inventory)
                return;
            if (Block.getBlockFromItem(slot.getStack().getItem()) == Blocks.beacon) {
                ItemStack itemToSavage = container.getSlot(22).getStack();
                if (itemToSavage == null) return;
                String loreLegendary = ItemUtils.getLoreLine(itemToSavage, legendaryPattern);
                String loreMythic = ItemUtils.getLoreLine(itemToSavage, mythicPattern);
                int stars = ItemUtils.getExtraAttributesIntTag(container.getSlot(22).getStack(), "stars");
                boolean starredItem = StringUtils.containsSubstring(starredItems, itemToSavage.getDisplayName());
                if (loreLegendary != null || loreMythic != null || stars > 0 || starredItem) {
                    event.setCanceled(true);
                    int[] cords = new int[]{Minecraft.getMinecraft().thePlayer.getPosition().getX(), Minecraft.getMinecraft().thePlayer.getPosition().getY(), Minecraft.getMinecraft().thePlayer.getPosition().getZ()};
                    SoundUtils.playSound(cords, "mob.villager.no", 2.0f, 1.0f);
                    ChatUtils.notifyChat(EnumChatFormatting.BLUE + "[NEF] " + EnumChatFormatting.RED + "Saved you from salvaging an important item (legendary+ or starred one). You can disable this feature in Dungeons QOL section.");
                }
            }
        }
    }
}

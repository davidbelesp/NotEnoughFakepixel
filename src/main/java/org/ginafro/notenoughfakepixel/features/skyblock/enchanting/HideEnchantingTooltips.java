package org.ginafro.notenoughfakepixel.features.skyblock.enchanting;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;

@RegisterEvents
public class HideEnchantingTooltips {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTooltipLow(ItemTooltipEvent event) {
        if (!NotEnoughFakepixel.feature.experimentation.experimentationHideTooltips) return;
        if (event.toolTip == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (mc.currentScreen instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) player.openContainer;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText();

            if ((chestName.startsWith("Chronomatron (") ||
                    chestName.startsWith("Ultrasequencer (")) &&
                    event.itemStack.getItem() != Items.clock &&
                    Block.getBlockFromItem(event.itemStack.getItem()) != Blocks.bookshelf) {
                event.toolTip.clear();
            }
        }
    }
}

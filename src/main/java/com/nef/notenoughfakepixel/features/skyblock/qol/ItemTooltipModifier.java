package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemTooltipModifier {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onItemTooltipFirst(ItemTooltipEvent e) {
        ItemStack hoveredItem = e.itemStack;
        if (e.toolTip == null) return;

        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (hoveredItem == null) {
        }



    }


}

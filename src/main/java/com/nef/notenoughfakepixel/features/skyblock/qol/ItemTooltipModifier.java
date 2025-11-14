package com.nef.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;

public class ItemTooltipModifier {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onItemTooltipFirst(ItemTooltipEvent e) {
        ItemStack hoveredItem = e.itemStack;
        if (e.toolTip == null) return;

        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (hoveredItem == null) {
        }



    }


}

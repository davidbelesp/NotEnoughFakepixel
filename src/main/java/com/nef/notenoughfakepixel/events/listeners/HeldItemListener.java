package com.nef.notenoughfakepixel.events.listeners;

import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@RegisterEvents
public class HeldItemListener {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            SkyblockData.setLastHandItemId("");
            return;
        }

        ItemStack heldItem = minecraft.thePlayer.getHeldItem();
        SkyblockData.setLastHandItemId(ItemUtils.getInternalName(heldItem));
    }
}

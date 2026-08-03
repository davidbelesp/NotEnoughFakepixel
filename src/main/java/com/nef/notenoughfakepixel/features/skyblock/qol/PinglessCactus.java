package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;

@RegisterEvents
public class PinglessCactus {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        if (Config.feature == null || Config.feature.qol == null
                || Config.feature.qol.pinglessCactus == null
                || !Config.feature.qol.pinglessCactus.enabled) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || event.entityPlayer != mc.thePlayer) return;
        if (event.pos == null || !isCactus(mc.theWorld.getBlockState(event.pos).getBlock())) return;

        if (!isCactusKnife(SkyblockData.getLastHandItemId())) return;

        mc.theWorld.setBlockToAir(new BlockPos(event.pos));
    }

    private static boolean isCactusKnife(String itemId) {
        if (itemId == null || itemId.isEmpty()) return false;
        return itemId.toLowerCase(Locale.ROOT).contains("cactus_knife");
    }

    private static boolean isCactus(Block block) {
        return block == net.minecraft.init.Blocks.cactus;
    }
}

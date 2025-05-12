package org.ginafro.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;

public class CrackedWallHighlight {

    @SubscribeEvent
    public void onRender(RenderBlockOverlayEvent e) {
        World w = e.player.worldObj;
        System.out.println("EVENT");
        if (w.getBlockState(e.blockPos).getBlock() == Blocks.stonebrick && w.getBlockState(e.blockPos).getValue(BlockStoneBrick.VARIANT) == BlockStoneBrick.EnumType.CRACKED) {
            System.out.println("CRACKED BLOCK");
            RenderUtils.highlightBlock(e.blockPos, ColorUtils.getColor(Config.feature.qol.fairySoulWaypointsColor), false, e.renderPartialTicks);
        }
    }

}

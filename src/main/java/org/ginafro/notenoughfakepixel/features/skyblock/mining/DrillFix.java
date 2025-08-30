package org.ginafro.notenoughfakepixel.features.skyblock.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.PacketReadEvent;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;

@RegisterEvents
public class DrillFix {

    /// Method ported from DSM
    /// thanks to Danker for making it public in
    /// https://github.com/bowser0000/SkyblockMod/blob/2b3568c76269dc6ae5053ea6aeee6944f9b79de6/src/main/java/me/Danker/features/DrillFix.java


    @SubscribeEvent
    public void onPacketRead(PacketReadEvent event) {
        if (!Config.feature.mining.miningDrillFix) return;
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!TablistParser.currentLocation.equals(Location.DWARVEN) && !TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS)) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        boolean isDown = mc.gameSettings.keyBindAttack.isKeyDown();

        if (player == null || player.inventory == null) return;

        if (event.packet instanceof S2FPacketSetSlot) {
            S2FPacketSetSlot packet = (S2FPacketSetSlot) event.packet;
            int windowId = packet.func_149175_c();
            int slot = packet.func_149173_d();
            ItemStack item = packet.func_149174_e();

            if (item != null && windowId == 0 && slot - 36 == player.inventory.currentItem && item.getItem().equals(Items.prismarine_shard) && isDown) {
                event.setCanceled(true);
            }
        } else if (event.packet instanceof S30PacketWindowItems) {
            S30PacketWindowItems packet = (S30PacketWindowItems) event.packet;
            ItemStack[] items = packet.getItemStacks();

            if (items.length == 45) {
                int slot = player.inventory.currentItem + 36;
                ItemStack item = items[slot];

                if (item != null && item.getItem().equals(Items.prismarine_shard) && isDown) {
                    items[slot] = player.inventory.getCurrentItem();
                    event.packet = packet;
                }
            }
        }
    }
}

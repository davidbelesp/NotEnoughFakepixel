package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ItemUtils;

@RegisterEvents
public class DisableHyperionExplosions {

    public static long lastClickedHyperion = 0;

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {

            ItemStack heldItem = event.entityPlayer.getHeldItem();

            if (heldItem == null || !heldItem.hasTagCompound()) return;

            String itemID = ItemUtils.getInternalName(heldItem);
            if (!itemID.isEmpty()) {
                if (itemID.equals("HYPERION") ||
                    itemID.equals("VALKYRIE") ||
                    itemID.equals("SCYLLA") ||
                    itemID.equals("ASTRAEA")) {
                    NBTTagCompound tag = heldItem.getTagCompound();
                    if (tag != null && tag.hasKey("ExtraAttributes")) {
                        NBTTagCompound extraAttributes = tag.getCompoundTag("ExtraAttributes");
                        if (extraAttributes != null && extraAttributes.hasKey("implosion")) {
                            lastClickedHyperion = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
    }

}

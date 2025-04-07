package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumChatFormatting;
import org.ginafro.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import org.ginafro.notenoughfakepixel.utils.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Inject(method = "dropOneItem", at = @At("HEAD"), cancellable = true)
    public void dropOneItem(CallbackInfoReturnable<EntityItem> ci) {

        int slot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
        if (SlotLocking.getInstance().isSlotIndexLocked(slot) || SlotLocking.getInstance().isSwapedSlotLocked()) {
            ci.cancel();
            ChatUtils.notifyChat(EnumChatFormatting.RED + "NotEnoughFakepixel has prevented you from dropping that locked item!");
        }
    }
}

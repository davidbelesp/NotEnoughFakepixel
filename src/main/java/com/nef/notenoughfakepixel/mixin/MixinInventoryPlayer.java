package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.events.ReplaceItemEvent;
import com.nef.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public class MixinInventoryPlayer {
    @Inject(method = "changeCurrentItem", at = @At("HEAD"))
    public void changeCurrentItemHead(int direction, CallbackInfo ci) {
        InventoryPlayer $this = (InventoryPlayer) (Object) this;

        SlotLocking.getInstance().changedSlot($this.currentItem);
    }

    @Shadow
    public ItemStack[] mainInventory;
    @Shadow
    public ItemStack[] armorInventory;

    @Inject(method = "getStackInSlot", at = @At("HEAD"), cancellable = true)
    public void on(int index, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack =
                index < mainInventory.length
                        ? this.mainInventory[index]
                        : this.armorInventory[index - mainInventory.length];
        ReplaceItemEvent replaceItemEventInventory = new ReplaceItemEvent(
                itemStack,
                ((InventoryPlayer) (Object) this),
                index
        );
        replaceItemEventInventory.post();
        if (replaceItemEventInventory.getReplacement() != replaceItemEventInventory.getOriginal()) {
            cir.setReturnValue(replaceItemEventInventory.getReplacement());
        }
    }
}

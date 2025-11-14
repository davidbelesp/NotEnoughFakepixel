package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.Minecraft;
import com.nef.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I", opcode = Opcodes.PUTFIELD))
    public void currentItemMixin(CallbackInfo ci) {
        SlotLocking.getInstance().changedSlot(Minecraft.getMinecraft().thePlayer.inventory.currentItem);
    }

}

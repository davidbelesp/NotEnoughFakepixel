package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public class MixinGuiInventory {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
//        if (!InvManager.isEditor) {
//            InvManager.drawButtons();
//        }
    }


}

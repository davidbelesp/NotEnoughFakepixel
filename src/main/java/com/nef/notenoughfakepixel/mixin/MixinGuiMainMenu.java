package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void onInitGui(CallbackInfo ci) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        fr.drawString(Minecraft.getMinecraft().getSession().getUsername(), 15, 15, -1);
    }
}

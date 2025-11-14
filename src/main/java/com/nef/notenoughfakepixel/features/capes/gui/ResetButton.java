package com.nef.notenoughfakepixel.features.capes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ResetButton extends GuiButton {

    public ResourceLocation tex = new ResourceLocation("notenoughfakepixel", "cape_gui.png");


    public ResetButton(int buttonId, int x, int y, int widthIn, int heightIn) {
        super(buttonId, x, y, widthIn, heightIn, "NO CAPE");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (enabled && visible) {
            GlStateManager.color(1f, 1f, 1f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
            drawScaledCustomSizeModalRect(xPosition, yPosition, 0, 0, width, height, width, height, width, height);
            drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + height / 2, -1);
        }
    }
}

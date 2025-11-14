package com.nef.notenoughfakepixel.features.capes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import com.nef.notenoughfakepixel.features.capes.Cape;
import com.nef.notenoughfakepixel.features.capes.CapeManager;
import com.nef.notenoughfakepixel.features.capes.CapePreviewRenderer;

public class CapeButton extends GuiButton {

    public Cape ca;
    public ResourceLocation tex;

    public CapeButton(int buttonId, int x, int y, int widthIn, int heightIn, Cape c, ResourceLocation tex) {
        super(buttonId, x, y, widthIn, heightIn, "");
        ca = c;
        this.tex = tex;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.enabled && this.visible) {
            GlStateManager.color(1f, 1f, 1f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
            drawScaledCustomSizeModalRect(xPosition, yPosition, 0, 0, width, height, width, height, width, height);
            CapePreviewRenderer.drawCape(xPosition + width / 2, yPosition + 15, 90, ca);
            if (ca != null) {
                CapePreviewRenderer.drawCape(xPosition + width / 2, yPosition + 15, 90, ca);
            }
            drawName(mc);
        }
    }

    private void drawName(Minecraft mc) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPosition + (float) (width / 2), yPosition + height - 30, 0);
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
        drawCenteredString(mc.fontRendererObj, ca.capeName, 0, 0, -1);
        GlStateManager.popMatrix();
    }

    public void process() {
        CapeManager.setCape(ca.capeID);
    }

}

package org.ginafro.notenoughfakepixel.features.skyblock.overlays.inventory.invbuttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.ginafro.notenoughfakepixel.utils.Utils;
import org.ginafro.notenoughfakepixel.variables.Resources;
import org.lwjgl.input.Mouse;

import java.awt.*;

@Deprecated
public class ButtonEditor extends GuiButton {

    public ButtonEditor(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

//
//    public boolean clicked = false;
//    public int scrollOffset = 0;
//
//    public ButtonEditor(int buttonId, int x, int y, int widthIn, int heightIn) {
//        super(buttonId, x, y, widthIn, heightIn, "");
//    }
//
//    @Override
//    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
//        if (enabled && visible) {
//            GlStateManager.pushMatrix();
//            GlStateManager.color(1f, 1f, 1f);
//            GlStateManager.enableBlend();
//            GlStateManager.enableAlpha();
//            GlStateManager.disableLighting();
//            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.ITEM_LOCK.getResource());
//            float scale = Utils.getScale();
//            drawScaledCustomSizeModalRect(xPosition, yPosition, 0f, 0f, width, height, width, height, width, height);
//            processClicks(mouseX, mouseY);
//            for (int i = 0; i < 5; i++) {
//                float xStart = xPosition + 11 * Utils.getScale();
//                if (InvManager.selected != null) {
//                    if (InvManager.selected.style == InvStyle.getStyle(i)) {
//                        int x = (int) (xStart + (i * (33 * scale)));
//                        int y = (int) (yPosition + (50 * scale));
//                        drawRect(x, y, x + (int) (32 * scale), y + (int) (32 * scale), new Color(0, 152, 0, 152).getRGB());
//                    }
//                }
//            }
//            GlStateManager.popMatrix();
//        }
//    }
//
//    private void processClicks(int mouseX, int mouseY) {
//        boolean pressed = Mouse.isButtonDown(0);
//        int xOff = mouseX - xPosition;
//        int yOff = mouseY - yPosition;
//        if (yOff > (50 * Utils.getScale()) && yOff < (83 * Utils.getScale())) {
//            float xStart = 11 * Utils.getScale();
//            for (int i = 0; i < 5; i++) {
//                int x = (int) (xStart + (i * (33 * Utils.getScale())));
//                if (xOff >= x && xOff < x + (32 * Utils.getScale())) {
//                    if (InvManager.selected != null) {
//                        if (pressed && !clicked) {
//                            InvManager.selected.style = InvStyle.getStyle(i);
//                        }
//                    }
//                }
//            }
//        }
//
//        clicked = pressed;
//    }

}

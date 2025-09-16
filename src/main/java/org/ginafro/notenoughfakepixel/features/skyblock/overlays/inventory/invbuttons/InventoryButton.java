package org.ginafro.notenoughfakepixel.features.skyblock.overlays.inventory.invbuttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.ginafro.notenoughfakepixel.utils.Utils;

public class InventoryButton {
//
//    public int x, y, id;
//    public int width;
//    public ItemStack logo;
//    public InvStyle style;
//    public String cmd;
//
//    public InventoryButton(int id, int xPos, int yPos, int w, String command, ItemStack stack, InvStyle buttonStyle) {
//        x = xPos;
//        y = yPos;
//        width = w;
//        logo = stack;
//        style = buttonStyle;
//        cmd = command;
//    }
//
//    public void render() {
//        GlStateManager.pushMatrix();
//        GlStateManager.disableLighting();
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.color(1f, 1f, 1f, 1f);
//        GlStateManager.enableAlpha();
//        GlStateManager.enableBlend();
//        GlStateManager.enableDepth();
//        Minecraft.getMinecraft().getTextureManager().bindTexture(style.tex);
//        float scale = Utils.getScale();
//        int xPos = (int) (x * scale);
//        int yPos = (int) (y * scale);
//        int w = (int) (width * scale);
//        Gui.drawScaledCustomSizeModalRect(xPos, yPos, 0f, 0f, w, w, w, w, w, w);
//        drawStackScaled();
//        GlStateManager.popMatrix();
//    }
//
//    private void drawStackScaled() {
//        GlStateManager.pushMatrix();
//        GlStateManager.disableLighting();
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.color(1f, 1f, 1f, 1f);
//        float scale = Utils.getScale();
//        int xPos = (int) (x * scale);
//        int yPos = (int) (y * scale);
//        GlStateManager.translate(xPos, yPos, 0);
//        GlStateManager.scale(scale * ((float) width / 16), scale * ((float) width / 16), scale * ((float) width / 16));
//        RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
//        RenderHelper.enableGUIStandardItemLighting();
//        renderer.renderItemAndEffectIntoGUI(logo, 0, 0);
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.popMatrix();
//    }
//
//    public void process() {
//        Minecraft.getMinecraft().thePlayer.sendChatMessage(cmd.startsWith("/") ? cmd : "/" + cmd);
//    }
//
//
//    public boolean isHovered(int mX, int mY) {
//        float scale = Utils.getScale();
//        int xPos = (int) (x * scale);
//        int yPos = (int) (y * scale);
//        int w = (int) (width * scale);
//        if (mX > xPos && mX < xPos + w) {
//            return mY > yPos && mY < yPos + w;
//        }
//        return false;
//    }

}

package org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.ConfigHandler;

import java.awt.*;
import java.util.Map;

public class StorageButton extends GuiButton {

    public StorageButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

//    public final int n;
//    private Map<Integer, ItemStack> stackMapCache = null;
//    private String typeCache = null;
//    private static final int SLOT_SPACING = 2;
//    private static final int SLOT_COLUMNS = 9;
//    private static final Color SLOT_BG_COLOR = new Color(60, 60, 60, 200);
//    private static final int MAX_ROWS = 6; // Enough for both backpacks and ender chests
//
//    public StorageButton(int buttonId, int x, int y, int widthIn, int heightIn, int no) {
//        super(buttonId, x, y, widthIn, heightIn, "");
//        n = no;
//    }
//
//    @Override
//    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
//        if (!this.enabled || !this.visible) return;
//
//        GlStateManager.pushMatrix();
//        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
//
//        drawRect(xPosition, yPosition, xPosition + width, yPosition + height,
//                ColorUtils.getColor(Config.feature.overlays.chestColor).getRGB());
//
//        boolean isEnderChest = id == n;
//        String type = isEnderChest ? "Ender Chest(Page" + (n + 1) + ")" : "Backpack" + (n + 1);
//        if (!type.equals(typeCache)) {
//            StorageData data = CustomConfigHandler.loadStorageData(type);
//            try {
//                stackMapCache = data != null ? data.getItemStacks() : null;
//            } catch (NBTException e) {
//                stackMapCache = null;
//            }
//            typeCache = type;
//        }
//
//        if (stackMapCache == null || stackMapCache.isEmpty()) {
//            drawRect(xPosition, yPosition, xPosition + width, yPosition + height,
//                    new Color(31, 31, 31, 250).getRGB());
//            drawCenteredString(mc.fontRendererObj, "Open For Preview",
//                    xPosition + width / 2, yPosition + height / 2, -1);
//            GlStateManager.popMatrix();
//            return;
//        }
//
//        int maxRow = 0;
//        for (int slotIndex : stackMapCache.keySet()) {
//            maxRow = Math.max(maxRow, slotIndex / SLOT_COLUMNS);
//        }
//        int rowsNeeded = maxRow + 1;
//
//        // Draw title
//        String title = (isEnderChest ? "Ender Chest" : "Backpack") + " " + (n + 1);
//        float textScale = Math.min(1.0f, (float) width / 150.0f);
//        int titleWidth = mc.fontRendererObj.getStringWidth(title);
//        int titleX = xPosition + (width - (int) (titleWidth * textScale)) / 2;
//        int titleY = yPosition + 5;
//
//        GlStateManager.pushMatrix();
//        GlStateManager.translate(titleX, titleY, 0);
//        GlStateManager.scale(textScale, textScale, 1.0f);
//        mc.fontRendererObj.drawStringWithShadow(title, 0, 0, 0xFFFFFF);
//        GlStateManager.popMatrix();
//
//        float availableWidth = width - 4f - (SLOT_COLUMNS - 1) * SLOT_SPACING;
//        float slotScale = Math.max(availableWidth / (SLOT_COLUMNS * 16f), 0.5f);
//        float slotSize = 16 * slotScale;
//        String searchText = StorageOverlay.overlayRenderer != null ?
//                StorageOverlay.overlayRenderer.searchBar.getText().toLowerCase() : "";
//
//        RenderHelper.enableStandardItemLighting();
//
//        for (Map.Entry<Integer, ItemStack> entry : stackMapCache.entrySet()) {
//            int slotIndex = entry.getKey();
//            int row = slotIndex / SLOT_COLUMNS;
//            int col = slotIndex % SLOT_COLUMNS;
//
//            float slotX = xPosition + 2 + col * (slotSize + SLOT_SPACING);
//            float slotY = yPosition + 15 + row * (slotSize + SLOT_SPACING);
//
//            // Draw slot background
//            GlStateManager.pushMatrix();
//            GlStateManager.translate(slotX, slotY, 0);
//            GlStateManager.scale(slotScale, slotScale, 1.0f);
//            drawRect(0, 0, 16, 16, SLOT_BG_COLOR.getRGB());
//            GlStateManager.popMatrix();
//
//            // Draw item
//            ItemStack stack = entry.getValue();
//            if (!searchText.isEmpty() && stack.getDisplayName().toLowerCase().contains(searchText)) {
//                drawRect((int) slotX, (int) slotY,
//                        (int) (slotX + slotSize),
//                        (int) (slotY + slotSize),
//                        ColorUtils.getColor(Config.feature.overlays.searchColor).getRGB());
//            }
//            renderItemScaled(mc, stack, slotX, slotY, slotScale);
//        }
//
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.popMatrix();
//    }
//
//    private void renderItemScaled(Minecraft mc, ItemStack stack, float x, float y, float scale) {
//        GlStateManager.pushMatrix();
//        GlStateManager.translate(x, y, 0);
//        GlStateManager.scale(scale, scale, 1.0f);
//        RenderHelper.enableGUIStandardItemLighting();
//        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
//        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, 0, 0, null);
//        GlStateManager.popMatrix();
//    }
//
//    public void process(GuiContainer c) {
//        Minecraft.getMinecraft().playerController.windowClick(
//                c.inventorySlots.windowId,
//                (id == n ? 9 : 27) + n,
//                0, 1,
//                Minecraft.getMinecraft().thePlayer
//        );
//    }
}
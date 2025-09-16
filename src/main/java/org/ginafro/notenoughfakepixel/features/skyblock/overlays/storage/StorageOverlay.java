package org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.ConfigHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
//public class StorageOverlay {
//    public static StorageOverlayRenderer overlayRenderer = null;
//
//    public static class StorageOverlayRenderer {
//        private final GuiContainer gc;
//        private final List<GuiButton> buttonList = new ArrayList<>();
//        public GuiTextField searchBar;
//        private float scrollOffset = 0.0f;
//        private int buttonCount = 0;
//        private int enderChests = 0;
//        private int boxWidth;
//        private int buttonListHeight;
//        private int xPos;
//        private int yPos;
//        private int buttonWidth;
//        private int buttonHeight;
//        private final int ROWS_VISIBLE = 2;
//        private final int BUTTONS_PER_ROW = 3;
//        private final int ROW_SPACING = 10;
//        private final int COL_SPACING = 10;
//        private final int PADDING = 10;
//        private final HashMap<Integer, Boolean> chestMap = new HashMap<>();
//        private final int scrollBarWidth = 8;
//        private boolean isScrollBarDragging = false;
//        private float scrollDragStartY;
//        private float scrollDragStartOffset;
//        private long lastClickTime = 0;
//
//        public StorageOverlayRenderer(GuiContainer gc) {
//            this.gc = gc;
//            init();
//        }
//
//        public void init() {
//            buttonList.clear();
//            buttonCount = 0;
//            enderChests = 0;
//            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//
//            boxWidth = (int) (sr.getScaledWidth() * 0.8);
//            buttonListHeight = (int) (sr.getScaledHeight() * 0.6);
//
//            buttonWidth = (boxWidth - 2 * PADDING - (BUTTONS_PER_ROW - 1) * COL_SPACING) / BUTTONS_PER_ROW;
//            buttonHeight = (buttonListHeight - 2 * PADDING - ROW_SPACING) / ROWS_VISIBLE;
//
//            xPos = (sr.getScaledWidth() - boxWidth) / 2;
//            yPos = (sr.getScaledHeight() - buttonListHeight) / 2 - 20;
//
//            int searchBarWidth = (int) (boxWidth * 0.6);
//            searchBar = new GuiTextField(1001, Minecraft.getMinecraft().fontRendererObj,
//                    xPos + (boxWidth - searchBarWidth) / 2, yPos - 45, searchBarWidth, 35);
//            searchBar.setEnableBackgroundDrawing(true);
//            searchBar.setMaxStringLength(50);
//
//            for (int i = 0; i < 9; i++) {
//                Slot slot = gc.inventorySlots.getSlot(9 + i);
//                if (slot != null && slot.getStack() != null && slot.getStack().getDisplayName().toLowerCase().contains("ender")) {
//                    buttonCount++;
//                    enderChests++;
//                }
//            }
//            if (enderChests > 9) {
//                enderChests = 9;
//            }
//            for (int i = 0; i < 18; i++) {
//                Slot slot = gc.inventorySlots.getSlot(27 + i);
//                if (slot != null && slot.getStack() != null && !slot.getStack().getDisplayName().toLowerCase().contains("empty")) {
//                    buttonCount++;
//                }
//            }
//            int minus = 0;
//            for (int i = 0; i < buttonCount; i++) {
//                if (i + 1 > enderChests) {
//                    minus = enderChests;
//                }
//                buttonList.add(new StorageButton(i, -99, -99, buttonWidth, buttonHeight, i - minus));
//            }
//            updateVisibleButtons();
//        }
//
//        public void render(int mouseX, int mouseY, float partialTicks) {
//            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//            int c = ColorUtils.getColor(Config.feature.overlays.storageColor).getRGB();
//            drawRect(xPos, yPos, xPos + boxWidth, yPos + buttonListHeight, c);
//            GL11.glEnable(GL11.GL_SCISSOR_TEST);
//            GL11.glScissor(xPos * sr.getScaleFactor(),
//                    (Minecraft.getMinecraft().displayHeight - (yPos + buttonListHeight) * sr.getScaleFactor()),
//                    boxWidth * sr.getScaleFactor(), buttonListHeight * sr.getScaleFactor());
//
//            GlStateManager.pushMatrix();
//            float slotHeight = buttonHeight + ROW_SPACING;
//            float pixelOffset = scrollOffset * slotHeight;
//            GlStateManager.translate(0, -pixelOffset, 0);
//
//            for (GuiButton button : buttonList) {
//                if (button.visible) {
//                    button.drawButton(Minecraft.getMinecraft(), mouseX, (int) (mouseY + pixelOffset));
//                }
//            }
//
//            GlStateManager.popMatrix();
//            GL11.glDisable(GL11.GL_SCISSOR_TEST);
//            drawScrollBar();
//
//            for (GuiButton gb : buttonList) {
//                if (gb instanceof StorageButton && gb.visible) {
//                    StorageButton b = (StorageButton) gb;
//                    String type = b.id == b.n ? "Ender Chest(Page" + (b.n + 1) + ")" : "Backpack" + (b.n + 1);
//                    StorageData data = CustomConfigHandler.loadStorageData(type);
//                    Map<Integer, ItemStack> stackMap = null;
//                    try {
//                        if (data != null) {
//                            stackMap = data.getItemStacks();
//                        }
//                    } catch (NBTException e) {
//                        throw new RuntimeException(e);
//                    }
//                    if (stackMap != null) {
//                        float slotSize = (b.width - 4f) / 9.00f;
//                        float slotScale = (slotSize - 2f) / 16.00f;
//                        for (Map.Entry<Integer, ItemStack> entry : stackMap.entrySet()) {
//                            int slotIndex = entry.getKey();
//                            ItemStack stack = entry.getValue();
//                            int columns = 9;
//                            int row = slotIndex / columns;
//                            int col = slotIndex % columns;
//                            float slotX = b.xPosition + 2 + col * (16 * slotScale + 2);
//                            float slotY = b.yPosition + 15 + row * (16 * slotScale + 2);
//                            int size = (int) (16 * slotScale);
//                            float renderedSlotY = slotY - pixelOffset;
//                            if (mouseX >= slotX && mouseX <= slotX + size &&
//                                    mouseY >= renderedSlotY && mouseY <= renderedSlotY + size &&
//                                    renderedSlotY >= yPos && renderedSlotY + size <= yPos + buttonListHeight) {
//                                drawToolTip(stack, mouseX, mouseY);
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (!Config.feature.overlays.storageSearch) {
//                searchBar.setVisible(false);
//                searchBar.setFocused(false);
//                searchBar.setEnabled(false);
//            } else {
//                searchBar.setVisible(true);
//                searchBar.setEnabled(true);
//                searchBar.drawTextBox();
//            }
//        }
//
//        private void drawScrollBar() {
//            int totalRows = (buttonCount + BUTTONS_PER_ROW - 1) / BUTTONS_PER_ROW;
//            if (totalRows <= ROWS_VISIBLE) return;
//
//            float scrollTrackHeight = buttonListHeight;
//            float scrollThumbHeight = (ROWS_VISIBLE / (float) totalRows) * scrollTrackHeight;
//            float maxScrollOffset = totalRows - ROWS_VISIBLE;
//            float scrollProgress = scrollOffset / maxScrollOffset;
//
//            int scrollThumbY = yPos + (int) (scrollProgress * (scrollTrackHeight - scrollThumbHeight));
//
//            drawRect(xPos + boxWidth - scrollBarWidth, yPos,
//                    xPos + boxWidth, yPos + buttonListHeight,
//                    new Color(30, 30, 30, 200).getRGB());
//
//            drawRect(xPos + boxWidth - scrollBarWidth, scrollThumbY,
//                    xPos + boxWidth, scrollThumbY + (int) scrollThumbHeight,
//                    new Color(100, 100, 100, 200).getRGB());
//        }
//
//        private void drawToolTip(ItemStack stack, int mouseX, int mouseY) {
//            GlStateManager.pushMatrix();
//            GlStateManager.translate(0, 0, 500);
//            renderCustomToolTip(stack, mouseX, mouseY);
//            GlStateManager.popMatrix();
//        }
//
//        private void renderCustomToolTip(ItemStack stack, int x, int y) {
//            List<String> tooltip = stack.getTooltip(Minecraft.getMinecraft().thePlayer,
//                    Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
//
//            for (int i = 0; i < tooltip.size(); ++i) {
//                if (i == 0) {
//                    tooltip.set(i, stack.getRarity().rarityColor + tooltip.get(i));
//                } else {
//                    tooltip.set(i, EnumChatFormatting.GRAY + tooltip.get(i));
//                }
//            }
//
//            FontRenderer font = stack.getItem().getFontRenderer(stack);
//            if (font == null) font = Minecraft.getMinecraft().fontRendererObj;
//
//            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//            int screenWidth = sr.getScaledWidth();
//            int screenHeight = sr.getScaledHeight();
//
//            int tooltipWidth = 0;
//            for (String line : tooltip) {
//                int width = font.getStringWidth(line);
//                if (width > tooltipWidth) tooltipWidth = width;
//            }
//
//            x += 12;
//            y -= 12;
//
//            if (x + tooltipWidth + 6 > screenWidth) {
//                x = screenWidth - tooltipWidth - 6;
//            }
//            if (y + tooltip.size() * 10 + 6 > screenHeight) {
//                y = screenHeight - tooltip.size() * 10 - 6;
//            }
//
//            drawHoveringText(tooltip, x, y, font);
//        }
//
//        private void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
//            if (!textLines.isEmpty()) {
//                GlStateManager.disableRescaleNormal();
//                RenderHelper.disableStandardItemLighting();
//                GlStateManager.disableLighting();
//                GlStateManager.disableDepth();
//
//                int tooltipWidth = 0;
//                for (String textLine : textLines) {
//                    int textLineWidth = font.getStringWidth(textLine);
//                    if (textLineWidth > tooltipWidth) {
//                        tooltipWidth = textLineWidth;
//                    }
//                }
//
//                int tooltipHeight = 8 + (textLines.size() - 1) * 10;
//                int backgroundColor = 0xF0100010;
//                int borderColorStart = 0x505000FF;
//                int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
//
//                drawRect(x - 3, y - 3, x + tooltipWidth + 3, y + tooltipHeight + 3, backgroundColor);
//                drawRect(x - 3, y - 3, x + tooltipWidth + 3, y - 2, borderColorStart);
//                drawRect(x - 3, y + tooltipHeight + 2, x + tooltipWidth + 3, y + tooltipHeight + 3, borderColorEnd);
//                drawRect(x - 3, y - 2, x - 2, y + tooltipHeight + 2, borderColorStart);
//                drawRect(x + tooltipWidth + 2, y - 2, x + tooltipWidth + 3, y + tooltipHeight + 2, borderColorEnd);
//
//                for (int i = 0; i < textLines.size(); ++i) {
//                    String line = textLines.get(i);
//                    font.drawStringWithShadow(line, (float) x, (float) y, -1);
//                    if (i == 0) {
//                        y += 2;
//                    }
//                    y += 10;
//                }
//
//                GlStateManager.enableLighting();
//                GlStateManager.enableDepth();
//                RenderHelper.enableStandardItemLighting();
//                GlStateManager.enableRescaleNormal();
//            }
//        }
//
//        private void updateVisibleButtons() {
//            int totalRows = (buttonCount + BUTTONS_PER_ROW - 1) / BUTTONS_PER_ROW;
//            float slotHeight = buttonHeight + ROW_SPACING;
//            int firstRow = (int) Math.floor(scrollOffset);
//            int lastRow = firstRow + ROWS_VISIBLE + 1;
//
//            for (int i = 0; i < buttonList.size(); i++) {
//                GuiButton button = buttonList.get(i);
//                int row = i / BUTTONS_PER_ROW;
//                int col = i % BUTTONS_PER_ROW;
//
//                if (row >= firstRow && row <= lastRow) {
//                    button.visible = true;
//                    button.enabled = true;
//                    button.xPosition = xPos + PADDING + col * (buttonWidth + COL_SPACING);
//                    button.yPosition = yPos + PADDING + row * (int) slotHeight;
//                } else {
//                    button.visible = false;
//                    button.enabled = false;
//                }
//            }
//        }
//
//        public void handleMouseInput() throws IOException {
//            int wheel = Mouse.getEventDWheel();
//            if (wheel != 0) {
//                float scrollAmount = wheel > 0 ? -0.2f : 0.2f;
//                int totalRows = (buttonCount + BUTTONS_PER_ROW - 1) / BUTTONS_PER_ROW;
//                float maxScrollOffset = Math.max(0, totalRows - ROWS_VISIBLE);
//                scrollOffset = Math.max(0, Math.min(scrollOffset + scrollAmount, maxScrollOffset));
//                updateVisibleButtons();
//            }
//        }
//
//        public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
//            if (mouseButton != 0) return false;
//
//            if (Config.feature.overlays.storageSearch) {
//                int searchBarX = xPos + (boxWidth - searchBar.width) / 2;
//                int searchBarY = yPos - 45;
//                if (mouseX >= searchBarX && mouseX <= searchBarX + searchBar.width &&
//                        mouseY >= searchBarY && mouseY <= searchBarY + searchBar.height) {
//                    searchBar.mouseClicked(mouseX, mouseY, mouseButton);
//                    return true;
//                }
//            }
//
//            if (mouseX < xPos || mouseX > xPos + boxWidth ||
//                    mouseY < yPos || mouseY > yPos + buttonListHeight) {
//                return false;
//            }
//
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastClickTime < 250) return true;
//            lastClickTime = currentTime;
//
//            if (mouseX >= xPos + boxWidth - scrollBarWidth &&
//                    mouseX <= xPos + boxWidth) {
//                isScrollBarDragging = true;
//                scrollDragStartY = mouseY;
//                scrollDragStartOffset = scrollOffset;
//                return true;
//            }
//
//            float slotHeight = buttonHeight + ROW_SPACING;
//            float pixelOffset = scrollOffset * slotHeight;
//            int adjustedMouseY = mouseY + (int) pixelOffset;
//
//            for (GuiButton button : buttonList) {
//                if (button.visible && button.mousePressed(Minecraft.getMinecraft(), mouseX, adjustedMouseY)) {
//                    button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
//                    if (button instanceof StorageButton) {
//                        ((StorageButton) button).process(gc);
//                    }
//                    return true;
//                }
//            }
//
//            return true;
//        }
//
//        public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
//            if (isScrollBarDragging) {
//                int constrainedY = Math.max(yPos, Math.min(mouseY, yPos + buttonListHeight));
//
//                int totalRows = (buttonCount + BUTTONS_PER_ROW - 1) / BUTTONS_PER_ROW;
//                float maxScrollOffset = Math.max(0, totalRows - ROWS_VISIBLE);
//
//                float deltaY = constrainedY - scrollDragStartY;
//                float scrollTrackHeight = buttonListHeight;
//                float scrollRatio = deltaY / scrollTrackHeight;
//
//                scrollOffset = Math.max(0, Math.min(
//                        scrollDragStartOffset + (maxScrollOffset * scrollRatio),
//                        maxScrollOffset
//                ));
//                updateVisibleButtons();
//            }
//        }
//
//        public void mouseReleased(int mouseX, int mouseY, int state) {
//            isScrollBarDragging = false;
//        }
//
//        public void keyTyped(char typedChar, int keyCode) throws IOException {
//            if (searchBar.isFocused()) {
//                searchBar.textboxKeyTyped(typedChar, keyCode);
//
//                if (keyCode == Keyboard.KEY_E) {
//                    return;
//                }
//            }
//
//            if (keyCode == Keyboard.KEY_ESCAPE) {
//                overlayRenderer = null;
//                return;
//            }
//
//            if (!searchBar.isFocused() &&
//                    keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
//                overlayRenderer = null;
//            }
//        }
//    }
//
//    @RegisterEvents
//    public static class StorageEvent {
//        @SubscribeEvent
//        public void onOpen(GuiScreenEvent.BackgroundDrawnEvent e) {
//            if (e.gui instanceof GuiContainer) {
//                GuiContainer gc = (GuiContainer) e.gui;
//                if (gc instanceof GuiInventory) return;
//                if (gc.inventorySlots.getSlot(4).getStack() != null) {
//                    if (gc.inventorySlots.getSlot(4).getStack().getDisplayName().contains("Ender")) {
//                        if (Config.feature.overlays.storageOverlay) {
//                            if(gc.inventorySlots.getSlot(4).getStack().getItem() == Item.getItemFromBlock(Blocks.ender_chest)) {
//                                overlayRenderer = new StorageOverlayRenderer(gc);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        @SubscribeEvent(priority = EventPriority.HIGHEST)
//        public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
//            if (overlayRenderer != null && event.gui == overlayRenderer.gc) {
//                event.setCanceled(true);
//                overlayRenderer.render(event.mouseX, event.mouseY, 0.0f);
//            }
//        }
//
//        @SubscribeEvent
//        public void onMouseInputPre(GuiScreenEvent.MouseInputEvent.Pre e) throws IOException {
//            if (overlayRenderer != null && e.gui == overlayRenderer.gc) {
//                if (Mouse.getEventButton() >= 0) {
//                    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//                    int mouseX = Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
//                    int mouseY = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
//
//                    if (Mouse.getEventButtonState()) {
//                        if (overlayRenderer.handleMouseClick(mouseX, mouseY, Mouse.getEventButton())) {
//                            e.setCanceled(true);
//                        }
//                    } else {
//                        overlayRenderer.mouseReleased(mouseX, mouseY, Mouse.getEventButton());
//                    }
//                } else if (Mouse.isButtonDown(0)) {
//                    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//                    int mouseX = Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
//                    int mouseY = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
//                    overlayRenderer.mouseClickMove(mouseX, mouseY, 0, 0);
//                } else {
//                    overlayRenderer.handleMouseInput();
//                }
//            }
//        }
//
//        @SubscribeEvent
//        public void onKeyTyped(GuiScreenEvent.KeyboardInputEvent.Pre e) throws IOException {
//            if (overlayRenderer != null && e.gui == overlayRenderer.gc) {
//                char typedChar = Keyboard.getEventCharacter();
//                int keyCode = Keyboard.getEventKey();
//
//                if (overlayRenderer.searchBar.isFocused() && keyCode == Keyboard.KEY_E) {
//                    e.setCanceled(true);
//                    overlayRenderer.keyTyped(typedChar, keyCode);
//                    return;
//                }
//
//                if (keyCode != 0 || typedChar >= 32) {
//                    overlayRenderer.keyTyped(typedChar, keyCode);
//                }
//            }
//        }
//    }
//
//    private static void drawRect(int left, int top, int right, int bottom, int color) {
//        Gui.drawRect(left, top, right, bottom, color);
//    }
//}
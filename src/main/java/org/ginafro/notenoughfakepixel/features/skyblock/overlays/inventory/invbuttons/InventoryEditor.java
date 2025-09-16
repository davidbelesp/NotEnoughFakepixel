package org.ginafro.notenoughfakepixel.features.skyblock.overlays.inventory.invbuttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.Utils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class InventoryEditor extends GuiScreen {
//
//    public ButtonEditor editor;
//    public GuiTextField commandField, searchBar;
//    private InventoryButton draggedButton = null;
//    private int dragOffsetX = 0;
//    private int dragOffsetY = 0;
//    private int scrollOffset = 0;
//    private boolean clicked = false;
//    private int rows = 5;
//    private final int GRID_SIZE = 16;
//
//    @Override
//    public void initGui() {
//        mc = Minecraft.getMinecraft();
//        float scale = Utils.getScale();
//        int editorWidth = (int) (304 * scale);
//        int editorHeight = (int) (432 * scale);
//        InvManager.isEditor = true;
//        int editorX = this.width - editorWidth;
//        int editorY = 5;
//        InvManager.load();
//        editor = new ButtonEditor(0, editorX, editorY, editorWidth, editorHeight);
//        this.buttonList.add(editor);
//        this.buttonList.add(new GuiButton(2, (int) (this.width - (35 * Utils.getScale())), editorY + editorHeight + (int) (35 * Utils.getScale()), (int) (30 * scale), (int) (30 * scale), "+"));
//        this.buttonList.add(new GuiButton(4, (int) (this.width - (70 * Utils.getScale())), editorY + editorHeight + (int) (35 * Utils.getScale()), (int) (30 * scale), (int) (30 * scale), "-"));
//        searchBar = new GuiTextField(3, mc.fontRendererObj,
//                (int) (editorX + (12 * scale)),
//                (int) (editorY + (105 * scale)),
//                (int) (282 * scale),
//                (int) (20 * scale)
//        );
//        commandField = new GuiTextField(1, mc.fontRendererObj,
//                (int) (editorX + (10 * scale)),
//                (int) (editorY + (10 * scale)),
//                (int) (284 * scale),
//                (int) (20 * scale)
//        );
//        searchBar.setEnableBackgroundDrawing(false);
//        commandField.setEnableBackgroundDrawing(false);
//        commandField.setTextColor(new Color(121, 132, 185).getRGB());
//        searchBar.setTextColor(new Color(44, 53, 77).getRGB());
//        InvManager.loadItemStacks();
//        System.out.println(InvManager.itemMap.size());
//        rows = InvManager.itemMap.size() / 8;
//    }
//
//
//    @Override
//    public void onGuiClosed() {
//        super.onGuiClosed();
//        InvManager.isEditor = false;
//        InvManager.selected = null;
//        InvManager.save();
//
//    }
//
//    public static boolean areStacksVisuallyEqual(ItemStack a, ItemStack b) {
//        if (a == null || b == null) return false;
//        if (!ItemStack.areItemsEqual(a, b)) return false;
//
//        if (a.getItem() == Items.skull && a.getMetadata() == 3) {
//            NBTTagCompound tagA = a.getTagCompound();
//            NBTTagCompound tagB = b.getTagCompound();
//            if (tagA == null || tagB == null) return false;
//
//            NBTTagCompound ownerA = tagA.getCompoundTag("SkullOwner");
//            NBTTagCompound ownerB = tagB.getCompoundTag("SkullOwner");
//
//            if (!ownerA.hasKey("Properties") || !ownerB.hasKey("Properties")) return false;
//
//            NBTTagList listA = ownerA.getCompoundTag("Properties").getTagList("textures", 10);
//            NBTTagList listB = ownerB.getCompoundTag("Properties").getTagList("textures", 10);
//
//            if (listA.tagCount() == 0 || listB.tagCount() == 0) return false;
//
//            String valA = listA.getCompoundTagAt(0).getString("Value");
//            String valB = listB.getCompoundTagAt(0).getString("Value");
//
//            return valA.equals(valB);
//        }
//
//        return true;
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        InvManager.drawButtons();
//        commandField.drawTextBox();
//        searchBar.drawTextBox();
//        if (InvManager.selected != null) {
//            InvManager.selected.cmd = commandField.getText().isEmpty() ? InvManager.selected.cmd : commandField.getText();
//        }
//        float scale = Utils.getScale();
//        drawRect(this.width / 2 - 88,
//                this.height / 2 - 83,
//                this.width / 2 + 88,
//                this.height / 2 + 83,
//                new Color(255, 255, 255, 88).getRGB());
//        mc.fontRendererObj.drawString("Inventory Here", this.width / 2 - mc.fontRendererObj.getStringWidth("Inventory Here"), this.height / 2, -1);
//        if (searchBar.getText().isEmpty()) {
//            mc.fontRendererObj.drawString("Search Bar", searchBar.xPosition, searchBar.yPosition, new Color(44, 53, 77).getRGB(), true);
//        }
//        if (commandField.getText().isEmpty()) {
//            mc.fontRendererObj.drawString("Command Here", commandField.xPosition, commandField.yPosition, new Color(121, 132, 185).getRGB(), true);
//        }
//        List<ItemStack> visibleItems = new ArrayList<>();
//        String query = searchBar.getText().toLowerCase();
//
//        for (ItemStack stack : InvManager.itemMap) {
//            if (query.isEmpty() || stack.getDisplayName().toLowerCase().contains(query)) {
//                visibleItems.add(stack);
//            }
//        }
//        rows = visibleItems.size() / 8 + 1;
//        if (scrollOffset > rows - 9) {
//            scrollOffset = rows - 9;
//        }
//        int offset = (int) (33 * scale);
//        int xStart = (int) (editor.xPosition + (22 * scale));
//        int yStart = (int) (editor.yPosition + (127 * scale));
//
//        int itemsPerRow = 8;
//        int maxVisibleRows = 9;
//
//        for (int i = 0; i < visibleItems.size(); i++) {
//            int row = i / itemsPerRow;
//            int col = i % itemsPerRow;
//
//            if (row < scrollOffset || row >= scrollOffset + maxVisibleRows) continue;
//
//            int renderX = xStart + (col * offset);
//            int renderY = yStart + ((row - scrollOffset) * offset);
//            ItemStack stack = visibleItems.get(i);
//
//            GlStateManager.pushMatrix();
//            GlStateManager.translate(renderX, renderY, 0);
//            GlStateManager.scale(2 * scale, 2 * scale, 2 * scale);
//
//            RenderHelper.enableGUIStandardItemLighting();
//            itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
//            RenderHelper.disableStandardItemLighting();
//
//            if (InvManager.selected != null && stack.getItem() == InvManager.selected.logo.getItem()) {
//                boolean match = stack.getItem() != Items.skull || areStacksVisuallyEqual(stack, InvManager.selected.logo);
//                if (match) {
//                    drawRect(0, 0, (int) (32 * scale), (int) (32 * scale), new Color(0, 180, 0, 152).getRGB());
//                }
//            }
//
//            if (mouseX >= renderX && mouseX <= renderX + (32 * scale) &&
//                    mouseY >= renderY && mouseY <= renderY + (32 * scale)) {
//                boolean mouse = Mouse.isButtonDown(0);
//                if (mouse && !clicked && InvManager.selected != null) {
//                    InvManager.selected.logo = stack;
//                }
//                clicked = mouse;
//            }
//
//            GlStateManager.popMatrix();
//        }
//    }
//
//    @Override
//    public void handleMouseInput() throws IOException {
//        super.handleMouseInput();
//        int wheel = Mouse.getDWheel();
//        if (wheel == 0) return;
//        if (wheel > 0) {
//            scrollOffset--;
//        } else {
//            scrollOffset++;
//        }
//        if (scrollOffset > (rows - 9)) {
//            scrollOffset = rows - 9;
//        } else if (scrollOffset < 0) {
//            scrollOffset = 0;
//        }
//    }
//
//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        super.mouseClicked(mouseX, mouseY, mouseButton);
//        commandField.mouseClicked(mouseX, mouseY, mouseButton);
//        searchBar.mouseClicked(mouseX, mouseY, mouseButton);
//        if (mouseButton == 0) {
//            for (InventoryButton b : InvManager.buttons) {
//                if (b.isHovered(mouseX, mouseY)) {
//                    draggedButton = b;
//                    InvManager.selected = b;
//                    commandField.setText(b.cmd);
//                    float scale = Utils.getScale();
//                    dragOffsetX = (int) (mouseX - b.x * scale);
//                    dragOffsetY = (int) (mouseY - b.y * scale);
//                    break;
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
//        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
//        if (draggedButton != null && clickedMouseButton == 0) {
//            float scale = Utils.getScale();
//            draggedButton.x = (int) ((mouseX - dragOffsetX) / scale);
//            draggedButton.y = (int) ((mouseY - dragOffsetY) / scale);
//        }
//    }
//
//    @Override
//    protected void mouseReleased(int mouseX, int mouseY, int state) {
//        super.mouseReleased(mouseX, mouseY, state);
//        if (state == 0 && draggedButton != null) {
//            float scale = Utils.getScale();
//            int unscaledX = (int) ((mouseX - dragOffsetX) / scale);
//            int unscaledY = (int) ((mouseY - dragOffsetY) / scale);
//
//            int snappedX = Math.round(unscaledX / (float) GRID_SIZE) * GRID_SIZE;
//            int snappedY = Math.round(unscaledY / (float) GRID_SIZE) * GRID_SIZE;
//            if (Config.feature.overlays.snapToGrid) {
//                draggedButton.x = snappedX;
//                draggedButton.y = snappedY;
//            }
//
//            draggedButton = null;
//        }
//    }
//
//    @Override
//    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        super.keyTyped(typedChar, keyCode);
//        if (keyCode == Keyboard.KEY_ESCAPE) {
//            if (commandField.isFocused()) {
//                commandField.setFocused(false);
//            } else if (searchBar.isFocused()) {
//                searchBar.setFocused(false);
//            } else {
//                Minecraft.getMinecraft().displayGuiScreen(null);
//            }
//        }
//        if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
//            if (!commandField.isFocused() && !searchBar.isFocused()) {
//                Minecraft.getMinecraft().displayGuiScreen(null);
//            }
//        }
//        searchBar.textboxKeyTyped(typedChar, keyCode);
//        commandField.textboxKeyTyped(typedChar, keyCode);
//    }
//
//    @Override
//    protected void actionPerformed(GuiButton button) {
//        if (button.id == 2) {
//            InvManager.addButton(new InventoryButton(InvManager.buttons.size(), 15, 15, 32, "command", new ItemStack(Items.skull), InvStyle.VANILLA));
//        }
//        if (button.id == 4) {
//            if (InvManager.selected != null) {
//                InvManager.removeButton(InvManager.selected);
//                InvManager.selected = null;
//            }
//        }
//    }
}

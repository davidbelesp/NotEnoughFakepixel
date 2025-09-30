package org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage.config.StorageContainer;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.variables.Resources;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.ginafro.notenoughfakepixel.utils.ResolutionUtils.getXStatic;
import static org.ginafro.notenoughfakepixel.utils.ResolutionUtils.getYStatic;

public class StorageRenderer {


    public float xStart,yStart,boxWidth,boxHeight,containersHeight;
    public float buttonOffsetX,buttonOffsetY;
    public int scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight;
    public float scrollHandleY, scrollHandleHeight;
    public final GuiContainer gc;
    public ScaledResolution sr;

    public StorageContainer focusedContainer;
    private ItemStack carriedStack = null;

    public float scrollOffset = 0f;
    public float targetScrollOffset = 0f;
    public final float scrollSpeed = 0.5f;
    public final float scrollStep = 1f;
    public boolean draggingScrollBar = false;
    public float dragStartY = 0f;
    public float scrollStartOffset = 0f;

    public List<StorageContainer> containers = new ArrayList<>();
    public StorageRenderer(GuiContainer g){
        gc = g;
        sr = new ScaledResolution(Minecraft.getMinecraft());
        loadContainers();
        loadConstants();
    }

    private void loadContainers() {

        // Ender Chests

        for(int i = 0;i < 9;i++){
            Slot s = gc.inventorySlots.getSlot(9 + i);
            if(s.getStack().getItem().equals(Item.getItemFromBlock(Blocks.stained_glass_pane))){
                if(s.getStack().getMetadata() == 10){
                    if(StringUtils.stripControlCodes(s.getStack().getDisplayName()).startsWith("Ender Chest")) {
                        try {
                            String[] name = s.getStack().getDisplayName().split(" ");
                            int num = Integer.parseInt(name[name.length - 1]);
                            StorageContainer container = new StorageContainer(num,5,"echest", s.getStack().getDisplayName());
                            containers.add(container);
                            System.out.println("Registered Ender Chest: " + num);
                        }catch(NumberFormatException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // Backpacks

        for(int i = 0;i < 18;i++){
            Slot s = gc.inventorySlots.getSlot(27 + i);
            if(s.getStack().getItem().equals(Items.skull)){
                if(StringUtils.stripControlCodes(s.getStack().getDisplayName()).startsWith("Backpack Slot")){
                    try {
                        String[] name = s.getStack().getDisplayName().split(" ");
                        int num = Integer.parseInt(name[name.length - 1]);
                        StorageContainer container = new StorageContainer(num,getBackpackSize(s.getStack()),"backpack", s.getStack().getDisplayName());
                        containers.add(container);
                        System.out.println("Registered Backpack: " + num);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Containers: " + containers.size());
    }

    private int getBackpackSize(ItemStack stack) {
        for(String s : getLore(stack)){
            if(s.contains("45")) return 5;
            if(s.contains("36")) return 4;
            if(s.contains("27")) return 3;
            if(s.contains("18")) return 2;
            if(s.contains("9")) return 1;
        }
        return 1;
    }

    public List<String> getLore(ItemStack stack){
        List<String> temp = new ArrayList<>();
            if (stack.hasTagCompound()) {
                NBTTagCompound tag = stack.getTagCompound();

                if (tag.hasKey("display", 10)) {
                    NBTTagCompound displayTag = tag.getCompoundTag("display");

                    if (displayTag.hasKey("Lore", 9)) {
                        NBTTagList loreList = displayTag.getTagList("Lore", 8);
                        for (int i = 0; i < loreList.tagCount(); i++) {
                            temp.add(loreList.getStringTagAt(i));
                        }
                    }
                }
            }
            return temp;
    }

    private void loadConstants() {
        xStart = getXStatic(15);
        yStart = sr.getScaledHeight() / 2f - getYStatic(475);
        boxWidth = getXStatic(1300);
        containersHeight = getXStatic(780);
        boxHeight = getYStatic(950);
        buttonOffsetX = getXStatic(12);
        buttonOffsetY = getYStatic(12);
        scrollBarWidth = 4;
        scrollBarX = (int) xStart - 4;
        scrollBarY = (int) yStart;
        scrollBarHeight = (int) boxHeight;
    }

    public void drawScreen(int mouseX, int mouseY, Minecraft mc) {
        scrollOffset += (targetScrollOffset - scrollOffset) * scrollSpeed;

        Gui.drawRect((int) xStart, (int) yStart, (int) (xStart + boxWidth), (int) (yStart + boxHeight), new Color(25, 25, 25, 255).getRGB());
        Gui.drawRect((int) xStart + 1, (int) yStart + 1, (int) (xStart + boxWidth - 1), (int) (yStart + boxHeight - 1), new Color(56, 56, 56, 255).getRGB());

        int scale = sr.getScaleFactor();
        int scissorX = (int) (xStart * scale);
        int scissorY = (int) ((sr.getScaledHeight() - (yStart + boxHeight)) * scale);
        int scissorW = (int) (boxWidth * scale);
        int scissorH = (int) (boxHeight * scale);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);
        drawContainers(mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        drawScrollBar();
        drawInventory(mouseX,mouseY);
        if (carriedStack != null) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            mc.getRenderItem().renderItemIntoGUI(carriedStack, mouseX - 8, mouseY - 8);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    private void drawInventory(int mouseX, int mouseY) {
        int xPos = (int)(sr.getScaledWidth() - getXStatic(520));
        int yPos = (int)(sr.getScaledHeight() / 2f - getYStatic(150));
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.STORAGE_INVENTORY.getResource());
        int width = (int) getXStatic(500);
        int height = (int) getYStatic(300);
        GlStateManager.resetColor();
        Color ce = ColorUtils.getColor(Config.feature.overlays.invTint);
        GlStateManager.color(ce.getRed()/255f,ce.getGreen()/255f,ce.getBlue()/255f,ce.getAlpha()/255f);
        Gui.drawScaledCustomSizeModalRect(
                xPos,
                yPos,
                0f, 0f,
                210, height / 2,
                width, height,
                210, height / 2f
        );
        GlStateManager.resetColor();
        GlStateManager.color(1f,1f,1f);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos + getXStatic(15), yPos + getYStatic(20), 0f);
        GlStateManager.scale(getXStatic(1), getXStatic(1),getXStatic(1));
        GlStateManager.resetColor();
        Minecraft.getMinecraft().fontRendererObj.drawString("Inventory", 0, 0, -1);
        GlStateManager.popMatrix();
        drawInventoryItems(xPos,yPos,mouseX,mouseY);
    }

    private void drawInventoryItems(int xPos, int yPos, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getMinecraft();
        Container inv = mc.thePlayer.inventoryContainer;
        RenderItem itemRenderer = mc.getRenderItem();

        int xStart = xPos + (int) getXStatic(16);
        int yStart = yPos + (int) getYStatic(66);
        int offset = (int) getXStatic(44);
        int xi = 0, yi = 0;

        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableRescaleNormal();

        for (int i = 0; i < 36; i++) {
            int slotX = xStart + xi * offset;
            int slotY = yStart + yi * offset;

            if (inv.getSlot(i).getHasStack()) {
                ItemStack stack = inv.getSlot(i).getStack();

                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 32);
                itemRenderer.zLevel = 100.0F;

                itemRenderer.renderItemAndEffectIntoGUI(stack, slotX + 10, slotY + 10);
                itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, stack, slotX + 10, slotY + 10, null);

                itemRenderer.zLevel = 0.0F;
                GlStateManager.popMatrix();
            }

            if (mouseX >= slotX && mouseX < slotX + offset && mouseY >= slotY && mouseY < slotY + offset) {
            }

            xi++;
            if (xi >= 9) {
                xi = 0;
                yi++;
            }
        }

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
    private void drawContainers(int mouseX, int mouseY) {
        int buttonStartX = (int) (xStart + getXStatic(7));
        int buttonStartY = (int) (yStart + getYStatic(7));
        int xi = 0;

        float yRunning = buttonStartY - scrollOffset;
        float rowHeight = 0;

        for (StorageContainer c : containers) {
            int width = (int) getXStatic(420);
            int height = (int) getYStatic(80 + (44 * c.size));
            int xPos = buttonStartX + (int) ((width + buttonOffsetX) * xi);
            float yPos = yRunning;
            GlStateManager.color(1f, 1f, 1f, 1f);
            c.draw(xPos, yPos);

            rowHeight = Math.max(rowHeight, height);
            xi++;
            if (xi >= 3) {
                xi = 0;
                yRunning += rowHeight + buttonOffsetY;
                rowHeight = 0;
            }
        }
    }

    private void drawScrollBar() {
        float maxScroll = getMaxScroll();
        if(maxScroll <= 0) return;


        scrollHandleHeight = Math.max(20, scrollBarHeight * (boxHeight / (boxHeight + maxScroll)));
        scrollHandleY = scrollBarY + (scrollBarHeight - scrollHandleHeight) * (scrollOffset / maxScroll);

        Gui.drawRect(scrollBarX, scrollBarY, scrollBarX + scrollBarWidth, scrollBarY + scrollBarHeight, new Color(0,0,0,255).getRGB());
        Gui.drawRect(scrollBarX, (int) scrollHandleY, scrollBarX + scrollBarWidth, (int)(scrollHandleY + scrollHandleHeight), new Color(200,200,200,255).getRGB());
    }


    public void handleMouseInput() {
        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            float scrollPixels = 20;
            targetScrollOffset -= wheel / 120f * scrollPixels;
            targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, getMaxScroll()));
        }
    }

    private float getMaxScroll() {
        int xi = 0;
        float totalHeight = 0;
        float rowHeight = 0;

        for (StorageContainer c : containers) {
            int height = (int) getYStatic(80 + (44 * c.size));
            rowHeight = Math.max(rowHeight, height);

            xi++;
            if (xi >= 3) {
                totalHeight += rowHeight + buttonOffsetY;
                xi = 0;
                rowHeight = 0;
            }
        }
        if (xi > 0) totalHeight += rowHeight + buttonOffsetY;
        return Math.max(0, totalHeight - boxHeight + getYStatic(14));
    }


    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if(button == 0) {
            if(mouseX >= scrollBarX && mouseX <= scrollBarX + scrollBarWidth
                    && mouseY >= scrollHandleY && mouseY <= scrollHandleY + scrollHandleHeight) {
                draggingScrollBar = true;
                dragStartY = mouseY;
                scrollStartOffset = targetScrollOffset;
            }
        }
        return true;
    }

    public void keyTyped(int code,char c){
        if(code == Keyboard.KEY_ESCAPE || code == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()){
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        draggingScrollBar = false;
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedButton, long timeSinceLastClick) {
        if(draggingScrollBar) {
            float deltaY = mouseY - dragStartY;
            float maxScroll = getMaxScroll();
            if(maxScroll > 0) {
                float scrollRange = scrollBarHeight - scrollHandleHeight;
                targetScrollOffset = scrollStartOffset + (deltaY / scrollRange) * maxScroll;
                targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, maxScroll));
            }
        }
    }
}

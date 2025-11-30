package com.nef.notenoughfakepixel.features.skyblock.overlays.storage.config;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.features.skyblock.overlays.storage.StorageHandler;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.HashMap;

import static com.nef.notenoughfakepixel.features.skyblock.overlays.storage.StorageHandler.*;
import static com.nef.notenoughfakepixel.utils.ResolutionUtils.getXStatic;
import static com.nef.notenoughfakepixel.utils.ResolutionUtils.getYStatic;

public class StorageContainer {
    public HashMap<Integer,String> items = new HashMap<>();
    public int containerNumber,size;
    public String type,title;
    private ItemStack carriedStack = null;
    private boolean wasMouseDown = false;

    public StorageContainer(int cn,int size,String type,String title){
        this.containerNumber = cn;
        this.size = size;
        this.type = type;
        this.title = title;
    }
    public void addItem(Integer slot, ItemStack item){
        this.items.put(slot,itemStackToJson(item));
    }

    public void removeItem(Integer slot){
        this.items.remove(slot);
    }

    public HashMap<Integer,ItemStack> getItems(){
        HashMap<Integer,ItemStack> temp = new HashMap<>();
        items.forEach((e,v) -> {
            try {
                ItemStack stack = jsonToItemStack(v);
                if(stack != null){
                    temp.put(e,stack);
                }
            } catch (NBTException ex) {
                throw new RuntimeException(ex);
            }
        });
        return temp;
    }

    public void draw(int xPos, float yPos) {
        GlStateManager.pushMatrix();

        Minecraft.getMinecraft().getTextureManager().bindTexture(StorageHandler.getResource(this.size));

        int width = (int) getXStatic(420);
        int height = (int) getYStatic(80 + (44 * this.size));

        Color ce = ColorUtils.getColor(
                this.type.equals("echest") ? Config.feature.overlays.enderTint : Config.feature.overlays.bagTint
        );

        GlStateManager.disableLighting();

        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.color(
                ce.getRed() / 255f,
                ce.getGreen() / 255f,
                ce.getBlue() / 255f,
                ce.getAlpha() / 255f
        );

        Gui.drawScaledCustomSizeModalRect(
                xPos, Math.round(yPos),
                0f, 0f,
                210, height / 2,
                width, height,
                210, height / 2f
        );

        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos + getXStatic(15), yPos + getYStatic(20), 0f);
        GlStateManager.scale(getXStatic(1), getXStatic(1), getXStatic(1));
        GlStateManager.disableLighting();
        Minecraft.getMinecraft().fontRendererObj.drawString(this.title, 0, 0, -1);
        GlStateManager.popMatrix();

        if (Mouse.isButtonDown(0)) {
            if (getMouseX() > xPos && getMouseX() < xPos + width &&
                    getMouseY() > yPos && getMouseY() < yPos + height) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/storage " + this.type + " " + this.containerNumber);
            }
        }

        drawItems(xPos, yPos, getMouseX(), getMouseY());

        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private void drawItems(int xPos, float yPos,int mouseX,int mouseY) {
        int width = (int) getXStatic(420);
        int height = (int) getYStatic(80 + (44 * this.size));
        boolean mouseDown = Mouse.isButtonDown(0);
        int xi = 0, yi = 0;
        for (int i = 0; i < (9 * size); i++) {
            String json = items.get(i);
            if (json == null || json.trim().isEmpty()) continue;
            try {
                ItemStack item = jsonToItemStack(items.get(i));
                int posX = (int) (xPos + (getXStatic(40) * xi));
                int posY = (int) (yPos + (getYStatic(40) * yi));

                if (item != null) {
                    Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(item, posX, posY);
                }

                if (mouseDown && !wasMouseDown &&
                        mouseX > posX && mouseX < posX + getXStatic(40) &&
                        mouseY > posY && mouseY < posY + getYStatic(40)) {

                    if (carriedStack == null && item != null) {
                        carriedStack = item;
                        removeItem(i);
                    } else if (carriedStack != null && item == null) {
                        addItem(i, carriedStack);
                        carriedStack = null;
                    }
                }
            } catch (NBTException e) {
                e.printStackTrace();
            }

            xi++;
            if (xi % 9 == 0) {
                xi = 0;
                yi++;
            }
        }

        if (carriedStack != null) {
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(
                    carriedStack, mouseX - 8, mouseY - 8
            );
        }

        wasMouseDown = mouseDown;
    }
}



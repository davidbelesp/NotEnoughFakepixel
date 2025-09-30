package org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.HashMap;

import static org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage.StorageHandler.*;
import static org.ginafro.notenoughfakepixel.utils.ResolutionUtils.getXStatic;
import static org.ginafro.notenoughfakepixel.utils.ResolutionUtils.getYStatic;

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

    public void draw(int xPos,float yPos){
        int width = (int) getXStatic(420);
        int height = (int) getYStatic(80 + (44 * this.size));

        GlStateManager.pushMatrix();
        GlStateManager.resetColor();
        Minecraft.getMinecraft().getTextureManager().bindTexture(getResource(this.size));
        if(this.type.equals("backpack")) {
            Color ce = ColorUtils.getColor(Config.feature.overlays.bagTint);
            GlStateManager.color(ce.getRed()/255f,ce.getGreen()/255f,ce.getBlue()/255f,ce.getAlpha()/255f);
        }
        if(this.type.equals("echest")){
            Color ce = ColorUtils.getColor(Config.feature.overlays.enderTint);
            GlStateManager.color(ce.getRed()/255f,ce.getGreen()/255f,ce.getBlue()/255f,ce.getAlpha()/255f);
        }

        Gui.drawScaledCustomSizeModalRect(
                xPos,
                Math.round(yPos),
                0f, 0f,
                210, height / 2,
                width, height,
                210, height / 2f
        );
        GlStateManager.popMatrix();
        drawItems(xPos,yPos,getMouseX(),getMouseY());
        drawContainerTitle(xPos, yPos);
    }

    private void drawItems(int xPos, float yPos,int mouseX,int mouseY) {
        int width = (int) getXStatic(420);
        int height = (int) getYStatic(80 + (44 * this.size));
        boolean mouseDown = Mouse.isButtonDown(0);
        int xi = 0, yi = 0;
        for (int i = 0; i < (9 * size); i++) {
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

    private void drawContainerTitle( int xPos, float yPos) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos + getXStatic(15), yPos + getYStatic(20), 0f);
        GlStateManager.scale(getXStatic(1), getXStatic(1),getXStatic(1));
        GlStateManager.resetColor();
//
//        Color color = ColorUtils.getColor(Config.feature.overlays.bagTint);
//        if(c.type.equals("echest")) color = ColorUtils.getColor(Config.feature.overlays.enderTint);
//        int mcColor = color.getRGB() & 0xFFFFFF;

        Minecraft.getMinecraft().fontRendererObj.drawString(this.title, 0, 0, -1);

        GlStateManager.popMatrix();
    }
}



package com.nef.notenoughfakepixel.features.skyblock.overlays.storage;

import com.nef.notenoughfakepixel.features.skyblock.overlays.storage.config.StorageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ConfigHandler;
import com.nef.notenoughfakepixel.variables.Resources;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;

@RegisterEvents
public class StorageHandler {

    // LOGIC

    public StorageRenderer renderer;
    @SubscribeEvent
    public void onOpen(GuiScreenEvent.BackgroundDrawnEvent e){
        if(!Config.feature.overlays.storageOverlay) return;
        if(e.gui instanceof GuiContainer){
            GuiContainer gc = (GuiContainer) e.gui;

            boolean itemCheck = false, guiCheck = false;
            IInventory inv = gc.inventorySlots.inventorySlots.get(0).inventory;
            System.out.println("Title: " + inv.getDisplayName().getUnformattedText());
            if(inv.getDisplayName().getUnformattedText().startsWith("Ender Chest")){
                String page = inv.getDisplayName().getUnformattedText().replace("Ender Chest","").replace("(Page","").replace(")","");
                int num = Integer.parseInt(page);
                if(renderer != null){
                    openContainer("echest",num);
                }
                return;
            }else if(inv.getDisplayName().getUnformattedText().contains("Backpack")){
                String str = inv.getDisplayName().getUnformattedText();
                    int slashIndex = str.indexOf('/');

                    int spaceIndex = str.lastIndexOf(' ', slashIndex);

                    String page = str.substring(spaceIndex + 1, slashIndex);
                    int num = Integer.parseInt(page);
                    if(renderer != null){
                        openContainer("backpack",num);
                    }
                    return;
            }

            renderer = null;
            if (gc instanceof GuiInventory) return;
            if (gc.inventorySlots.getSlot(4).getStack() != null) {
                if (gc.inventorySlots.getSlot(4).getStack().getDisplayName().contains("Ender")) {
                    itemCheck = true;
                    System.out.println("Item Check True");
                }
            }
                    if(inv.getDisplayName().getUnformattedText().equals("Storage")){
                        guiCheck = true;
                        System.out.println("Gui Check True");
                    }
                   if(itemCheck && guiCheck){
                       this.renderer = new StorageRenderer(gc);
                       System.out.println("Storage GUI");
                }
            }
    }

    private void openContainer(String type, int num) {
        renderer.containers.forEach(c -> {
            if(c.type.equalsIgnoreCase(type) && c.containerNumber == num){
                renderer.focusedContainer = c;
            }
        });
    }

    @SubscribeEvent
    public void onDraw(GuiScreenEvent.DrawScreenEvent.Pre e){
        if(renderer != null && renderer.gc != null){
            if(renderer.gc.equals(e.gui)){
                e.setCanceled(true);
                renderer.drawScreen(getMouseX(),getMouseY(),e.gui.mc);
            }
        }
    }

    @SubscribeEvent
    public void onClick(GuiScreenEvent.MouseInputEvent.Pre e){
        if (renderer != null && e.gui == renderer.gc) {
            if (Mouse.getEventButton() >= 0) {
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                int mouseX = Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
                int mouseY = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
                if (Mouse.getEventButtonState()) {
                    if (renderer.mouseClicked(mouseX, mouseY, Mouse.getEventButton())) {
                        e.setCanceled(true);
                    }
                } else {
                    renderer.mouseReleased(mouseX, mouseY, Mouse.getEventButton());
                }
            } else if (Mouse.isButtonDown(0)) {
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                int mouseX = Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
                int mouseY = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
                renderer.mouseClickMove(mouseX, mouseY, 0, 0);
            } else {
                renderer.handleMouseInput();
            }
        }
    }

    @SubscribeEvent
    public void onType(GuiScreenEvent.KeyboardInputEvent.Pre e){
        if(renderer != null && renderer.gc != null){
            if(renderer.gc.equals(e.gui)){
                e.setCanceled(true);
                renderer.keyTyped(Keyboard.getEventKey(),Keyboard.getEventCharacter());
            }
        }
    }

    // HELPER METHODS
    public static Minecraft mc = Minecraft.getMinecraft();
    public static int getMouseX() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
    }

    public static int getMouseY() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
    }


    public static ResourceLocation getResource(int size) {
        switch(size){
            case 1:
                return Resources.STORAGE_SMALL.getResource();
            case 2:
                return Resources.STORAGE_MEDIUM.getResource();
            case 3:
                return Resources.STORAGE_LARGE.getResource();
            case 4:
                return Resources.STORAGE_GREATER.getResource();
            default:
                return Resources.STORAGE_JUMBO.getResource();
        }
    }


    // CONFIG
    public StorageData storageData = new StorageData();
    public void saveConfig(){
        ConfigHandler.saveConfig(storageData,new File(Config.configDirectory,"storage.json"),ConfigHandler.GSON);
    }


    public void saveCorruptFile(File f){
        File renameFile = new File(f.getPath().replace(".json",".corrupted"));
        if (f.renameTo(renameFile)) {
            System.out.println("Renamed successfully!");
        } else {
            System.out.println("Failed to rename file.");
        }
    }

    public void loadConfig() {
        StorageData data = ConfigHandler.loadConfig(StorageData.class, new File(Config.configDirectory, "storage.json"), ConfigHandler.GSON,false,true);
        if (data != null) {
            this.storageData = data;
            return;
        }
        System.out.println("COULD NOT LOAD STORAGE CONFIG: CONFIG IS CORRUPTED");
        saveCorruptFile(new File(Config.configDirectory, "storage.json"));
    }

    public static String itemStackToJson(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        stack.writeToNBT(nbt);
        return nbt.toString();
    }

    public static ItemStack jsonToItemStack(String json) throws NBTException {
        if (json == null || json.isEmpty()) return null;
        NBTTagCompound nbt = JsonToNBT.getTagFromJson(json);
        if (nbt != null) {
            return ItemStack.loadItemStackFromNBT(nbt);
        }
        return null;
    }



}

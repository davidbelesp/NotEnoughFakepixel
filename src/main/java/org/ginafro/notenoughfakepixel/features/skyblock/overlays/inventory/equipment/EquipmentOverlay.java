package org.ginafro.notenoughfakepixel.features.skyblock.overlays.inventory.equipment;

import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.mixin.Accesors.AccessorGuiContainer;
import org.ginafro.notenoughfakepixel.utils.CustomConfigFiles;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.Utils;
import org.ginafro.notenoughfakepixel.variables.Gamemode;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ginafro.notenoughfakepixel.utils.CustomConfigFiles.EQUIPMENTS;
import static org.ginafro.notenoughfakepixel.utils.CustomConfigHandler.gson;

@RegisterEvents
public class EquipmentOverlay {

    public static final int EQUIPMENT_SLOT_OFFSET_Y = 8;
    public static final int ARMOR_OVERLAY_OVERHAND_WIDTH = 24;
    public static final int ARMOR_OVERLAY_HEIGHT = 86;
    public static final int ARMOR_OVERLAY_WIDTH = 31;
    protected boolean mouseClick = false;
    protected long lastClick = 0;
    protected static Map<Integer, ItemStack> equipments = new HashMap<>();
    protected static EquipmentData data;
    Minecraft mc;

    @SubscribeEvent
    public void onDraw(GuiScreenEvent.DrawScreenEvent.Post e) throws NBTException {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK || !Config.feature.overlays.equipment) return;
        RenderHelper.enableGUIStandardItemLighting();
        if (e.gui instanceof GuiInventory) {
            GuiInventory inventory = (GuiInventory) e.gui;
            AccessorGuiContainer container = ((AccessorGuiContainer) inventory);
            final int overlayLeft = container.getGuiLeft() - ARMOR_OVERLAY_OVERHAND_WIDTH;
            final int overlayTop = container.getGuiTop();
            renderHudBackground(e.gui);
            if (Mouse.isButtonDown(0)) {
                if (e.mouseX > overlayLeft && e.mouseY > overlayTop) {
                    if (e.mouseX < overlayLeft + ARMOR_OVERLAY_WIDTH && e.mouseY < overlayTop + ARMOR_OVERLAY_HEIGHT) {
                        sendEquipmentCommand(e.gui.mc.thePlayer);
                    }
                }
            }
            int yIndex = 0;
            if (data == null) return;
            if (!data.items.isEmpty()) {
                for (Integer i : data.getItemStacks().keySet()) {
                    ItemStack stack = data.getItemStacks().get(i);
                    if (stack.getItem() instanceof ItemBlock) {
                        ItemBlock itemBlock = (ItemBlock) stack.getItem();
                        if (itemBlock.block instanceof BlockStainedGlassPane) {
                            yIndex++;
                            continue;
                        }
                    }
                    Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, overlayLeft + 8, overlayTop + EQUIPMENT_SLOT_OFFSET_Y + (yIndex * 18));
                    if (e.mouseX > overlayLeft + 8 && e.mouseY > overlayTop + EQUIPMENT_SLOT_OFFSET_Y + (yIndex * 16)) {
                        if (e.mouseX < overlayLeft + 24 && e.mouseY < overlayTop + EQUIPMENT_SLOT_OFFSET_Y + (yIndex * 16) + 16) {

                            renderToolTip(e.gui, stack, e.mouseX, e.mouseY);
                        }
                    }
                    if (yIndex % 3 == 0) {
                        yIndex = 0;
                    }
                    yIndex++;
                }
            }
        }
        if (e.gui instanceof GuiContainer) {
            if (e.gui instanceof GuiChest) {
                ContainerChest cc = (ContainerChest) ((GuiContainer) e.gui).inventorySlots;
                if (cc.getLowerChestInventory().getDisplayName().getUnformattedText().startsWith("Your Equipment")) {
                    for (int i = 10; i < 38; i += 9) {
                        equipments.put(i, cc.getSlot(i).getStack());
                    }
                    data = new EquipmentData(equipments);
                    saveData();
                }
            }
        }
        RenderHelper.disableStandardItemLighting();
    }

    protected void renderToolTip(GuiScreen screen, ItemStack stack, int x, int y) {
        mc = Minecraft.getMinecraft();
        List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, EnumChatFormatting.GRAY + list.get(i));
            }
        }

        FontRenderer font = stack.getItem().getFontRenderer(stack);
        this.drawHoveringText(list, x, y, font == null ? this.mc.fontRendererObj : font, screen);
    }

    protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font, GuiScreen screen) {
        GuiUtils.drawHoveringText(textLines, x, y, screen.width, screen.height, -1, font);
    }

    public static void saveData() {
        File file = new File(EQUIPMENTS.path);
        try (FileWriter writer = new FileWriter(file)) {
            String json = gson.toJson(data);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        if (!new File(CustomConfigFiles.EQUIPMENTS.path).exists()) return;
        File file = new File(EQUIPMENTS.path);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            data = gson.fromJson(reader, EquipmentData.class); // Convert JSON back to StorageData
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEquipmentCommand(EntityPlayerSP p) {
        if (mouseClick) {
            p.sendChatMessage("/equipment");
            mouseClick = false;
            lastClick = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() - lastClick > 1000) {
                p.sendChatMessage("/equipment");
                lastClick = System.currentTimeMillis();
            }
        }
    }

    public void renderHudBackground(GuiScreen inventory) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        AccessorGuiContainer container = ((AccessorGuiContainer) inventory);
        final int overlayLeft = container.getGuiLeft() - ARMOR_OVERLAY_OVERHAND_WIDTH;
        final int overlayTop = container.getGuiTop();
        ResourceLocation equipmentTexture = new ResourceLocation("notenoughfakepixel", "equipment.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(equipmentTexture);
        Gui.drawScaledCustomSizeModalRect(overlayLeft, overlayTop, 0f, 0f, ARMOR_OVERLAY_WIDTH, ARMOR_OVERLAY_HEIGHT, ARMOR_OVERLAY_WIDTH, ARMOR_OVERLAY_HEIGHT, ARMOR_OVERLAY_WIDTH, ARMOR_OVERLAY_HEIGHT);
        GlStateManager.bindTexture(0);
    }
    public static void renderHudBackground(int guiLeft,int guiTop) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        final int overlayLeft = guiLeft - ARMOR_OVERLAY_OVERHAND_WIDTH;
        ResourceLocation equipmentTexture = new ResourceLocation("notenoughfakepixel", "equipment.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(equipmentTexture);
        Gui.drawScaledCustomSizeModalRect(overlayLeft, guiTop, 0f, 0f, ARMOR_OVERLAY_WIDTH, ARMOR_OVERLAY_HEIGHT, ARMOR_OVERLAY_WIDTH, ARMOR_OVERLAY_HEIGHT, ARMOR_OVERLAY_WIDTH, ARMOR_OVERLAY_HEIGHT);
        GlStateManager.bindTexture(0);
    }


}

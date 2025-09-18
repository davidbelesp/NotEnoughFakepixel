package org.ginafro.notenoughfakepixel.features.skyblock.accessories;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.serverdata.AccessoriesData;
import org.ginafro.notenoughfakepixel.utils.ItemUtils;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.ginafro.notenoughfakepixel.utils.StringUtils;
import org.ginafro.notenoughfakepixel.variables.Rarity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RegisterEvents
public class MissingAccessories {

    private static int ticks = 0;

    @SubscribeEvent
    public void onOpen(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!(e.gui instanceof GuiChest)) return;
        GuiChest chestGui = (GuiChest) e.gui;
        Container container = chestGui.inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        if (!checkEssentials()) return;
        ContainerChest containerChest = (ContainerChest) container;

        // throttle to 1 every 10 ticks
        ticks++;
        if (ticks < 100) return;
        ticks = 0;

        String name = containerChest
                .getLowerChestInventory()
                .getDisplayName()
                .getUnformattedText();

        if (name.equals("Accessory Bag")) {
            AccessoriesData.show = true;
            for (Slot slot : containerChest.inventorySlots) {
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;

                ItemStack item = slot.getStack();
                if (item == null) continue;



                if(ItemUtils.isSkyblockItem(item)) {
                    AccessoriesData.Accessory acc = new AccessoriesData.Accessory(
                            ItemUtils.getRarity(item).name(),
                            StringUtils.stripFormattingFast(item.getDisplayName())
                    );
                    AccessoriesData.INSTANCE.addAccessory(acc);
                    Logger.log(acc);
                }

                if (slot.getSlotIndex() == containerChest.inventorySlots.size() - 37) {
                    if (!item.getDisplayName().equals("§aNext Page")){
                        AccessoriesData.finalPage = true;
                    }
                }

            }
            AccessoriesData.calculateMp();
            //AccessoriesData.INSTANCE.getMissingAccessories().forEach(acc -> Logger.log("Missing: " + acc.getName()));
        } else {
            AccessoriesData.show = false;
        }
    }

    public boolean checkEssentials() {
        return true;
    }

    private GuiScreen lastScreen = null;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        GuiScreen current = Minecraft.getMinecraft().currentScreen;

        if (lastScreen instanceof GuiChest && !(current instanceof GuiChest)) {
            if (!AccessoriesData.INSTANCE.getCurrentAccessories().isEmpty()) {
                AccessoriesData.INSTANCE.clearAccessories();
            }
            AccessoriesData.finalPage = false;
            AccessoriesData.show = false;
            Logger.log("Cleared accessories data");
        }

        lastScreen = current;
    }

    @SubscribeEvent
    public void onDrawChestPanel(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!(e.gui instanceof GuiChest)) return;
        if (!checkEssentials()) return;
        GuiChest chest = (GuiChest) e.gui;

        if (!AccessoriesData.show) return;

        int guiLeft  = Ref.intField(GuiContainer.class, "guiLeft",  chest);
        int guiTop   = Ref.intField(GuiContainer.class, "guiTop",   chest);
        int xSize    = Ref.intField(GuiContainer.class, "xSize",    chest);
        int ySize    = Ref.intField(GuiContainer.class, "ySize",    chest);


        final int pad = 6;
        final int panelX = guiLeft + xSize + pad;
        final int panelY = guiTop;
        final int panelW = 140;

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        HashMap<String, Rarity> accessories = getMissingByRarity();

        int headerH = fr.FONT_HEIGHT + 6;
        int lineH   = fr.FONT_HEIGHT + 2;
        int panelH  = Math.max(ySize, headerH + accessories.size() * lineH + 6);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        Gui.drawRect(panelX, panelY, panelX + panelW, panelY + panelH, 0xB0000000); // translucent black

        Gui.drawRect(panelX, panelY, panelX + panelW, panelY + 1, 0x40FFFFFF);
        Gui.drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0x40000000);
        Gui.drawRect(panelX, panelY, panelX + 1, panelY + panelH, 0x40000000);
        Gui.drawRect(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, 0x40000000);

        String title = "Missing Accessories";
        fr.drawStringWithShadow(title, panelX + 6, panelY + 4, 0xFFFFFF);

        Gui.drawRect(panelX + 6, panelY + headerH - 2, panelX + panelW - 6, panelY + headerH - 1, 0x40FFFFFF);

        // Text lines
        int y = panelY + headerH;
        // check accessories hashmap and print each
        for (String name: accessories.keySet()) {
            Rarity rarity = accessories.get(name);
            String line = Rarity.getColor(rarity) + "■ §f" + name;
            String clipped = fr.trimStringToWidth(line, panelW - 12);
            fr.drawString(clipped, panelX + 6, y, 0xFFAAAAAA);
            y += lineH;
        }

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    @SubscribeEvent
    public void onDrawChestPanelLeft(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!(e.gui instanceof GuiChest)) return;
        if (!checkEssentials()) return;
        GuiChest chest = (GuiChest) e.gui;

        if (!AccessoriesData.show) return;

        int guiLeft = Ref.intField(GuiContainer.class, "guiLeft", chest);
        int guiTop  = Ref.intField(GuiContainer.class, "guiTop", chest);
        int ySize   = Ref.intField(GuiContainer.class, "ySize", chest);

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        String line1 = "Your Mp: " + AccessoriesData.totalMp + " / " + AccessoriesData.maxMp;
        String line2 = "Your Mp (Recomb): " + AccessoriesData.totalMp + " / " + AccessoriesData.maxMpRec;

        int maxWidth = Math.max(fr.getStringWidth(line1), fr.getStringWidth(line2));
        int panelW   = maxWidth + 12;
        int lineH    = fr.FONT_HEIGHT + 2;
        int panelH   = lineH * 2 + 6;

        int pad      = 6;
        int panelX   = guiLeft - panelW - pad;
        int panelY   = guiTop + (ySize - panelH) / 2;

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        Gui.drawRect(panelX, panelY, panelX + panelW, panelY + panelH, 0xB0000000);

        Gui.drawRect(panelX, panelY, panelX + panelW, panelY + 1, 0x40FFFFFF);
        Gui.drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0x40000000);
        Gui.drawRect(panelX, panelY, panelX + 1, panelY + panelH, 0x40000000);
        Gui.drawRect(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, 0x40000000);

        int textX = panelX + 6;
        int textY = panelY + 4;
        fr.drawStringWithShadow(line1, textX, textY, 0xFFFFFF);
        fr.drawStringWithShadow(line2, textX, textY + lineH, 0xFFFFFF);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private HashMap<String, Rarity> getMissingByRarity() {
        HashMap<String, Rarity> map = new HashMap<>();
        List<AccessoriesData.Accessory> missing = AccessoriesData.INSTANCE.getMissingAccessories();
        for (AccessoriesData.Accessory accessory : missing) {
            Rarity rarity = Rarity.fromString(accessory.getRarity());
            map.put(accessory.getName(), rarity);
        }
        return map;
    }


    private List<String> getMissing() {
        AccessoriesData data = AccessoriesData.INSTANCE;
        return data.getMissingAccessories().stream().map(
                AccessoriesData.Accessory::getName
        ).collect(Collectors.toList());
    }

    static final class Ref {
        static int intField(Class<?> owner, String name, Object instance) {
            try {
                Field f = owner.getDeclaredField(name);
                f.setAccessible(true);
                return f.getInt(instance);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}

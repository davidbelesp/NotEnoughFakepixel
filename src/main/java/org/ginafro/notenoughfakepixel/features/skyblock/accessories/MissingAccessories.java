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
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.serverdata.AccessoriesData;
import org.ginafro.notenoughfakepixel.utils.ItemUtils;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.ginafro.notenoughfakepixel.utils.ReflectionUtils;
import org.ginafro.notenoughfakepixel.utils.StringUtils;
import org.ginafro.notenoughfakepixel.variables.Rarity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RegisterEvents
public class MissingAccessories {

    private static int ticks = 0;

    // Panel scrolling
    private int accScroll = 0, accMaxScroll = 0;
    private int accPanelX, accPanelY, accPanelW, accPanelH;

    // Field cache, reflections
    private static final Field F_GUI_LEFT = ReflectionHelper.findField(GuiContainer.class,
            "field_147003_i", "guiLeft");
    private static final Field F_GUI_TOP  = ReflectionHelper.findField(GuiContainer.class,
            "field_147009_r", "guiTop");
    private static final Field F_XSIZE    = ReflectionHelper.findField(GuiContainer.class,
            "field_146999_f", "xSize");
    private static final Field F_YSIZE    = ReflectionHelper.findField(GuiContainer.class,
            "field_147000_g", "ySize");

    static {
        F_GUI_LEFT.setAccessible(true);
        F_GUI_TOP.setAccessible(true);
        F_XSIZE.setAccessible(true);
        F_YSIZE.setAccessible(true);
    }

    @SubscribeEvent public void onOpen(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!(e.gui instanceof GuiChest)) return;
        GuiChest chestGui = (GuiChest) e.gui;
        Container container = chestGui.inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        if (!checkEssentials()) return;

        ContainerChest containerChest = (ContainerChest) container;

        ticks++; if (ticks < 10) return;
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
                            StringUtils.stripFormattingFast(item.getDisplayName()));
                    AccessoriesData.INSTANCE.addAccessory(acc);
                }

                if (slot.getSlotIndex() == containerChest.inventorySlots.size() - 37) {
                    if (!item.getDisplayName().equals("§aNext Page")){
                        AccessoriesData.finalPage = true;
                    }
                }
            } AccessoriesData.calculateMp();
        } else AccessoriesData.show = false;
    }

    private GuiScreen lastScreen = null;

    public boolean checkEssentials() {
        return Config.feature.accessories.enable;
    }

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
        }
        lastScreen = current;
    }

    @SubscribeEvent
    public void onMouseWheel(GuiScreenEvent.MouseInputEvent.Pre e) {
        if (!(e.gui instanceof GuiChest)) return;
        if (!AccessoriesData.show) return;

        int dWheel = org.lwjgl.input.Mouse.getDWheel();
        if (dWheel == 0) return;

        final Minecraft mc = Minecraft.getMinecraft();
        int mouseX = org.lwjgl.input.Mouse.getX() * e.gui.width  / mc.displayWidth;
        int mouseY = e.gui.height - org.lwjgl.input.Mouse.getY() * e.gui.height / mc.displayHeight - 1;

        if (mouseX < accPanelX || mouseX > accPanelX + accPanelW || mouseY < accPanelY || mouseY > accPanelY + accPanelH) return;

        final int step = mc.fontRendererObj.FONT_HEIGHT + 2;
        accScroll = clamp(accScroll - Integer.signum(dWheel) * step, 0, accMaxScroll);
    }
    private static int clamp(int v, int lo, int hi){
        return v < lo ? lo : (v > hi ? hi : v);
    }

    // Also scroll with arrow keys
    @SubscribeEvent
    public void onKeyDown(GuiScreenEvent.KeyboardInputEvent.Pre e) {
        if (!(e.gui instanceof GuiChest)) return;
        if (!AccessoriesData.show) return;

        final Minecraft mc = Minecraft.getMinecraft();
        int key = org.lwjgl.input.Keyboard.getEventKey();
        if (key == 0) return;
        if (!org.lwjgl.input.Keyboard.getEventKeyState()) return;

        int step = mc.fontRendererObj.FONT_HEIGHT + 2;
        if (key == org.lwjgl.input.Keyboard.KEY_UP) {
            accScroll = clamp(accScroll - step, 0, accMaxScroll);
            e.setCanceled(true);
        } else if (key == org.lwjgl.input.Keyboard.KEY_DOWN) {
            accScroll = clamp(accScroll + step, 0, accMaxScroll);
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDrawChestPanel(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!(e.gui instanceof GuiChest)) return;
        if (!checkEssentials()) return;
        if (!AccessoriesData.show) return;
        if (!Config.feature.accessories.showMissingAccessoriesList) return;

        GuiChest chest = (GuiChest) e.gui;

        try {
            int xSize = ReflectionUtils.getInt(F_XSIZE, chest, 176);
            int ySize = ReflectionUtils.getInt(F_YSIZE, chest, 222);
            int guiLeft = ReflectionUtils.getInt(F_GUI_LEFT, chest, (e.gui.width  - xSize) / 2);
            int guiTop  = ReflectionUtils.getInt(F_GUI_TOP,  chest, (e.gui.height - ySize) / 2);

            final int pad = 6;
            final int panelX = guiLeft + xSize + pad;
            final int panelY = guiTop;
            final int panelW = 140;

            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            HashMap<String, Rarity> accessories = getMissingByRarity();

            int headerH = fr.FONT_HEIGHT + 6;
            int lineH   = fr.FONT_HEIGHT + 2;

            // --- Decide a fixed visible height so scrolling is useful
            int visibleListH = Math.min(180, ySize - headerH - 6);
            int panelH = headerH + Math.max(0, visibleListH) + 6;

            // Save panel rect for scissor & scrollbar
            accPanelX = panelX; accPanelY = panelY; accPanelW = panelW; accPanelH = panelH;

            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.translate(0, 0, -200);

            // Background + border
            Gui.drawRect(panelX, panelY, panelX + panelW, panelY + panelH, 0xB0000000);
            Gui.drawRect(panelX, panelY, panelX + panelW, panelY + 1, 0x40FFFFFF);
            Gui.drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0x40000000);
            Gui.drawRect(panelX, panelY, panelX + 1, panelY + panelH, 0x40000000);
            Gui.drawRect(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, 0x40000000);

            // Title + underline
            fr.drawStringWithShadow("§7Missing Accessories", panelX + 6, panelY + 4, 0xFFFFFF);
            Gui.drawRect(panelX + 6, panelY + headerH - 2, panelX + panelW - 6, panelY + headerH - 1, 0x40FFFFFF);

            if (!AccessoriesData.finalPage || accessories.isEmpty()) {
                String line = AccessoriesData.finalPage ? "None! You're all set!" : "Visit all pages to see";
                String clipped = fr.trimStringToWidth(line, panelW - 12);
                int textW = fr.getStringWidth(clipped);
                int textX = panelX + (panelW - textW) / 2;
                int textY = panelY + (panelH - fr.FONT_HEIGHT) / 2;
                fr.drawString(clipped, textX, textY, 0xFFAAAAAA);

                GlStateManager.enableDepth();
                GlStateManager.enableLighting();
                accScroll = 0; accMaxScroll = 0;
                return;
            }

            // ---- SCROLLABLE LIST ----
            java.util.List<java.util.Map.Entry<String, Rarity>> entries =
                    new java.util.ArrayList<>(accessories.entrySet());
            entries.sort((a, b) -> {
                int c = b.getValue().ordinal() - a.getValue().ordinal();
                if (c != 0) return c;
                return a.getKey().compareToIgnoreCase(b.getKey());
            });

            int contentH = entries.size() * lineH + 8;
            int viewH = Math.max(0, panelH - headerH - 6);
            accMaxScroll = Math.max(0, contentH - viewH);

            if (accScroll < 0) accScroll = 0;
            if (accScroll > accMaxScroll) accScroll = accMaxScroll;

            int listX = panelX + 6;
            int listY = panelY + headerH;
            enableScissor(listX - 2, listY, panelW - 8, viewH);

            int y = listY + 4 - accScroll;
            for (java.util.Map.Entry<String, Rarity> e2 : entries) {
                String name = e2.getKey();
                Rarity rar  = e2.getValue();
                String line = Rarity.getColor(rar) + "■ " + name;

                if (y > listY - lineH && y < listY + viewH) {
                    fr.drawString(fr.trimStringToWidth(line, panelW - 12), listX, y, 0xFFFFFFFF);
                }
                y += lineH;
            }

            disableScissor();

            if (accMaxScroll > 0) {
                int trackX = panelX + panelW - 3;
                int trackY = listY + 2;
                int trackH = viewH - 4;

                int barH = Math.max(16, (int) (trackH * (viewH / (float) (contentH))));
                int barY = trackY + (int) ((trackH - barH) * (accScroll / (float) accMaxScroll));

                Gui.drawRect(trackX, trackY, trackX + 2, trackY + trackH, 0x40FFFFFF);
                Gui.drawRect(trackX, barY,   trackX + 2, barY + barH,   0xFFFFFFFF);
            }

            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.translate(0, 0, +200);
        } catch (Exception ex) {
            Logger.logConsole("Error drawing accessories panel" + ex.getMessage());
        }
    }

    private void enableScissor(int x, int y, int w, int h) {
        Minecraft mc = Minecraft.getMinecraft();
        net.minecraft.client.gui.ScaledResolution sr = new net.minecraft.client.gui.ScaledResolution(mc);
        int scale = sr.getScaleFactor();
        int fbH = mc.displayHeight;

        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor(
                x * scale,
                fbH - (y + h) * scale,
                w * scale,
                h * scale
        );
    }

    private void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }

    @SubscribeEvent
    public void onDrawChestPanelLeft(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!(e.gui instanceof GuiChest)) return;
        if (!checkEssentials()) return;
        GuiChest chest = (GuiChest) e.gui;

        if (!AccessoriesData.show) return;

        int xSize = ReflectionUtils.getInt(F_XSIZE, chest, 176);
        int ySize = ReflectionUtils.getInt(F_YSIZE, chest, 222);
        int guiLeft = ReflectionUtils.getInt(F_GUI_LEFT, chest, (e.gui.width  - xSize) / 2);
        int guiTop  = ReflectionUtils.getInt(F_GUI_TOP,  chest, (e.gui.height - ySize) / 2);

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        List<String> lines = new ArrayList<>();
        lines.add("§6MP Estimation");
        if (AccessoriesData.finalPage) {
            if (AccessoriesData.totalMp < AccessoriesData.maxMp) {
                lines.add("§7Your Mp: " + AccessoriesData.getColorLevel(AccessoriesData.maxMp) + AccessoriesData.totalMp + "§7 / §b" + AccessoriesData.maxMp);
            }
            lines.add("§7Your Mp (§6Recomb§7): " + AccessoriesData.getColorLevel(AccessoriesData.maxMpRec) + AccessoriesData.totalMp + "§7 / §b" + AccessoriesData.maxMpRec);
        } else {
            lines.add("§cVisit All pages first");
            lines.add("Max mp is: §b" + AccessoriesData.maxMpRec);
        }

        int maxWidth = lines.stream().map(fr::getStringWidth).max(Integer::compare).orElse(0);
        int panelW   = maxWidth + 12;
        int lineH    = fr.FONT_HEIGHT + 2;
        int panelH   = lineH * lines.size() + 6;

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
        for (String line : lines) {
            fr.drawStringWithShadow(line, textX, textY, 0xFFFFFF);
            textY += lineH;
        }

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
        return data.getRepoAccessories().stream().map(
                AccessoriesData.Accessory::getName
        ).collect(Collectors.toList());
    }
}

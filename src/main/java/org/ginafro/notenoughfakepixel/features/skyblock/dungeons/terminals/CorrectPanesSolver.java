package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.terminals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorrectPanesSolver {

    private static final int SLOT_SIZE = 16;
    private static final int COLUMNS = 9;
    private static final int ROWS = 6;

    private static final int INNER_COLUMNS = 7;
    private static final int INNER_ROWS = 4;

    private List<Slot> lastCorrectSlots = new ArrayList<>();
    private Map<Integer, SlotPosition> slotPositions = new HashMap<>();

    private static class SlotPosition {
        final int x;
        final int y;
        final int slotId;

        SlotPosition(int x, int y, int slotId) {
            this.x = x;
            this.y = y;
            this.slotId = slotId;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!(event.gui instanceof GuiChest)) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        GuiChest chestGui = (GuiChest) event.gui;
        Container container = chestGui.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        String displayName = ((ContainerChest) container)
                .getLowerChestInventory()
                .getDisplayName()
                .getUnformattedText()
                .trim();
        if (Configuration.dungeonsTerminalCustomGui && displayName.equals("Correct all the panes!")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Configuration.dungeonsTerminalCorrectPanesSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!(event.gui instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String name = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!name.equals("Correct all the panes!")) return;

        lastCorrectSlots.clear();
        slotPositions.clear();

        if (Configuration.dungeonsTerminalCustomGui) {
            for (Slot slot : containerChest.inventorySlots) {
                int slotId = containerChest.inventorySlots.indexOf(slot);
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory || slotId == 49) continue;

                int originalRow = slotId / COLUMNS;
                int originalColumn = slotId % COLUMNS;

                if (originalRow == 0 || originalRow == ROWS - 1 || originalColumn == 0 || originalColumn == COLUMNS - 1) {
                    continue;
                }

                ItemStack item = slot.getStack();
                if (item == null) continue;
                if (!(Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane)) continue;

                int meta = item.getMetadata();
                if (meta == 14 || meta == 0) {
                    lastCorrectSlots.add(slot);
                    int innerX = (originalColumn - 1) * SLOT_SIZE;
                    int innerY = (originalRow - 1) * SLOT_SIZE;
                    slotPositions.put(slotId, new SlotPosition(innerX, innerY, slotId));
                }
            }

            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            int screenWidth = sr.getScaledWidth();
            int screenHeight = sr.getScaledHeight();

            GlStateManager.pushMatrix();
            float scale = Configuration.dungeonsTerminalScale;
            int guiWidth = (int) (INNER_COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int) (INNER_ROWS * SLOT_SIZE * scale);
            int guiLeft = (screenWidth - guiWidth) / 2;
            int guiTop = (screenHeight - guiHeight) / 2;

            GlStateManager.translate(guiLeft, guiTop, 0);
            GlStateManager.scale(scale, scale, 1.0f);

            Gui.drawRect(-2, -12,
                    (INNER_COLUMNS * SLOT_SIZE) + 2,
                    (INNER_ROWS * SLOT_SIZE) + 2,
                    0x80000000);

            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    "§8[§bNEF§8] §aRed & Green!",
                    0,
                    -10,
                    0xFFFFFF);

            for (Slot slot : lastCorrectSlots) {
                SlotPosition pos = slotPositions.get(containerChest.inventorySlots.indexOf(slot));
                if (pos != null) {
                    drawRect(pos.x + 1, pos.y + 1, pos.x + SLOT_SIZE - 1, pos.y + SLOT_SIZE - 1, Configuration.dungeonsCorrectColor.getRGB());
                }
            }
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!Configuration.dungeonsTerminalCorrectPanesSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!(e.gui instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) e.gui;
        Container container = chest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String name = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!name.equals("Correct all the panes!")) return;

        if (!Configuration.dungeonsTerminalCustomGui) {
            for (Slot slot : containerChest.inventorySlots) {
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                int slotId = containerChest.inventorySlots.indexOf(slot);
                if (slotId == 49) continue;

                int originalRow = slotId / COLUMNS;
                int originalColumn = slotId % COLUMNS;
                if (originalRow == 0 || originalRow == ROWS - 1 || originalColumn == 0 || originalColumn == COLUMNS - 1) {
                    continue;
                }

                ItemStack item = slot.getStack();
                if (item == null) continue;
                if (Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane) {
                    int meta = item.getMetadata();
                    if (meta == 5) {
                        item.getItem().setDamage(item, 15);
                    } else if (meta == 14 || meta == 0) {
                        RenderUtils.drawOnSlot(chest.inventorySlots.inventorySlots.size(),
                                slot.xDisplayPosition, slot.yDisplayPosition,
                                Configuration.dungeonsCorrectColor.getRGB());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Configuration.dungeonsPreventMissclicks) return;
        if (!Configuration.dungeonsTerminalCorrectPanesSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!Mouse.getEventButtonState()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChest)) return;

        GuiChest guiChest = (GuiChest) mc.currentScreen;
        Container container = guiChest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String title = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.equals("Correct all the panes!")) return;

        if (Configuration.dungeonsTerminalCustomGui) {
            ScaledResolution sr = new ScaledResolution(mc);
            float scale = Configuration.dungeonsTerminalScale;

            int button = Mouse.getEventButton();
            if (button != 0) return; // Only process left clicks

            int mouseX = (Mouse.getEventX() * sr.getScaledWidth()) / mc.displayWidth;
            int mouseY = sr.getScaledHeight() - (Mouse.getEventY() * sr.getScaledHeight()) / mc.displayHeight - 1;

            int guiWidth = (int) (INNER_COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int) (INNER_ROWS * SLOT_SIZE * scale);
            int guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
            int guiTop = (sr.getScaledHeight() - guiHeight) / 2;

            if (mouseX < guiLeft || mouseX >= guiLeft + guiWidth ||
                    mouseY < guiTop || mouseY >= guiTop + guiHeight) {
                event.setCanceled(true);
                return;
            }

            float relX = (mouseX - guiLeft) / scale;
            float relY = (mouseY - guiTop) / scale;

            int innerColumn = (int) (relX / SLOT_SIZE);
            int innerRow = (int) (relY / SLOT_SIZE);

            if (innerColumn < 0 || innerColumn >= INNER_COLUMNS || innerRow < 0 || innerRow >= INNER_ROWS) {
                event.setCanceled(true);
                return;
            }

            int originalRow = innerRow + 1;
            int originalColumn = innerColumn + 1;
            int slotId = originalRow * COLUMNS + originalColumn;

            if (slotId < 0 || slotId >= containerChest.inventorySlots.size()) {
                event.setCanceled(true);
                return;
            }

            Slot slot = containerChest.inventorySlots.get(slotId);
            if (lastCorrectSlots.contains(slot)) {
                mc.playerController.windowClick(
                        containerChest.windowId,
                        slot.slotNumber,
                        0,
                        0,
                        mc.thePlayer
                );
                mc.playerController.windowClick(
                        containerChest.windowId,
                        slot.slotNumber,
                        0,
                        0,
                        mc.thePlayer
                );
                event.setCanceled(true);
            } else {
                event.setCanceled(true);
            }
        }
    }

    private static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}

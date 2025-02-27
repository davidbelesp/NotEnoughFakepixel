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
import net.minecraft.util.StringUtils;
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

public class StartingWithSolver {

    private static final int SLOT_SIZE = 16;
    private static final int COLUMNS = 9;
    private static final int ROWS = 6;

    private List<Slot> lastCorrectSlots = new ArrayList<>();
    private Map<Integer, SlotPosition> slotPositions = new HashMap<>();
    private char lastLetter = ' ';

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

        if (Configuration.dungeonsTerminalCustomGui && displayName.contains("What starts with")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Configuration.dungeonsTerminalStartsWithSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!(event.gui instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;

        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String name = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!name.contains("What starts with")) return;

        lastLetter = name.charAt(name.length() - 2);
        lastCorrectSlots.clear();
        slotPositions.clear();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        if (Configuration.dungeonsTerminalCustomGui) {
            final int INNER_COLUMNS = 7;
            final int INNER_ROWS = 4;

            for (Slot slot : containerChest.inventorySlots) {
                int slotId = containerChest.inventorySlots.indexOf(slot);

                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory || slotId == 49) continue;

                int row = slotId / COLUMNS;
                int col = slotId % COLUMNS;

                if (row < 1 || row > 4 || col < 1 || col > 7) continue;

                ItemStack item = slot.getStack();
                if (item == null || item.isItemEnchanted()) continue;

                if (StringUtils.stripControlCodes(item.getDisplayName()).charAt(0) == lastLetter) {
                    lastCorrectSlots.add(slot);
                    slotPositions.put(slotId, new SlotPosition(
                            slot.xDisplayPosition,
                            slot.yDisplayPosition,
                            slotId
                    ));
                }
            }

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
                    0x80000000
            );

            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    "§8[§bNEF§8] §aStarts With '" + lastLetter + "'",
                    0,
                    -10,
                    0xFFFFFF
            );

            for (Slot slot : lastCorrectSlots) {
                int slotIndex = containerChest.inventorySlots.indexOf(slot);
                int row = slotIndex / COLUMNS;
                int col = slotIndex % COLUMNS;

                int innerCol = col - 1;
                int innerRow = row - 1;

                int x = innerCol * SLOT_SIZE;
                int y = innerRow * SLOT_SIZE;

                drawRect(x + 1, y + 1,
                        x + SLOT_SIZE - 1,
                        y + SLOT_SIZE - 1,
                        Configuration.dungeonsCorrectColor.getRGB()
                );
            }
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!Configuration.dungeonsTerminalStartsWithSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!(e.gui instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) e.gui;
        Container container = chest.inventorySlots;

        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String name = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (name.contains("What starts with")) {
            char letter = name.charAt(name.length() - 2);

            for (Slot slot : containerChest.inventorySlots) {
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                int slotId = containerChest.inventorySlots.indexOf(slot);

                if (slotId == 49) continue;

                ItemStack item = slot.getStack();
                if (item == null) continue;

                if (containerChest.inventorySlots.indexOf(slot) == 49) {
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    item.setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                    item.getItem().setDamage(item, 15);
                    continue;
                }

                if (item.isItemEnchanted()) {
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    slot.getStack().setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                    slot.getStack().getItem().setDamage(slot.getStack(), 15);
                    continue;
                }

                if (StringUtils.stripControlCodes(item.getDisplayName()).charAt(0) == letter) {
                    RenderUtils.drawOnSlot(chest.inventorySlots.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, Configuration.dungeonsCorrectColor.getRGB());
                } else {
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    if (Block.getBlockFromItem(slot.getStack().getItem()) instanceof BlockStainedGlassPane) {
                        if (slot.getStack().getMetadata() != 15) {
                            slot.getStack().getItem().setDamage(slot.getStack(), 15);
                        }
                    } else {
                        slot.getStack().setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                        slot.getStack().getItem().setDamage(slot.getStack(), 15);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!Configuration.dungeonsPreventMissclicks) return;
        if (!Configuration.dungeonsTerminalStartsWithSolver) return;
        if (!Mouse.getEventButtonState()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChest)) return;

        GuiChest guiChest = (GuiChest) mc.currentScreen;
        Container container = guiChest.inventorySlots;

        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String title = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.contains("What starts with")) return;

        if (Configuration.dungeonsTerminalCustomGui) {
            ScaledResolution sr = new ScaledResolution(mc);
            float scale = Configuration.dungeonsTerminalScale;
            int button = Mouse.getEventButton();
            if (button != 0) return; // Only handle left clicks

            int mouseX = (Mouse.getEventX() * sr.getScaledWidth()) / mc.displayWidth;
            int mouseY = sr.getScaledHeight() - (Mouse.getEventY() * sr.getScaledHeight()) / mc.displayHeight - 1;

            int guiWidth = (int)(COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int)(ROWS * SLOT_SIZE * scale);
            int guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
            int guiTop = (sr.getScaledHeight() - guiHeight) / 2;

            if (mouseX < guiLeft || mouseX >= guiLeft + guiWidth ||
                    mouseY < guiTop || mouseY >= guiTop + guiHeight) {
                event.setCanceled(true);
                return;
            }

            float relX = (mouseX - guiLeft) / scale;
            float relY = (mouseY - guiTop) / scale;

            boolean validClick = false;
            for (Slot slot : lastCorrectSlots) {
                int slotIndex = containerChest.inventorySlots.indexOf(slot);
                if (slotIndex == -1) continue;

                int x = (slotIndex % COLUMNS) * SLOT_SIZE;
                int y = (slotIndex / COLUMNS) * SLOT_SIZE;

                if (relX >= x && relX <= x + SLOT_SIZE &&
                        relY >= y && relY <= y + SLOT_SIZE) {
                    // Send two left clicks
                    mc.playerController.windowClick(
                            containerChest.windowId,
                            slot.slotNumber,
                            0, // 0 for left-click action
                            button,
                            mc.thePlayer
                    );
                    mc.playerController.windowClick(
                            containerChest.windowId,
                            slot.slotNumber,
                            0, // 0 for left-click action
                            button,
                            mc.thePlayer
                    );
                    validClick = true;
                    break;
                }
            }

            if (!validClick) event.setCanceled(true);
        } else {
            Slot hoveredSlot = guiChest.getSlotUnderMouse();
            if (hoveredSlot != null && hoveredSlot.getStack() != null) {
                ItemStack item = hoveredSlot.getStack();
                boolean isBadItem = Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane
                        && item.getMetadata() == 15;
                boolean isWrongLetter = StringUtils.stripControlCodes(item.getDisplayName()).charAt(0) != lastLetter;

                if (isBadItem || isWrongLetter) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}

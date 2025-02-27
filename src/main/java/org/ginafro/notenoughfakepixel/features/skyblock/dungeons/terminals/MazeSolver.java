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

public class MazeSolver {
    private static final int SLOT_SIZE = 16;
    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int[] ADJACENT_OFFSETS = {1, -1, 9, -9};
    int[] adjacentPositions = new int[] {1,-1,9,-9};

    private final List<Slot> targetSlots = new ArrayList<>();
    private final Map<Integer, SlotPosition> slotPositions = new HashMap<>();

    private static class SlotPosition {
        final int x;
        final int y;
        final boolean isTarget;

        SlotPosition(int x, int y, boolean isTarget) {
            this.x = x;
            this.y = y;
            this.isTarget = isTarget;
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

        if (Configuration.dungeonsTerminalCustomGui && displayName.contains("Complete the maze!")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRender(GuiScreenEvent.BackgroundDrawnEvent e){
        if(!Configuration.dungeonsTerminalMazeSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if(!(e.gui instanceof GuiChest)) return;
        GuiChest chest = (GuiChest) e.gui;
        Container container = chest.inventorySlots;
        if(!(container instanceof ContainerChest)) return;
        ContainerChest containerChest = (ContainerChest) container;
        String name = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if(name.contains("Complete the maze!")){
            for(Slot slot : containerChest.inventorySlots){
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                ItemStack item = slot.getStack();
                if(item == null) continue;
                if (Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane) {
                    if (item.getMetadata() == 5) {
                        Slot targetSlot = getClosestWhiteSlot(containerChest.inventorySlots, slot);
                        if (targetSlot == null) continue;
                        RenderUtils.drawOnSlot(container.inventorySlots.size(), targetSlot.xDisplayPosition, targetSlot.yDisplayPosition, Configuration.dungeonsCorrectColor.getRGB());
                    }
                }
            }
        }
    }

    private Slot getClosestWhiteSlot(List<Slot> inventory, Slot slot) {
        for (int i:adjacentPositions) {
            if (slot.getSlotIndex()+i < 0 || slot.getSlotIndex()+i > 53) continue;
            try {
                if (inventory.get(slot.getSlotIndex() + i).getStack().getMetadata() == 0)
                    return inventory.get(slot.getSlotIndex() + i);
            } catch ( NullPointerException exception) {
                return null;
            }
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Configuration.dungeonsTerminalMazeSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!(event.gui instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String name = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!name.contains("Complete the maze!")) return;

        targetSlots.clear();
        slotPositions.clear();

        for (Slot slot : containerChest.inventorySlots) {
            if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;

            ItemStack item = slot.getStack();
            if (item == null) continue;

            Block block = Block.getBlockFromItem(item.getItem());
            if (!(block instanceof BlockStainedGlassPane)) continue;

            int meta = item.getMetadata();
            int slotIndex = slot.slotNumber;

            if (meta == 0) {
                slotPositions.put(slotIndex, new SlotPosition(0, 0, false));
            } else if (meta == 5) {
                findAdjacentWhiteSlots(containerChest.inventorySlots, slot).forEach(target -> {
                    targetSlots.add(target);
                });
            }
        }

        if (Configuration.dungeonsTerminalCustomGui) {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            GlStateManager.pushMatrix();
            float scale = Configuration.dungeonsTerminalScale;

            int guiWidth = (int)(COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int)(ROWS * SLOT_SIZE * scale);
            int guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
            int guiTop = (sr.getScaledHeight() - guiHeight) / 2;

            GlStateManager.translate(guiLeft, guiTop, 0);
            GlStateManager.scale(scale, scale, 1.0f);

            Gui.drawRect(-2, -12,
                    COLUMNS * SLOT_SIZE + 2,
                    ROWS * SLOT_SIZE + 2,
                    0x80000000);

            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    "§8[§bNEF§8] §aMaze!",
                    0,
                    -10,
                    0xFFFFFF
            );

            slotPositions.forEach((id, pos) -> {
                if (!pos.isTarget) {
                    Gui.drawRect(
                            (id % COLUMNS) * SLOT_SIZE,
                            (id / COLUMNS) * SLOT_SIZE,
                            (id % COLUMNS) * SLOT_SIZE + SLOT_SIZE,
                            (id / COLUMNS) * SLOT_SIZE + SLOT_SIZE,
                            0x99FFFFFF
                    );
                }
            });

            targetSlots.forEach(slot -> {
                int x = (slot.slotNumber % COLUMNS) * SLOT_SIZE;
                int y = (slot.slotNumber / COLUMNS) * SLOT_SIZE;
                Gui.drawRect(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1,
                        Configuration.dungeonsCorrectColor.getRGB());
            });

            GlStateManager.popMatrix();
        } else {
            for (Slot slot : containerChest.inventorySlots) {
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;

                if (slotPositions.containsKey(slot.slotNumber)) {
                    RenderUtils.drawOnSlot(
                            containerChest.inventorySlots.size(),
                            slot.xDisplayPosition,
                            slot.yDisplayPosition,
                            0x99FFFFFF
                    );
                }

                if (targetSlots.contains(slot)) {
                    RenderUtils.drawOnSlot(
                            containerChest.inventorySlots.size(),
                            slot.xDisplayPosition,
                            slot.yDisplayPosition,
                            Configuration.dungeonsCorrectColor.getRGB()
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Configuration.dungeonsTerminalMazeSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        int button = Mouse.getEventButton();
        if (button != 0 || !Mouse.getEventButtonState()) return; // Only register left click

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChest)) return;

        GuiChest guiChest = (GuiChest) mc.currentScreen;
        ContainerChest container = (ContainerChest) guiChest.inventorySlots;
        String title = container.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.contains("Complete the maze!")) return;

        if (Configuration.dungeonsTerminalCustomGui) {
            ScaledResolution sr = new ScaledResolution(mc);
            float scale = Configuration.terminalsScale;

            int mouseX = (Mouse.getEventX() * sr.getScaledWidth()) / mc.displayWidth;
            int mouseY = sr.getScaledHeight() - (Mouse.getEventY() * sr.getScaledHeight()) / mc.displayHeight - 1;

            int guiWidth = (int) (COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int) (ROWS * SLOT_SIZE * scale);
            int guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
            int guiTop = (sr.getScaledHeight() - guiHeight) / 2;

            if (mouseX < guiLeft || mouseX >= guiLeft + guiWidth ||
                    mouseY < guiTop || mouseY >= guiTop + guiHeight) {
                event.setCanceled(true);
                return;
            }

            float relX = (mouseX - guiLeft) / scale;
            float relY = (mouseY - guiTop) / scale;

            int slotCol = (int) (relX / SLOT_SIZE);
            int slotRow = (int) (relY / SLOT_SIZE);
            int slotId = slotRow * COLUMNS + slotCol;

            if (slotId < 0 || slotId >= container.inventorySlots.size()) {
                event.setCanceled(true);
                return;
            }

            Slot slot = container.inventorySlots.get(slotId);
            if (targetSlots.contains(slot)) {
                mc.playerController.windowClick(
                        container.windowId,
                        slot.slotNumber,
                        0,
                        0,
                        mc.thePlayer
                );
                mc.playerController.windowClick(
                        container.windowId,
                        slot.slotNumber,
                        0,
                        0,
                        mc.thePlayer
                );
                event.setCanceled(true);
            }
        } else {
            Slot hoveredSlot = guiChest.getSlotUnderMouse();
            if (hoveredSlot != null && targetSlots.contains(hoveredSlot)) {
                mc.playerController.windowClick(
                        container.windowId,
                        hoveredSlot.slotNumber,
                        0,
                        0,
                        mc.thePlayer
                );
                mc.playerController.windowClick(
                        container.windowId,
                        hoveredSlot.slotNumber,
                        0,
                        0,
                        mc.thePlayer
                );
            }
        }
    }

    private List<Slot> findAdjacentWhiteSlots(List<Slot> slots, Slot origin) {
        List<Slot> adjacentWhites = new ArrayList<>();
        for (int offset : ADJACENT_OFFSETS) {
            int targetIndex = origin.slotNumber + offset;
            if (targetIndex >= 0 && targetIndex < slots.size()) {
                Slot target = slots.get(targetIndex);
                ItemStack item = target.getStack();
                if (item != null &&
                        Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane &&
                        item.getMetadata() == 0) {
                    adjacentWhites.add(target);
                }
            }
        }
        return adjacentWhites;
    }
}

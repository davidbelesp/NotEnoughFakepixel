package com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.utils.SoundUtils;
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
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.*;

@RegisterEvents
public class MazeSolver {
    private static final int SLOT_SIZE = 16;
    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int[] ADJACENT_OFFSETS = {1, -1, 9, -9};
    private final int[] adjacentPositions = {1, -1, 9, -9};

    private final List<Slot> targetSlots = new ArrayList<>();
    private final List<Slot> alternativeSlots = new ArrayList<>();
    private final Map<Integer, SlotPosition> slotPositions = new HashMap<>();
    private final long lastScanTime = 0;
    private final ContainerChest lastContainer = null;

    private final LinkedList<Integer> clickQueue = new LinkedList<>();
    private long lastClickTime = 0;

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

    @SubscribeEvent
    public void onOpen(GuiOpenEvent e) {
        if (!(e.gui instanceof GuiChest)) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        GuiChest chestGui = (GuiChest) e.gui;
        Container container = chestGui.inventorySlots;

        if (!(container instanceof ContainerChest)) return;

        String displayName = ((ContainerChest) container)
                .getLowerChestInventory()
                .getDisplayName()
                .getUnformattedText()
                .trim();

        if (Config.feature.dungeons.dungeonsCustomGuiMaze && displayName.contains("Complete the maze!")) {
            targetSlots.clear();
            alternativeSlots.clear();
            slotPositions.clear();
            clickQueue.clear();
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

        if (Config.feature.dungeons.dungeonsCustomGuiMaze && displayName.contains("Complete the maze!")) {
            event.setCanceled(true);
        }
    }

    private void updateSlots(ContainerChest containerChest) {
        targetSlots.clear();
        alternativeSlots.clear();
        slotPositions.clear();

        for (Slot slot : containerChest.inventorySlots) {
            if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;

            ItemStack item = slot.getStack();
            if (item == null) continue;

            Block block = Block.getBlockFromItem(item.getItem());
            if (!(block instanceof BlockStainedGlassPane)) continue;

            int meta = item.getMetadata();
            if (meta == 0) {
                slotPositions.put(slot.slotNumber, new SlotPosition(
                        slot.xDisplayPosition,
                        slot.yDisplayPosition,
                        false
                ));
            } else if (meta == 5) {
                targetSlots.addAll(findAdjacentWhiteSlots(containerChest.inventorySlots, slot));
            }
        }

        for (Slot targetSlot : targetSlots) {
            List<Slot> adjacent = findAdjacentWhiteSlots(containerChest.inventorySlots, targetSlot);
            for (Slot adj : adjacent) {
                if (!targetSlots.contains(adj) && !alternativeSlots.contains(adj)) {
                    alternativeSlots.add(adj);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Config.feature.dungeons.dungeonsTerminalMazeSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!(event.gui instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        ContainerChest containerChest = (ContainerChest) container;
        String name = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!name.contains("Complete the maze!")) return;

        updateSlots(containerChest);

        if (Config.feature.dungeons.dungeonsCustomGuiMaze) {
            renderCustomGui(containerChest);
        } else {
            renderVanillaOverlay(containerChest);
        }

        long currentTime = System.currentTimeMillis();
        if (!clickQueue.isEmpty() && currentTime - lastClickTime > 20) {
            int slotNumber = clickQueue.poll();
            Slot slot = containerChest.getSlot(slotNumber);
            if (slot != null) {
                Minecraft.getMinecraft().playerController.windowClick(
                            containerChest.windowId,
                            slotNumber,
                            2,
                            0,
                            Minecraft.getMinecraft().thePlayer
                );
                lastClickTime = currentTime;
                playCompletionSound();
            }
        }
    }

    private void renderCustomGui(ContainerChest container) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GlStateManager.pushMatrix();
        float scale = Config.feature.dungeons.dungeonsTerminalsScale;

        int guiWidth = (int) (COLUMNS * SLOT_SIZE * scale);
        int guiHeight = (int) (ROWS * SLOT_SIZE * scale);
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

        for (Map.Entry<Integer, SlotPosition> entry : slotPositions.entrySet()) {
            int id = entry.getKey();
            Gui.drawRect(
                    (id % COLUMNS) * SLOT_SIZE,
                    (id / COLUMNS) * SLOT_SIZE,
                    (id % COLUMNS) * SLOT_SIZE + SLOT_SIZE,
                    (id / COLUMNS) * SLOT_SIZE + SLOT_SIZE,
                    0x99FFFFFF
            );
        }

        targetSlots.forEach(slot -> {
            int x = (slot.slotNumber % COLUMNS) * SLOT_SIZE;
            int y = (slot.slotNumber / COLUMNS) * SLOT_SIZE;
            Gui.drawRect(x, y, x + SLOT_SIZE, y + SLOT_SIZE,
                    ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor).getRGB());
        });

        alternativeSlots.forEach(slot -> {
            int x = (slot.slotNumber % COLUMNS) * SLOT_SIZE;
            int y = (slot.slotNumber / COLUMNS) * SLOT_SIZE;
            Gui.drawRect(x, y, x + SLOT_SIZE, y + SLOT_SIZE,
                    ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor).getRGB());
        });

        GlStateManager.popMatrix();
    }

    private void renderVanillaOverlay(ContainerChest container) {
        for (Slot slot : container.inventorySlots) {
            if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;

            ItemStack item = slot.getStack();
            if (item == null) continue;

            if (Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane &&
                    item.getMetadata() == 0) {
                RenderUtils.drawOnSlot(
                        container.inventorySlots.size(),
                        slot.xDisplayPosition,
                        slot.yDisplayPosition,
                        0x99FFFFFF
                );
            }

            if (targetSlots.contains(slot)) {
                RenderUtils.drawOnSlot(
                        container.inventorySlots.size(),
                        slot.xDisplayPosition,
                        slot.yDisplayPosition,
                        ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor).getRGB()
                );
            } else if (alternativeSlots.contains(slot)) {
                RenderUtils.drawOnSlot(
                        container.inventorySlots.size(),
                        slot.xDisplayPosition,
                        slot.yDisplayPosition,
                        ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor).getRGB()
                );
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Config.feature.dungeons.dungeonsTerminalMazeSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        int button = Mouse.getEventButton();
        if (button != 0 || !Mouse.getEventButtonState()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChest)) return;

        GuiChest guiChest = (GuiChest) mc.currentScreen;
        ContainerChest container = (ContainerChest) guiChest.inventorySlots;
        String title = container.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.contains("Complete the maze!")) return;

        if (Config.feature.dungeons.dungeonsCustomGuiMaze) {
            handleCustomGuiClick(container, event);
        } else {
            handleVanillaGuiClick(container);
        }
    }

    private void handleCustomGuiClick(ContainerChest container, GuiScreenEvent.MouseInputEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        float scale = Config.feature.dungeons.dungeonsTerminalsScale;

        int mouseX = (Mouse.getEventX() * sr.getScaledWidth()) / mc.displayWidth;
        int mouseY = sr.getScaledHeight() - (Mouse.getEventY() * sr.getScaledHeight()) / mc.displayHeight - 1;

        int guiWidth = (int) (COLUMNS * SLOT_SIZE * scale);
        int guiHeight = (int) (ROWS * SLOT_SIZE * scale);
        int guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
        int guiTop = (sr.getScaledHeight() - guiHeight) / 2;

        if (mouseX >= guiLeft && mouseX < guiLeft + guiWidth &&
                mouseY >= guiTop && mouseY < guiTop + guiHeight) {
            int slotId = calculateClickedSlot(mouseX, mouseY, guiLeft, guiTop, scale);
            if (slotId >= 0 && slotId < container.inventorySlots.size()) {
                clickQueue.add(slotId);
                mc.playerController.windowClick(
                            container.windowId,
                            slotId,
                            2,
                            0,
                            mc.thePlayer
                    );
                playCompletionSound();
                updateSlots(container);
            }
            event.setCanceled(true);
        }
    }


    private void handleVanillaGuiClick(ContainerChest container) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiChest guiChest = (GuiChest) mc.currentScreen;
        Slot hoveredSlot = guiChest.getSlotUnderMouse();

        if (hoveredSlot != null) {
            clickQueue.add(hoveredSlot.slotNumber);
            mc.playerController.windowClick(
                        container.windowId,
                        hoveredSlot.slotNumber,
                        2,
                        0,
                        mc.thePlayer
            );
            playCompletionSound();
            updateSlots(container);
        }
    }


    private int calculateClickedSlot(int mouseX, int mouseY, int guiLeft, int guiTop, float scale) {
        float relX = (mouseX - guiLeft) / scale;
        float relY = (mouseY - guiTop) / scale;
        int slotCol = (int) (relX / SLOT_SIZE);
        int slotRow = (int) (relY / SLOT_SIZE);
        return slotRow * COLUMNS + slotCol;
    }

    private void playCompletionSound() {
        Minecraft mc = Minecraft.getMinecraft();
        SoundUtils.playSound(
                mc.thePlayer.getPosition(),
                "gui.button.press",
                1.0f,
                1.0f
        );
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
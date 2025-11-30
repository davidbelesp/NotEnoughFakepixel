package com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import com.nef.notenoughfakepixel.utils.ColorUtils;
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
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.*;

@RegisterEvents
public class CorrectPanesSolver {

    private static final int SLOT_SIZE = 16;
    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int INNER_COLUMNS = 7;
    private static final int INNER_ROWS = 4;

    private final List<Slot> lastCorrectSlots = new ArrayList<>();
    private final Map<Integer, SlotPosition> slotPositions = new HashMap<>();
    private final Set<Integer> clickedSlots = new HashSet<>();
    private long lastRecheckTime = 0;

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
        if (!Config.feature.dungeons.dungeonsTerminalCorrectPanesSolver) return;
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
        if (Config.feature.dungeons.dungeonsCustomGuiPanes && displayName.equals("Correct all the panes!")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Config.feature.dungeons.dungeonsTerminalCorrectPanesSolver) return;
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

        if (Config.feature.dungeons.dungeonsCustomGuiPanes) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRecheckTime >= 1000) {
                clickedSlots.clear();
                lastRecheckTime = currentTime;
            }

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
            GlStateManager.pushMatrix();
            float scale = Config.feature.dungeons.dungeonsTerminalsScale;
            int guiWidth = (int) (INNER_COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int) (INNER_ROWS * SLOT_SIZE * scale);
            int guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
            int guiTop = (sr.getScaledHeight() - guiHeight) / 2;

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
                if (!clickedSlots.contains(slot.slotNumber)) {
                    SlotPosition pos = slotPositions.get(containerChest.inventorySlots.indexOf(slot));
                    if (pos != null) {
                        drawRect(pos.x + 1, pos.y + 1,
                                pos.x + SLOT_SIZE - 1, pos.y + SLOT_SIZE - 1,
                                ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor).getRGB());
                    }
                }
            }
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Config.feature.dungeons.dungeonsPreventMissclicks) return;
        if (!Config.feature.dungeons.dungeonsTerminalCorrectPanesSolver) return;
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

        if (Config.feature.dungeons.dungeonsCustomGuiPanes) {
            ScaledResolution sr = new ScaledResolution(mc);
            float scale = Config.feature.dungeons.dungeonsTerminalsScale;

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

            boolean validClick = false;
            for (Slot slot : lastCorrectSlots) {
                SlotPosition pos = slotPositions.get(containerChest.inventorySlots.indexOf(slot));
                if (pos != null && relX >= pos.x && relX <= pos.x + SLOT_SIZE &&
                        relY >= pos.y && relY <= pos.y + SLOT_SIZE) {
                    mc.playerController.windowClick(
                            containerChest.windowId,
                            slot.slotNumber,
                            2,
                            0,
                            mc.thePlayer
                    );
                    playCompletionSound();
                    clickedSlots.add(slot.slotNumber);
                    validClick = true;
                    break;
                }
            }
            event.setCanceled(true);
        }
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

    private static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}
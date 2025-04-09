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
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import org.ginafro.notenoughfakepixel.utils.SoundUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClickInOrderSolver {

    // We remove the static 'round' variable and use the sum of processed rounds and queued clicks
    private final LinkedList<Integer> clickQueue = new LinkedList<>();
    private int processedRounds = 0;
    private static final int SLOT_SIZE = 16;
    private static final int REGION_COLS = 7;
    private static final int REGION_ROWS = 2;

    public ClickInOrderSolver() {
        // Start the queue processing task when the solver is instantiated
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::processQueue, 0, 0, TimeUnit.MILLISECONDS);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsTerminalClickInOrderSolver) return;
        if (!(event.gui instanceof GuiChest)) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        Container container = ((GuiChest) event.gui).inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (NotEnoughFakepixel.feature.dungeons.dungeonsCustomGuiClickIn && title.startsWith("Click in")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onOpen(GuiOpenEvent e) {
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsTerminalClickInOrderSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (e.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) e.gui;
            Container container = chest.inventorySlots;
            if (container instanceof ContainerChest) {
                String title = ((ContainerChest) container).getLowerChestInventory()
                        .getDisplayName().getUnformattedText();
                if (!title.startsWith("Click in")) return;
                processedRounds = 0;
                clickQueue.clear();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsTerminalClickInOrderSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!(event.gui instanceof GuiChest)) return;
        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory()
                .getDisplayName().getUnformattedText();
        if (!title.startsWith("Click in")) return;
        ContainerChest containerChest = (ContainerChest) container;

        // Compute effective round: processed rounds + queued clicks
        int effectiveRound = processedRounds + clickQueue.size();

        if (NotEnoughFakepixel.feature.dungeons.dungeonsCustomGuiClickIn) {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            float scale = NotEnoughFakepixel.feature.dungeons.dungeonsTerminalsScale;
            int guiWidth = (int) (REGION_COLS * SLOT_SIZE * scale);
            int guiHeight = (int) (REGION_ROWS * SLOT_SIZE * scale);
            int screenWidth = sr.getScaledWidth();
            int screenHeight = sr.getScaledHeight();
            int guiLeft = (screenWidth - guiWidth) / 2;
            int guiTop = (screenHeight - guiHeight) / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft, guiTop, 0);
            GlStateManager.scale(scale, scale, 1.0f);

            Gui.drawRect(-2, -12, (REGION_COLS * SLOT_SIZE) + 2, (REGION_ROWS * SLOT_SIZE) + 2, 0x80000000);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    "§8[§bNEF§8] §aClick in Order!",
                    0, -10, 0xFFFFFF);

            for (int row = 1; row <= REGION_ROWS; row++) {
                for (int col = 1; col <= REGION_COLS; col++) {
                    int slotIndex = row * 9 + col;
                    Slot slot = containerChest.getSlot(slotIndex);
                    if (slot == null || slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                    if (slot.getStack() == null || !(Block.getBlockFromItem(slot.getStack().getItem()) instanceof BlockStainedGlassPane))
                        continue;

                    int overlayColor = 0;
                    if (slot.getStack().stackSize == effectiveRound + 1) {
                        overlayColor = ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsCorrectColor).getRGB();
                    } else if (slot.getStack().stackSize == effectiveRound + 2) {
                        overlayColor = ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsAlternativeColor).getRGB();
                    } else if (slot.getStack().stackSize == effectiveRound + 3) {
                        // Get the base color as a Color object
                        Color baseColor = ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsAlternativeColor);
                        // Create a new Color with alpha 150
                        Color altColor = new Color(
                                baseColor.getRed(),
                                baseColor.getGreen(),
                                baseColor.getBlue(),
                                150
                        );
                        overlayColor = altColor.getRGB();
                    }

                    if (NotEnoughFakepixel.feature.dungeons.dungeonsTerminalHideIncorrect &&
                            slot.getStack().stackSize > effectiveRound + 2 &&
                            slot.getStack().getItemDamage() == 14) {
                        slot.getStack().getItem().setDamage(slot.getStack(), 15);
                        overlayColor = new Color(113, 113, 113).getRGB();
                    }

                    int x = (col - 1) * SLOT_SIZE;
                    int y = (row - 1) * SLOT_SIZE;
                    drawRect(x, y, x + SLOT_SIZE, y + SLOT_SIZE, overlayColor);

                    String stackSizeText = String.valueOf(slot.getStack().stackSize);
                    int textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(stackSizeText);
                    int textX = x + (SLOT_SIZE / 2) - (textWidth / 2);
                    int textY = y + (SLOT_SIZE / 2) - 4;
                    if (slot.getStack().stackSize > effectiveRound) {
                        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                                stackSizeText, textX, textY, 0xFFFFFF);
                    }
                }
            }
            GlStateManager.popMatrix();
        } else {
            for (int row = 1; row <= REGION_ROWS; row++) {
                for (int col = 1; col <= REGION_COLS; col++) {
                    int slotIndex = row * 9 + col;
                    Slot slot = containerChest.getSlot(slotIndex);
                    if (slot == null || slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                    if (slot.getStack() == null || !(Block.getBlockFromItem(slot.getStack().getItem()) instanceof BlockStainedGlassPane))
                        continue;

                    int overlayColor = 0;
                    if (slot.getStack().stackSize == effectiveRound + 1) {
                        overlayColor = ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsCorrectColor).getRGB();
                    } else if (slot.getStack().stackSize == effectiveRound + 2) {
                        overlayColor = ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsAlternativeColor).getRGB();
                    }

                    RenderUtils.drawOnSlot(container.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, overlayColor);
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsTerminalClickInOrderSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            Container container = chest.inventorySlots;
            if (container instanceof ContainerChest) {
                String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
                if (!title.startsWith("Click in")) return;
                ContainerChest containerChest = (ContainerChest) container;
                // Compute effective round
                int effectiveRound = processedRounds + clickQueue.size();

                for (int i = 1; i < 3; i++) {
                    for (int j = 1; j < 8; j++) {
                        Slot slot = containerChest.getSlot(i * 9 + j);
                        if (slot == null || slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                        if (slot.getStack() == null || !(Block.getBlockFromItem(slot.getStack().getItem()) instanceof BlockStainedGlassPane))
                            continue;

                        if (slot.getStack().stackSize == effectiveRound + 1) {
                            if (slot.getStack().getItemDamage() == 14 || slot.getStack().getItemDamage() == 15) {
                                slot.getStack().getItem().setDamage(slot.getStack(), 0);
                            }
                            RenderUtils.drawOnSlot(container.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsCorrectColor).getRGB());
                        } else if (slot.getStack().stackSize == effectiveRound + 2) {
                            if (slot.getStack().getItemDamage() == 14 || slot.getStack().getItemDamage() == 15) {
                                slot.getStack().getItem().setDamage(slot.getStack(), 0);
                            }
                            RenderUtils.drawOnSlot(container.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition,  ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsAlternativeColor).getRGB());
                        } else if (slot.getStack().stackSize == effectiveRound + 3) {
                            if (slot.getStack().getItemDamage() == 14 || slot.getStack().getItemDamage() == 15) {
                                slot.getStack().getItem().setDamage(slot.getStack(), 0);
                            }
                            Color base = ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsAlternativeColor);
                            RenderUtils.drawOnSlot(container.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, new Color(base.getRed(), base.getGreen(), base.getBlue(), 150).getRGB());
                        }

                        if (NotEnoughFakepixel.feature.dungeons.dungeonsTerminalHideIncorrect && slot.getStack().stackSize > effectiveRound + 1 && slot.getStack().getItemDamage() == 14) {
                            slot.getStack().getItem().setDamage(slot.getStack(), 15);
                            RenderUtils.drawOnSlot(container.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, new Color(113, 113, 113).getRGB());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsTerminalClickInOrderSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsTerminalHideIncorrect) return;
        if (!Mouse.getEventButtonState() || Mouse.getEventButton() != 0) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChest)) return;
        GuiChest guiChest = (GuiChest) mc.currentScreen;
        Container container = guiChest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory()
                .getDisplayName().getUnformattedText();
        if (!title.startsWith("Click in")) return;

        if (NotEnoughFakepixel.feature.dungeons.dungeonsCustomGuiClickIn) {
            ScaledResolution sr = new ScaledResolution(mc);
            float scale = NotEnoughFakepixel.feature.dungeons.dungeonsTerminalsScale;
            int guiWidth = (int) (REGION_COLS * SLOT_SIZE * scale);
            int guiHeight = (int) (REGION_ROWS * SLOT_SIZE * scale);
            int screenWidth = sr.getScaledWidth();
            int screenHeight = sr.getScaledHeight();
            int guiLeft = (screenWidth - guiWidth) / 2;
            int guiTop = (screenHeight - guiHeight) / 2;

            int mouseX = (Mouse.getEventX() * sr.getScaledWidth()) / mc.displayWidth;
            int mouseY = sr.getScaledHeight() - (Mouse.getEventY() * sr.getScaledHeight()) / mc.displayHeight - 1;

            if (mouseX < guiLeft || mouseX >= guiLeft + guiWidth ||
                    mouseY < guiTop || mouseY >= guiTop + guiHeight) {
                event.setCanceled(true);
                return;
            }

            float relX = (mouseX - guiLeft) / scale;
            float relY = (mouseY - guiTop) / scale;
            int col = (int) (relX / SLOT_SIZE);
            int row = (int) (relY / SLOT_SIZE);
            if (col < 0 || col >= REGION_COLS || row < 0 || row >= REGION_ROWS) {
                event.setCanceled(true);
                return;
            }
            int slotIndex = (row + 1) * 9 + (col + 1);
            Slot slot = ((ContainerChest) container).getSlot(slotIndex);
            if (slot == null || slot.getStack() == null) {
                event.setCanceled(true);
                return;
            }
            int expectedRound = processedRounds + clickQueue.size() + 1;
            if (slot.getStack().stackSize == expectedRound) {
                clickQueue.add(slot.slotNumber);
            } else {
                event.setCanceled(true);
            }
        } else {
            Slot hoveredSlot = guiChest.getSlotUnderMouse();
            if (hoveredSlot != null && hoveredSlot.getStack() != null) {
                int expectedRound = processedRounds + clickQueue.size() + 1;
                if (hoveredSlot.getStack().stackSize == expectedRound) {
                    clickQueue.add(hoveredSlot.slotNumber);
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }

    private void processQueue() {
        if (clickQueue.isEmpty()) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChest)) return;
        GuiChest guiChest = (GuiChest) mc.currentScreen;
        Container container = guiChest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory()
                .getDisplayName().getUnformattedText();
        ContainerChest cc = (ContainerChest) container;
        int slotNumber = clickQueue.getFirst();
        Slot slot = cc.getSlot(slotNumber);
        if (slot == null || slot.getStack() == null) return;
        if (!title.startsWith("Click in")) return;

        // Check if the pane is green (damage value 5 indicates success)
        if (slot.getStack().getItemDamage() == 5) {
            // Successfully clicked, remove from queue and increment processed rounds
            clickQueue.removeFirst();
            float pitch = 0.8f + (float) (Math.random() * 0.4); // Random pitch between 0.8 and 1.2
            SoundUtils.playSound(mc.thePlayer.getPosition(), "random.orb", 1.0f, pitch);
            processedRounds++;
        } else {
            // Pane is still red or not updated, send a click
            mc.playerController.windowClick(cc.windowId, slotNumber, 0, 0, mc.thePlayer);
        }
    }

    private static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}

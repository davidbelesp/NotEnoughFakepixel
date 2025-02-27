package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.terminals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockCarpet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
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
import org.ginafro.notenoughfakepixel.variables.F7ColorsDict;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class ClickOnColorsSolver {

    private static final int SLOT_SIZE = 16;
    private static final int COLUMNS = 9;
    private static final int INNER_COLUMNS = 7;
    private static final int INNER_ROWS = 4;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!(event.gui instanceof GuiChest)) return;
        //if (!DungeonManager.checkEssentialsF7()) return;
        Container container = ((GuiChest) event.gui).inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (Configuration.dungeonsTerminalCustomGui && title.startsWith("Select all the")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiChest)) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.startsWith("Select all the")) return;

        String targetColor = title.split("the ")[1].split(" items")[0].toLowerCase();

        if (Configuration.dungeonsTerminalCustomGui) {
            List<Slot> correctSlots = new ArrayList<>();
            for (Slot slot : ((ContainerChest) container).inventorySlots) {
                int slotId = ((ContainerChest) container).inventorySlots.indexOf(slot);
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory || slotId == 49) continue;

                int row = slotId / COLUMNS;
                int col = slotId % COLUMNS;

                if (row < 1 || row > 4 || col < 1 || col > 7) continue;

                ItemStack item = slot.getStack();
                if (item == null) continue;
                if (item.isItemEnchanted()) {
                    if (Configuration.dungeonsTerminalHideIncorrect) {
                        item.setItem(((ContainerChest) container).inventorySlots.get(0).getStack().getItem());
                        item.getItem().setDamage(item, 15);
                    }
                    continue;
                }
                boolean isCorrect = false;
                if (item.getItem() == Items.dye) {
                    isCorrect = targetColor.equals(F7ColorsDict.getColorFromDye(item.getMetadata()).toString());
                } else if (Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlass ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockColored ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockCarpet) {
                    isCorrect = targetColor.equals(F7ColorsDict.getColorFromMain(item.getMetadata()).toString());
                }
                if (isCorrect) {
                    correctSlots.add(slot);
                } else {
                    if (Configuration.dungeonsTerminalHideIncorrect) {
                        item.setItem(((ContainerChest) container).inventorySlots.get(0).getStack().getItem());
                        item.getItem().setDamage(item, 15);
                    }
                }
            }

            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            float scale = Configuration.dungeonsTerminalScale;
            int guiWidth = (int) (INNER_COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int) (INNER_ROWS * SLOT_SIZE * scale);
            int guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
            int guiTop = (sr.getScaledHeight() - guiHeight) / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft, guiTop, 0);
            GlStateManager.scale(scale, scale, 1.0f);

            Gui.drawRect(-2, -12,
                    (INNER_COLUMNS * SLOT_SIZE) + 2,
                    (INNER_ROWS * SLOT_SIZE) + 2,
                    0x80000000
            );

            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    "§8[§bNEF§8] §aColors", 0, -10, 0xFFFFFF);

            for (Slot slot : correctSlots) {
                int slotId = ((ContainerChest) container).inventorySlots.indexOf(slot);
                int row = slotId / COLUMNS;
                int col = slotId % COLUMNS;

                int innerX = (col - 1) * SLOT_SIZE;
                int innerY = (row - 1) * SLOT_SIZE;

                drawRect(innerX + 1, innerY + 1,
                        innerX + SLOT_SIZE - 1,
                        innerY + SLOT_SIZE - 1,
                        Configuration.dungeonsCorrectColor.getRGB());
            }
            GlStateManager.popMatrix();
        } else {
            for (Slot slot : ((ContainerChest) container).inventorySlots) {
                if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                int slotId = ((ContainerChest) container).inventorySlots.indexOf(slot);
                if (slotId == 49) {
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    ItemStack item = slot.getStack();
                    if (item != null) {
                        item.setItem(((ContainerChest) container).inventorySlots.get(0).getStack().getItem());
                        item.getItem().setDamage(item, 15);
                    }
                    continue;
                }
                ItemStack item = slot.getStack();
                if (item == null) continue;
                if (item.isItemEnchanted()) {
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    item.setItem(((ContainerChest) container).inventorySlots.get(0).getStack().getItem());
                    item.getItem().setDamage(item, 15);
                    continue;
                }
                boolean isCorrect = false;
                if (item.getItem() == Items.dye) {
                    isCorrect = targetColor.equals(F7ColorsDict.getColorFromDye(item.getMetadata()).toString());
                } else if (Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlass ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockColored ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockCarpet) {
                    isCorrect = targetColor.equals(F7ColorsDict.getColorFromMain(item.getMetadata()).toString());
                }
                if (isCorrect) {
                    RenderUtils.drawOnSlot(((ContainerChest) container).inventorySlots.size(),
                            slot.xDisplayPosition, slot.yDisplayPosition, Configuration.dungeonsCorrectColor.getRGB());
                } else {
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    item.setItem(((ContainerChest) container).inventorySlots.get(0).getStack().getItem());
                    item.getItem().setDamage(item, 15);
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.BackgroundDrawnEvent e){
        if(!Configuration.dungeonsTerminalSelectColorsSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if(!(e.gui instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) e.gui;
        Container container = chest.inventorySlots;

        if(!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.startsWith("Select all the")) return;
        String color = title.split("the ")[1].split(" items")[0].toLowerCase();

        ContainerChest containerChest = (ContainerChest) container;
        for(Slot slot : containerChest.inventorySlots) {
            if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;

            ItemStack itemStack = slot.getStack();
            if (itemStack == null) continue;

            if (containerChest.inventorySlots.indexOf(slot) == 49) {
                if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                itemStack.setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                itemStack.getItem().setDamage(itemStack, 15);
                continue;
            }

            // Hide already clicked
            if (itemStack.isItemEnchanted()) {
                if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                itemStack.setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                itemStack.getItem().setDamage(itemStack, 15);
                continue;
            }

            // If its tint
            if (itemStack.getItem() == Items.dye) {
                //System.out.println(F7ColorsDict.getColorFromDye(itemStack.getMetadata()).toString() + " " + color);
                if (color.equals(F7ColorsDict.getColorFromDye(itemStack.getMetadata()).toString())) {
                    RenderUtils.drawOnSlot(chest.inventorySlots.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, Configuration.dungeonsCorrectColor.getRGB());
                } else {
                    // HIDE OTHER SLOTS
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    // Hide unwanted slots
                    itemStack.setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                    itemStack.getItem().setDamage(itemStack, 15);
                }
                // If its glass type
            } else if (Block.getBlockFromItem(itemStack.getItem()) instanceof BlockStainedGlassPane ||
                    Block.getBlockFromItem(itemStack.getItem()) instanceof BlockStainedGlass ||
                    Block.getBlockFromItem(itemStack.getItem()) instanceof BlockColored ||
                    Block.getBlockFromItem(itemStack.getItem()) instanceof BlockCarpet) {
                if (color.equals(F7ColorsDict.getColorFromMain(itemStack.getMetadata()).toString())) {
                    //itemStack.getItem().setDamage(itemStack, 0);
                    RenderUtils.drawOnSlot(chest.inventorySlots.inventorySlots.size(), slot.xDisplayPosition, slot.yDisplayPosition, Configuration.dungeonsCorrectColor.getRGB());
                } else {
                    if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                    if (itemStack.getMetadata() != 15) {
                        itemStack.setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                        itemStack.getItem().setDamage(itemStack, 15);
                    }
                }
            } else {
                if (!Configuration.dungeonsTerminalHideIncorrect) continue;
                itemStack.setItem(containerChest.inventorySlots.get(0).getStack().getItem());
                itemStack.getItem().setDamage(itemStack, 15);
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Configuration.dungeonsPreventMissclicks) return;
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!Mouse.getEventButtonState() || Mouse.getEventButton() != 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChest)) return;
        GuiChest guiChest = (GuiChest) mc.currentScreen;
        Container container = guiChest.inventorySlots;
        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.startsWith("Select all the")) return;

        if (Configuration.dungeonsTerminalCustomGui) {
            final int SLOT_SIZE = 18;
            final int COLUMNS = 9;
            final int ROWS = 6;

            ScaledResolution sr = new ScaledResolution(mc);
            float scale = Configuration.dungeonsTerminalScale;
            int guiWidth = (int) (INNER_COLUMNS * SLOT_SIZE * scale);
            int guiHeight = (int) (INNER_ROWS * SLOT_SIZE * scale);
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

            List<Slot> correctSlots = new ArrayList<>();
            String targetColor = title.split("the ")[1].split(" items")[0].toLowerCase();
            for (Slot slot : ((ContainerChest) container).inventorySlots) {
                int slotId = ((ContainerChest) container).inventorySlots.indexOf(slot);
                if (slot.inventory == mc.thePlayer.inventory || slotId == 49) continue;
                int row = slotId / COLUMNS;
                int col = slotId % COLUMNS;
                if (row == 0 || row == (ROWS - 1) || col == 0 || col == (COLUMNS - 1)) continue;

                ItemStack item = slot.getStack();
                if (item == null) continue;
                if (item.isItemEnchanted()) continue;
                boolean isCorrect = false;
                if (item.getItem() == Items.dye) {
                    isCorrect = targetColor.equals(F7ColorsDict.getColorFromDye(item.getMetadata()).toString());
                } else if (Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlass ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockColored ||
                        Block.getBlockFromItem(item.getItem()) instanceof BlockCarpet) {
                    isCorrect = targetColor.equals(F7ColorsDict.getColorFromMain(item.getMetadata()).toString());
                }
                if (isCorrect) {
                    correctSlots.add(slot);
                }
            }

            boolean validClick = false;
            for (Slot slot : correctSlots) {
                int slotId = ((ContainerChest) container).inventorySlots.indexOf(slot);
                int row = slotId / COLUMNS;
                int col = slotId % COLUMNS;

                int innerX = (col - 1) * SLOT_SIZE;
                int innerY = (row - 1) * SLOT_SIZE;

                if (relX >= innerX && relX < innerX + SLOT_SIZE &&
                        relY >= innerY && relY < innerY + SLOT_SIZE) {
                    mc.playerController.windowClick(
                            ((ContainerChest) container).windowId,
                            slot.slotNumber,
                            2, // Left click
                            0,
                            mc.thePlayer
                    );
                    mc.playerController.windowClick(
                            ((ContainerChest) container).windowId,
                            slot.slotNumber,
                            2, // Left click again
                            0,
                            mc.thePlayer
                    );
                    validClick = true;
                    break;
                }
            }
            if (!validClick) {
                event.setCanceled(true);
            }
        } else {
            GuiChest gui = (GuiChest) mc.currentScreen;
            Slot hoveredSlot = gui.getSlotUnderMouse();
            if (hoveredSlot != null && hoveredSlot.getStack() != null) {
                ItemStack item = hoveredSlot.getStack();
                if (Block.getBlockFromItem(item.getItem()) instanceof BlockStainedGlassPane && item.getMetadata() == 15) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}

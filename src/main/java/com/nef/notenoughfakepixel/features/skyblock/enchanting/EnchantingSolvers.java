package com.nef.notenoughfakepixel.features.skyblock.enchanting;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.SlotClickEvent;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.Logger;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

@RegisterEvents
public class EnchantingSolvers {

    public static SolverTypes currentSolverType = SolverTypes.NONE;
    static List<UltrasequencerSlot> ultrasequencerSlots = new ArrayList<>();
    static List<Integer> chronomatronOrder = new ArrayList<Integer>();
    private int previousIndex = 0;
    private boolean noteFinished = true;
    private boolean resolved = false;
    static boolean resolving = false;
    private final Color green = new Color(0, 255, 0);
    static int slotToClickUltrasequencer = 1;
    private boolean clicked = false;
    static int roundUltraSequencerSolver = 1;

    static class UltrasequencerSlot {
        public Slot slot;
        public int quantity;

        public UltrasequencerSlot(Slot slot, int quantity) {
            this.slot = slot;
            this.quantity = quantity;
        }
    }

    public enum SolverTypes {
        NONE,
        CHRONOMATRON,
        ULTRASEQUENCER,
        SUPERPAIRS
    }

    @SubscribeEvent()
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) return;
        if (!(event.gui instanceof GuiChest)) return;

        String chestName = TablistParser.currentOpenChestName;
        if (chestName == null || chestName.isEmpty()) return;

        if (chestName.startsWith("Chronomatron")) {
            currentSolverType = SolverTypes.CHRONOMATRON;
        } else if (chestName.startsWith("Ultrasequencer")) {
            currentSolverType = SolverTypes.ULTRASEQUENCER;
        } else if (chestName.startsWith("Superpairs")) {
            if (!chestName.contains("Stakes")) currentSolverType = SolverTypes.SUPERPAIRS;
        } else if (chestName.startsWith("Experiment Over")) {
            currentSolverType = SolverTypes.NONE;
        } else {
            currentSolverType = SolverTypes.NONE;
        }
        resolving = false;
        clicked = false;
        slotToClickUltrasequencer = 1;
        roundUltraSequencerSolver = 1;
    }

    @SubscribeEvent
    public void onGuiDrawn(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (Config.feature.experimentation.experimentationUltraSequencerSolver && currentSolverType == SolverTypes.ULTRASEQUENCER) {

            if (!(event.gui instanceof GuiChest)) return;
            GuiChest chest = (GuiChest) event.gui;
            Container container = chest.inventorySlots;

            if (!(container instanceof ContainerChest)) return;
            String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
            if (!title.startsWith("Ultrasequencer (")) return;
            ContainerChest containerChest = (ContainerChest) container;
            // Check if its in remember state
            IInventory lower = ((ContainerChest) container).getLowerChestInventory();
            ItemStack timerStack = lower.getStackInSlot(lower.getSizeInventory() - 5);
            if (timerStack == null) return;
            boolean isClock = timerStack.getItem() == Items.clock;

            // if not clock, then remember the items
            if (!isClock) {
                if (roundUltraSequencerSolver == ultrasequencerSlots.size()) return;
                if (resolving) ultrasequencerSlots.clear();
                resolving = false;
                for (Slot slot : containerChest.inventorySlots) {
                    // select only the items in the chest
                    if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
                    ItemStack item = slot.getStack();
                    if (item == null) continue;

                    if (item.getItem() == Items.dye) {

                        int stackSize = item.stackSize;
                        ultrasequencerSlots.add(new UltrasequencerSlot(slot, stackSize));
                    }
                }
                slotToClickUltrasequencer = 1;
            } else {
                resolving = true;
                // if its clock, draw the items in the list
                for (UltrasequencerSlot slot : ultrasequencerSlots) {
                    ItemStack itemInSlot = containerChest.inventorySlots.get(slot.slot.slotNumber).getStack();
                    if (itemInSlot == null) continue;
                    if (itemInSlot.getItem() == Items.dye) continue;
                    Color color = new Color(255, 0, 0);
                    if (slot.quantity == slotToClickUltrasequencer) color = new Color(0, 255, 0);
                    RenderUtils.drawOnSlot(containerChest.inventorySlots.size(), slot.slot.xDisplayPosition, slot.slot.yDisplayPosition, color.getRGB(), slot.quantity);
                }
            }
        } else if (Config.feature.experimentation.experimentationChronomatronSolver && currentSolverType == SolverTypes.CHRONOMATRON) {
            if (!(event.gui instanceof GuiChest)) return;
            GuiChest chest = (GuiChest) event.gui;
            Container container = chest.inventorySlots;

            if (!(container instanceof ContainerChest)) return;
            String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
            if (!title.startsWith("Chronomatron (")) return;
            ContainerChest containerChest = (ContainerChest) container;
            // Check if its in remember state
            IInventory lower = ((ContainerChest) container).getLowerChestInventory();
            ItemStack timerStack = lower.getStackInSlot(lower.getSizeInventory() - 5);
            if (timerStack == null) return;
            boolean isClock = timerStack.getItem() == Items.clock;
            // if is not clock, then remember the items
            if (!isClock) {
                if (resolving) chronomatronOrder.clear();
                resolving = false;
                // getting item in slot 4
                ItemStack itemInSlot = containerChest.inventorySlots.get(4).getStack();
                // checking its stack quantity
                if (itemInSlot == null) return;
                int round = itemInSlot.stackSize;
                if (chronomatronOrder.size() >= round) return;

                List<Slot> slots = containerChest.inventorySlots;
                if (slots == null) return;
                Slot tempSlot = slots.get(previousIndex);
                if (tempSlot == null) return;
                ItemStack slotStack = tempSlot.getStack();
                if (slotStack == null) return;
                Item lastItem = slotStack.getItem();
                if (Block.getBlockFromItem(lastItem) == Blocks.stained_glass) {
                    noteFinished = true;
                }
                if (!noteFinished) return;

                List<Integer> slotRanges = new ArrayList<>();

                for (int i = 9; i <= 18; i++) {
                    slotRanges.add(i);
                }

                if (TablistParser.currentOpenChestName.contains("Transcendent") ||
                        TablistParser.currentOpenChestName.contains("Metaphysical")) {
                    for (int i = 28; i <= 37; i++) {
                        slotRanges.add(i);
                    }
                }

                for (int index : slotRanges) {
                    Slot slot = containerChest.inventorySlots.get(index);
                    ItemStack item = slot.getStack();

                    if (item == null) continue;

                    if (Block.getBlockFromItem(item.getItem()) != Blocks.stained_hardened_clay) {
                        continue;
                    }

                    previousIndex = slot.getSlotIndex();
                    chronomatronOrder.add(previousIndex);
                    noteFinished = false;
                    break;
                }

            } else {
                resolving = true;
                if (!chronomatronOrder.isEmpty()) {
                    int resultIndex = chronomatronOrder.get(0);
                    if (containerChest.inventorySlots.get(resultIndex).getStack() == null) return;
                    Item resultItem = containerChest.inventorySlots.get(resultIndex).getStack().getItem();
                    if (Block.getBlockFromItem(resultItem) == Blocks.stained_glass) {
                        if (resolved) {
                            chronomatronOrder.remove(0);
                            resolved = false;
                            clicked = false;
                            return;
                        }
                        Slot slot1 = containerChest.inventorySlots.get(resultIndex);
                        Slot slot2 = containerChest.inventorySlots.get(resultIndex + 9);
                        RenderUtils.drawOnSlot(containerChest.inventorySlots.size(), slot1.xDisplayPosition, slot1.yDisplayPosition, green.getRGB());
                        RenderUtils.drawOnSlot(containerChest.inventorySlots.size(), slot2.xDisplayPosition, slot2.yDisplayPosition, green.getRGB());
                        if (!TablistParser.currentOpenChestName.contains("Transcendent") && !TablistParser.currentOpenChestName.contains("Metaphysical")) {
                            Slot slot3 = containerChest.inventorySlots.get(resultIndex + 18);
                            RenderUtils.drawOnSlot(containerChest.inventorySlots.size(), slot3.xDisplayPosition, slot3.yDisplayPosition, green.getRGB());
                        }
                    } else if (Block.getBlockFromItem(resultItem) == Blocks.stained_hardened_clay && !resolved) {
                        resolved = true;
                    }

                }
                previousIndex = 0;
                noteFinished = true;
            }
        } else if (currentSolverType == SolverTypes.NONE) {
            if (chronomatronOrder.isEmpty() && ultrasequencerSlots.isEmpty()) return;

            ultrasequencerSlots = new ArrayList<>();
            chronomatronOrder = new ArrayList<>();

            successfulMatches.clear();
            powerupMatches.clear();
            possibleMatches.clear();
            lastSlotClicked = -1;

            previousIndex = 0;
            noteFinished = true;
            resolved = false;
            resolving = false;
            clicked = false;
            slotToClickUltrasequencer = 1;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) {
            currentSolverType = SolverTypes.NONE;
        }

        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        processInventoryContents(true);
    }

    @SubscribeEvent
    public void onStackClick(SlotClickEvent event) {
        if (!Config.feature.experimentation.experimentationSolvers) return;

        if (currentSolverType == SolverTypes.SUPERPAIRS) {
            lastSlotClicked = event.slotId;
        }
    }

    public static void processInventoryContents(boolean fromTick) {
        if (!Config.feature.experimentation.experimentationSolvers) {
            return;
        }

        if (!SkyblockData.getCurrentGamemode().isSkyblock()) {
            return;
        }

        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
            ContainerChest container = (ContainerChest) chest.inventorySlots;
            IInventory lower = container.getLowerChestInventory();

            if (currentSolverType == SolverTypes.SUPERPAIRS) {
                successfulMatches.clear();
                possibleMatches.clear();
                powerupMatches.clear();
                out:
                for (int index = 0; index < lower.getSizeInventory(); index++) {
                    ItemStack stack = lower.getStackInSlot(index);
                    if (stack == null) continue;
                    if (stack.getItem() != Item.getItemFromBlock(Blocks.stained_glass) &&
                            stack.getItem() != Item.getItemFromBlock(Blocks.stained_glass_pane)) {
                        superpairStacks.put(index, stack);

                        NBTTagCompound tag = stack.getTagCompound();
                        if (tag != null) {
                            NBTTagCompound display = tag.getCompoundTag("display");
                            if (display.hasKey("Lore", 9)) {
                                NBTTagList list = display.getTagList("Lore", 8);
                                for (int i = 0; i < list.tagCount(); i++) {
                                    if (list.getStringTagAt(i).toLowerCase(Locale.ROOT).contains("powerup")) {
                                        powerupMatches.add(index);
                                        continue out;
                                    }
                                }
                            }
                        }

                        int numMatches = 0;
                        for (int index2 = 0; index2 < lower.getSizeInventory(); index2++) {
                            ItemStack stack2 = lower.getStackInSlot(index2);
                            if (stack2 != null && stack2.getDisplayName().equals(stack.getDisplayName()) &&
                                    stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage()) {
                                numMatches++;
                            }
                        }
                        boolean oddMatches = (numMatches % 2) == 1;

                        if ((!oddMatches || index != lastSlotClicked) && !successfulMatches.contains(index)) {
                            for (int index2 = 0; index2 < lower.getSizeInventory(); index2++) {
                                if (index == index2) continue;
                                if (oddMatches && index2 == lastSlotClicked) continue;

                                ItemStack stack2 = lower.getStackInSlot(index2);
                                if (stack2 != null && stack2.getDisplayName().equals(stack.getDisplayName()) &&
                                        stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage()) {
                                    successfulMatches.add(index);
                                    successfulMatches.add(index2);
                                }
                            }
                        }
                    } else {
                        if (superpairStacks.containsKey(index) && superpairStacks.get(index) != null &&
                                !possibleMatches.contains(index)) {
                            ItemStack stack1 = superpairStacks.get(index);
                            for (int index2 = 0; index2 < lower.getSizeInventory(); index2++) {
                                if (index == index2) continue;

                                if (superpairStacks.containsKey(index2) && superpairStacks.get(index2) != null) {
                                    ItemStack stack2 = superpairStacks.get(index2);
                                    if (stack1.getDisplayName().equals(stack2.getDisplayName()) &&
                                            stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage()) {
                                        possibleMatches.add(index);
                                        possibleMatches.add(index2);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                superpairStacks.clear();
                successfulMatches.clear();
                powerupMatches.clear();
                lastSlotClicked = -1;
            }
        }
    }

    private static final Map<Integer, ItemStack> superpairStacks = new HashMap<>();
    private static final HashSet<Integer> successfulMatches = new HashSet<>();
    private static final HashSet<Integer> possibleMatches = new HashSet<>();
    private static final HashSet<Integer> powerupMatches = new HashSet<>();
    private static int lastSlotClicked = -1;

    public static ItemStack overrideStack(IInventory inventory, int slotIndex, ItemStack stack) {
        if (!Config.feature.experimentation.experimentationSuperpairsSolver) {
            return null;
        }

        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return null;

        if (stack != null && stack.getDisplayName() != null) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
                GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
                ContainerChest container = (ContainerChest) chest.inventorySlots;
                IInventory lower = container.getLowerChestInventory();

                if (lower != inventory) {
                    return null;
                }

                String displayName = stack.getDisplayName();
                if (currentSolverType == SolverTypes.SUPERPAIRS) {
                    if (stack.getItem() == Item.getItemFromBlock(Blocks.stained_glass) && superpairStacks.containsKey(slotIndex)) {
                        ItemStack stack2 = superpairStacks.get(slotIndex);
                        stack2.setStackDisplayName(displayName);
                        return stack2;
                    }
                }
            }
        }

        return null;
    }

    public static boolean onStackRender(ItemStack stack, IInventory inventory, int slotIndex, int x, int y) {
        if (!Config.feature.experimentation.experimentationSolvers) return false;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return false;

        if (stack != null && stack.getDisplayName() != null) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
                GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
                ContainerChest container = (ContainerChest) chest.inventorySlots;
                IInventory lower = container.getLowerChestInventory();

                if (lower != inventory) {
                    return false;
                }

                if (currentSolverType == SolverTypes.SUPERPAIRS) {
                    int meta = 0;
                    if (stack.getItem() == Item.getItemFromBlock(Blocks.stained_glass) &&
                            superpairStacks.containsKey(slotIndex)) {
                        if (possibleMatches.contains(slotIndex)) {
                            meta = Config.feature.experimentation.supPossible;
                        } else {
                            meta = Config.feature.experimentation.supUnmatched;
                        }
                    } else {
                        if (powerupMatches.contains(slotIndex)) {
                            meta = Config.feature.experimentation.supPower;
                        } else if (successfulMatches.contains(slotIndex)) {
                            meta = Config.feature.experimentation.supMatched;
                        }
                    }
                    if (meta > 0) {
                        RenderUtils.drawItemStack(new ItemStack(Item.getItemFromBlock(Blocks.stained_glass_pane), 1, meta - 1), x, y);
                    }
                }
            }
        }
        return false;
    }


}
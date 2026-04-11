package com.nef.notenoughfakepixel.features;

import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.PetData;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Rarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RegisterEvents
public class PetTracker {
    private final Map<String, Integer> ownedPets = new HashMap<>();
    private final List<String> missingPets = new ArrayList<>();
    private final List<String> upgradeablePets = new ArrayList<>();

    private int totalPetScore = 0;
    private int scrollIndexRight = 0;
    private int scrollIndexLeft = 0;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) {
            ownedPets.clear();
            missingPets.clear();
            upgradeablePets.clear();
            totalPetScore = 0;
            scrollIndexRight = 0;
            scrollIndexLeft = 0;
        }
    }

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            ContainerChest container = (ContainerChest) chest.inventorySlots;
            IInventory lowerChestInventory = container.getLowerChestInventory();

            if (lowerChestInventory.getDisplayName().getUnformattedText().contains("Pets")) {
                scanPets(lowerChestInventory);
                RenderUtils.drawPetSidebar(chest, totalPetScore, missingPets, upgradeablePets, scrollIndexRight, scrollIndexLeft);
                renderHoveredTooltip(chest, event.mouseX, event.mouseY);
            }
        }
    }

    private void renderHoveredTooltip(GuiChest chest, int mouseX, int mouseY) {
        try {
            Field theSlotField;
            try {
                theSlotField = GuiContainer.class.getDeclaredField("theSlot");
            } catch (NoSuchFieldException e) {
                theSlotField = GuiContainer.class.getDeclaredField("field_147006_u");
            }
            theSlotField.setAccessible(true);
            Slot slot = (Slot) theSlotField.get(chest);
            if (slot == null || !slot.getHasStack()) return;

            ItemStack stack = slot.getStack();
            Method renderToolTip;
            try {
                renderToolTip = GuiScreen.class.getDeclaredMethod("renderToolTip", ItemStack.class, int.class, int.class);
            } catch (NoSuchMethodException e) {
                renderToolTip = GuiScreen.class.getDeclaredMethod("func_146284_a", ItemStack.class, int.class, int.class);
            }
            renderToolTip.setAccessible(true);
            renderToolTip.invoke(chest, stack, mouseX, mouseY);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            IInventory inv = ((ContainerChest) chest.inventorySlots).getLowerChestInventory();

            if (inv.getDisplayName().getUnformattedText().contains("Pets")) {
                int wheel = Mouse.getEventDWheel();
                if (wheel != 0) {
                    Minecraft mc = Minecraft.getMinecraft();
                    int mouseX = Mouse.getEventX() * chest.width / mc.displayWidth;
                    int guiLeft = (chest.width - 176) / 2;

                    if (mouseX < guiLeft) {
                        scrollIndexLeft += (wheel > 0 ? -1 : 1);
                        if (scrollIndexLeft < 0) scrollIndexLeft = 0;
                        int maxScroll = Math.max(0, upgradeablePets.size() - 15);
                        if (scrollIndexLeft > maxScroll) scrollIndexLeft = maxScroll;
                    } else {
                        scrollIndexRight += (wheel > 0 ? -1 : 1);
                        if (scrollIndexRight < 0) scrollIndexRight = 0;
                        int maxScroll = Math.max(0, missingPets.size() - 15);
                        if (scrollIndexRight > maxScroll) scrollIndexRight = maxScroll;
                    }
                }
            }
        }
    }

    private void scanPets(IInventory inventory) {
        Map<String, Rarity> allPets = PetData.getAllPets();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (item == null || !item.hasDisplayName()) continue;

            String rawDisplayName = item.getDisplayName();
            String cleanName = StringUtils.stripControlCodes(rawDisplayName).trim();

            if (cleanName.isEmpty() || cleanName.equals("Pets") || cleanName.contains("Page") || cleanName.equals("Go Back") || cleanName.equals("Close"))
                continue;

            cleanName = cleanName.replaceAll("\\[Lvl \\d+\\] ", "").trim();
            int rarityScore = getRarityScoreFromColor(rawDisplayName);

            for (String petName : allPets.keySet()) {
                if (cleanName.contains(petName)) {
                    int currentHighest = ownedPets.getOrDefault(petName, 0);
                    ownedPets.put(petName, Math.max(currentHighest, rarityScore));
                    break;
                }
            }
        }

        totalPetScore = 0;
        for (int score : ownedPets.values()) {
            totalPetScore += score;
        }

        missingPets.clear();
        upgradeablePets.clear();

        for (Entry<String, Rarity> entry : allPets.entrySet()) {
            String pet = entry.getKey();
            int maxScore = rarityToScore(entry.getValue());

            if (!ownedPets.containsKey(pet)) {
                missingPets.add("\u00A7c\u2718 " + pet);
            } else {
                int score = ownedPets.get(pet);
                if (score < maxScore) {
                    int potentialGain = maxScore - score;
                    String color = getColorPrefix(score);
                    upgradeablePets.add(color + pet + " \u00A78\u279C \u00A7e+" + potentialGain + " pts");
                }
            }
        }
    }

    private int getRarityScoreFromColor(String rawDisplayName) {
        int bracketIndex = rawDisplayName.indexOf(']');
        String relevantPart = rawDisplayName;
        if (bracketIndex != -1 && bracketIndex < rawDisplayName.length() - 1) {
            relevantPart = rawDisplayName.substring(bracketIndex + 1);
        }

        for (int i = 0; i < relevantPart.length() - 1; i++) {
            if (relevantPart.charAt(i) == '\u00A7') {
                char nextChar = Character.toLowerCase(relevantPart.charAt(i + 1));
                if ("0123456789abcdef".indexOf(nextChar) != -1) {
                    return colorCodeToScore(nextChar);
                }
            }
        }
        return 1;
    }

    private int colorCodeToScore(char code) {
        switch (code) {
            case 'd':
                return 6; // Mythic
            case '6':
                return 5; // Legendary
            case '5':
                return 4; // Epic
            case '9':
                return 3; // Rare
            case 'a':
                return 2; // Uncommon
            default:
                return 1; // Common
        }
    }

    private int rarityToScore(Rarity rarity) {
        switch (rarity) {
            case MYTHIC:
            case DIVINE:
                return 6;
            case LEGENDARY:
                return 5;
            case EPIC:
                return 4;
            case RARE:
                return 3;
            case UNCOMMON:
                return 2;
            default:
                return 1;
        }
    }

    private String getColorPrefix(int score) {
        switch (score) {
            case 6:
                return "\u00A7d"; // Mythic (Pink)
            case 5:
                return "\u00A76"; // Legendary (Gold)
            case 4:
                return "\u00A75"; // Epic (Purple)
            case 3:
                return "\u00A79"; // Rare (Blue)
            case 2:
                return "\u00A7a"; // Uncommon (Green)
            default:
                return "\u00A7f"; // Common (White)
        }
    }
}

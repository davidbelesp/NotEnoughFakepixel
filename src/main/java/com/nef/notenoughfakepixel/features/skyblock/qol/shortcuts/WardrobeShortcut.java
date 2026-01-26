package com.nef.notenoughfakepixel.features.skyblock.qol.shortcuts;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.config.KeybindHelper;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashSet;
import java.util.Set;

@RegisterEvents
public class WardrobeShortcut {

    private final Set<Integer> activeKeySet = new HashSet<>();

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (!Config.feature.qol.qolShortcutWardrobe) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        boolean keyPressed = KeybindHelper.isKeyDown(Config.feature.qol.qolWardrobeKey);

        if (keyPressed && !activeKeySet.contains(Config.feature.qol.qolWardrobeKey)) {
            player.sendChatMessage("/wardrobe");

            activeKeySet.add(Config.feature.qol.qolWardrobeKey);
        }

        if (!KeybindHelper.isKeyDown(Config.feature.qol.qolWardrobeKey)) {
            activeKeySet.remove(Config.feature.qol.qolWardrobeKey);
        }
    }

    @SubscribeEvent
    public void onKeyPressOnGui(GuiScreenEvent.KeyboardInputEvent event) {
        if (!Config.feature.qol.qolShortcutWardrobe) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (!(event.gui instanceof GuiChest)) return;
        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;

        if (!(container instanceof ContainerChest)) return;
        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.startsWith("Wardrobe")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        boolean keyPressed = KeybindHelper.isKeyDown(Config.feature.qol.qolWardrobeKey);

        if (keyPressed && !activeKeySet.contains(Config.feature.qol.qolWardrobeKey)) {
            Minecraft.getMinecraft().thePlayer.closeScreen();

            activeKeySet.add(Config.feature.qol.qolWardrobeKey);
        }

        if (!KeybindHelper.isKeyDown(Config.feature.qol.qolWardrobeKey)) {
            activeKeySet.remove(Config.feature.qol.qolWardrobeKey);
        }
    }

    private static final int[] WARDROBE_SLOTS = {36, 37, 38, 39, 40, 41, 42, 43, 44};
    private int currentWindowId = -1;
    private final boolean[] keyStates = new boolean[9];
    private long lastClickTime = 0;

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!(event.gui instanceof GuiChest)) {
            resetState();
            return;
        }

        GuiChest chestGui = (GuiChest) event.gui;
        ContainerChest container = (ContainerChest) chestGui.inventorySlots;
        String chestName = container.getLowerChestInventory().getDisplayName().getUnformattedText();

        if (chestName.startsWith("Wardrobe")) {
            currentWindowId = container.windowId;
            handleWardrobeInput(chestGui);
        } else {
            resetState();
        }
    }

    private void handleWardrobeInput(GuiChest chestGui) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock() ||
                !Config.feature.qol.qolShortcutSlotsWardrobe) {
            return;
        }

        long now = System.currentTimeMillis();

        for (int slot = 0; slot < 9; slot++) {
            int keyCode = getKeyCode(slot + 1);
            boolean keyDown = false;
            try {
                keyDown = KeybindHelper.isKeyDown(keyCode);
            } catch (Exception e) {
                Logger.logErrorPlayers("Error checking key state for wardrobe slot " + (slot + 1) + ". Resetting Wardrobe keys to default.");
                Config.feature.qol.resetWardrobeSlotKeybinds();
                continue;
            }


            if (keyDown && !keyStates[slot] && (now - lastClickTime) > 100) {
                clickSlot(chestGui, slot);
                lastClickTime = now;
                keyStates[slot] = true;
            } else if (!keyDown) {
                keyStates[slot] = false;
            }
        }
    }

    private void clickSlot(GuiChest chestGui, int slotIndex) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.playerController == null) return;

        mc.playerController.windowClick(
                currentWindowId,
                WARDROBE_SLOTS[slotIndex],
                0,
                0,
                mc.thePlayer
        );
    }

    private int getKeyCode(int slot) {
        switch (slot) {
            case 1: return Config.feature.qol.qolWardrobeKey1;
            case 2: return Config.feature.qol.qolWardrobeKey2;
            case 3: return Config.feature.qol.qolWardrobeKey3;
            case 4: return Config.feature.qol.qolWardrobeKey4;
            case 5: return Config.feature.qol.qolWardrobeKey5;
            case 6: return Config.feature.qol.qolWardrobeKey6;
            case 7: return Config.feature.qol.qolWardrobeKey7;
            case 8: return Config.feature.qol.qolWardrobeKey8;
            case 9: return Config.feature.qol.qolWardrobeKey9;
            default: return Keyboard.KEY_NONE;
        }
    }

    private void resetState() {
        currentWindowId = -1;
        for (int i = 0; i < keyStates.length; i++) {
            keyStates[i] = false;
        }
    }
}
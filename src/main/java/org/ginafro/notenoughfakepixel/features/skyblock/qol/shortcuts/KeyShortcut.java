package org.ginafro.notenoughfakepixel.features.skyblock.qol.shortcuts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.ginafro.notenoughfakepixel.config.gui.core.config.KeybindHelper;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class KeyShortcut {

    private final Set<Integer> activeKeySet = new HashSet<>();

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (!getConfigOption() || !ScoreboardUtils.currentGamemode.isSkyblock()) return;
        int keyBind = getKeyBind();
        if (KeybindHelper.isKeyDown(keyBind)) {
            if (activeKeySet.add(keyBind)) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage(getCommand());
            }
        } else activeKeySet.remove(keyBind);
    }

    @SubscribeEvent
    public void onKeyPressOnGui(GuiScreenEvent event) {
        if (!getConfigOption() || !ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!(event.gui instanceof GuiChest)) return;

        Container container = ((GuiChest) event.gui).inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        String title = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!title.startsWith(getMenuTitle())) return;

        int keyBind = getKeyBind();

        if (KeybindHelper.isKeyDown(keyBind) && activeKeySet.add(keyBind)) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        } else if (!KeybindHelper.isKeyDown(keyBind)) {
            activeKeySet.remove(keyBind);
        }
    }

    public abstract boolean getConfigOption();

    public abstract int getKeyBind();

    public abstract String getCommand();

    public abstract String getMenuTitle();

}

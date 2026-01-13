package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@RegisterEvents
public class ScrollableTooltips {

    public static int scrollOffset = 0;
    private static ItemStack lastStack = null;

    @SubscribeEvent
    public void onMouse(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Config.feature.misc.qolScrollableTooltips) return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) return;

        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            int scrollSpeed = 10;
            if (dWheel > 0) {
                scrollOffset -= scrollSpeed;
            } else {
                scrollOffset += scrollSpeed;
            }
        }
    }

    @SubscribeEvent
    public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!Config.feature.misc.qolScrollableTooltips) return;

        int key = Keyboard.getEventKey();

        if (Keyboard.getEventKeyState()) {
            int scrollSpeed = 5;

            if (key == Keyboard.KEY_UP) {
                scrollOffset += scrollSpeed;
            }
            else if (key == Keyboard.KEY_DOWN) {
                scrollOffset -= scrollSpeed;
            }
        }
    }

    public static void resetScroll() {
        scrollOffset = 0;
    }

    @SubscribeEvent
    public void onTooltipRender(ItemTooltipEvent event) {
        if (event.itemStack != lastStack) {
            scrollOffset = 0;
            lastStack = event.itemStack;
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        ScrollableTooltips.resetScroll();
    }


}

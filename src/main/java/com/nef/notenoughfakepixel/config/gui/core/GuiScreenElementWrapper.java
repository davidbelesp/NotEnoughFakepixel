package com.nef.notenoughfakepixel.config.gui.core;

import net.minecraft.client.gui.GuiScreen;
import com.nef.notenoughfakepixel.config.gui.Config;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiScreenElementWrapper extends GuiScreen {

    public final GuiElement element;

    public GuiScreenElementWrapper(GuiElement element) {
        this.element = element;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        element.render();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        element.mouseInput(i, j);
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        element.keyboardInput();
    }

    @Override
    public void onGuiClosed() {
        Config.saveConfig();
    }
}
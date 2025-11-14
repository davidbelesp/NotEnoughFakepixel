package com.nef.notenoughfakepixel.gui;

import net.minecraft.client.gui.GuiButton;

public abstract class Button extends GuiButton {
    public Button(int buttonId, int x, int y, int w, int h, String buttonText) {
        super(buttonId, x, y, w, h, buttonText);
    }

    public abstract void execute();

}

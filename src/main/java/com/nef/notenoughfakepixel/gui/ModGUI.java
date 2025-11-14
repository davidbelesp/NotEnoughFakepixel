package com.nef.notenoughfakepixel.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ModGUI extends GuiScreen {

    protected List<Button> buttons = new ArrayList<>();

    protected void drawrect(int x, int y, int x1, int y1, int color) {
        net.minecraft.client.gui.Gui.drawRect(x, y, x1, y1, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int j;
        for (j = 0; j < this.buttons.size(); ++j) {
            this.buttons.get(j).drawButton(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof Button) {
            Button b = (Button) button;
            b.execute();
        }
    }
}


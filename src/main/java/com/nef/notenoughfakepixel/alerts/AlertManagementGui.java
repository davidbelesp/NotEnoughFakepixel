package com.nef.notenoughfakepixel.alerts;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class AlertManagementGui extends GuiScreen {
    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 50, 10, 100, 20, "Add Alert"));

        int y = 40;
        for (int i = 0; i < Alerts.alerts.size(); i++) {
            Alerts.Alert alert = Alerts.alerts.get(i);
            int buttonX = width - 130;
            int buttonY = y + 5;
            buttonList.add(new GuiButton(2 * i + 1, buttonX, buttonY, 50, 20, "Toggle"));
            buttonList.add(new GuiButton(2 * i + 2, buttonX + 60, buttonY, 50, 20, "Remove"));
            y += 30;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0x80000000);

        int y = 40;
        int padding = 5;
        int buttonWidth = 50;
        int buttonHeight = 20;
        int textX = 10 + padding;
        int buttonX = width - 130;

        for (Alerts.Alert alert : Alerts.alerts) {
            String display = "Alert (" + (alert.toggled ? "Enabled" : "Disabled") + "): Mode: " +
                    alert.mode + ", Location: " + alert.location + ", Message: " +
                    alert.message + ", Alert: " + alert.alert;

            int textY = y + (30 - fontRendererObj.FONT_HEIGHT) / 2;
            int buttonY = y + (30 - buttonHeight) / 2;

            int rectLeft = textX - padding;
            int rectRight = buttonX + 60 + buttonWidth + padding;
            int rectTop = y;
            int rectBottom = y + 30;

            // Draw white outline
            int white = 0xFFFFFFFF;
            drawRect(rectLeft, rectTop, rectRight, rectTop + 1, white);
            drawRect(rectLeft, rectBottom - 1, rectRight, rectBottom, white);
            drawRect(rectLeft, rectTop, rectLeft + 1, rectBottom, white);
            drawRect(rectRight - 1, rectTop, rectRight, rectBottom, white);

            fontRendererObj.drawString(display, textX, textY, 0xFFFFFF);
            y += 30;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(new AddAlertGui(this));
        } else {
            int index = (button.id - 1) / 2;
            if (index >= 0 && index < Alerts.alerts.size()) {
                Alerts.Alert alert = Alerts.alerts.get(index);
                if (button.id % 2 == 1) { // Toggle
                    alert.toggle();
                    Alerts.save();
                } else { // Remove
                    Alerts.alerts.remove(index);
                    Alerts.save();
                    initGui();
                }
            }
        }
    }
}
package org.ginafro.notenoughfakepixel.alerts;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class AddAlertGui extends GuiScreen {
    private final GuiScreen parentScreen;
    private GuiTextField messageField;
    private GuiTextField alertField;
    private int modeIndex = 0;
    private int locationIndex = 0;
    private final String[] modes = {"Starts With", "Contains", "Ends With"};
    private final String[] locations = {"Anywhere", "Skyblock", "Dungeons"};
    private GuiButton modeButton;
    private GuiButton locationButton;

    public AddAlertGui(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int centerX = width / 2;
        int centerY = height / 2;
        int fieldWidth = 200;
        int buttonWidth = 100;
        int spacing = 25;

        messageField = new GuiTextField(1, fontRendererObj, centerX - fieldWidth / 2, centerY - spacing - 10, fieldWidth, 20);
        alertField = new GuiTextField(2, fontRendererObj, centerX - fieldWidth / 2, centerY + 10, fieldWidth, 20);
        modeButton = new GuiButton(3, centerX - buttonWidth - 10, centerY + spacing + 20, buttonWidth, 20, "Mode: " + modes[modeIndex]);
        locationButton = new GuiButton(4, centerX + 10, centerY + spacing + 20, buttonWidth, 20, "Location: " + locations[locationIndex]);
        GuiButton saveButton = new GuiButton(5, centerX - buttonWidth / 2, centerY + 2 * spacing + 30, buttonWidth, 20, "Save");

        buttonList.add(modeButton);
        buttonList.add(locationButton);
        buttonList.add(saveButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0x80000000);

        int centerX = width / 2;
        int centerY = height / 2;
        int spacing = 25;

        String messageLabel = "Message:";
        fontRendererObj.drawString(messageLabel, centerX - fontRendererObj.getStringWidth(messageLabel) / 2, centerY - spacing - 20, 0xFFFFFF);
        messageField.drawTextBox();

        String alertLabel = "Alert Text:";
        fontRendererObj.drawString(alertLabel, centerX - fontRendererObj.getStringWidth(alertLabel) / 2, centerY, 0xFFFFFF);
        alertField.drawTextBox();

        String instruction = "To use Color codes use '&' it will replace the default color code symbol since we cant type it";
        int saveButtonY = centerY + 2 * spacing + 30;
        int instructionY = saveButtonY + 20 + 10;
        fontRendererObj.drawString(instruction, centerX - fontRendererObj.getStringWidth(instruction) / 2, instructionY, 0xFFFFFF);
        int yPos = 15;
        int xPos = 15;
        int yHeight = 4 + (Variables.values().length * 15);
        int wHeight = 350;
        drawRect(xPos - 4,yPos - 4,xPos + wHeight,yHeight + yPos,Color.darkGray.getRGB());
        for(Variables v : Variables.values()){
            fontRendererObj.drawString(v.variableText + ":",xPos,yPos, Color.green.getRGB());
            fontRendererObj.drawString(v.variableUsage,xPos + fontRendererObj.getStringWidth(v.variableText + ":"),yPos,Color.yellow.getRGB());
            yPos += 15;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        messageField.textboxKeyTyped(typedChar, keyCode);
        alertField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        messageField.mouseClicked(mouseX, mouseY, mouseButton);
        alertField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 3) {
            modeIndex = (modeIndex + 1) % modes.length;
            modeButton.displayString = "Mode: " + modes[modeIndex];
        } else if (button.id == 4) {
            locationIndex = (locationIndex + 1) % locations.length;
            locationButton.displayString = "Location: " + locations[locationIndex];
        } else if (button.id == 5) {
            String mode = modes[modeIndex];
            String location = locations[locationIndex];
            String message = messageField.getText();
            String alertText = alertField.getText();
            Alerts.Alert newAlert = new Alerts.Alert(mode, location, message, alertText, true);
            Alerts.alerts.add(newAlert);
            Alerts.save();
            if (parentScreen instanceof AlertManagementGui) {
                parentScreen.initGui();
            }
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
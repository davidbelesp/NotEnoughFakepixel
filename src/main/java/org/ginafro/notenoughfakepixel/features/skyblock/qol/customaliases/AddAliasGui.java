package org.ginafro.notenoughfakepixel.features.skyblock.qol.customaliases;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.ginafro.notenoughfakepixel.utils.Utils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class AddAliasGui extends GuiScreen {

    private final GuiScreen parentScreen;
    private GuiTextField messageField;
    private GuiTextField aliasField;
    private final int modeIndex = 0;
    private int locationIndex = 0;
    private final String[] locations = {"Anywhere", "Skyblock", "Dungeons"};
    private GuiButton locationButton;
    private GuiButton keybindButton;
    private boolean isFocused = false;
    private CustomAliases.Alias alias;
    private int keyBinding;
    private boolean displayError = false;
    private String errorMessage = "";

    public AddAliasGui(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public AddAliasGui(GuiScreen parentScreen, CustomAliases.Alias alias) {
        this.parentScreen = parentScreen;
        this.alias = alias;
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
        aliasField = new GuiTextField(2, fontRendererObj, centerX - fieldWidth / 2, centerY + 10, fieldWidth, 20);
        keyBinding = 0;
        if (alias != null) {
            messageField.setText(alias.command);
            aliasField.setText(alias.alias);
            keyBinding = alias.key;
            for (int i = 0; i < locations.length; i++) {
                if (locations[i].equals(alias.location)) {
                    locationIndex = i;
                }
            }
        }
        locationButton = new GuiButton(4, centerX - (buttonWidth + spacing), centerY + spacing + 20, buttonWidth, 20, "Location: " + locations[locationIndex]);
        GuiButton saveButton = new GuiButton(5, centerX - buttonWidth / 2, centerY + 2 * spacing + 30, buttonWidth, 20, "Save");
        keybindButton = new GuiButton(6, centerX + spacing, centerY + spacing + 20, buttonWidth, 20, "Keybind: " + Utils.getKeyDesc(keyBinding));
        buttonList.add(locationButton);
        buttonList.add(saveButton);
        buttonList.add(keybindButton);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0x80000000);

        int centerX = width / 2;
        int centerY = height / 2;
        int spacing = 25;

        String command = "Command:";
        fontRendererObj.drawString(command, centerX - fontRendererObj.getStringWidth(command) / 2, centerY - spacing - 20, 0xFFFFFF);
        messageField.drawTextBox();

        String allias = "Alias:";
        fontRendererObj.drawString(allias, centerX - fontRendererObj.getStringWidth(allias) / 2, centerY, 0xFFFFFF);
        aliasField.drawTextBox();

        if (displayError && !errorMessage.isEmpty()) {
            mc.fontRendererObj.drawString(errorMessage, centerX - fontRendererObj.getStringWidth(errorMessage) / 2, centerY - spacing - 40, Color.red.getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (isFocused) {
            keyBinding = keyCode;
            keybindButton.displayString = "Keybind: " + Utils.getKeyDesc(keyBinding);
            isFocused = false;
        }
        messageField.textboxKeyTyped(typedChar, keyCode);
        aliasField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        messageField.mouseClicked(mouseX, mouseY, mouseButton);
        aliasField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 6) {
            if (!isFocused) {
                keybindButton.displayString = "Listening";
                isFocused = true;
            }
        }
        if (button.id == 4) {
            locationIndex = (locationIndex + 1) % locations.length;
            locationButton.displayString = "Location: " + locations[locationIndex];
        } else if (button.id == 5) {
            String location = locations[locationIndex];
            String command = messageField.getText();
            if (messageField.getText().isEmpty() || aliasField.getText().isEmpty()) {
                displayError = true;
                errorMessage = "Please Fill out all fields";
                return;
            }
            if (!command.startsWith("/")) {
                command = "/" + command;
            }
            String allias = aliasField.getText();
            CustomAliases.Alias alias1 = new CustomAliases.Alias(location, command, allias, true);
            if (keyBinding != 0) {
                alias1.key = keyBinding;
            }
            if (alias != null) {
                CustomAliases.aliases.remove(alias);
            }
            CustomAliases.aliases.add(alias1);
            CustomAliases.save();
            CustomAliases.registerAliases();
            if (parentScreen instanceof AliasManagementGui) {
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

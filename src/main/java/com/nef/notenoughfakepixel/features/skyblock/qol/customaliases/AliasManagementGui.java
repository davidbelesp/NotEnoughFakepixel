package com.nef.notenoughfakepixel.features.skyblock.qol.customaliases;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import com.nef.notenoughfakepixel.utils.Utils;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class AliasManagementGui extends GuiScreen {
    private int scrollOffset = 0;
    private static final int MAX_DISPLAYED = 12; // Max aliases to display at once
    private static final int ENTRY_HEIGHT = 30;
    private int sliderY = 40;
    private boolean dragging = false;

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 50, 10, 100, 20, "Add Alias"));
        updateButtons();
    }

    private void updateButtons() {
        buttonList.removeIf(button -> button.id != 0); // Keep only "Add Alias" button

        int y = 40;
        List<CustomAliases.Alias> displayedAliases = CustomAliases.aliases.subList(
                Math.min(scrollOffset, CustomAliases.aliases.size()),
                Math.min(scrollOffset + MAX_DISPLAYED, CustomAliases.aliases.size())
        );

        for (int i = 0; i < displayedAliases.size(); i++) {
            CustomAliases.Alias alias = displayedAliases.get(i);
            int buttonX = width - 130;
            int buttonY = y + 5;
            buttonList.add(new GuiButton(2 * i + 1, buttonX, buttonY, 50, 20, "Toggle"));
            buttonList.add(new GuiButton(2 * i + 2, buttonX + 60, buttonY, 50, 20, "Remove"));
            buttonList.add(new EditButton(2 * i + 3, buttonX - 60, buttonY, 50, 20, "Edit"));
            y += ENTRY_HEIGHT;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(10, 10, width - 10, height - 10, 0x80000000);

        int y = 40;
        int textX = 15;
        int buttonX = width - 125;

        List<CustomAliases.Alias> displayedAliases = CustomAliases.aliases.subList(
                Math.min(scrollOffset, CustomAliases.aliases.size()),
                Math.min(scrollOffset + MAX_DISPLAYED, CustomAliases.aliases.size())
        );

        for (CustomAliases.Alias alias : displayedAliases) {
            String display = "Alias: " + alias.alias + " | Command: " + alias.command + " "
                    + (alias.toggled ? "[Enabled]" : "[Disabled]") + " | Location: " + alias.location + " | Key: " + Utils.getKeyDesc(alias.key);

            drawRect(textX - 5, y, buttonX + 110, y + ENTRY_HEIGHT - 5, 0xFF333333);
            fontRendererObj.drawString(display, textX, y + 10, 0xFFFFFF);
            y += ENTRY_HEIGHT;
        }

        // Draw scrollbar
        int scrollbarX = width - 10;
        int scrollbarHeight = (int) ((float) MAX_DISPLAYED / CustomAliases.aliases.size() * (height - 60));
        drawRect(scrollbarX, sliderY, scrollbarX + 5, sliderY + scrollbarHeight, 0xFFAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            if (scroll > 0) {
                scrollOffset = Math.max(scrollOffset - 1, 0);
            } else {
                scrollOffset = Math.min(scrollOffset + 1, Math.max(CustomAliases.aliases.size() - MAX_DISPLAYED, 0));
            }
            updateButtons();
            updateSlider();
        }
    }

    private void updateSlider() {
        int maxScroll = Math.max(CustomAliases.aliases.size() - MAX_DISPLAYED, 0);
        if (maxScroll > 0) {
            sliderY = 40 + (int) ((float) scrollOffset / maxScroll * (height - 100));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int scrollbarX = width - 10;
        int scrollbarHeight = (int) ((float) MAX_DISPLAYED / CustomAliases.aliases.size() * (height - 60));
        if (mouseX >= scrollbarX && mouseX <= scrollbarX + 5 && mouseY >= sliderY && mouseY <= sliderY + scrollbarHeight) {
            dragging = true;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (dragging) {
            int maxScroll = Math.max(CustomAliases.aliases.size() - MAX_DISPLAYED, 0);
            if (maxScroll > 0) {
                int scrollRange = height - 100;
                scrollOffset = (int) ((float) (mouseY - 40) / scrollRange * maxScroll);
                scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
                updateButtons();
                updateSlider();
            }
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(new AddAliasGui(this));
            return;
        }

        int aliasIndex = (button.id - 1) / 3 + scrollOffset;
        if (aliasIndex < 0 || aliasIndex >= CustomAliases.aliases.size()) return;

        CustomAliases.Alias alias = CustomAliases.aliases.get(aliasIndex);

        int buttonType = (button.id - 1) % 3;
        if (buttonType == 0) { // Toggle
            alias.toggle();
            if (alias.toggled) {
                CustomAliases.registerAliases();
            } else {
                CustomAliases.unregisterAlias(alias);
            }
            CustomAliases.save();
        } else if (buttonType == 1) { // Remove
            CustomAliases.aliases.remove(aliasIndex);
            CustomAliases.unregisterAlias(alias);
            CustomAliases.save();
            updateButtons();
        } else if (buttonType == 2 && button instanceof EditButton) { // Edit
            mc.displayGuiScreen(new AddAliasGui(this, alias));
        }
    }
}
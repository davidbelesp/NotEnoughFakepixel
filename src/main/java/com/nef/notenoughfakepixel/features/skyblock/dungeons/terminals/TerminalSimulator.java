package com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RandomUtils;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TerminalSimulator extends GuiScreen {

    private static final String[] PUZZLE_TYPES = {"Starts With", "Click in Order", "Panes", "Colors"};
    private String currentMode = "selector";
    private String currentPuzzle = "";
    private long startTime = 0;
    private final Random random = RandomUtils.getInstance();
    private static final int SLOT_SIZE = 16;
    private static final int INNER_COLUMNS = 7;
    private static final int INNER_ROWS = 4;
    private static final int PANES_COLUMNS = 5;
    private static final int PANES_ROWS = 3;
    private static final int CLICK_IN_ORDER_ROWS = 2;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 10;
    private static final int PADDING = 5;
    private static final int TITLE_HEIGHT = 10;
    private final float scale = Config.feature.dungeons.dungeonsTerminalsScale;
    private int guiLeft, guiTop, guiWidth, guiHeight;
    private boolean initialRenderDone = false;
    private int puzzleRows;

    private final Set<Integer> correctSlots = new HashSet<>();
    private final List<Integer> clickOrder = new ArrayList<>();
    private int currentClickIndex = 0;
    private char lastLetter = ' ';
    private final Set<Integer> clickedSlots = new HashSet<>();
    private String targetColor = "";

    public TerminalSimulator() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();
        openSelector();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!initialRenderDone) {
            initialRenderDone = true;
        }

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        guiLeft = (sr.getScaledWidth() - guiWidth) / 2;
        guiTop = (sr.getScaledHeight() - guiHeight) / 2;

        if (currentMode.equals("selector")) {
            renderSelectorGui();
        } else if (currentMode.equals("puzzle")) {
            renderPuzzleGui();
        }
    }

    private void renderSelectorGui() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(scale, scale, 1.0f);

        int totalButtonWidth = 2 * BUTTON_WIDTH + BUTTON_SPACING;
        int totalButtonHeight = 2 * BUTTON_HEIGHT + BUTTON_SPACING;
        int backgroundWidth = totalButtonWidth + 2 * PADDING;
        int backgroundHeight = totalButtonHeight + 2 * PADDING + TITLE_HEIGHT;
        Gui.drawRect(0, 0, backgroundWidth, backgroundHeight, 0x80000000);
        fontRendererObj.drawStringWithShadow("§8[§bNEF§8] §aSelect a Puzzle", PADDING, PADDING, 0xFFFFFF);

        for (int i = 0; i < PUZZLE_TYPES.length; i++) {
            int row = i / 2;
            int col = i % 2;
            int x = PADDING + col * (BUTTON_WIDTH + BUTTON_SPACING);
            int y = PADDING + TITLE_HEIGHT + row * (BUTTON_HEIGHT + BUTTON_SPACING);
            Gui.drawRect(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, 0xFF555555);
            String text = PUZZLE_TYPES[i];
            int textWidth = fontRendererObj.getStringWidth(text);
            int textX = x + (BUTTON_WIDTH - textWidth) / 2;
            int textY = y + (BUTTON_HEIGHT - 8) / 2;
            fontRendererObj.drawStringWithShadow(text, textX, textY, 0xFFFFFF);
        }

        GlStateManager.popMatrix();
    }

    private void renderPuzzleGui() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(scale, scale, 1.0f);

        int gridWidth = currentPuzzle.equals("Panes") ? PANES_COLUMNS * SLOT_SIZE : INNER_COLUMNS * SLOT_SIZE;
        int gridHeight = currentPuzzle.equals("Panes") ? PANES_ROWS * SLOT_SIZE : puzzleRows * SLOT_SIZE;
        int backgroundWidth = gridWidth + 2 * (PADDING + 5);
        int backgroundHeight = gridHeight + 2 * PADDING + TITLE_HEIGHT;
        Gui.drawRect(0, 0, backgroundWidth, backgroundHeight, 0x80000000);

        switch (currentPuzzle) {
            case "Starts With":
                fontRendererObj.drawStringWithShadow("§8[§bNEF§8] §aStarts With '" + lastLetter + "'", PADDING, PADDING, 0xFFFFFF);
                renderGrid((PADDING + 5), PADDING + TITLE_HEIGHT, INNER_COLUMNS, puzzleRows, correctSlots);
                break;
            case "Click in Order":
                fontRendererObj.drawStringWithShadow("§8[§bNEF§8] §aClick in Order!", PADDING, PADDING, 0xFFFFFF);
                renderClickInOrderGrid((PADDING + 5), PADDING + TITLE_HEIGHT, INNER_COLUMNS, puzzleRows);
                break;
            case "Panes":
                fontRendererObj.drawStringWithShadow("§8[§bNEF§8] §aRed & Green!", PADDING, PADDING, 0xFFFFFF);
                renderGrid((PADDING + 5), PADDING + TITLE_HEIGHT, PANES_COLUMNS, PANES_ROWS, correctSlots);
                break;
            case "Colors":
                fontRendererObj.drawStringWithShadow("§8[§bNEF§8] §a" + targetColor, PADDING, PADDING, 0xFFFFFF);
                renderGrid((PADDING + 5), PADDING + TITLE_HEIGHT, INNER_COLUMNS, puzzleRows, correctSlots);
                break;
        }

        GlStateManager.popMatrix();
    }

    private void renderGrid(int xOffset, int yOffset, int columns, int rows, Set<Integer> highlightSlots) {
        for (int i = 0; i < columns * rows; i++) {
            int row = i / columns;
            int col = i % columns;
            int x = xOffset + col * SLOT_SIZE;
            int y = yOffset + row * SLOT_SIZE;
            Gui.drawRect(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF555555);
            if (highlightSlots.contains(i) && !clickedSlots.contains(i)) {
                int color = ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor).getRGB();
                Gui.drawRect(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, color);
            }
        }
    }

    private void renderClickInOrderGrid(int xOffset, int yOffset, int columns, int rows) {
        for (int i = 0; i < columns * rows; i++) {
            int row = i / columns;
            int col = i % columns;
            int x = xOffset + col * SLOT_SIZE;
            int y = yOffset + row * SLOT_SIZE;
            Gui.drawRect(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF555555);
        }

        for (int i = 0; i < clickOrder.size(); i++) {
            if (i >= currentClickIndex) {
                int slot = clickOrder.get(i);
                int row = slot / columns;
                int col = slot % columns;
                int x = xOffset + col * SLOT_SIZE;
                int y = yOffset + row * SLOT_SIZE;
                int color;
                if (i == currentClickIndex) {
                    color = ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor).getRGB();
                } else if (i == currentClickIndex + 1) {
                    color = ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor).getRGB();
                } else if (i == currentClickIndex + 2) {
                    Color baseColor = ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor);
                    color = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 150).getRGB();
                } else {
                    color = 0xFF888888;
                }
                Gui.drawRect(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, color);
                String num = String.valueOf(i + 1);
                int textWidth = fontRendererObj.getStringWidth(num);
                fontRendererObj.drawStringWithShadow(num, x + (SLOT_SIZE - textWidth) / 2, y + 4, 0xFFFFFF);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (currentMode.equals("selector")) {
            handleSelectorClick(mouseX, mouseY);
        } else if (currentMode.equals("puzzle")) {
            handlePuzzleClick(mouseX, mouseY);
        }
    }

    private void handleSelectorClick(int mouseX, int mouseY) {
        float relX = (mouseX - guiLeft) / scale;
        float relY = (mouseY - guiTop) / scale;

        for (int i = 0; i < PUZZLE_TYPES.length; i++) {
            int row = i / 2;
            int col = i % 2;
            int x = PADDING + col * (BUTTON_WIDTH + BUTTON_SPACING);
            int y = PADDING + TITLE_HEIGHT + row * (BUTTON_HEIGHT + BUTTON_SPACING);
            if (relX >= x && relX <= x + BUTTON_WIDTH &&
                    relY >= y && relY <= y + BUTTON_HEIGHT) {
                currentPuzzle = PUZZLE_TYPES[i];
                currentMode = "puzzle";
                startTime = System.currentTimeMillis();
                initializePuzzle();
                SoundUtils.playSound(Minecraft.getMinecraft().thePlayer.getPosition(), "gui.button.press", 1.0f, 1.0f);
                return;
            }
        }
    }

    private void handlePuzzleClick(int mouseX, int mouseY) {
        float relX = (mouseX - guiLeft) / scale;
        float relY = (mouseY - guiTop) / scale;

        int col, columns;
        if (currentPuzzle.equals("Panes")) {
            columns = PANES_COLUMNS;
        } else {
            columns = INNER_COLUMNS;
        }
        col = (int) ((relX - (PADDING + 5)) / SLOT_SIZE);
        int row = (int) ((relY - PADDING - TITLE_HEIGHT) / SLOT_SIZE);
        if (col < 0 || col >= columns || row < 0 || row >= puzzleRows) {
            return;
        }
        int slotIndex = row * columns + col;

        boolean validClick = false;

        switch (currentPuzzle) {
            case "Starts With":
                if (correctSlots.contains(slotIndex) && !clickedSlots.contains(slotIndex)) {
                    clickedSlots.add(slotIndex);
                    validClick = true;
                }
                break;

            case "Click in Order":
                if (currentClickIndex < clickOrder.size() &&
                        clickOrder.get(currentClickIndex) == slotIndex) {
                    currentClickIndex++;
                    validClick = true;
                }
                break;

            case "Panes":
            case "Colors":
                if (correctSlots.contains(slotIndex) && !clickedSlots.contains(slotIndex)) {
                    clickedSlots.add(slotIndex);
                    validClick = true;
                }
                break;
        }

        if (validClick) {
            SoundUtils.playSound(Minecraft.getMinecraft().thePlayer.getPosition(), "gui.button.press", 1.0f, 1.0f);

            if (checkPuzzleCompletion()) {
                long timeTaken = System.currentTimeMillis() - startTime;
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText("§8[§bNEF§8] §7" + currentPuzzle + " solved in " + String.format("§f%.2f", timeTaken / 1000.0) + " §7seconds!")
                );
                openSelector();
            }
        }
    }

    private void openSelector() {
        currentMode = "selector";
        correctSlots.clear();
        clickOrder.clear();
        clickedSlots.clear();
        currentClickIndex = 0;

        int totalButtonWidth = 2 * BUTTON_WIDTH + BUTTON_SPACING;
        int totalButtonHeight = 2 * BUTTON_HEIGHT + BUTTON_SPACING;
        int backgroundWidth = totalButtonWidth + 2 * PADDING;
        int backgroundHeight = totalButtonHeight + 2 * PADDING + TITLE_HEIGHT;
        guiWidth = (int) (backgroundWidth * scale);
        guiHeight = (int) (backgroundHeight * scale);
    }

    private void initializePuzzle() {
        correctSlots.clear();
        clickOrder.clear();
        clickedSlots.clear();
        currentClickIndex = 0;

        int gridWidth, gridHeight;
        if (currentPuzzle.equals("Panes")) {
            gridWidth = PANES_COLUMNS * SLOT_SIZE;
            gridHeight = PANES_ROWS * SLOT_SIZE;
            puzzleRows = PANES_ROWS;
        } else {
            gridWidth = INNER_COLUMNS * SLOT_SIZE;
            gridHeight = currentPuzzle.equals("Click in Order") ? CLICK_IN_ORDER_ROWS * SLOT_SIZE : INNER_ROWS * SLOT_SIZE;
            puzzleRows = currentPuzzle.equals("Click in Order") ? CLICK_IN_ORDER_ROWS : INNER_ROWS;
        }
        int backgroundWidth = gridWidth + 2 * PADDING;
        int backgroundHeight = gridHeight + 2 * PADDING + TITLE_HEIGHT;
        guiWidth = (int) (backgroundWidth * scale);
        guiHeight = (int) (backgroundHeight * scale);

        switch (currentPuzzle) {
            case "Starts With":
                lastLetter = (char) ('A' + random.nextInt(26));
                for (int i = 0; i < INNER_COLUMNS * puzzleRows; i++) {
                    if (random.nextInt(5) == 0) {
                        correctSlots.add(i);
                    }
                }
                break;

            case "Click in Order":
                List<Integer> allSlots = new ArrayList<>();
                for (int i = 0; i < INNER_COLUMNS * puzzleRows; i++) {
                    allSlots.add(i);
                }
                Collections.shuffle(allSlots);
                clickOrder.addAll(allSlots);
                break;

            case "Panes":
                for (int i = 0; i < PANES_COLUMNS * PANES_ROWS; i++) {
                    if (random.nextInt(3) == 0) {
                        correctSlots.add(i);
                    }
                }
                break;

            case "Colors":
                targetColor = getColorName(random.nextInt(16));
                for (int i = 0; i < INNER_COLUMNS * puzzleRows; i++) {
                    if (random.nextInt(5) == 0) {
                        correctSlots.add(i);
                    }
                }
                break;
        }
    }

    private boolean checkPuzzleCompletion() {
        switch (currentPuzzle) {
            case "Starts With":
            case "Panes":
            case "Colors":
                return clickedSlots.size() == correctSlots.size();

            case "Click in Order":
                return currentClickIndex == clickOrder.size();
        }
        return false;
    }

    private String getColorName(int meta) {
        String[] colors = {"White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray",
                "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
        return colors[meta];
    }
}
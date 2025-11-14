package com.nef.notenoughfakepixel.features.skyblock.overlays.stats;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;

import java.io.IOException;
import java.util.EnumMap;

public class PositionEditorScreen extends GuiScreen {

    private final EnumMap<StatBars.BarType, Position> editingPositions = new EnumMap<>(StatBars.BarType.class);
    private StatBars.BarType draggingBar = null;
    private int dragOffsetX, dragOffsetY;

    @Override
    public void initGui() {
        editingPositions.put(StatBars.BarType.HEALTH, Config.feature.overlays.posHealth);
        editingPositions.put(StatBars.BarType.MANA, Config.feature.overlays.posMana);
        editingPositions.put(StatBars.BarType.EXP, Config.feature.overlays.posExp);
        editingPositions.put(StatBars.BarType.SPEED, Config.feature.overlays.posSpeed);
        editingPositions.put(StatBars.BarType.DEFENCE, Config.feature.overlays.posDefense);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution res = new ScaledResolution(mc);

        for (StatBars.BarType type : StatBars.BarType.values()) {
            Position pos = editingPositions.get(type);
            int x = pos.getRawX();
            int y = pos.getRawY();
            StatBars.BarLength length = new StatBars().getBarLength(type);

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color(type.color[0],type.color[1],type.color[2]);
            mc.getTextureManager().bindTexture(length.fill);
            drawTexturedModalRect(x, y, 0, 0, length.width, 7);

            GlStateManager.popMatrix();
        }

        drawCenteredString(fontRendererObj, "Drag bars to reposition. ESC to exit.", width / 2, height - 20, 0xAAAAAA);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (StatBars.BarType type : StatBars.BarType.values()) {
            Position pos = editingPositions.get(type);
            int x = pos.getRawX();
            int y = pos.getRawY();
            StatBars.BarLength length = new StatBars().getBarLength(type);

            if (mouseX >= x && mouseX <= x + length.width && mouseY >= y && mouseY <= y + 7) {
                draggingBar = type;
                dragOffsetX = mouseX - x;
                dragOffsetY = mouseY - y;
                break;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingBar = null;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (draggingBar != null) {
            Position pos = editingPositions.get(draggingBar);

            int newX = mouseX - dragOffsetX;
            int newY = mouseY - dragOffsetY;

            // Hotbar snapping threshold (within 5px)
            ScaledResolution res = new ScaledResolution(mc);
            int screenWidth = res.getScaledWidth();
            int screenHeight = res.getScaledHeight();

            int barWidth = new StatBars().getBarLength(draggingBar).width;

            int vanillaHotbarX = (screenWidth / 2) - (barWidth / 2);
            int vanillaHotbarY = screenHeight - 49; // Vanilla position just above hotbar

            int snapDistance = 5;

            if (Math.abs(newX - vanillaHotbarX) <= snapDistance)
                newX = vanillaHotbarX;

            if (Math.abs(newY - vanillaHotbarY) <= snapDistance)
                newY = vanillaHotbarY;

            pos.set(new Position(newX, newY));
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

package com.nef.notenoughfakepixel.features.capes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import com.nef.notenoughfakepixel.features.capes.Cape;
import com.nef.notenoughfakepixel.features.capes.CapeManager;
import com.nef.notenoughfakepixel.utils.Utils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CapeGui extends GuiScreen {

    public int wX, wY, wH, wW, bList;
    public int bW, bH;
    public float scale;
    public int scrollOffset;
    public int wheel = 0;
    public ScaledResolution sr;
    public static Cape selected;
    public Map<Integer, CapeButton> capeMap = new HashMap<>();
    public GuiTextField searchBar;
    public int sX, sY, sW, sH;
    public ResourceLocation gui_cape = new ResourceLocation("notenoughfakepixel", "cape_gui.png");
    public ResourceLocation gui_cape_s = new ResourceLocation("notenoughfakepixel", "cape_gui_s.png");

    @Override
    public void initGui() {
        super.initGui();
        mc = Minecraft.getMinecraft();
        sr = new ScaledResolution(mc);
        scale = Utils.getScale();
        wX = sr.getScaledWidth() / 2 - (((int) (749 * scale)) / 2);
        wY = sr.getScaledHeight() / 2 - (((int) (472 * scale)) / 2);
        wW = (int) (749 * scale);
        wH = (int) (472 * scale);
        bH = (int) (300 * scale);
        bList = wY + bH;
        bW = (wH - (int) (30 * scale)) / 3;
        for (int i = 0; i < CapeManager.getAllCapes().size(); i++) {
            Cape c = CapeManager.getAllCapes().get(i);
            ResourceLocation tex = gui_cape;
            if (c == CapeManager.getCape()) {
                selected = c;
                tex = gui_cape_s;
            }
            CapeButton b = new CapeButton(i, -9999, -9999, bW, bH, c, tex);
            capeMap.putIfAbsent(c.capeID, b);
            buttonList.add(b);
        }
        buttonList.add(new ResetButton(101, wX + (wW / 2) - (int) (50 * scale), wY + wH - (int) (50 * scale), (int) (100 * scale), (int) (35 * scale)));
        sW = (int) (326 * scale);
        sH = (int) (37 * scale);
        sX = wX + wH - (int) (90 * scale);
        sY = wY + (int) (15 * scale);
        searchBar = new GuiTextField(1001, mc.fontRendererObj, sX, sY, sW, sH);
        searchBar.setEnableBackgroundDrawing(false);
        update();
    }

    public void update() {
        if (!searchBar.getText().isEmpty()) {
            for (Integer in : capeMap.keySet()) {
                CapeButton b = capeMap.get(in);
                if (b.ca.capeName.toLowerCase().startsWith(searchBar.getText().toLowerCase())) {
                    scrollOffset = b.id;
                    break;
                }
            }
        }
        for (int i = 0; i <= capeMap.size(); i++) {
            CapeButton b = capeMap.get(i);
            if (b == null) {
                continue;
            }
            if (b.id < scrollOffset || b.id > scrollOffset + 4) {
                b.visible = false;
                b.enabled = false;
                b.xPosition = -9999;
                b.yPosition = -9999;
            }
            if (b.id >= scrollOffset && b.id < scrollOffset + 4) {
                b.xPosition = wX + (int) (45 * scale) + ((bW + (int) (10 * scale)) * Math.abs(scrollOffset - b.id));
                b.yPosition = wY + (int) (75 * scale);
                b.enabled = true;
                b.visible = true;
            }
            if (b.tex == gui_cape_s && b.ca != selected) {
                b.tex = gui_cape;
            }
            if (b.ca == selected && b.tex == gui_cape) {
                b.tex = gui_cape_s;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("notenoughfakepixel", "cape_manager.png"));
        drawScaledCustomSizeModalRect(wX, wY, 0, 0, wW, wH, wW, wH, wW, wH);
        update();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(wX * sr.getScaleFactor(), wY * sr.getScaleFactor(), wW * sr.getScaleFactor(), wH * sr.getScaleFactor());
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("notenoughfakepixel", "cape_search.png"));
        drawScaledCustomSizeModalRect(sX, sY, 0f, 0f, sW, sH, sW, sH, sW, sH);
        searchBar.drawTextBox();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_LEFT) {
            scrollOffset -= 1;
        }
        if (keyCode == Keyboard.KEY_RIGHT) {
            scrollOffset += 1;
        }
        if (scrollOffset > capeMap.size() - 4) {
            scrollOffset = capeMap.size() - 4;
        }
        if (scrollOffset < 0) {
            scrollOffset = 0;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (searchBar.isFocused()) {
                searchBar.setFocused(false);
            } else {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
        if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode() && !searchBar.isFocused()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        searchBar.textboxKeyTyped(typedChar, keyCode);

    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        wheel = Mouse.getDWheel();
        if (wheel == 0) return;
        if (wheel > 0) {
            scrollOffset--;
        } else {
            scrollOffset++;
        }
        if (scrollOffset > capeMap.size() - 4) {
            scrollOffset = capeMap.size() - 4;
        }
        if (scrollOffset < 0) {
            scrollOffset = 0;
        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchBar.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button instanceof CapeButton) {
            ((CapeButton) button).process();
            selected = ((CapeButton) button).ca;
        }
        if (button instanceof ResetButton) {
            CapeManager.setCape(-11);
            selected = null;
            for (GuiButton b : buttonList) {
                if (b instanceof CapeButton) {
                    CapeButton cb = (CapeButton) b;
                    if (cb.tex == gui_cape_s) {
                        cb.tex = gui_cape;
                    }
                }
            }
        }
    }
}

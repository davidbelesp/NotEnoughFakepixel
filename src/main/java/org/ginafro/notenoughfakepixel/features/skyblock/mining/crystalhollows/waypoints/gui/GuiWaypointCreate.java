package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.ChWaypoint;
import org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.CrystalWaypoints;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Locale;

public class GuiWaypointCreate extends GuiScreen {

    private GuiTextField nameField, xField, yField, zField;
    private GuiButton btnSave, btnCancel, btnTemp, btnUsePos;
    private boolean temporary = false;

    private final ChWaypoint editing;
    private final boolean editMode;

    private String errorMsg = null;
    private long errorUntilMs = 0L;

    public GuiWaypointCreate() {
        this.editing = null;
        this.editMode = false;
    }

    public GuiWaypointCreate(ChWaypoint toEdit) {
        this.editing = toEdit;
        this.editMode = toEdit != null;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int panelW = 320, panelH = 160;
        int cx = this.width / 2, cy = this.height / 2;
        int left = cx - panelW / 2, top = cy - panelH / 2;

        this.buttonList.clear();

        // ---- Fields ----
        this.nameField = new GuiTextField(0, this.fontRendererObj, left + 90, top + 20, 200, 18);
        this.nameField.setMaxStringLength(48);

        this.xField = new GuiTextField(1, this.fontRendererObj, left + 90,  top + 63, 60, 18);
        this.yField = new GuiTextField(2, this.fontRendererObj, left + 156, top + 63, 60, 18);
        this.zField = new GuiTextField(3, this.fontRendererObj, left + 222, top + 63, 60, 18);

        if (editMode && editing != null) {
            nameField.setText(editing.name);
            xField.setText(fmt1(editing.x));
            yField.setText(fmt1(editing.y));
            zField.setText(fmt1(editing.z));

            this.temporary = readTemporary(editing);
        } else {
            EntityPlayerSP p = mc.thePlayer;
            nameField.setText("");
            xField.setText(fmt1(p != null ? p.posX : 0));
            yField.setText(fmt1(p != null ? p.posY : 0));
            zField.setText(fmt1(p != null ? p.posZ : 0));
            this.temporary = false;
        }

        this.nameField.setFocused(true);

        this.btnTemp   = new GuiButton(10, left + 90,  top + 90, 90, 20, labelTemp());
        this.btnUsePos = new GuiButton(11, left + 186, top + 90, 104, 20, "Use my position");
        this.buttonList.add(this.btnTemp);
        if (!editMode) this.buttonList.add(this.btnUsePos);

        this.btnSave   = new GuiButton(12, left + 30,  top + 133, 90, 20, editMode ? "Update" : "Save");
        this.btnCancel = new GuiButton(13, left + 204, top + 133, 86, 20, "Cancel");
        this.buttonList.add(this.btnSave);
        this.buttonList.add(this.btnCancel);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 10: temporary = !temporary; btnTemp.displayString = labelTemp(); break;
            case 11: {
                EntityPlayerSP p = mc.thePlayer;
                if (p != null) {
                    xField.setText(fmt1(p.posX));
                    yField.setText(fmt1(p.posY));
                    zField.setText(fmt1(p.posZ));
                }
                break;
            }
            case 12: doSave(); break;
            case 13: mc.displayGuiScreen(null); break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_TAB) {
            if (nameField.isFocused()) { nameField.setFocused(false); xField.setFocused(true); }
            else if (xField.isFocused()) { xField.setFocused(false); yField.setFocused(true); }
            else if (yField.isFocused()) { yField.setFocused(false); zField.setFocused(true); }
            else { zField.setFocused(false); nameField.setFocused(true); }
            return;
        }
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            doSave();
            return;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null);
            return;
        }

        if (nameField.isFocused()) {
            nameField.textboxKeyTyped(typedChar, keyCode);
        } else {
            if (xField.isFocused()) numericKeyTyped(xField, typedChar, keyCode);
            if (yField.isFocused()) numericKeyTyped(yField, typedChar, keyCode);
            if (zField.isFocused()) numericKeyTyped(zField, typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
        xField.mouseClicked(mouseX, mouseY, mouseButton);
        yField.mouseClicked(mouseX, mouseY, mouseButton);
        zField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Black Overlay + panel
        drawGradientRect(0, 0, this.width, this.height, 0xAA000000, 0xAA000000);
        int panelW = 320, panelH = 160;
        int cx = this.width / 2, cy = this.height / 2;
        int left = cx - panelW / 2, top = cy - panelH / 2, right = left + panelW, bottom = top + panelH;

        drawRect(left, top, right, bottom, 0x66000000);
        drawHorizontalLine(left, right, top, 0x33FFFFFF);
        drawHorizontalLine(left, right, bottom, 0x33FFFFFF);
        drawVerticalLine(left, top, bottom, 0x33FFFFFF);
        drawVerticalLine(right, top, bottom, 0x33FFFFFF);

        // Title
        String title = EnumChatFormatting.AQUA + "New Waypoint";
        this.drawCenteredString(this.fontRendererObj, title, cx, top - 12, 0xFFFFFF);

        // Labels
        this.fontRendererObj.drawString("Name:", left + 20, top + 24, 0xFFFFFF, false);
        this.fontRendererObj.drawString("X:", left + 120, top + 48, 0xFFFFFF, false);
        this.fontRendererObj.drawString("Y:", left + 156, top + 48, 0xFFFFFF, false);
        this.fontRendererObj.drawString("Z:", left + 222, top + 48, 0xFFFFFF, false);

        // Fields
        nameField.drawTextBox();
        xField.drawTextBox();
        yField.drawTextBox();
        zField.drawTextBox();

        // Error message
        if (errorMsg != null && System.currentTimeMillis() < errorUntilMs) {
            int w = this.fontRendererObj.getStringWidth(errorMsg);
            drawRect(cx - w/2 - 6, bottom + 6, cx + w/2 + 6, bottom + 6 + 14, 0xAA7F1B1B);
            this.drawCenteredString(this.fontRendererObj, errorMsg, cx, bottom + 8, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void doSave() {
        String name = sanitizeName(nameField.getText());
        if (name == null) { showError("Name required"); return; }

        Double dx = parseDoubleSafe(xField.getText());
        Double dy = parseDoubleSafe(yField.getText());
        Double dz = parseDoubleSafe(zField.getText());
        if (dx == null || dy == null || dz == null) { showError("Invalid coordinates"); return; }

        if (editMode) {
            ChWaypoint updated;
            updated = new ChWaypoint(dx, dy, dz, editing.id, name, editing.isTemporarySafe(), editing.getColorRgbOrDefault(), editing.isToggledSafe() );
            CrystalWaypoints.getInstance().updateWaypoint(updated);
            CrystalWaypoints.getInstance().saveIfDirty();
        } else {
            ChWaypoint wp = ChWaypoint.of(dx, dy, dz, name);
            CrystalWaypoints.getInstance().addWaypoint(wp);
            CrystalWaypoints.getInstance().saveIfDirty();
        }
        mc.displayGuiScreen(null);
    }

    // ---- Helpers ----

    private static String fmt1(double d) {
        return String.format(Locale.ROOT, "%.1f", d);
    }

    private static String sanitizeName(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;
        s = EnumChatFormatting.getTextWithoutFormattingCodes(s);
        return s.replace(':', ';');
    }

    private static Double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.replace(',', '.')); }
        catch (Exception e) { return null; }
    }

    private String labelTemp() { return temporary ? "Temp: §aON" : "Temp: §cOFF"; }

    private boolean readTemporary(ChWaypoint wp) {
        try {
            return (boolean) ChWaypoint.class.getMethod("isTemporarySafe").invoke(wp);
        } catch (Exception ignore) {
            try { return (boolean) ChWaypoint.class.getMethod("isTemporary").invoke(wp); }
            catch (Exception ignore2) { return false; }
        }
    }

    @SuppressWarnings("unused")
    private int getColorOrDefault(ChWaypoint wp) {
        try { return (int) ChWaypoint.class.getMethod("getColorRgbOrDefault").invoke(wp); }
        catch (Exception ignore) {
            return ColorUtils.getColor(Config.feature.mining.crystalWaypointColor).getRGB();
        }
    }

    private void showError(String msg) {
        this.errorMsg = "§c" + msg;
        this.errorUntilMs = System.currentTimeMillis() + 2200L;
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), 0.8F));
    }

    private static boolean isAllowedNumericChar(char c) {
        return (c >= '0' && c <= '9') || c == '.' || c == '-' || c == Keyboard.CHAR_NONE;
    }

    private void numericKeyTyped(GuiTextField tf, char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE
                || keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT
                || keyCode == Keyboard.KEY_HOME || keyCode == Keyboard.KEY_END) {
            tf.textboxKeyTyped(typedChar, keyCode);
            return;
        }
        if (!isAllowedNumericChar(typedChar)) return;
        if (typedChar == '.') {
            if (tf.getText().indexOf('.') >= 0) return;
        }
        if (typedChar == '-') {
            if (tf.getCursorPosition() != 0 || tf.getText().indexOf('-') == 0) return;
        }
        tf.textboxKeyTyped(typedChar, keyCode);
    }
}

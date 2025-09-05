package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.gui;

import com.sun.org.apache.bcel.internal.generic.I2F;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumChatFormatting;
import org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.ChWaypoint;
import org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.CrystalWaypoints;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class GuiWaypointManager extends GuiScreen implements RowActions {

    private WaypointList list;
    private GuiButton closeBtn;
    private GuiButton removeTempsBtn;
    private GuiButton addNewBtn;
    private List<ChWaypoint> waypoints;

    @Override
    public void initGui() {
        this.waypoints = CrystalWaypoints.getInstance().getAll();

        int panelW = 360, top = 32, bottom = height - 40, panelL = (width - panelW) / 2;

        this.list = new WaypointList(this.mc, this.width, this.height, 32, this.height - 40, 20, waypoints, this);

        int cx = this.width / 2;
        this.buttonList.clear();
        this.closeBtn = new GuiButton(0, cx - 40, bottom + 10, 80, 20, "Close");
        this.buttonList.add(this.closeBtn);

        this.removeTempsBtn = new GuiButton(1, 8, bottom + 10, 140, 20, "Remove all Temps");
        try {
            this.removeTempsBtn.enabled = CrystalWaypoints.getInstance().hasTemp();
        } catch (Throwable ignored) {}
        this.buttonList.add(this.removeTempsBtn);

        this.addNewBtn = new GuiButton(2, width - 8 - 140, bottom + 10, 140, 20, "Add New Waypoint");
        this.buttonList.add(this.addNewBtn);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(null);
        }
        if (button.id == 1) {
            CrystalWaypoints.getInstance().removeAllTemp();

            this.waypoints = CrystalWaypoints.getInstance().getAll();
            this.list.setData(this.waypoints);

            try {
                CrystalWaypoints.getInstance().markDirty();
            } catch (Throwable ignored) {
                CrystalWaypoints.getInstance().saveIfDirty();
            }

            try {
                this.removeTempsBtn.enabled = CrystalWaypoints.getInstance().hasTemp();
            } catch (Throwable ignored) {}

            return;
        }
        if (button.id == 2) {
            mc.displayGuiScreen(new GuiWaypointCreate());
            return;
        }
    }

    @Override
    public void onDelete(ChWaypoint wp) {
        if (CrystalWaypoints.getInstance().removeById(wp.getId())) {
            this.waypoints = CrystalWaypoints.getInstance().getAll();
            this.list.setData(this.waypoints);
            CrystalWaypoints.getInstance().saveIfDirty();
        }
    }

    @Override
    public void onShare(ChWaypoint wp) {
        if (wp == null) return;
        final EntityPlayerSP p = mc.thePlayer;
        if (p == null) return;

        String name = wp.getName() != null ? wp.getName() : "Waypoint";
        name = EnumChatFormatting.getTextWithoutFormattingCodes(name).replace(':', ';');

        String msgToShare = String.format(java.util.Locale.ROOT,
                "CHW:%s:%.1f:%.1f:%.1f", name, wp.x, wp.y, wp.z);

        p.sendChatMessage(msgToShare);
    }

    @Override
    public void onEdit(ChWaypoint wp) {
        mc.displayGuiScreen(new GuiWaypointCreate(wp));
    }

    @Override
    public void onGuiClosed() {
        CrystalWaypoints.getInstance().saveIfDirty();
        super.onGuiClosed();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        this.list.drawScreen(mouseX, mouseY, partialTicks);

        String title = EnumChatFormatting.AQUA + "Waypoints (" + waypoints.size() + ")";
        this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 12, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static class WaypointList extends GuiSlot {

        @Setter private List<ChWaypoint> data;
        private final Minecraft mc;
        private final RowActions actions;

        private static final int LEFT_MARGIN = 6;
        private static final int CB_SIZE = 12;
        private static final int CB_GAP_TEXT = 6;

        // Buttons style
        private static final int BTN_H = 14;
        private static final int BTN_PAD_X = 4; // Button padding in X
        private static final int BTN_GAP = 4; // Gap between buttons
        private static final int RIGHT_MARGIN = 8; // Margin right of last button

        private static final String LBL_DELETE = "Delete";
        private static final String LBL_SHARE  = "Share";
        private static final String LBL_EDIT   = "Edit";

        private final int rowH;

        private final int W_DELETE, W_SHARE, W_EDIT;

        public WaypointList(Minecraft mc, int w, int h, int top, int bottom, int slotHeight,
                            List<ChWaypoint> data, RowActions actions) {
            super(mc, w, h, top, bottom, slotHeight);
            this.mc = mc;
            this.data = data;
            this.actions = actions;
            this.rowH = slotHeight;

            FontRenderer fr = mc.fontRendererObj;
            this.W_DELETE = fr.getStringWidth(LBL_DELETE) + BTN_PAD_X * 2;
            this.W_SHARE  = fr.getStringWidth(LBL_SHARE)  + BTN_PAD_X * 2;
            this.W_EDIT   = fr.getStringWidth(LBL_EDIT)   + BTN_PAD_X * 2;
        }


        @Override protected int getSize() { return data.size(); }
        @Override protected void drawBackground() {}
        @Override protected boolean isSelected(int index) { return false; }

        // Debouncing prevention for clicks
        private long lastClickMs = 0L;
        private int lastClickIndex = -1;

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            if (index < 0 || index >= data.size()) return;
            if (Mouse.getEventButton() != 0 || !Mouse.getEventButtonState()) return;
            if (doubleClick) return;

            long now = System.currentTimeMillis();
            if (index == lastClickIndex && (now - lastClickMs) < 120L) return;
            lastClickIndex = index;
            lastClickMs = now;

            ChWaypoint wp = data.get(index);

            BtnRect cb = rectCheckboxForIndex(index);
            if (cb.contains(mouseX, mouseY)) {
                wp.setToggled(!wp.getToggled());
                try {
                    CrystalWaypoints.getInstance().markDirty();
                } catch (Throwable ignored) {
                    CrystalWaypoints.getInstance().saveIfDirty();
                }
                return;
            }

            final int slotTopY = this.top + index * this.rowH - (int)this.amountScrolled;
            final int btnY = slotTopY + (this.rowH - BTN_H) / 2;

            final int listRight = this.right;
            int xRight = listRight - RIGHT_MARGIN;

            BtnRect del = new BtnRect(xRight - W_DELETE, btnY, W_DELETE, BTN_H); xRight -= (W_DELETE + BTN_GAP);
            BtnRect shr = new BtnRect(xRight - W_SHARE,  btnY, W_SHARE,  BTN_H); xRight -= (W_SHARE  + BTN_GAP);
            BtnRect edt = new BtnRect(xRight - W_EDIT,   btnY, W_EDIT,   BTN_H);

            if (del.contains(mouseX, mouseY)) { actions.onDelete(wp); return; }
            if (shr.contains(mouseX, mouseY)) { actions.onShare(wp);  return; }
            if (edt.contains(mouseX, mouseY)) { actions.onEdit(wp);   return; }
        }

        @Override
        protected void drawSlot(int idx, int x, int y, int slotHeight, int mouseX, int mouseY) {
            if (y + slotHeight < this.top || y > this.bottom) return;
            ChWaypoint wp = data.get(idx);
            FontRenderer fr = mc.fontRendererObj;

            final int listLeft  = this.left;
            final int listRight = this.right;

            // 1) Buttons (right)
            int xRight = listRight - RIGHT_MARGIN;

            BtnRect del = makeBtnRect(xRight - W_DELETE, y + ((slotHeight - BTN_H) / 2), W_DELETE, BTN_H); xRight -= (W_DELETE + BTN_GAP);
            BtnRect shr = makeBtnRect(xRight - W_SHARE,  y + ((slotHeight - BTN_H) / 2), W_SHARE,  BTN_H); xRight -= (W_SHARE  + BTN_GAP);
            BtnRect edt = makeBtnRect(xRight - W_EDIT,   y + ((slotHeight - BTN_H) / 2), W_EDIT,   BTN_H);

            drawButton(edt,  LBL_EDIT,   mouseX, mouseY, fr);
            drawButton(shr,  LBL_SHARE,  mouseX, mouseY, fr);
            drawButton(del,  LBL_DELETE, mouseX, mouseY, fr);

            // 2) Checkbox (left)
            BtnRect cb = rectCheckboxForIndex(idx);
            boolean hoverCb = cb.contains(mouseX, mouseY);

            Gui.drawRect(cb.x - 1, cb.y - 1, cb.x + cb.w + 1, cb.y + cb.h + 1, 0xFF444444);
            int bg = wp.isToggledSafe() ? (hoverCb ? 0xFF37D279 : 0xFF2ECC71) : (hoverCb ? 0xFF333333 : 0xFF222222);
            Gui.drawRect(cb.x, cb.y, cb.x + cb.w, cb.y + cb.h, bg);

            if (wp.isToggledSafe()) {
                String tick = "✔";
                int tw = fr.getStringWidth(tick);
                fr.drawString(tick, cb.x + (CB_SIZE - tw) / 2, cb.y + 1, 0xFF0B5);
            }

            // 3) Left Text: name + coords
            final int nameX = listLeft + LEFT_MARGIN + CB_SIZE + CB_GAP_TEXT;
            final int textRightLimit = Math.min(edt.x - 8, listRight - RIGHT_MARGIN);

            final int xi = (int)Math.round(wp.getX());
            final int yi = (int)Math.round(wp.getY());
            final int zi = (int)Math.round(wp.getZ());
            final String coords = EnumChatFormatting.GRAY + "[" + xi + "," + yi + "," + zi + "]";
            final int coordsW = fr.getStringWidth(coords);
            final int coordsX = textRightLimit - coordsW;

            int nameMaxW = Math.max(16, coordsX - 6 - nameX);
            String name = wp.getName() != null ? wp.getName() : "Waypoint";
            if (fr.getStringWidth(name) > nameMaxW) name = trimToWidthWithEllipsis(fr, name, nameMaxW);

            fr.drawString(name,   nameX,        y + 3, 0xFFFFFF, false);
            fr.drawString(coords, coordsX,      y + 3, 0xAAAAAA, false);
        }

        @Override public int getListWidth() {
            return this.width;
        }

        @Override protected int getScrollBarX() {
            return this.right - 6;
        }

        //* This function is taken from Vanilla's GuiSlot and modified to use a custom background
        @Override protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float f = 32.0F;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldrenderer.pos((double)this.left, (double)endY, (double)0.0F).tex((double)0.0F, (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
            worldrenderer.pos((double)(this.left + this.width), (double)endY, (double)0.0F).tex((double)((float)this.width / 32.0F), (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
            worldrenderer.pos((double)(this.left + this.width), (double)startY, (double)0.0F).tex((double)((float)this.width / 32.0F), (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
            worldrenderer.pos((double)this.left, (double)startY, (double)0.0F).tex((double)0.0F, (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
            tessellator.draw();
        }

        private BtnRect rectCheckboxForIndex(int index) {
            int slotTopY = this.top + index * this.rowH - (int) this.amountScrolled;
            int cbX = this.left + LEFT_MARGIN;
            int cbY = slotTopY + (this.rowH - CB_SIZE) / 2;
            return new BtnRect(cbX, cbY, CB_SIZE, CB_SIZE);
        }

        @Override protected void drawContainerBackground(Tessellator tess) {
            drawRect(this.left, this.top, this.right, this.bottom, 0x55000000);
        }

        // ---------- Button Helpers ----------
        private static final class BtnRect {
            final int x, y, w, h;
            BtnRect(int x, int y, int w, int h) { this.x = x; this.y = y; this.w = w; this.h = h; }
            boolean contains(int mx, int my) { return mx >= x && mx < x + w && my >= y && my < y + h; }
        }
        private static BtnRect makeBtnRect(int x, int y, int w, int h) { return new BtnRect(x, y, w, h); }

        private BtnRect makeBtnFromRight(int rightX, int mouseY, int width) {
            int x0 = rightX - width;
            int y0 = Math.max(this.top, mouseY - (BTN_H / 2));
            return new BtnRect(x0, y0, width, BTN_H);
        }

        private void drawButton(BtnRect r, String label, int mouseX, int mouseY, FontRenderer fr) {
            final boolean hover = r.contains(mouseX, mouseY);
            int bg = hover ? 0xFFFFFFFF : 0xFF444444;
            switch (label) {
                case LBL_SHARE:
                    bg = hover ? 0xFFfcae42 : 0xFFf89537;
                    break;
                case LBL_DELETE:
                    bg = hover ? 0xFFe76e49 : 0xFFe14f30;
                    break;
                case LBL_EDIT:
                    bg = hover ? 0xFF2a91ce : 0xFF1272b9;
                    break;
            }

            Gui.drawRect(r.x, r.y, r.x + r.w, r.y + r.h, bg);

            int tl = hover ? 0x55FFFFFF : 0x33333333;
            int br = hover ? 0x33000000 : 0x55000000;
            Gui.drawRect(r.x,             r.y,             r.x + r.w,     r.y + 1,     tl); // top
            Gui.drawRect(r.x,             r.y + r.h - 1,   r.x + r.w,     r.y + r.h,   br); // bottom
            Gui.drawRect(r.x,             r.y,             r.x + 1,       r.y + r.h,   tl); // left
            Gui.drawRect(r.x + r.w - 1,   r.y,             r.x + r.w,     r.y + r.h,   br); // right

            int tx = r.x + (r.w - fr.getStringWidth(label)) / 2;
            int ty = r.y + (r.h - 8) / 2;

            fr.drawString(label, tx, ty, 0xFFFFFF, false);
        }

        private static String trimToWidthWithEllipsis(FontRenderer fr, String s, int maxWidth) {
            if (maxWidth <= 6) return "...";
            String trimmed = fr.trimStringToWidth(s, maxWidth - 6);
            return (trimmed.endsWith(" ") ? trimmed.substring(0, trimmed.length() - 1) : trimmed) + "...";
        }

    }

}

package com.nef.notenoughfakepixel.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Utils {

    private static final LinkedList<Integer> guiScales = new LinkedList<>();
    private static ScaledResolution lastScale = new ScaledResolution(Minecraft.getMinecraft());
    private static final FloatBuffer projectionMatrixOld = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer modelviewMatrixOld = BufferUtils.createFloatBuffer(16);

    private static final char[] c = new char[]{'k', 'm', 'b', 't'};

    public static boolean overlayShouldRender(RenderGameOverlayEvent.ElementType type, boolean... booleans) {
        return overlayShouldRender(false, type, RenderGameOverlayEvent.ElementType.HOTBAR, booleans);
    }

    public static boolean overlayShouldRender(boolean hideOnf3, RenderGameOverlayEvent.ElementType type, RenderGameOverlayEvent.ElementType checkType, boolean... booleans) {
        Minecraft mc = Minecraft.getMinecraft();
        for (boolean aBoolean : booleans) if (!aBoolean) return false;
        if (hideOnf3) {
            if (mc.gameSettings.showDebugInfo || (mc.gameSettings.keyBindPlayerList.isKeyDown() && (!mc.isIntegratedServerRunning() || mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1))) {
                return false;
            }
        }
        return ((type == null && Loader.isModLoaded("labymod")) || type == checkType);
    }

    public static void drawStringScaledMaxWidth(String str, FontRenderer fr, float x, float y, boolean shadow, int len, int colour) {
        int strLen = fr.getStringWidth(str);
        float factor = len / (float) strLen;
        factor = Math.min(1, factor);
        drawStringScaled(str, fr, x, y, shadow, colour, factor);
    }

    public static void drawStringScaled(String str, FontRenderer fr, float x, float y, boolean shadow, int colour, float factor) {
        GlStateManager.scale(factor, factor, 1);
        fr.drawString(str, x / factor, y / factor, colour, shadow);
        GlStateManager.scale(1 / factor, 1 / factor, 1);
    }

    public static void drawTexturedRect(float x, float y, float width, float height) {
        drawTexturedRect(x, y, width, height, 0, 1, 0, 1);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        drawTexturedRect(x, y, width, height, 0, 1, 0, 1, filter);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, GL11.GL_LINEAR);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(uMin, vMax).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(uMax, vMax).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(uMax, vMin).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.disableBlend();
    }

    public static void resetGuiScale() {
        guiScales.clear();
    }

    public static ScaledResolution peekGuiScale() {
        return lastScale;
    }

    public static ScaledResolution pushGuiScale(int scale) {
        if (guiScales.isEmpty() && Loader.isModLoaded("labymod")) {
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixOld);
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrixOld);
        }

        if (scale < 0) {
            if (!guiScales.isEmpty()) guiScales.pop();
        } else {
            guiScales.push(scale == 0 ? Minecraft.getMinecraft().gameSettings.guiScale : scale);
        }

        int newScale = !guiScales.isEmpty() ? Math.max(0, Math.min(4, guiScales.peek())) : Minecraft.getMinecraft().gameSettings.guiScale;
        if (newScale == 0) newScale = Minecraft.getMinecraft().gameSettings.guiScale;

        int oldScale = Minecraft.getMinecraft().gameSettings.guiScale;
        Minecraft.getMinecraft().gameSettings.guiScale = newScale;
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft.getMinecraft().gameSettings.guiScale = oldScale;

        if (!guiScales.isEmpty()) {
            GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        } else if (Loader.isModLoaded("labymod") && projectionMatrixOld.limit() > 0 && modelviewMatrixOld.limit() > 0) {
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GL11.glLoadMatrix(projectionMatrixOld);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadMatrix(modelviewMatrixOld);
        } else {
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        }

        lastScale = scaledresolution;
        return scaledresolution;
    }

    public static void drawStringCentered(String str, FontRenderer fr, float x, float y, boolean shadow, int colour) {
        int strLen = fr.getStringWidth(str);
        float x2 = x - strLen / 2f;
        float y2 = y - fr.FONT_HEIGHT / 2f;
        GL11.glTranslatef(x2, y2, 0);
        fr.drawString(str, 0, 0, colour, shadow);
        GL11.glTranslatef(-x2, -y2, 0);
    }

    public static void copyToClipboard(String str) {
        GuiScreen.setClipboardString(str);
    }

    public static float map(float x, float inputStart, float inputEnd, float outputStart, float outputEnd) {
        return (x - inputStart) / (inputEnd - inputStart) * (outputEnd - outputStart) + outputStart;
    }

    public static float getScale() {
        int sc = Minecraft.getMinecraft().gameSettings.guiScale;
        if (sc == 0) {
            return 1.0f;
        }
        switch (sc) {
            case 2:
                return 1f / 2.0f;
            case 3:
                return 1f / 3.0f;
            case 4:
                return 1f / 4.0f;
            default:
                return 1.0f;
        }
    }

    public static String shortNumberFormat(double n, int iteration) {
        // This function will convert a number into a short number format.
        // For example, 1231 -> 1.2k, 1233000 -> 1.2m, 1323000000 -> 1.3b, 1000000000000 -> 1t

        double d = ((double) (long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;
        //this determines the class, i.e. 'k', 'm' etc
        //this decides whether to trim the decimals
        // (int) d * 10 / 10 drops the decimal
        return d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                (isRound || d > 9.99 ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "") // (int) d * 10 / 10 drops the decimal
                        + "" + c[iteration] : shortNumberFormat(d, iteration + 1);

    }


    public static @NotNull String commaFormat(double n) {
        // This function will only apply commas to a number.
        return String.format("%,d", (long) n);
    }

    @Setter
    @Getter
    private String lockedEnchantment = "";

    public static String getKeyDesc(int keycode) {
        switch (keycode) {
            case 2:
                return "1";
            case 3:
                return "2";
            case 4:
                return "3";
            case 5:
                return "4";
            case 6:
                return "5";
            case 7:
                return "6";
            case 8:
                return "7";
            case 9:
                return "8";
            case 10:
                return "9";
            case 11:
                return "0";
            case 12:
                return "-";
            case 13:
                return "=";
            case 14:
                return "Backspace";
            case 15:
                return "Tab";
            case 16:
                return "Q";
            case 17:
                return "W";
            case 18:
                return "E";
            case 19:
                return "R";
            case 20:
                return "T";
            case 21:
                return "Y";
            case 22:
                return "U";
            case 23:
                return "I";
            case 24:
                return "O";
            case 25:
                return "P";
            case 26:
                return "[";
            case 27:
                return "]";
            case 28:
                return "ENTER";
            case 29:
                return "LCONTROL";
            case 30:
                return "A";
            case 31:
                return "S";
            case 32:
                return "D";
            case 33:
                return "F";
            case 34:
                return "G";
            case 35:
                return "H";
            case 36:
                return "J";
            case 37:
                return "K";
            case 38:
                return "L";
            case 39:
                return ";";
            case 40:
                return "'";
            case 41:
                return "`";
            case 42:
                return "LSHIFT";
            case 43:
                return "\\";
            case 44:
                return "Z";
            case 45:
                return "X";
            case 46:
                return "C";
            case 47:
                return "V";
            case 48:
                return "B";
            case 49:
                return "N";
            case 50:
                return "M";
            case 51:
                return ",";
            case 52:
                return ".";
            case 53:
                return "/";
            case 54:
                return "RSHIFT";
            case 56:
                return "LALT";
            case 57:
                return "SPACE";
            case 58:
                return "CAPS";
            case 59:
                return "F1";
            case 60:
                return "F2";
            case 61:
                return "F3";
            case 62:
                return "F4";
            case 63:
                return "F5";
            case 64:
                return "F6";
            case 65:
                return "F7";
            case 66:
                return "F8";
            case 67:
                return "F9";
            case 68:
                return "F10";
            case 87:
                return "F11";
            case 88:
                return "F12";
            case 69:
                return "NUMLOCK";
            case 70:
                return "SCROLL";
            case 71:
                return "NUM7";
            case 72:
                return "NUM8";
            case 73:
                return "NUM9";
            case 74:
                return "-";
            case 75:
                return "NUM4";
            case 76:
                return "NUM5";
            case 77:
                return "NUM6";
            case 78:
                return "+";
            case 79:
                return "NUM1";
            case 80:
                return "NUM2";
            case 81:
                return "NUM3";
            case 82:
                return "NUM0";
            case 83:
                return ".";
            default:
                return "NONE";
        }
    }

    private final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-OR]");

    public String stripColor(final String input) {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

}


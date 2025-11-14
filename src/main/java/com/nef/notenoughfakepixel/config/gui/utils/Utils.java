package com.nef.notenoughfakepixel.config.gui.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.nio.FloatBuffer;
import java.util.LinkedList;

public class Utils {

    private static final LinkedList<Integer> guiScales = new LinkedList<>();
    private static ScaledResolution lastScale = new ScaledResolution(Minecraft.getMinecraft());
    //Labymod compatibility
    private static final FloatBuffer projectionMatrixOld = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer modelviewMatrixOld = BufferUtils.createFloatBuffer(16);

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

    public static void drawTexturedRect(float x, float y, float width, float height) {
        drawTexturedRect(x, y, width, height, 0, 1, 0, 1);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        drawTexturedRect(x, y, width, height, 0, 1, 0, 1, filter);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, GL11.GL_LINEAR);
    }

    public static void resetGuiScale() {
        guiScales.clear();
    }

    public static ScaledResolution peekGuiScale() {
        return lastScale;
    }

    public static ScaledResolution pushGuiScale(int scale) {
        if (guiScales.size() == 0) {
            if (Loader.isModLoaded("labymod")) {
                GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixOld);
                GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrixOld);
            }
        }

        if (scale < 0) {
            if (guiScales.size() > 0) {
                guiScales.pop();
            }
        } else {
            if (scale == 0) {
                guiScales.push(Minecraft.getMinecraft().gameSettings.guiScale);
            } else {
                guiScales.push(scale);
            }
        }

        int newScale = guiScales.size() > 0 ? Math.max(0, Math.min(4, guiScales.peek())) : Minecraft.getMinecraft().gameSettings.guiScale;
        if (newScale == 0) newScale = Minecraft.getMinecraft().gameSettings.guiScale;

        int oldScale = Minecraft.getMinecraft().gameSettings.guiScale;
        Minecraft.getMinecraft().gameSettings.guiScale = newScale;
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft.getMinecraft().gameSettings.guiScale = oldScale;

        if (guiScales.size() > 0) {
            GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        } else {
            if (Loader.isModLoaded("labymod") && projectionMatrixOld.limit() > 0 && modelviewMatrixOld.limit() > 0) {
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
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(str), null);
    }
}

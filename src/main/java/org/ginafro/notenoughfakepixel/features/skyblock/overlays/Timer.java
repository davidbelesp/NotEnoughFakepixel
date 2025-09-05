package org.ginafro.notenoughfakepixel.features.skyblock.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Abstract base class for creating timer overlays on the screen.
 * <p>
 * A {@code Timer} displays an icon and a formatted text (typically a countdown
 * to a goal in epoch milliseconds). The logic for when and how the timer should
 * appear is left to subclasses, allowing configurable overlays for different
 * in-game events (e.g. cooldowns, spawn timers, ability durations).
 *
 * <h2>Core Features</h2>
 * <ul>
 *   <li>Automatically subscribes to
 *       {@link net.minecraftforge.client.event.RenderGameOverlayEvent.Text}
 *       and renders each frame if {@link #shouldShow()} returns {@code true}.</li>
 *   <li>Caches the formatted text per whole second to minimize memory
 *       allocations and improve performance.</li>
 *   <li>Renders an optional icon and text side by side, with configurable
 *       position, padding, scale, and shadow.</li>
 *   <li>Utility methods for computing the overlay’s dimensions
 *       ({@link #getObjectWidth()} / {@link #getObjectHeight()}) which can be
 *       used to clamp the element inside the screen bounds on different
 *       resolutions and aspect ratios.</li>
 * </ul>
 *
 * <h2>Typical Usage</h2>
 * <pre>{@code
 * public class WormSpawnTimer extends Timer {
 *
 *     @Override
 *     public boolean shouldShow() {
 *         return Config.feature.mining.wormTimerCooldown && Timer.getGoalEpochMs() > 0;
 *     }
 *
 *     @Override
 *     public ResourceLocation getIcon() {
 *         return Resources.SCATHA.getResource();
 *     }
 * }
 * }</pre>
 *
 * Subclasses are expected to override:
 * <ul>
 *   <li>{@link #shouldShow()} – logic to control visibility (config, world checks, etc.)</li>
 *   <li>{@link #getIcon()} – resource location of the icon to display (may be {@code null})</li>
 *   <li>{@link #preRender()} – optional hook to update state before rendering</li>
 *   <li>Optionally override {@link #getTextColor(long)},
 *       {@link #formatDelta(long)}, {@link #getScale()}, {@link #getX()},
 *       {@link #getY()}, etc. for customization.</li>
 * </ul>
 */
public abstract class Timer {

    /**
     * Last cached whole-second bucket used to avoid reformatting strings every frame.
     */
    private long lastBucketSec = Long.MIN_VALUE;

    /**
     * Cached text corresponding to {@link #lastBucketSec}.
     */
    private String cachedText = "0:00";

    /**
     * Shared goal epoch timestamp in milliseconds.
     * <p>
     * Represents the absolute target time for this timer.
     */
    private static long goal = 0L;

    /**
     * Forge overlay event listener.
     * <p>
     * Called each frame when the HUD is rendered. Updates the cached text
     * and delegates to {@link #render(RenderGameOverlayEvent.Text)} if
     * {@link #shouldShow()} returns true.
     *
     * @param event the overlay render event
     */
    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (!shouldShow()) return;

        updateCache();
        render(event);
    }

    private void updateCache() {
        long deltaMs = getGoalEpochMs() - System.currentTimeMillis();
        long bucket = deltaMs / 1000L;
        if (bucket != lastBucketSec) {
            lastBucketSec = bucket;
            cachedText = formatDelta(deltaMs);
        }
    }

    /**
     * Renders the timer overlay on the screen.
     * <p>
     * This draws the icon (if provided) and the cached text next to it,
     * applying scale, padding, and shadow options.
     *
     * @param event the render overlay event
     */
    public void render(RenderGameOverlayEvent.Text event) {
        preRender();
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;

        long deltaMs = getGoalEpochMs() - System.currentTimeMillis();
        int color = getTextColor(deltaMs);
        float scale = getScale();

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1f);

        ScaledResolution sr = new ScaledResolution(mc);
        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        int objW = getObjectWidth();
        int objH = getObjectHeight();

        int clampedX = getX();
        int clampedY = getY();
        if (clampedX + objW > screenW) clampedX = Math.max(0, screenW - objW);
        if (clampedY + objH > screenH) clampedY = Math.max(0, screenH - objH);
        if (clampedX < 0) clampedX = 0;
        if (clampedY < 0) clampedY = 0;

        int rx = (int) (clampedX / scale);
        int ry = (int) (clampedY / scale);

        // --- text measuring ---
        int textW = fr.getStringWidth(cachedText);
        int textH = fr.FONT_HEIGHT;

        // --- icon ---
        int texW = getIconTextureWidth();
        int texH = getIconTextureHeight();
        int iconDrawH = textH;
        int iconDrawW = (int) Math.round((texW / (double) texH) * iconDrawH);

        int cursorX = rx;

        ResourceLocation icon = getIcon();
        if (icon != null) {
            mc.getTextureManager().bindTexture(icon);

            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    770, 771, 1, 0);
            GlStateManager.color(1f, 1f, 1f, 1f);

            GlStateManager.enableBlend();
            Gui.drawModalRectWithCustomSizedTexture(
                    cursorX, ry - 1,
                    0f, 0f,
                    iconDrawW, iconDrawH,
                    iconDrawW, iconDrawH
            );

            GlStateManager.color(1f, 1f, 1f, 1f);
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();

            cursorX += iconDrawW + getPadding();
        }

        if (getTextShadow()) {
            fr.drawStringWithShadow(cachedText, cursorX, ry, color);
        } else {
            fr.drawString(cachedText, cursorX, ry, color);
        }

        GlStateManager.popMatrix();
    }

    public int getObjectWidth() {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        int textW = fr.getStringWidth(cachedText);
        int texW = getIconTextureWidth();
        int texH = getIconTextureHeight();
        int textH = fr.FONT_HEIGHT;
        int iconDrawW = (int) Math.round((texW / (double) texH) * textH);
        return (int)(getScale() * (textW + (getIcon() != null ? iconDrawW + getPadding() : 0)));
    }

    public int getObjectHeight() {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        int textH = fr.FONT_HEIGHT;
        return (int)(getScale() * textH);
    }

    // ====================== ABSTRACT / OVERRIDE ======================

    protected int getTextColor(long deltaMs) {
        if (deltaMs < 0) return 0xFFFFFF55;
        if (deltaMs < 5000L) return 0xFF55FF55;
        return 0xFFFFFFFF;
    }

    /**
     * Formats the delta time in ms into a string, by default "mm:ss".
     *
     * @param deltaMs the difference (goal - currentTimeMillis)
     * @return formatted string for display
     */
    protected String formatDelta(long deltaMs) {
        if (deltaMs < 0) deltaMs = 0L;
        long totalSec = deltaMs / 1000L;
        long min = totalSec / 60L;
        long sec = totalSec % 60L;
        return (min < 10 ? "0" + min : String.valueOf(min)) + ":" +
                (sec < 10 ? "0" + sec : String.valueOf(sec));
    }

    // Should the countdown be shown?
    public abstract boolean shouldShow();

    // Runs pre render logic
    public void preRender() {};

    // ResourceLocation of the texture to display as icon
    public abstract ResourceLocation getIcon();
    protected int getIconTextureWidth()  { return 16; }
    protected int getIconTextureHeight() { return 16; }

    // Goal in epoch milliseconds to count to
    public abstract long getGoalEpochMs();

    // ====================== UTILS ======================

    public int getX() { return 0; }
    public int getY() { return 0; }

    public boolean getTextShadow() { return false; }
    public float getScale() { return 1f; }
    public int getPadding() { return 3; }


}

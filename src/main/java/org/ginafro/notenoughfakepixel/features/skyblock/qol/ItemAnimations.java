package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.mixin.accesors.EntityPlayerAccessor;

@RegisterEvents
public class ItemAnimations {

    private static final float PI_F = (float) Math.PI;

    public static final class RenderConstants {
        public static final float VANILLA_PIVOT_X = 0.56f;
        public static final float VANILLA_PIVOT_Y = 0.52f;
        public static final float VANILLA_PIVOT_Z = 0.72f;
        private RenderConstants() {}
    }

    public static Minecraft mc = Minecraft.getMinecraft();

    private static float cachedSizeScale = 1.0f;
    private static float cachedPivotX = RenderConstants.VANILLA_PIVOT_X;
    private static float cachedPivotY = - RenderConstants.VANILLA_PIVOT_Y;
    private static float cachedPivotZ = - RenderConstants.VANILLA_PIVOT_Z;

    private static float lastCustomSize = Float.NaN;
    private static float lastCustomX = Float.NaN;
    private static float lastCustomY = Float.NaN;
    private static float lastCustomZ = Float.NaN;

    private static void recacheIfNeeded() {
        final float cSize = Config.feature.qol.customSize;
        final float cX    = Config.feature.qol.customX;
        final float cY    = Config.feature.qol.customY;
        final float cZ    = Config.feature.qol.customZ;

        if (cSize != lastCustomSize) {
            // era: 0.4f * exp(size)
            cachedSizeScale = (float) (0.4f * Math.exp(cSize));
            lastCustomSize = cSize;
        }
        if (cX != lastCustomX || cY != lastCustomY || cZ != lastCustomZ) {
            // Mantengo tu convención de signos (X: +, Y: -, Z: -)
            cachedPivotX = RenderConstants.VANILLA_PIVOT_X * (1.0f + cX);
            cachedPivotY = -RenderConstants.VANILLA_PIVOT_Y * (1.0f - cY);
            cachedPivotZ = -RenderConstants.VANILLA_PIVOT_Z * (1.0f + cZ);
            lastCustomX = cX;
            lastCustomY = cY;
            lastCustomZ = cZ;
        }
    }

    private static float clamp01(float v) {
        return v < 0f ? 0f : (Math.min(v, 1f));
    }

    /**
     * Transforma el ítem en primera persona (equip + swing + rotaciones personalizadas + escala).
     *
     * @param equipProgress 0..1
     * @param swingProgress 0..1
     */
    public static void processItemAnimation(float equipProgress, float swingProgress) {
        recacheIfNeeded();

        // Custom offsets
        GlStateManager.translate(cachedPivotX, cachedPivotY, cachedPivotZ);

        // Equip animation (vanilla-like)
        GlStateManager.translate(0.0f, -0.6f * equipProgress, 0.0f);

        // Custom Rotations
        GlStateManager.rotate(Config.feature.qol.customPitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(Config.feature.qol.customYaw,   0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Config.feature.qol.customRoll,  0.0f, 0.0f, 1.0f);

        // Vanilla-like yaw
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);

        // Swing (precompute)
        final float swingSin     = MathHelper.sin(swingProgress * swingProgress * PI_F);
        final float swingSqrtSin = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * PI_F);

        GlStateManager.rotate(-20.0f * swingSin,     0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-20.0f * swingSqrtSin, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-80.0f * swingSqrtSin, 1.0f, 0.0f, 0.0f);

        GlStateManager.scale(cachedSizeScale, cachedSizeScale, cachedSizeScale);
    }

    /**
     * Applies a custom swing-based translation to the held item.
     *
     * @param swingProgress Swing animation progress (0.0F - 1.0F).
     */
    public static void changeSwingScale(float swingProgress) {
        recacheIfNeeded();

        // Precompute common
        final float swingSqrt = MathHelper.sqrt_float(swingProgress) * PI_F;
        final float sinSqrt   = MathHelper.sin(swingSqrt);
        final float sinSqrt2  = MathHelper.sin(swingSqrt * 2.0f);
        final float sinLinear = MathHelper.sin(swingProgress * PI_F);

        final float offsetX = -0.4f * sinSqrt  * cachedSizeScale;
        final float offsetY =  0.2f * sinSqrt2 * cachedSizeScale;
        final float offsetZ = -0.2f * sinLinear* cachedSizeScale;

        GlStateManager.translate(offsetX, offsetY, offsetZ);
    }

    /**
     * Applies a vertical "drinking" bobbing effect without rotating the item.
     *
     * @param clientPlayer The player rendering the drink animation.
     * @param partialTicks Partial render ticks for smooth interpolation.
     */
    public static void rotationlessDrink(AbstractClientPlayer clientPlayer, float partialTicks) {
        if (clientPlayer == null || mc.thePlayer == null || mc.thePlayer.getHeldItem() == null) return;

        final EntityPlayerAccessor accessor = (EntityPlayerAccessor) clientPlayer;
        final float useTimeLeft = accessor.getItemInUseCount() - partialTicks + 1.0f;

        final int maxUse = Math.max(1, mc.thePlayer.getHeldItem().getMaxItemUseDuration());
        final float useProgress = clamp01(useTimeLeft / maxUse);

        float yOffset = MathHelper.abs(MathHelper.cos(useTimeLeft * 0.25f * PI_F) * 0.1f);
        if (useProgress >= 0.8f) yOffset = 0.0f;

        GlStateManager.translate(0.0f, yOffset, 0.0f);
    }

    /**
     * Drinking animation con pivot personalizado y sin fugas de transformaciones.
     * Compatible con MC 1.8.9 (MathHelper.sqrt_float).
     *
     * @param clientPlayer jugador (render)
     * @param partialTicks partial ticks
     * @param itemToRender item actualmente en uso (no asumo mc.thePlayer.getHeldItem())
     */
    public static void scaledDrinking(AbstractClientPlayer clientPlayer, float partialTicks, ItemStack itemToRender) {
        if (clientPlayer == null || itemToRender == null) return;
        recacheIfNeeded();

        final EntityPlayerAccessor accessor = (EntityPlayerAccessor) clientPlayer;
        final float useTimeLeft = accessor.getItemInUseCount() - partialTicks + 1.0f;

        final int maxUse = Math.max(1, itemToRender.getMaxItemUseDuration());
        final float useProgress = clamp01(useTimeLeft / maxUse);

        final float wave = MathHelper.cos(useTimeLeft * 0.25f * PI_F);
        float yOffset = MathHelper.abs(wave * 0.1f);
        if (useProgress >= 0.8f) yOffset = 0.0f;

        final float ease = 1.0f - (float) Math.pow(useProgress, 27.0);

        final float pivotX = cachedPivotX - RenderConstants.VANILLA_PIVOT_X;
        final float pivotY = cachedPivotY + RenderConstants.VANILLA_PIVOT_Y;
        final float pivotZ = cachedPivotZ + RenderConstants.VANILLA_PIVOT_Z;

        GlStateManager.pushMatrix();
        GlStateManager.translate(pivotX, pivotY, pivotZ);

        GlStateManager.translate(0.0f, yOffset, 0.0f);
        GlStateManager.translate(ease * 0.6f, ease * -0.5f, 0.0f);
        GlStateManager.rotate(ease * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(ease * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(ease * 30.0f, 0.0f, 0.0f, 1.0f);

        GlStateManager.popMatrix();
    }

    // Flags

    public static boolean shouldChange() {
        return Config.feature.qol.customAnimations;
    }

    public static boolean shouldChangeScaleSwing() {
        return shouldChange() && Config.feature.qol.doesScaleSwing;
    }

    public static boolean shouldRotationlessDrink() {
        return shouldChange() && Config.feature.qol.drinkingSelector == 1;
    }

    public static boolean shouldScaledDrink() {
        return shouldChange() && Config.feature.qol.drinkingSelector == 2;
    }
}

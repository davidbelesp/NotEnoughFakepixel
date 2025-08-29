package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.mixin.accesors.EntityPlayerAccessor;

/**
 * Module to change the appearance of held items.
 * <p>
 * This module uses the EntityLivingBase and ItemRenderer Mixins to function.
 * Because only this module and no others are supposed to modify their behavior direct references are used instead of
 * forge events.
 *
 * @author Aton - THANK YOU
 */
@RegisterEvents
public class ItemAnimations {

    public static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Directly referenced hook for the itemTransform Inject in the ItemRenderer Mixin.
     * Takes care of scaling and positioning the held item.
     */
    public static boolean itemTransformHook(float equipProgress, float swingProgress) {
        if (!Config.feature.qol.customAnimations) return false;

        float newSize = (float) (0.4f * Math.exp(Config.feature.qol.customSize));
        float newX = 0.56f * (1 + Config.feature.qol.customX);
        float newY = -0.52f * (1 - Config.feature.qol.customY);
        float newZ = -0.71999997f * (1 + Config.feature.qol.customZ);

        GlStateManager.translate(newX, newY, newZ);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);

        // Rotation
        GlStateManager.rotate(Config.feature.qol.customPitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(Config.feature.qol.customYaw, 0.0f, 1f, 0f);
        GlStateManager.rotate(Config.feature.qol.customRoll, 0f, 0f, 1f);

        GlStateManager.rotate(45f, 0.0f, 1f, 0f);

        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f1 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f1 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(newSize, newSize, newSize);
        return true;
    }

    /**
     * Directly referenced by the ItemRendererMixin. If enabled will scale the item swing animation.
     * Returns whether custom animation was performed.
     */
    public static boolean scaledSwing(float swingProgress) {
        if (!Config.feature.qol.customAnimations || !Config.feature.qol.doesScaleSwing) return false;

        float scale = (float) Math.exp(Config.feature.qol.customSize);
        float f = -0.4f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI) * scale;
        float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0f) * scale;
        float f2 = -0.2f * MathHelper.sin(swingProgress * (float) Math.PI) * scale;
        GlStateManager.translate(f, f1, f2);
        return true;
    }

    /**
     * Directly referenced by the ItemRendererMixin. If enabled will scale the potion drink animation.
     * Returns whether custom animation was performed.
     */
    public static boolean rotationlessDrink(AbstractClientPlayer clientPlayer, float partialTicks) {
        if (!Config.feature.qol.customAnimations || Config.feature.qol.drinkingSelector != 1) return false;

        EntityPlayerAccessor accessor = (EntityPlayerAccessor) clientPlayer;
        float f = accessor.getItemInUseCount() - partialTicks + 1.0f;
        float f1 = f / mc.thePlayer.getHeldItem().getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0f * 3.1415927f) * 0.1f);

        if (f1 >= 0.8f) {
            f2 = 0.0f;
        }

        GlStateManager.translate(0.0f, f2, 0.0f);
        return true;
    }

    /**
     * Directly referenced by the ItemRendererMixin. If enabled will scale the potion drink animation.
     * Returns whether custom animation was performed.
     */
    public static boolean scaledDrinking(AbstractClientPlayer clientPlayer, float partialTicks, ItemStack itemToRender) {
        if (!Config.feature.qol.customAnimations || Config.feature.qol.drinkingSelector != 2) return false;

        EntityPlayerAccessor accessor = (EntityPlayerAccessor) clientPlayer;
        float f = accessor.getItemInUseCount() - partialTicks + 1.0f;
        float f1 = f / itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0f * (float) Math.PI) * 0.1f);

        if (f1 >= 0.8f) {
            f2 = 0.0f;
        }

        // Transform to correct rotation center
        float newX = 0.56f * (1 + Config.feature.qol.customX);
        float newY = -0.52f * (1 - Config.feature.qol.customY);
        float newZ = -0.71999997f * (1 + Config.feature.qol.customZ);
        GlStateManager.translate(-0.56f, 0.52f, 0.71999997f);
        GlStateManager.translate(newX, newY, newZ);

        GlStateManager.translate(0.0f, f2, 0.0f);
        float f3 = 1.0f - (float) Math.pow(f1, 27.0);
        GlStateManager.translate(f3 * 0.6f, f3 * -0.5f, f3 * 0.0f);
        GlStateManager.rotate(f3 * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f3 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f3 * 30.0f, 0.0f, 0.0f, 1.0f);

        // Transform back
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(-newX, -newY, -newZ);
        return true;
    }
}

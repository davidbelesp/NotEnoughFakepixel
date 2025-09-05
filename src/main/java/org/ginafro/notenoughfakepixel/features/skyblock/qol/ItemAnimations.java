package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.mixin.accesors.EntityPlayerAccessor;

@Deprecated
//@RegisterEvents
public class ItemAnimations {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static boolean itemTransformHook(float equipProgress, float swingProgress) {
        return false;
    }

    public static boolean scaledSwing(float swingProgress) {
       return false;
    }

    public static boolean rotationlessDrink(AbstractClientPlayer clientPlayer, float partialTicks) {
        return false;
    }

    public static boolean scaledDrinking(AbstractClientPlayer clientPlayer, float partialTicks, ItemStack itemToRender) {
       return false;
    }
}

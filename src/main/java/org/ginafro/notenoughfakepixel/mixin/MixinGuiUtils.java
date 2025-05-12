package org.ginafro.notenoughfakepixel.mixin;

import net.minecraftforge.fml.client.config.GuiUtils;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.ScrollableTooltips;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(value = GuiUtils.class, remap = false)
public class MixinGuiUtils {

    @ModifyVariable(at = @At("HEAD"), method = "drawHoveringText")
    private static List<String> onDrawHoveringText(List<String> textLines) {
        return ScrollableTooltips.handleTextLineRendering(textLines);
    }

}

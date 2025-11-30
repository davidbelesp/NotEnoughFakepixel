package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.features.skyblock.qol.ScrollableTooltips;
import net.minecraftforge.fml.client.config.GuiUtils;
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

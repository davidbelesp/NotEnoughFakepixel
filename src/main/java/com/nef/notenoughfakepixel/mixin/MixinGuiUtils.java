package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.features.skyblock.qol.ScrollableTooltips;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiUtils.class, remap = false)
public class MixinGuiUtils {

    @ModifyVariable(
            method = "drawHoveringText",
            at = @At(
                    value = "LOAD",
                    opcode = Opcodes.ILOAD,
                    ordinal = 4
            ),
            index = 10,
            remap = false
    )
    private static int modifyX(int x) {
        return x;
    }

    @ModifyVariable(
            method = "drawHoveringText",
            at = @At(
                    value = "LOAD",
                    opcode = Opcodes.ILOAD,
                    ordinal = 3
            ),
            index = 11,
            remap = false
    )
    private static int modifyY(int y) {
        return y + ScrollableTooltips.scrollOffset;
    }

}

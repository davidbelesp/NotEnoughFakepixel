package org.ginafro.notenoughfakepixel.mixin.accesors;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface BlockAccessor {

    @Accessor
    void setMaxY(double maxY);

}

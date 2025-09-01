package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BlockStainedGlassPane.class})
public abstract class MixinBlockStainedGlassPane extends BlockPane {

    protected MixinBlockStainedGlassPane(Material materialIn, boolean canDrop) {
        super(materialIn, canDrop);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        if (Config.feature.mining.crystalFullBlockPane) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            super.setBlockBoundsBasedOnState(worldIn, pos);
        }
    }

}

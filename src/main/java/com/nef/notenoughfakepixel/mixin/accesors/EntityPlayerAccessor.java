package com.nef.notenoughfakepixel.mixin.accesors;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayer.class)
public interface EntityPlayerAccessor {
    @Accessor("itemInUseCount")
    int getItemInUseCount();
}
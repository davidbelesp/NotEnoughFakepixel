package com.nef.notenoughfakepixel.mixin.accesors;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface SessionAccessor {
    @Accessor("session")
    void setSession(Session session);
}

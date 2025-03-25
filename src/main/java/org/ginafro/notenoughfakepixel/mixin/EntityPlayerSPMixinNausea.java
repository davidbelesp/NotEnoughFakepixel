package org.ginafro.notenoughfakepixel.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixinNausea extends AbstractClientPlayer {

    public EntityPlayerSPMixinNausea(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }
}

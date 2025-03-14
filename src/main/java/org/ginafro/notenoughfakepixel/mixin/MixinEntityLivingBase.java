package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;  // Import your main class
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityLivingBase.class})
public abstract class MixinEntityLivingBase extends MixinEntity {

    @Shadow
    public abstract boolean isPotionActive(Potion potionIn);

    @Shadow
    public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    @Inject(method = "getArmSwingAnimationEnd()I", at = @At("HEAD"), cancellable = true)
    public void adjustSwingLength(CallbackInfoReturnable<Integer> cir) {
        if (!NotEnoughFakepixel.getConfig().customAnimations) return;

        int length = NotEnoughFakepixel.getConfig().ignoreHaste ? 6 :
                this.isPotionActive(Potion.digSpeed) ?
                        6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) :
                        (this.isPotionActive(Potion.digSlowdown) ?
                                6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 :
                                6);

        cir.setReturnValue(Math.max((int) (length * Math.exp(-NotEnoughFakepixel.getConfig().customSpeed)), 1));
    }
}
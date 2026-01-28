package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.features.skyblock.qol.SmoothAote;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @ModifyVariable(method = "setupTerrain", at = @At(value = "STORE"), ordinal = 4)
    public double setupTerrain_d0(double d3) {
        if (SmoothAote.getInstance() == null) return d3;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.x;
        }
        return d3;
    }

    @ModifyVariable(method = "setupTerrain", at = @At(value = "STORE"), ordinal = 5)
    public double setupTerrain_d1(double d4) {
        if (SmoothAote.getInstance() == null) return d4;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.y;
        }
        return d4;
    }

    @ModifyVariable(method = "setupTerrain", at = @At(value = "STORE"), ordinal = 6)
    public double setupTerrain_d2(double d5) {
        if (SmoothAote.getInstance() == null) return d5;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.z;
        }
        return d5;
    }

    //renderEntities
    @ModifyVariable(method = "renderEntities", at = @At(value = "STORE"), ordinal = 3)
    public double renderEntities_d0(double d3) {
        if (SmoothAote.getInstance() == null) return d3;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.x;
        }
        return d3;
    }

    @ModifyVariable(method = "renderEntities", at = @At(value = "STORE"), ordinal = 4)
    public double renderEntities_d1(double d4) {
        if (SmoothAote.getInstance() == null) return d4;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.y;
        }
        return d4;
    }

    @ModifyVariable(method = "renderEntities", at = @At(value = "STORE"), ordinal = 5)
    public double renderEntities_d2(double d5) {
        if (SmoothAote.getInstance() == null) return d5;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.z;
        }
        return d5;
    }

    //drawBlockDamageTexture
    @ModifyVariable(method = "drawBlockDamageTexture", at = @At(value = "STORE"), ordinal = 0)
    public double drawBlockDamageTexture_d0(double d0) {
        if (SmoothAote.getInstance() == null) return d0;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.x;
        }
        return d0;
    }

    @ModifyVariable(method = "drawBlockDamageTexture", at = @At(value = "STORE"), ordinal = 1)
    public double drawBlockDamageTexture_d1(double d1) {
        if (SmoothAote.getInstance() == null) return d1;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.y;
        }
        return d1;
    }

    @ModifyVariable(method = "drawBlockDamageTexture", at = @At(value = "STORE"), ordinal = 2)
    public double drawBlockDamageTexture_d2(double d2) {
        if (SmoothAote.getInstance() == null) return d2;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            return currentPosition.z;
        }
        return d2;
    }

    @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
    private void onDrawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci) {
        if (SmoothAote.getInstance() == null) return;
        Vector3f currentPosition = SmoothAote.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            ci.cancel();
        }
    }

}

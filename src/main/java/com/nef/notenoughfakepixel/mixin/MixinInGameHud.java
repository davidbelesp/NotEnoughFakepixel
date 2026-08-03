package com.nef.notenoughfakepixel.mixin;

import com.nef.notenoughfakepixel.features.skyblock.garden.VacuumPestsBag;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinInGameHud {

    @Inject(method = "renderHotbarItem", at = @At("RETURN"))
    private void renderVacuumPestsBag(int index, int x, int y, float partialTicks,
                                      EntityPlayer player, CallbackInfo ci) {
        VacuumPestsBag.render(player.inventory.getStackInSlot(index), x, y);
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    public void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {

        // TODO custom scoreboard rendering

    }

}

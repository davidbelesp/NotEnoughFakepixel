package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class Fullbright {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final float BRIGHTEST = 1.0f;
    private static final float FULLBRIGHT = 10000.0f;

    @SubscribeEvent
    public void onRender(RenderHandEvent event) {
        if (Config.feature.qol.qolFullbright) changeBrightness(FULLBRIGHT);
        else changeBrightness(BRIGHTEST);
    }

    private void changeBrightness(float toLevel) {
        float moveBy = toLevel - mc.gameSettings.gammaSetting;
        if (moveBy == 0) return;
        mc.gameSettings.gammaSetting += moveBy;
        mc.gameSettings.saveOptions();
    }
}

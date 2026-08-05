package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.Configuration;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class Fullbright {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final float BRIGHTEST = 1.0f;
    // Very large gamma values can overflow the lightmap calculations in Pojav's GLES
    private static final float POJAV_FULLBRIGHT = 15.0f;
    private static final float FULLBRIGHT = 10000.0f;

    @SubscribeEvent
    public void onRender(RenderHandEvent event) {
        if (Config.feature.qol.visualTweaks.qolFullbright) {
            changeBrightness(Configuration.isPojav() ? POJAV_FULLBRIGHT : FULLBRIGHT);
        }
        else changeBrightness(BRIGHTEST);
    }

    private void changeBrightness(float toLevel) {
        float moveBy = toLevel - mc.gameSettings.gammaSetting;
        if (moveBy == 0) return;
        mc.gameSettings.gammaSetting += moveBy;
        mc.gameSettings.saveOptions();
    }
}


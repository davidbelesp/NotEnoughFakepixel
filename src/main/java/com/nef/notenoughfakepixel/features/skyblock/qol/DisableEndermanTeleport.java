package com.nef.notenoughfakepixel.features.skyblock.qol;

import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.variables.Gamemode;

@RegisterEvents
public class DisableEndermanTeleport {

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event) {
        if (ScoreboardUtils.currentGamemode == Gamemode.SKYBLOCK && Config.feature.qol.qolDisableEnderManTeleport) {
            event.setCanceled(true);
        }
    }
}
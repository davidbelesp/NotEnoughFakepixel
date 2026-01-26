package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class DisableEndermanTeleport {

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event) {
        if (SkyblockData.getCurrentGamemode().isSkyblock() && Config.feature.qol.qolDisableEnderManTeleport) {
            event.setCanceled(true);
        }
    }
}
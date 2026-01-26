package com.nef.notenoughfakepixel.events.listeners;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.qol.HidePlayersNearNpcs;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class PlayerListener {

    @SubscribeEvent
    public void onLivingUpdate(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent e) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (!Config.feature.qol.qolHidePlayerNearNpcs) return;
        if (SkyblockData.getCurrentLocation() == Location.PRIVATE_ISLAND || SkyblockData.getCurrentLocation() == Location.DUNGEON) return;

        final Entity entity = e.entity;
        if (!(entity instanceof EntityOtherPlayerMP)) return;

        if (HidePlayersNearNpcs.isNpc(entity)) {
            HidePlayersNearNpcs.trackNpc((EntityOtherPlayerMP) entity);
        }
    }

}

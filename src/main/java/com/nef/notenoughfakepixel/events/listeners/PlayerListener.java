package com.nef.notenoughfakepixel.events.listeners;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.qol.HidePlayersNearNpcs;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.variables.Location;

@RegisterEvents
public class PlayerListener {

    @SubscribeEvent
    public void onLivingUpdate(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent e) {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!Config.feature.qol.qolHidePlayerNearNpcs) return;
        if (TablistParser.currentLocation == Location.PRIVATE_ISLAND || TablistParser.currentLocation == Location.DUNGEON) return;

        final Entity entity = e.entity;
        if (!(entity instanceof EntityOtherPlayerMP)) return;

        if (HidePlayersNearNpcs.isNpc(entity)) {
            HidePlayersNearNpcs.trackNpc((EntityOtherPlayerMP) entity);
        }
    }

}

package org.ginafro.notenoughfakepixel.events.listeners;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.HidePlayersNearNpcs;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;

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

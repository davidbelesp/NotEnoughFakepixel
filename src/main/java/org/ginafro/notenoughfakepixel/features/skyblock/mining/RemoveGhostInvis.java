package org.ginafro.notenoughfakepixel.features.skyblock.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.Location;

import java.util.List;

@RegisterEvents
public class RemoveGhostInvis {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!Config.feature.mining.miningShowGhosts) return;
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (ScoreboardUtils.currentLocation != Location.DWARVEN) return;

        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        List<Entity> entities = Minecraft.getMinecraft().theWorld.loadedEntityList;
        if (entities == null || entities.isEmpty()) return;

        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity == null) continue;
            if (entity instanceof EntityCreeper && entity.isInvisible()) {
                //Removing the invisibility effect from the creeper
                entity.setInvisible(false);
            }
        }
    }

    public static void resetGhostInvis() {
        if (Config.feature.mining.miningShowGhosts) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;

        List<Entity> entities = Minecraft.getMinecraft().theWorld.loadedEntityList;
        if (entities == null || entities.isEmpty()) return;
        entities.forEach(entity -> {
            if (entity instanceof EntityCreeper) {
                entity.setInvisible(true);
            }
        });
    }


}

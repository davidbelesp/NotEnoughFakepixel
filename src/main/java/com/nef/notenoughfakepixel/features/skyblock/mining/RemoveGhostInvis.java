package com.nef.notenoughfakepixel.features.skyblock.mining;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

@RegisterEvents
public class RemoveGhostInvis {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!Config.feature.mining.miningShowGhosts) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (SkyblockData.getCurrentLocation() != Location.DWARVEN) return;

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

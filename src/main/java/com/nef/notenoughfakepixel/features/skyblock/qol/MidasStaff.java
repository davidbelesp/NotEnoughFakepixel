package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.PacketReadEvent;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

@RegisterEvents
public class MidasStaff {

    private int grassSoundCounter = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Config.feature.qol.qolDisableMidaStaffAnimation) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (InventoryUtils.getSlot("Midas Staff") == -1) return;
        AxisAlignedBB bb = Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().expand(20, 20, 20);
        Collection<EntityFallingBlock> entities = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(EntityFallingBlock.class, bb);
        for (EntityFallingBlock entity : entities) {
            if (entity.getBlock().toString().equals("minecraft:gold_block")) {
                if (!entity.isDead) entity.setDead();
            }
        }
    }

    @SubscribeEvent
    public void onParticlePacketReceive(PacketReadEvent event) {
        if (!Config.feature.qol.qolDisableMidaStaffAnimation) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (InventoryUtils.getSlot("Midas Staff") == -1) return;
        Packet packet = event.packet;
        if (packet instanceof S2APacketParticles) {
            S2APacketParticles particle = (S2APacketParticles) packet;
            if (particle.getParticleType().getParticleName().equals("lava")) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onSoundPacketReceive(PacketReadEvent event) {
        if (!Config.feature.qol.qolDisableMidaStaffAnimation) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (InventoryUtils.getSlot("Midas Staff") == -1) return;
        Packet packet = event.packet;
        if (packet instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundEffect = (S29PacketSoundEffect) packet;
            String soundName = soundEffect.getSoundName();
            if (soundName.equals("dig.grass")) {
                grassSoundCounter++;
                if (grassSoundCounter > 1) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void handleClick(PlayerInteractEvent event) {
        if (!Config.feature.qol.qolDisableMidaStaffAnimation) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (InventoryUtils.getSlot("Midas Staff") != -1) {
            if (!(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
                return; // Check if right click on air
            grassSoundCounter = 0;
        }
    }

}

package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.PacketReadEvent;
import org.ginafro.notenoughfakepixel.utils.InventoryUtils;

@RegisterEvents
public class SoundRemover {
    private float lastZombieDeath = -1;

    @SubscribeEvent
    public void onSoundPacketReceive(PacketReadEvent event) {
        Packet packet = event.packet;
        if (packet instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundEffect = (S29PacketSoundEffect) packet;

            switch (soundEffect.getSoundName()) {
                case "mob.villager.yes":
                case "mob.villager.haggle":
                    if (Config.feature.qol.qolDisableJerryChineGunSounds && InventoryUtils.getSlot("Jerry-chine Gun") == InventoryUtils.getCurrentSlot()) {
                        if (event.isCancelable()) event.setCanceled(true);
                    }
                    break;
                case "mob.endermen.portal":
                    if (Config.feature.qol.qolDisableAoteSounds && InventoryUtils.getSlot("Aspect of the End") == InventoryUtils.getCurrentSlot()) {
                        if (event.isCancelable()) event.setCanceled(true);
                    }
                    break;
                case "mob.zombie.death":
                    if (Config.feature.qol.qolDisableZombieRareDrops) {
                        lastZombieDeath = System.currentTimeMillis();
                    }
                    break;
                case "note.pling":
                    if (Config.feature.qol.qolDisableZombieRareDrops) {
                        if (System.currentTimeMillis() - lastZombieDeath < 1000 && (soundEffect.getVolume() == 0.4f || soundEffect.getVolume() == 0.8f)) {
                            if (event.isCancelable()) event.setCanceled(true);
                        }
                    }
            }


        }
    }
}

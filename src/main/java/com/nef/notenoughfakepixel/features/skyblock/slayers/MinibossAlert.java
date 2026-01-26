package com.nef.notenoughfakepixel.features.skyblock.slayers;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.PacketReadEvent;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import com.nef.notenoughfakepixel.utils.TitleUtils;
import com.nef.notenoughfakepixel.variables.Gamemode;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class MinibossAlert {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onReceivePacket(PacketReadEvent event) {
        if (SkyblockData.getCurrentGamemode() != Gamemode.SKYBLOCK) return;
        if (event.packet instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect packet = (S29PacketSoundEffect) event.packet;
            if (packet.getSoundName().equals("random.explode") && packet.getVolume() == 0.6f && packet.getPitch() == 9 / 7f) {
                if (Config.feature.slayer.slayerMinibossTitle) {
                    TitleUtils.showTitle(EnumChatFormatting.RED + "MINIBOSS!", 1000);
                }
                if (Config.feature.slayer.slayerMinibossSound) {
                    SoundUtils.playSound(mc.thePlayer.getPosition(), "random.orb", 1.0F, 1.0F);
                }
            }
        }
    }
}
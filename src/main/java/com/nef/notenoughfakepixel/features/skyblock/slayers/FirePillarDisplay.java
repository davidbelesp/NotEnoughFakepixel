package com.nef.notenoughfakepixel.features.skyblock.slayers;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import com.nef.notenoughfakepixel.utils.TitleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class FirePillarDisplay {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private EntityArmorStand trackedPillar;
    private long lastSoundTime;
    private static final String displayText = "";
    private static final long endTime = 0;

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!Config.feature.slayer.slayerFirePillarDisplay || mc.theWorld == null) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (!SkyblockData.getCurrentLocation().isCrimson()) return;

        if (mc.theWorld.getTotalWorldTime() % 5 != 0) return;

        mc.theWorld.getLoadedEntityList().stream()
                .filter(e -> e instanceof EntityArmorStand)
                .map(e -> (EntityArmorStand) e)
                .forEach(this::processArmorStand);
    }

    private void processArmorStand(EntityArmorStand armorStand) {

        if (armorStand.getDisplayName() == null) return;


        String rawName = armorStand.getDisplayName().getUnformattedText();
        String cleanName = rawName.trim().replaceAll("§.", "");

        String[] parts = cleanName.split(" ");
        if (parts.length != 3) return;
        if (!parts[0].endsWith("s") || !parts[2].equals("hits")) return;


        if (trackedPillar == null || trackedPillar.isDead) {
            trackedPillar = armorStand;
            lastSoundTime = System.currentTimeMillis();
        }


        if (trackedPillar.equals(armorStand)) {
            updatePillarDisplay(cleanName);
        }
    }

    private void updatePillarDisplay(String cleanName) {

        int seconds = Integer.parseInt(cleanName.split(" ")[0].replace("s", ""));


        TitleUtils.showTitle(
                trackedPillar.getDisplayName().getFormattedText(),
                500
        );

        if (System.currentTimeMillis() - lastSoundTime > seconds * 150L) {
            SoundUtils.playSound(
                    mc.thePlayer.getPosition(),
                    "note.pling",
                    1.0F,
                    1.0F
            );
            lastSoundTime = System.currentTimeMillis();
        }
    }
}
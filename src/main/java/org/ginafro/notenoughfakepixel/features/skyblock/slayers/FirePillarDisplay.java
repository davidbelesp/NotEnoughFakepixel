package org.ginafro.notenoughfakepixel.features.skyblock.slayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.SoundUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.utils.TitleUtils;

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
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!TablistParser.currentLocation.isCrimson()) return;

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
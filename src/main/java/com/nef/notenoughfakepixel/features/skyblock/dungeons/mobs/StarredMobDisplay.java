package com.nef.notenoughfakepixel.features.skyblock.dungeons.mobs;

import com.nef.notenoughfakepixel.Configuration;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.RenderEntityModelEvent;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.EntityHighlightUtils;
import com.nef.notenoughfakepixel.utils.OutlineUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.MobDisplayTypes;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@RegisterEvents
public class StarredMobDisplay {

    private static final Pattern PATTERN1 = Pattern.compile("^§.\\[§.Lv\\d+§.\\] §.+ (?:§.)+0§f/.+§c❤$");
    private static final Pattern PATTERN2 = Pattern.compile("^.+ (?:§.)+0§c❤$");
    private static final Pattern PATTERN_RUNIC = Pattern.compile("^§.\\[§.Runic§.\\] §.+ (?:§.)+0§f/.+§c❤$");

    private long lastUpdateTime = 0;

    @Getter
    private final Set<EntityLivingBase> currentEntities = new HashSet<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderEntityModel(RenderEntityModelEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (Config.feature.dungeons.dungeonsStarredMobs == 2) return;
        if (Config.feature.dungeons.dungeonsStarredMobs == 1) {

            final EntityLivingBase entity = event.getEntity();
            if (!currentEntities.contains(entity)) return;
            if (isDying(entity)) return;
            if (entity.isInvisible()) return;

            Color color = new Color(
                    ColorUtils.getColor(Config.feature.dungeons.dungeonsStarredBoxColor).getRGB()
            );

            boolean canSee = Minecraft.getMinecraft().thePlayer.canEntityBeSeen(entity);

            if (!Config.feature.dungeons.dungeonsStarredMobsEsp && !canSee) {
                return;
            }
            if (Configuration.isPojav()) {
                EntityHighlightUtils.renderEntityOutline(event, color);
            } else {
                OutlineUtils.outlineEntity(event, 5.0f, color, true);
            }
        }
    }


    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<EntityLivingBase> event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 20) {
            clearCache();
            lastUpdateTime = currentTime;
        }
        WorldClient world = Minecraft.getMinecraft().theWorld;
        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof EntityArmorStand) {
                EntityArmorStand armorStand = (EntityArmorStand) entity;
                if (armorStand.getName().contains("✮")) {
                    EntityLivingBase mob = findAssociatedMob(armorStand);
                    if (mob != null && !isDying(mob)) {
                        currentEntities.add(mob);
                    }
                }
            }
        }
    }

    private EntityLivingBase findAssociatedMob(EntityArmorStand armorStand) {
        return armorStand.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
                        armorStand.getEntityBoundingBox().expand(0.5, 3.0, 0.5),
                        e -> e != null &&
                                !(e instanceof EntityArmorStand) &&
                                e != Minecraft.getMinecraft().thePlayer
                ).stream()
                .findFirst()
                .orElse(null);
    }

    private boolean isDying(EntityLivingBase entity) {
        if (entity == null || entity.isDead) return true;
        if (entity.getHealth() <= 0.1f) return true;

        IChatComponent displayName = entity.getDisplayName();
        if (displayName == null) return false;

        String name = displayName.getUnformattedText();
        return PATTERN1.matcher(name).matches() ||
                PATTERN2.matcher(name).matches() ||
                PATTERN_RUNIC.matcher(name).matches();
    }

    public void clearCache() {
        currentEntities.clear();
    }

    MobDisplayTypes mobDisplayType = MobDisplayTypes.NONE;

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;

        if (Config.feature.dungeons.dungeonsStarredMobs == 2) return;
        if (Config.feature.dungeons.dungeonsStarredMobs == 0) {


            WorldClient world = Minecraft.getMinecraft().theWorld;

            world.loadedEntityList.forEach(entity -> {
                if (entity == null || entity.getName() == null || !(entity instanceof EntityArmorStand)) return;
                if (!entity.getName().contains("✮")) return;

                // Ensure entity is not dying before rendering
                if (isDying((EntityLivingBase) entity)) return;

                Color color = new Color(
                        ColorUtils.getColor(Config.feature.dungeons.dungeonsStarredBoxColor).getRGB()
                );

                if (entity.getName().contains("Stormy")) {
                    color = new Color(
                            ColorUtils.getColor(Config.feature.dungeons.dungeonsStormyColor).getRGB()
                    );
                } else if (entity.getName().contains("Withermancer")) {
                    color = new Color(
                            ColorUtils.getColor(Config.feature.dungeons.dungeonsWithermancerColor).getRGB()
                    );
                    mobDisplayType = MobDisplayTypes.WITHERMANCER;
                } else if (entity.getName().contains("Zombie Commander")) {
                    color = new Color(
                            ColorUtils.getColor(Config.feature.dungeons.dungeonsZombieCommanderColor).getRGB()
                    );
                } else if (entity.getName().contains("Skeleton Master")) {
                    color = new Color(
                            ColorUtils.getColor(Config.feature.dungeons.dungeonsSkeletonMasterColor).getRGB()
                    );
                } else if (entity.getName().contains("Fels")) {
                    mobDisplayType = MobDisplayTypes.FELALIVE;
                }

                if (Config.feature.dungeons.dungeonsStarredMobsEsp) GlStateManager.disableDepth();
                RenderUtils.renderEntityHitbox(
                        entity,
                        event.partialTicks,
                        color,
                        mobDisplayType
                );
                if (Config.feature.dungeons.dungeonsStarredMobsEsp) GlStateManager.enableDepth();
                mobDisplayType = MobDisplayTypes.NONE;
            });
        }
    }
}

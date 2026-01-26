package com.nef.notenoughfakepixel.features.skyblock.slayers;

import com.nef.notenoughfakepixel.Configuration;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.RenderEntityModelEvent;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.EntityHighlightUtils;
import com.nef.notenoughfakepixel.utils.OutlineUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Constants;
import com.nef.notenoughfakepixel.variables.MobDisplayTypes;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RegisterEvents
public class SlayerMobsDisplay {

    @Getter
    public final Set<EntityLivingBase> slayerEntity = new HashSet<>();
    @Getter
    public final Set<EntityLivingBase> slayerMiniEntity = new HashSet<>();
    private long lastUpdateTime = 0;

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (Config.feature.slayer.slayerBossesOutline) return;
        if (Config.feature.slayer.slayerBosses) onRender(event, true);
        if (Config.feature.slayer.slayerMinibosses) onRender(event, false);
    }

    private void onRender(RenderWorldLastEvent event, boolean isBoss) {
        switch (SkyblockData.getCurrentLocation()) {
            case HUB:
            case PRIVATE_HUB:
                showHitboxHub(event.partialTicks);
                break;
            case PARK:
                showHitbox(MobDisplayTypes.WOLF, event.partialTicks, Constants.SVEN_SLAYER_MINIBOSSES, isBoss);
                break;
            case SPIDERS_DEN:
                showHitbox(MobDisplayTypes.SPIDER, event.partialTicks, Constants.TARANTULA_SLAYER_MINIBOSSES, isBoss);
                break;
            case THE_END:
                showHitbox(MobDisplayTypes.ENDERMAN, event.partialTicks, Constants.VOIDGLOOM_SLAYER_MINIBOSSES, isBoss);
                break;
            case CRIMSON_ISLE:
                showHitbox(MobDisplayTypes.NONE, event.partialTicks, Constants.BLAZE_SLAYER_MINIBOSSES, isBoss);
                break;
        }
    }

    private void showHitboxHub(float partialTicks) {
        Color bossColor = ColorUtils.getColor(Config.feature.slayer.slayerBossColor);
        Color minibossColor = ColorUtils.getColor(Config.feature.slayer.slayerColor);

        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;

        world.loadedEntityList.forEach(entity -> {
            if (entity == null || entity.getName() == null) return;
            if (!(entity instanceof EntityArmorStand)) return;

            // Check for bosses
            for (String name : Constants.SLAYER_BOSSES) {
                if (entity.getName().contains(name)) {
                    MobDisplayTypes type = entity.getName().contains("Sven Packmaster") ? MobDisplayTypes.WOLF : MobDisplayTypes.NONE;
                    RenderUtils.renderEntityHitbox(entity, partialTicks, bossColor, type);
                    return;
                }
            }

            // Check for Sven minibosses
            for (String name : Constants.SVEN_SLAYER_MINIBOSSES) {
                if (entity.getName().contains(name)) {
                    RenderUtils.renderEntityHitbox(entity, partialTicks, minibossColor, MobDisplayTypes.WOLF);
                    return;
                }
            }

            // Check for Revenant minibosses
            for (String name : Constants.REVENANT_SLAYER_MINIBOSSES) {
                if (entity.getName().contains(name)) {
                    RenderUtils.renderEntityHitbox(entity, partialTicks, minibossColor, MobDisplayTypes.NONE);
                    return;
                }
            }
        });
    }

    private void showHitbox(MobDisplayTypes type, float partialTicks, String[] namesList, boolean isBoss) {
        Color color = ColorUtils.getColor(Config.feature.slayer.slayerBossColor);
        WorldClient world = Minecraft.getMinecraft().theWorld;
        world.loadedEntityList.forEach(entity -> {
            if (entity == null || entity.getName() == null) return;
            if (!(entity instanceof EntityArmorStand)) return;

            String[] names = isBoss ? Constants.SLAYER_BOSSES : namesList;
            for (String name : names) {
                if (entity.getName().contains(name)) {
                    RenderUtils.renderEntityHitbox(entity, partialTicks, color, type);
                }
            }
        });
    }

    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!Config.feature.slayer.slayerBossesOutline) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 20) {
            slayerEntity.clear();
            lastUpdateTime = currentTime;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase entity = event.entity;

        if (mc.theWorld == null || entity == null || entity.isInvisible() || entity instanceof EntityPlayer) return;

        List<Entity> armorStands = mc.theWorld.getEntitiesWithinAABB(
                EntityArmorStand.class,
                entity.getEntityBoundingBox().offset(0, 2.0, 0).expand(1.0, 1.0, 1.0)
        );

        for (Entity armorStand : armorStands) {
            if (!(armorStand instanceof EntityArmorStand) || armorStand.getName() == null) continue;

            // Check for bosses
            for (String name : Constants.SLAYER_BOSSES) {
                if (armorStand.getName().contains(name) && Config.feature.slayer.slayerBosses) {
                    slayerEntity.add(entity);
                    return;
                }
            }

            // Check for all minibosses
            for (String[] minibosses : new String[][]{
                    Constants.SVEN_SLAYER_MINIBOSSES,
                    Constants.REVENANT_SLAYER_MINIBOSSES,
                    Constants.TARANTULA_SLAYER_MINIBOSSES,
                    Constants.VOIDGLOOM_SLAYER_MINIBOSSES,
                    Constants.BLAZE_SLAYER_MINIBOSSES
            }) {
                for (String name : minibosses) {
                    if (armorStand.getName().contains(name) && Config.feature.slayer.slayerMinibosses) {
                        slayerMiniEntity.add(entity);
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderEntityModel(RenderEntityModelEvent event) {
        if (!Config.feature.slayer.slayerBossesOutline) return;

        EntityLivingBase entity = event.getEntity();
        if (entity == null || !slayerEntity.contains(entity)) return;

        Color bossColor = ColorUtils.getColor(Config.feature.slayer.slayerBossColor);
        Color minibossColor = ColorUtils.getColor(Config.feature.slayer.slayerColor);
        Color color = minibossColor;

        List<Entity> armorStands = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(
                EntityArmorStand.class,
                entity.getEntityBoundingBox().offset(0, 2.0, 0).expand(1.0, 1.0, 1.0)
        );
        for (Entity armorStand : armorStands) {
            if (!(armorStand instanceof EntityArmorStand) || armorStand.getName() == null) continue;
            for (String name : Constants.SLAYER_BOSSES) {
                if (armorStand.getName().contains(name) && Config.feature.slayer.slayerBosses) {
                    color = bossColor;
                    break;
                }
            }
        }

        if (Configuration.isPojav()) {
            EntityHighlightUtils.renderEntityOutline(event, color);
        } else {
            OutlineUtils.outlineEntity(event, 6.0f, color, true);
        }
    }
}
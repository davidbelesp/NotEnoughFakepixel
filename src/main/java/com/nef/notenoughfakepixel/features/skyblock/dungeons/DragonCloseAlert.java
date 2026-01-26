package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import com.nef.notenoughfakepixel.Configuration;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.RenderEntityModelEvent;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.*;
import com.nef.notenoughfakepixel.variables.DungeonFloor;
import com.nef.notenoughfakepixel.variables.Location;
import com.nef.notenoughfakepixel.variables.Skins;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RegisterEvents
public class DragonCloseAlert {

    @Getter
    public static DragonCloseAlert INSTANCE;

    public DragonCloseAlert() {
        INSTANCE = this;
    }

    @AllArgsConstructor
    @Data
    private static class Orb {
        private final BlockPos pos;
        private final Skins skin;
        private final Color color;
    }

    private static final List<Orb> ORBS = Arrays.asList(
            new Orb(new BlockPos(43,6,64), Skins.RED_RELIC, Color.RED),
            new Orb(new BlockPos(43,6,102), Skins.GREEN_RELIC, Color.GREEN),
            new Orb(new BlockPos(85, 6, 102), Skins.BLUE_RELIC, Color.CYAN),
            new Orb(new BlockPos(85, 6, 64), Skins.ORANGE_RELIC, Color.ORANGE),
            new Orb(new BlockPos(64, 6, 125), Skins.PURPLE_RELIC, Color.PINK)
    );

    private static final Map<EntityDragon, Color> DRAGON_COLOR_MAP = new HashMap<>();
    private static final Map<EntityDragon, String> DRAGON_HEALTH_MAP = new HashMap<>();

    public static final Map<String, Color> DRAGON_COLORS = MapUtils.mapOf(
            new MapUtils.Pair<>("Apex Dragon", Color.GREEN),
            new MapUtils.Pair<>("Flame Dragon", Color.ORANGE),
            new MapUtils.Pair<>("Power Dragon", Color.RED),
            new MapUtils.Pair<>("Soul Dragon", Color.PINK),
            new MapUtils.Pair<>("Ice Dragon", Color.CYAN)
    );

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onUnLoad(WorldEvent.Unload e){
        DRAGON_COLOR_MAP.clear();
        DRAGON_HEALTH_MAP.clear();
    }

    public List<EntityDragon> getDragonsByColor(Color color) {
        return DRAGON_COLOR_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(color))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void registerDragon(EntityDragon dragon, String health) {
        DRAGON_HEALTH_MAP.put(dragon, health);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (SkyblockData.getCurrentLocation() != Location.DUNGEON ||
                SkyblockData.getCurrentFloor() != DungeonFloor.M7 || !M7RelicWaypoints.isFinalPhase) return;

        DRAGON_COLOR_MAP.keySet().removeIf(this::isDying);
        DRAGON_HEALTH_MAP.keySet().removeIf(this::isDying);

        renderBoxes(e);
        renderDragonDistance(e);
    }

    private void renderDragonDistance(RenderWorldLastEvent e) {
        ORBS.forEach(orb -> {
            BlockPos pos = orb.getPos().add(0, 20, 0);

            for (EntityDragon dragon : DRAGON_COLOR_MAP.keySet()) {
                if (isDying(dragon)) continue;

                Color color = DRAGON_COLOR_MAP.get(dragon);
                if (color == null || !color.equals(orb.getColor())) continue;

                // Render health on dragon
                String health = DRAGON_HEALTH_MAP.get(dragon);
                if (health != null && !health.isEmpty()) {
                    RenderUtils.renderWaypointText(health, dragon.getPosition(), e.partialTicks, false);
                }

                // Proximity warning logic
                double distance = new Vec3(pos.getX(), pos.getY(), pos.getZ())
                        .distanceTo(new Vec3(dragon.posX, dragon.posY, dragon.posZ));

                if (distance < 20) {
                    String dragonName = DRAGON_COLORS.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(color))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse("Dragon");

                    //TitleUtils.showTitle(dragonName, 2000);
                    //SoundUtils.playSound(mc.thePlayer.getPosition(), "note.pling", 2.0F, 1.0F);
                }
            }
        });
    }

    private void renderBoxes(RenderWorldLastEvent e) {
        if (!M7RelicWaypoints.isFinalPhase) return;
        drawDragonBox(e);

        if (!Config.feature.dungeons.m7Relics) return;
        ORBS.forEach(orb -> {
            Color color = orb.getColor();
            BlockPos position = orb.getPos().add(0, 20, 0);
            if (orb.getSkin().equals(Skins.BLUE_RELIC) || orb.getSkin().equals(Skins.ORANGE_RELIC)) {
                RenderUtils.renderBoxAtCoords(
                        position.getX() - 21, position.getY() - 11, position.getZ() -11,
                        position.getX() + 11, position.getY() + 11, position.getZ() + 13,
                        e.partialTicks, color, false
                );
            }

            if (orb.getSkin().equals(Skins.GREEN_RELIC) || orb.getSkin().equals(Skins.RED_RELIC)) {
                RenderUtils.renderBoxAtCoords(
                        position.getX() - 11, position.getY() - 11, position.getZ() - 11,
                        position.getX() + 21, position.getY() + 11, position.getZ() + 13,
                        e.partialTicks, color, false
                );
            }

            if (orb.getSkin().equals(Skins.PURPLE_RELIC)) {
                RenderUtils.renderBoxAtCoords(
                        position.getX() - 11, position.getY() - 11, position.getZ() - 21,
                        position.getX() + 13, position.getY() + 11, position.getZ() + 11,
                        e.partialTicks, color, false
                );
            }

        });
    }

    private void drawDragonBox(RenderWorldLastEvent e) {
        if (!Config.feature.dungeons.dragOutline) return;
        mc.theWorld.getLoadedEntityList().forEach(entity -> {
            if (entity instanceof EntityArmorStand) {
                EntityArmorStand stand = (EntityArmorStand) entity;
                ItemStack skull = stand.getCurrentArmor(3);
                if (skull == null || skull.getItem() == null || !skull.getItem().getUnlocalizedName().contains("skull"))
                    return;

                String texture = ItemUtils.getSkullTexture(skull);
                if (texture == null || texture.isEmpty()) return;

                List<Skins> skins = ORBS.stream().map(Orb::getSkin).collect(Collectors.toList());
                Skins skin = Skins.getSkinByValue(texture);
                if (skin == null || !skins.contains(skin)) return;

                Color color = ORBS.stream()
                        .filter(orb -> orb.getSkin().equals(skin))
                        .map(Orb::getColor)
                        .findFirst()
                        .orElse(Color.WHITE);

                EntityLivingBase entityLiving = stand.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
                                stand.getEntityBoundingBox().expand(1.5, 3.0, 1.5),
                                e1 -> e1 instanceof EntityDragon && !e1.isDead && e1 != mc.thePlayer)
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (entityLiving instanceof EntityDragon) {
                    EntityDragon dragon = (EntityDragon) entityLiving;
                    if (isDying(dragon)) return;
                    DRAGON_COLOR_MAP.put(dragon, color);
                }
            }
        });
    }

    @SubscribeEvent
    public void render(RenderEntityModelEvent e) {
        if (!Config.feature.dungeons.dragOutline) return;
        EntityLivingBase entity = e.getEntity();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!(entity instanceof EntityDragon) || entity.isInvisible() || isDying(entity))
            return;

        EntityDragon dragon = (EntityDragon) entity;
        if (dragon.isDead || dragon.getHealth() <= 0.1f) return;

        Color c = DRAGON_COLOR_MAP.get(dragon);
        if (c != null) {
            if (Configuration.isPojav()) {
                EntityHighlightUtils.renderEntityOutline(e, c);
            } else {
                OutlineUtils.outlineEntity(e, 4.0f, c, true);
            }
        }
    }

    private boolean isDying(EntityLivingBase entity) {
        return entity == null || entity.isDead || entity.getHealth() <= 0.1f;
    }
}

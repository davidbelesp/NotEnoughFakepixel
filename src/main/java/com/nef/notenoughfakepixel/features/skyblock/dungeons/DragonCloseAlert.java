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

    private Color getColorFromName(String name) {
        if (name == null || name.length() < 2) return null;
        if (name.startsWith("\u00a7c")) return Color.RED;    // §c Power Dragon
        if (name.startsWith("\u00a7a")) return Color.GREEN;  // §a Apex Dragon
        if (name.startsWith("\u00a7b")) return Color.CYAN;   // §b Ice Dragon
        if (name.startsWith("\u00a76")) return Color.ORANGE; // §6 Flame Dragon
        if (name.startsWith("\u00a7d")) return Color.PINK;   // §d Soul Dragon
        return null;
    }

    private void drawDragonBox(RenderWorldLastEvent e) {
        if (!Config.feature.dungeons.dragOutline) return;
        mc.theWorld.getLoadedEntityList().forEach(entity -> {
            if (!(entity instanceof EntityDragon)) return;
            EntityDragon dragon = (EntityDragon) entity;
            if (isDying(dragon)) return;

            String name = dragon.getDisplayName().getFormattedText();
            Color color = getColorFromName(name);
            if (color == null) return;

            DRAGON_COLOR_MAP.put(dragon, color);
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
            EntityHighlightUtils.renderEntityOutline(e, c);
        }
    }

    private boolean isDying(EntityLivingBase entity) {
        return entity == null || entity.isDead || entity.getHealth() <= 0.1f;
    }
}

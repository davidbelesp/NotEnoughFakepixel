package org.ginafro.notenoughfakepixel.features.skyblock.crimson;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.events.PacketReadEvent;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.SoundUtils;
import org.ginafro.notenoughfakepixel.utils.Waypoint;
import org.ginafro.notenoughfakepixel.variables.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ginafro.notenoughfakepixel.Configuration.*;

public class AshfangHelper {

    private final Waypoint[] waypoints = new Waypoint[2];
    private static int blazingSoulsCounter = 0;
    private static int ashfangFollowersCounter = 0;
    private final Queue<S2APacketParticles> particlesQueue = new ConcurrentLinkedQueue<>();
    private String underling = "§c§cAshfang Underling";
    private String follower = "§c§8Ashfang Follower";
    private String acolyte = "§c§9Ashfang Acolyte";
    private String blazingSoul = "Blazing Soul";
    private static Entity currentAshfang;
    private static Entity currentGravityOrb;
    private Waypoint waypointAshfang;
    private Waypoint waypointGravityOrb;
    private static Pattern ashfangHPPattern = Pattern.compile("([0-9]*[.,]?[0-9]*)([Mk])");
    private String gravityOrbID = "e0614291-7855-32a6-825a-2315725b4cfa";
    private float lastHP = 50_000_000;
    public AshfangHelper() {
        this.waypoints[0] = new Waypoint("ASHFANG",new int[]{-484, 141, -1015});
        this.waypoints[1] = new Waypoint("GRAVITYORB",new int[]{-490, -200, -1015});
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (Crimson.checkEssentials()) return;
        int[] position = new int[] {Minecraft.getMinecraft().thePlayer.getPosition().getX(), Minecraft.getMinecraft().thePlayer.getPosition().getY(), Minecraft.getMinecraft().thePlayer.getPosition().getZ()};
        if (!Crimson.checkAshfangArea(position)) return;
        if (org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangWaypoint || org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonGravityOrbWaypoint) drawWaypoints(event.partialTicks);
        renderEntities(event.partialTicks);
        checkHPForSound();
    }

    @SubscribeEvent
    public void onChat(@NotNull ClientChatReceivedEvent e){
        if (Crimson.checkEssentials()) return;
        if (org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangMuteChat) {
            Matcher matcher = Pattern.compile("can only be damaged by").matcher(e.message.getUnformattedText());
            Matcher matcher2 = Pattern.compile("hit you for").matcher(e.message.getUnformattedText());
            if (matcher.find() || matcher2.find()) {
                e.setCanceled(true);
                return;
            }
        }
        if (org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangMuteChat) {
            Matcher matcher3 = Pattern.compile("The Blazing Soul dealt").matcher(e.message.getUnformattedText());
            if (matcher3.find()) {
                int[] position = new int[]{Minecraft.getMinecraft().thePlayer.getPosition().getX(), Minecraft.getMinecraft().thePlayer.getPosition().getY(), Minecraft.getMinecraft().thePlayer.getPosition().getZ()};
                if (org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangMuteChat) e.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onSoundPacketReceive(PacketReadEvent event) {
        if (Crimson.checkEssentials()) return;
        if (!org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangMuteSound) return;
        Packet packet = event.packet;
        if (packet instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundEffect = (S29PacketSoundEffect) packet;
            String soundName = soundEffect.getSoundName();
            // Remove blaze unhittable sound feature
            if (soundName.equals("mob.bat.hurt")) {
                if (event.isCancelable()) event.setCanceled(true);
            }
        }
    }

    private void renderEntities(float partialTicks) {
        final Color[] newColor = {new Color(255, 0, 0, 100)};
        WorldClient world = Minecraft.getMinecraft().theWorld;
        AtomicInteger blazingSoulCounter = new AtomicInteger();
        AtomicInteger ashfangFollowerCounter = new AtomicInteger();
        boolean gravityFound = false;
        for (int i = 0; i < world.loadedEntityList.size(); i++) {
            Entity entity = world.loadedEntityList.get(i);
            if (entity == null) continue;
            if (entity.getName() == null) continue;
            int[] position = new int[]{entity.getPosition().getX(), entity.getPosition().getY(), entity.getPosition().getZ()};

            String entityName = entity.getName();
            // GRAVITY ORB
            if (entity instanceof EntityArmorStand) {
                if (!Crimson.checkAshfangArea(position)) continue;
                ItemStack it = ((EntityArmorStand) entity).getEquipmentInSlot(4); // Head slot
                if (it != null && it.getItem() == Items.skull) {
                    if (!org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonGravityOrbWaypoint) continue;
                    NBTTagCompound nbt = it.getTagCompound();
                    if(nbt != null && nbt.hasKey("SkullOwner") && nbt.getCompoundTag("SkullOwner").hasKey("Id")) {
                        String id = nbt.getCompoundTag("SkullOwner").getString("Id");
                        if (id.equals(gravityOrbID)) {
                            // Gravity Orb
                            //System.out.println("GRAVITY ORB DETECTED: " + id + ", " + entity.getPosition().getX() + ", " + entity.getPosition().getY() + ", " + entity.getPosition().getZ());
                            waypointGravityOrb = new Waypoint("GRAVITYORB", position);
                            gravityFound = true;
                            if (currentGravityOrb == null) {
                                SoundUtils.playSound(position, "random.pop", 5.0f, 1.5f);
                            } else if (entity.getUniqueID() != currentGravityOrb.getUniqueID()) {
                                SoundUtils.playSound(position, "random.pop", 5.0f, 1.5f);
                            }
                            currentGravityOrb = entity;
                        }
                    }
                } else if (entityName.contains("Ashfang") && !isNameAshfangMinion(entityName) &&
                        (currentAshfang == null || currentAshfang.getUniqueID() != entity.getUniqueID())) {
                    if (!org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangWaypoint) continue;
                    if (!Crimson.checkAshfangArea(position)) continue;
                    waypointAshfang = new Waypoint("ASHFANG", position);
                    //System.out.println("ASHFANG DETECTED: " + entity.getUniqueID() + ", " + entity.getPosition().getX() + ", " + entity.getPosition().getY() + ", " + entity.getPosition().getZ());
                    currentAshfang = entity;
                } else if (isNameAshfangMinion(entityName)) {
                    if (!org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangHitboxes) continue;
                    if (entityName.contains(underling)) {
                        newColor[0] = new Color(255, 0, 0, 150);
                    } else if (entityName.contains(acolyte)) {
                        newColor[0] = new Color(0, 0, 255, 150);
                    } else if (entityName.contains(follower)) {
                        ashfangFollowerCounter.getAndIncrement();
                        newColor[0] = new Color(255, 255, 255, 255); // new Color(96, 96, 96, 150);
                    }
                    RenderUtils.renderEntityHitbox(
                            entity,
                            partialTicks,
                            newColor[0],
                            MobDisplayTypes.BLAZE
                    );
                } else if (entityName.contains(blazingSoul)) {
                    if (!org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangHitboxes) continue;
                    blazingSoulCounter.getAndIncrement();
                    newColor[0] = new Color(255, 255, 0, 255);
                    RenderUtils.renderEntityHitbox(
                            entity,
                            partialTicks,
                            newColor[0],
                            MobDisplayTypes.BLAZINGSOUL
                    );
                }
            }
        }
        blazingSoulsCounter = blazingSoulCounter.get();
        if (!gravityFound) waypointGravityOrb = null;
    }

    private Waypoint[] getWaypoints() {
        return waypoints;
    }

    private boolean isNameAshfangMinion(String name) {
        return name.contains(underling) || name.contains(acolyte) || name.contains(follower);
    }

    private void drawWaypoint (Waypoint waypoint, float partialTicks) {
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        if (waypoint == null) return;
        Color colorDrawWaypoint = new Color(255,255,255);
        int offsetBossY = 0;

        if (waypoint.getType().equals("GRAVITYORB")) colorDrawWaypoint = ColorUtils.getColor(org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonBlazingSoulWaypointColor);
        if (waypoint.getType().equals("ASHFANG")) {
            colorDrawWaypoint = ColorUtils.getColor(org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangWaypointColor);
            offsetBossY = -6;
        }
        //System.out.println("Drawing "+waypoint.getType() + colorDrawWaypoint);
        colorDrawWaypoint = new Color(colorDrawWaypoint.getRed(), colorDrawWaypoint.getGreen(), colorDrawWaypoint.getBlue(), 75);
        AxisAlignedBB bb = new AxisAlignedBB(
                waypoint.getCoordinates()[0] - viewerX,
                waypoint.getCoordinates()[1] - viewerY + offsetBossY,
                waypoint.getCoordinates()[2] - viewerZ,
                waypoint.getCoordinates()[0] + 1 - viewerX,
                waypoint.getCoordinates()[1] + 1 - viewerY + 150,
                waypoint.getCoordinates()[2] + 1 - viewerZ
        ).expand(0.01f, 0.01f, 0.01f);
        //if (waypoint.getType().equals("GRAVITYORB")) GlStateManager.disableCull();
        GlStateManager.disableCull();
        if (waypoint.getType().equals("ASHFANG")) GlStateManager.disableDepth();
        RenderUtils.drawFilledBoundingBox(bb, 1f, colorDrawWaypoint);
        if (waypoint.getType().equals("ASHFANG")) GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
    }

    private void drawWaypoints(float partialTicks){
        if (org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonAshfangWaypoint) {
            if (!BossNotifier.getAshfangScheduled()[0]) {
                drawWaypoint(waypointAshfang, partialTicks);
            }
        }
        if (org.ginafro.notenoughfakepixel.config.features.Crimson.crimsonGravityOrbWaypoint) {
            drawWaypoint(waypointGravityOrb, partialTicks);
        }
    }

    private void checkHPForSound() {
        float currentHP = getAshfangHP();
        if (currentHP < lastHP && currentHP != -1) {
            int[] position = new int[]{Minecraft.getMinecraft().thePlayer.getPosition().getX(), Minecraft.getMinecraft().thePlayer.getPosition().getY(), Minecraft.getMinecraft().thePlayer.getPosition().getZ()};
            SoundUtils.playSound(position, "mob.wither.hurt", 0.7f, 0.7f);
        }
        lastHP = currentHP;
    }

    public static int getBlazingSoulCounter(){
        return blazingSoulsCounter;
    }

    public static int getAshfangFollowersCounter() {
        return ashfangFollowersCounter;
    }

    public static float getAshfangHP() {
        if (currentAshfang == null) return -1.0f;
        Matcher matcher = ashfangHPPattern.matcher(currentAshfang.getName());
        if (matcher.find()) {
            String numberString = matcher.group(1).replace(",", ""); // Eliminar comas
            String ordinalString = matcher.group(2);
            try {
                float number = Float.parseFloat(numberString); // Convertir el número limpio
                if (ordinalString == null) return number;
                if (ordinalString.equals("M")) return number * (float)Math.pow(10, 6);
                if (ordinalString.equals("k")) return number * (float)Math.pow(10, 3);
                return number;
            } catch (NumberFormatException e) {
                System.err.println("Error when converting " + numberString);
                //e.printStackTrace();
                return -1.0f;
            }
        }
        return -1.0f;
    }


    public static int getHitsNeeded() {
        if (currentAshfang == null) return -1;
        return (int)Math.ceil(getAshfangHP()/(2*Math.pow(10,6)));
    }

}

package com.nef.notenoughfakepixel.features.skyblock.slayers;

import com.nef.notenoughfakepixel.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBeacon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.*;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.NefPacketBlockChange;
import com.nef.notenoughfakepixel.variables.Slayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RegisterEvents
public class VoidgloomSeraph {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final Map<Waypoint, Long> waypoints = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == net.minecraftforge.fml.relauncher.Side.CLIENT && !ScoreboardUtils.isSlayerActive){
            clearWaypointsSafe();
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (checkEssentials()
                || !Config.feature.slayer.slayerShowBeaconPath
                || ScoreboardUtils.currentSlayer != Slayer.VOIDGLOOM) {
            return;
        }

        if (!ScoreboardUtils.isSlayerActive) return;
        final long now = System.currentTimeMillis();

        removeExpiredWaypoints(now, 20_000L);

        for (Map.Entry<Waypoint, Long> entry : waypoints.entrySet()) {
            BlockPos waypoint = entry.getKey().getBlockPos();

            if (Config.feature.slayer.showTracerToBeacon) {
                RenderUtils.draw3DLine(
                        new Vec3(waypoint.getX() + .5, waypoint.getY() + .5, waypoint.getZ() + .5),
                        mc.thePlayer.getPositionEyes(event.partialTicks),
                        ColorUtils.getColor(Config.feature.slayer.slayerBeaconColor),
                        8,
                        true,
                        event.partialTicks
                );
            }

            RenderUtils.renderBeaconBeam(
                    waypoint,
                    ColorUtils.getColor(Config.feature.slayer.slayerBeaconColor).getRGB(),
                    1.0f,
                    event.partialTicks
            );
        }

    }

    private static void notifyPlayer() {
        if (Config.feature.slayer.notifyBeaconInScreen
                && mc.thePlayer != null
                && mc.theWorld != null
                && mc.ingameGUI != null
                && mc.ingameGUI.getChatGUI() != null) {
            TitleUtils.showTitle(EnumChatFormatting.RED + "Beacon", 1000);
            SoundUtils.playSound(mc.thePlayer.getPosition(), "note.pling", 1.0f, 1.0f);
        }
    }

    public static void addWaypointSafe(final Waypoint wp) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Runnable r = () -> waypoints.put(wp, System.currentTimeMillis());
        if (mc.isCallingFromMinecraftThread()) r.run();
        else mc.addScheduledTask(r);
    }

    public static void removeExpiredWaypoints(long now, long maxAgeMs) {
        Minecraft mc = Minecraft.getMinecraft();
        Runnable r = () -> waypoints.entrySet().removeIf(e -> (now - e.getValue()) > maxAgeMs);

        if (mc.isCallingFromMinecraftThread()) r.run();
        else mc.addScheduledTask(r);
    }

    public static void removeWaypointSafe(final BlockPos position) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Runnable r = () -> waypoints.entrySet().removeIf(e ->
                sameBlockPos(e.getKey().getBlockPos(), position)
        );
        if (mc.isCallingFromMinecraftThread()) r.run();
        else mc.addScheduledTask(r);
    }

    private static boolean sameBlockPos(BlockPos a, BlockPos b) {
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    public static void clearWaypointsSafe() {
        Minecraft mc = Minecraft.getMinecraft();
        Runnable r = waypoints::clear;

        if (mc.isCallingFromMinecraftThread()) r.run();
        else mc.addScheduledTask(r);
    }

    public static void processBlockChange(NefPacketBlockChange packetIn) {
        if (!Config.feature.slayer.slayerShowBeaconPath) return;
        Block block = packetIn.getBlock();
        BlockPos position = packetIn.getPacket().getBlockPosition();

        // Check if the block is a beacon and add a waypoint
        if (block instanceof BlockBeacon && block.getLocalizedName().contains("Beacon") && ScoreboardUtils.isSlayerActive) {
            EntityPlayerSP player = mc.thePlayer;
            if (player == null || player.getPosition() == null) return;

            double distance = new Vec3(player.getPosition()).distanceTo(new Vec3(position));
            if (distance > 32) return;

            notifyPlayer();
            addWaypointSafe(new Waypoint("BEACON", new int[]{position.getX(), position.getY(), position.getZ()}));
        } else if (block instanceof BlockAir) {
            // If the block is air, it means the beacon was removed
            removeWaypointSafe(position);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        clearWaypointsSafe();
    }

    private static boolean checkEssentials() {
        return (mc.thePlayer == null) ||
                (!ScoreboardUtils.currentGamemode.isSkyblock()) ||
                (!TablistParser.currentLocation.isEnd());
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (Config.feature.slayer.slayerShowBeaconPath && ScoreboardUtils.currentGamemode.isSkyblock() && TablistParser.currentLocation.isEnd()) {
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
            if (message.contains("SLAYER QUEST COMPLETE!") || message.contains("SLAYER QUEST FAILED!")) {
                ScoreboardUtils.isSlayerActive = false;
                clearWaypointsSafe();
            }
        }
    }

}
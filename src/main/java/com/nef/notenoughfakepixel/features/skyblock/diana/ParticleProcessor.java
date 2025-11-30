package com.nef.notenoughfakepixel.features.skyblock.diana;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import com.nef.notenoughfakepixel.utils.Waypoint;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ParticleProcessor {
    private final String waypointSound = "random.pop";
    private final String waypointTreasureSound = "random.pop";
    private final float volumeWaypointSound = 4.0f;
    private final float volumeWaypointTreasureSound = 3.0f;
    private final float distanceThreshold = 2.0f;
    private final int particleThreshold = 6;

    private final Queue<S2APacketParticles> particleEnchantmentTableQueue = new ConcurrentLinkedQueue<>();
    private final Queue<S2APacketParticles> particleCritQueue = new ConcurrentLinkedQueue<>();
    private final Queue<S2APacketParticles> particleMagicCritQueue = new ConcurrentLinkedQueue<>();
    private final Queue<S2APacketParticles> particleDripLavaQueue = new ConcurrentLinkedQueue<>();

    @Getter
    private final List<Waypoint> waypoints = new CopyOnWriteArrayList<>();
    private final Executor particleExecutor = Executors.newSingleThreadExecutor();

    public void addParticle(S2APacketParticles particle) {
        if (particle == null) return;

        switch (particle.getParticleType().getParticleName()) {
            case "crit":
                particleCritQueue.add(particle);
                break;
            case "magicCrit":
                particleMagicCritQueue.add(particle);
                break;
            case "enchantmenttable":
                particleEnchantmentTableQueue.add(particle);
                break;
            case "dripLava":
                particleDripLavaQueue.add(particle);
                break;
            default:
                return;
        }

        particleExecutor.execute(this::processParticles);
    }

    public void processParticles() {
        if (particleEnchantmentTableQueue.isEmpty()) {
            return;
        }

        int[] playerPos = new int[]{
                Minecraft.getMinecraft().thePlayer.getPosition().getX(),
                Minecraft.getMinecraft().thePlayer.getPosition().getY(),
                Minecraft.getMinecraft().thePlayer.getPosition().getZ()
        };
        waypoints.removeIf(waypoint -> !waypoint.getType().equals("MINOS") &&
                !areCoordinatesClose(playerPos, waypoint.getCoordinates(), 64));

        Waypoint result = detectResult();
        if (result == null) return;

        BlockPos block = new BlockPos(result.getCoordinates()[0], result.getCoordinates()[1] - 1, result.getCoordinates()[2]);
        if (!isDuplicateResult(result) && !Minecraft.getMinecraft().theWorld.isAirBlock(block)) {
            waypoints.add(result);

            if (Config.feature.diana.dianaWaypointSounds) {
                if (result.getType().equals("EMPTY") || result.getType().equals("MOB")) {
                    SoundUtils.playSound(result.getCoordinates(), waypointSound, volumeWaypointSound, 2.0f);
                } else if (result.getType().equals("TREASURE")) {
                    SoundUtils.playSound(result.getCoordinates(), waypointTreasureSound, volumeWaypointTreasureSound, 0.5f);
                }
            }
        }

    }

    private Waypoint detectResult() {
        if (particleEnchantmentTableQueue.isEmpty()) return null;

        Queue<S2APacketParticles> largestQueue = getLargestQueue();
        if (largestQueue == null || largestQueue.size() < particleThreshold) return null;

        S2APacketParticles enchParticle = particleEnchantmentTableQueue.poll();
        List<S2APacketParticles> combinedParticles = new ArrayList<>();
        combinedParticles.add(enchParticle);
        particleEnchantmentTableQueue.clear();

        largestQueue.removeIf(particle -> particle == null ||
                !areCoordinatesClose(
                        roundToCoords(particle.getXCoordinate(), particle.getYCoordinate(), particle.getZCoordinate()),
                        roundToCoords(enchParticle.getXCoordinate(), enchParticle.getYCoordinate(), enchParticle.getZCoordinate()),
                        distanceThreshold
                )
        );
        combinedParticles.addAll(largestQueue);
        largestQueue.clear();

        particleCritQueue.clear();
        particleMagicCritQueue.clear();
        particleDripLavaQueue.clear();

        Waypoint waypoint = classifyGroup(combinedParticles);
        return (waypoint != null && isDevPetClose(waypoint.getCoordinates())) ? null : waypoint;
    }

    private Queue<S2APacketParticles> getLargestQueue() {
        return Arrays.asList(particleCritQueue, particleMagicCritQueue, particleDripLavaQueue).stream()
                .max(Comparator.comparingInt(Queue::size))
                .orElse(null);
    }

    private Waypoint classifyGroup(List<S2APacketParticles> group) {
        if (group == null || group.isEmpty()) return null;

        Set<String> groupTypes = new HashSet<>();
        double sumX = 0, sumY = 0, sumZ = 0;

        for (S2APacketParticles particle : group) {
            if (particle == null || particle.getParticleType() == null) continue;
            String particleName = particle.getParticleType().getParticleName();
            if (particleName == null) continue;

            groupTypes.add(particleName);
            sumX += particle.getXCoordinate();
            sumY += particle.getYCoordinate();
            sumZ += particle.getZCoordinate();
        }

        if (groupTypes.isEmpty()) return null;

        int size = group.size();
        int[] avgCoordinates = {
                (int) Math.floor(sumX / size),
                (int) Math.floor(sumY / size),
                (int) Math.floor(sumZ / size)
        };

        if (groupTypes.containsAll(Arrays.asList("magicCrit", "enchantmenttable"))) {
            return new Waypoint("EMPTY", avgCoordinates);
        }
        if (groupTypes.containsAll(Arrays.asList("crit", "enchantmenttable"))) {
            return new Waypoint("MOB", avgCoordinates);
        }
        if (groupTypes.containsAll(Arrays.asList("dripLava", "enchantmenttable"))) {
            return new Waypoint("TREASURE", avgCoordinates);
        }

        return null;
    }

    private boolean isDuplicateResult(Waypoint result) {
        if (result == null) return false;

        return waypoints.stream()
                .filter(Objects::nonNull)
                .anyMatch(waypoint -> areCoordinatesClose(waypoint.getCoordinates(), result.getCoordinates(), distanceThreshold));
    }

    public boolean areCoordinatesClose(int[] coords1, int[] coords2, float threshold) {
        double distance = Math.sqrt(
                Math.pow(coords1[0] - coords2[0], 2) +
                        Math.pow(coords1[1] - coords2[1], 2) +
                        Math.pow(coords1[2] - coords2[2], 2)
        );
        return distance < threshold;
    }

    public static float getDistance(int[] coords1, int[] coords2) {
        return (float) Math.sqrt(
                Math.pow(coords1[0] - coords2[0], 2) +
                        Math.pow(coords1[1] - coords2[1], 2) +
                        Math.pow(coords1[2] - coords2[2], 2)
        );
    }

    private int[] roundToCoords(double x, double y, double z) {
        return new int[]{(int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)};
    }

    public void addWaypoint(Waypoint waypoint) {
        if (waypoint != null) {
            waypoints.add(waypoint);

        }
    }

    public void deleteWaypoint(Waypoint result) {
        waypoints.remove(result);
    }

    public void clearWaypoints() {
        waypoints.clear();
    }

    public Waypoint getClosestWaypoint(int[] coords) {
        return waypoints.stream()
                .min(Comparator.comparing(waypoint -> getDistance(coords, waypoint.getCoordinates())))
                .orElse(null);
    }

    private boolean isDevPetClose(int[] coords) {
        return Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
                .filter(EntityArmorStand.class::isInstance)
                .filter(entity -> entity.getName() != null && entity.getName().contains("Developer's Pet"))
                .map(entity -> new int[]{
                        entity.getPosition().getX(),
                        entity.getPosition().getY(),
                        entity.getPosition().getZ()
                })
                .anyMatch(entityCoords -> getDistance(coords, entityCoords) <= 5);
    }
}
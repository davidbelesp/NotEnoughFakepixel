package com.nef.notenoughfakepixel.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.BlockPos;

import java.util.List;

@Getter
public class Waypoint {
    private final String type;
    private final int[] coordinates;
    @Setter
    private boolean hidden = false;

    public Waypoint(String type, int[] coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public static Waypoint getClosestWaypoint(List<Waypoint> waypoints, int[] coords) {
        if (waypoints.isEmpty()) return null;

        Waypoint closestWaypoint = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Waypoint waypoint : waypoints) {
            double distance = distance(coords, waypoint.getCoordinates());
            if (distance < shortestDistance) {
                shortestDistance = distance;
                closestWaypoint = waypoint;
            }
        }
        return closestWaypoint;
    }

    public static double distance(int[] coords1, int[] coords2) {
        return Math.sqrt(
                Math.pow(coords1[0] - coords2[0], 2) +
                        Math.pow(coords1[1] - coords2[1], 2) +
                        Math.pow(coords1[2] - coords2[2], 2)
        );
    }

    public BlockPos getBlockPos() {
        return new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
    }

    @Override
    public String toString() {
        return String.format("Waypoint{type='%s', coordinates=(%d, %d, %d)}",
                type, coordinates[0], coordinates[1], coordinates[2]
        );
    }
}
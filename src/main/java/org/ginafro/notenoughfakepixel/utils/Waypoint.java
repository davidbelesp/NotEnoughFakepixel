package org.ginafro.notenoughfakepixel.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

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

    public static Waypoint getClosestWaypoint(ArrayList<Waypoint> waypoints, int[] coords) {
        if (waypoints.isEmpty()) return null;
        // Variables to keep track of the closest waypoint and shortest distance
        Waypoint closestWaypoint = null;
        double shortestDistance = Double.MAX_VALUE;

        // Iterate through all waypoints
        for (Waypoint waypoint : waypoints) {
            int[] waypointCoordinates = waypoint.getCoordinates();
            // Calculate the Euclidean distance
            double distance = distance(coords, waypointCoordinates);
            // Check if this waypoint is closer than the current closest
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

    @Override
    public String toString() {
        return String.format("Waypoint{type='%s', coordinates=(%d, %d, %d)}",
                type, coordinates[0], coordinates[1], coordinates[2]
        );
    }
}
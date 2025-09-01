package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ChWaypoint {
    public final double x;
    public final double y;
    public final double z;
    public final String id;
    public final String name;

    public static ChWaypoint of(double x, double y, double z, String name) {
        return new ChWaypoint(x, y, z, UUID.randomUUID().toString(), name);
    }
}

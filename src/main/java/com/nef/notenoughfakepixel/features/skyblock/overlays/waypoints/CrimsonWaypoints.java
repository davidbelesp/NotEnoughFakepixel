package com.nef.notenoughfakepixel.features.skyblock.overlays.waypoints;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.variables.Location;

import java.awt.*;

@RegisterEvents
public class CrimsonWaypoints extends IslandWaypoints {

    private final Color bossColor = new Color(159, 31, 31);

    @Override
    public Waypoint[] getWaypoints() {
        return new Waypoint[] {
                new Waypoint("Barbarian Duke",-535, 117, -902, bossColor),
                new Waypoint("Mage Outlaw",-179, 105, -857, bossColor),
                new Waypoint("Bladesoul",-294, 82, -517, bossColor),

                new Waypoint("Stronghold",-360, 95, -516),
                new Waypoint("Mystic Marsh",-197, 77, -767),
                new Waypoint("Blazing Volcano",-364, 119, -768),
                new Waypoint("Ashfang",-483, 135, -1014),
                new Waypoint("Smoldering",-275, 130, -979),
                new Waypoint("Burning Desert",-506, 91, -774),
                new Waypoint("Dragontail",-622, 199, -817),
                new Waypoint("Scarleton",-78, 107, -803),
                new Waypoint("Dojo",-235, 107, -596),
        };
    }

    @Override
    public boolean shouldShow() {
        return Config.feature.waypoints.crimsonWaypoints;
    }

    @Override
    public boolean renderBeacon() {
        return Config.feature.waypoints.crimsonBeacons;
    }

    @Override
    public Color getWaypointColor() {
        return ColorUtils.getColor(Config.feature.waypoints.crimsonBeaconsColor);
    }

    @Override
    public Location getIslandType() {
        return Location.CRIMSON_ISLE;
    }

}

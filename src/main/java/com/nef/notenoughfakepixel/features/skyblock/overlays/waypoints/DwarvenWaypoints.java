package com.nef.notenoughfakepixel.features.skyblock.overlays.waypoints;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.variables.Location;

import java.awt.*;

@RegisterEvents
public class DwarvenWaypoints extends IslandWaypoints {

    @Override
    public IslandWaypoints.Waypoint[] getWaypoints() {
        return new Waypoint[]{
                new Waypoint("The Lift", -62, 200, -121),
                new Waypoint("Dwarven Vilage", 7, 200, -121),
                new Waypoint("Lava Springs", 57, 207, -9),
                new Waypoint("The Forge", 0, 148, -53),
                new Waypoint("Rampart's Quarry", -77, 153, -10),
                new Waypoint("Far Reserve", -154, 149, -17),
                new Waypoint("Goblin Burrows", -137, 146, 128),
                new Waypoint("The Great Ice Wall", 0, 128, 160),
                new Waypoint("Royal Palace", 129, 195, 176),
                new Waypoint("Royal Mines", 150, 151, 33),
                new Waypoint("Cliffside Veins", 38, 128, 32),
                new Waypoint("Divan's Gateway", 0, 128, 96),
                new Waypoint("Upper Mines", -117, 181, -63),
                new Waypoint("The Mist", -12, 76, 109),
        };
    }

    @Override
    public boolean shouldShow() {
        return Config.feature.waypoints.miningDwarvenWaypoints;
    }

    @Override
    public boolean renderBeacon() {
        return Config.feature.waypoints.miningDwarvenBeacons;
    }

    @Override
    public Color getWaypointColor() {
        return ColorUtils.getColor(Config.feature.waypoints.miningDwarvenBeaconsColor);
    }

    @Override
    public Location getIslandType() {
        return Location.DWARVEN;
    }

}

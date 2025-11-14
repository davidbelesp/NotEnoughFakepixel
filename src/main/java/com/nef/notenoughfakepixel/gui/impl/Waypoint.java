package com.nef.notenoughfakepixel.gui.impl;

import lombok.Getter;
import net.minecraft.world.World;

public class Waypoint {

    private static double x, y, z;
    @Getter
    private static World world;

    public Waypoint(double x1, double y1, double z1, World w) {
        x = x1;
        y = y1;
        z = z1;
        world = w;
    }

    public static double[] getPos() {
        return new double[]{x, y, z};
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

}

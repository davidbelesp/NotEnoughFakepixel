package com.nef.notenoughfakepixel.features.skyblock.crimson;

import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.client.Minecraft;

public class Crimson {

    private static final int[][] ashfangArea = new int[][]{{-510, 100, -1040}, {-450, 200, -990}};

    public static boolean checkEssentials() {
        return (Minecraft.getMinecraft().thePlayer == null) ||
                (!SkyblockData.getCurrentGamemode().isSkyblock()) ||
                (!SkyblockData.getCurrentLocation().isCrimson());
    }

    public static boolean checkAshfangArea(int[] coords) {
        for (int i = 0; i < coords.length; i++) {
            if (coords[i] < ashfangArea[0][i] || coords[i] > ashfangArea[1][i]) {
                return false;
            }
        }
        return true;
    }
}

package org.ginafro.notenoughfakepixel.features.skyblock.crimson;

import net.minecraft.client.Minecraft;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;

public class Crimson {

    private static final int[][] ashfangArea = new int[][]{{-510, 100, -1040}, {-450, 200, -990}};

    public static boolean checkEssentials() {
        return (Minecraft.getMinecraft().thePlayer == null) ||
                (!ScoreboardUtils.currentGamemode.isSkyblock()) ||
                (!TablistParser.currentLocation.isCrimson());
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

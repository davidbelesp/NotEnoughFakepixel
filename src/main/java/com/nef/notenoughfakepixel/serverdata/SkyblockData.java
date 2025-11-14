package com.nef.notenoughfakepixel.serverdata;

import lombok.Getter;
import lombok.Setter;
import com.nef.notenoughfakepixel.variables.Gamemode;
import com.nef.notenoughfakepixel.variables.Location;

public class SkyblockData {

    // Profile data
    @Getter @Setter private static String currentProfile = null; //todo: implement profile detection
    @Getter @Setter private static boolean isSlayerActive = false; //todo: implement slayer detection
    // Location data
    @Getter @Setter private static Location currentLocation = null; //todo: implement location detection
    @Getter @Setter private static Gamemode currentGamemode = null; //todo: implement gamemode detection
    // Time data
    @Getter @Setter private static int sbHour = 0;
    @Getter @Setter private static int sbMinute = 0;
    @Getter @Setter private static boolean am = false;
    @Getter @Setter private static Season season = Season.SPRING;

    private SkyblockData() {}

    // Data models

    public enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }

}

package org.ginafro.notenoughfakepixel.serverdata;

import lombok.Getter;
import lombok.Setter;

public class SkyblockData {

    @Getter @Setter private static String currentProfile = null;
    @Getter @Setter private static int sbHour = 0;
    @Getter @Setter private static int sbMinute = 0;
    @Getter @Setter private static boolean am = false;
    @Getter @Setter private static Season season = Season.SPRING;

    private SkyblockData() {}

    public enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }

}

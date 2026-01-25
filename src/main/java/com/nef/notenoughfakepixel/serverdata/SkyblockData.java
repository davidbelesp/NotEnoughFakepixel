package com.nef.notenoughfakepixel.serverdata;

import com.nef.notenoughfakepixel.variables.*;
import lombok.Getter;
import lombok.Setter;

public class SkyblockData {

    // Profile data
    @Getter @Setter private static String currentProfile = null; //todo: implement profile detection
    // Slayer/Boss data
    @Getter @Setter private static boolean isSlayerActive = false;
    @Getter @Setter private static boolean isBossActive = false;
    @Getter private static Slayer currentSlayer = Slayer.NONE;
    public static void setCurrentSlayer(Slayer newSlayer) {
        if (currentSlayer == newSlayer) return;
        currentSlayer = newSlayer;
        resetSlayerData();
    }
    @Getter @Setter private static int slayerLevel = 0;
    @Getter @Setter private static int slayerXp = 0;
    @Getter @Setter private static int nextLevelXp = 0;
    @Getter @Setter private static int xpToNextLevel = 0;
    @Getter @Setter private static int sessionBosses = 0;
    @Getter @Setter private static float RNGesusMeter = 0;
    @Getter @Setter private static double totalSeconds = 0;
    // Location data
    @Getter @Setter private static Location currentLocation = Location.NONE;
    @Getter @Setter private static Gamemode currentGamemode = Gamemode.LOBBY;
    @Getter @Setter private static Area currentArea = Area.NONE;
    // Time data
    @Getter @Setter private static int sbHour = 0;
    @Getter @Setter private static int sbMinute = 0;
    @Getter @Setter private static boolean am = false;
    @Getter @Setter private static Season season = Season.SPRING;
    // Mining data
    @Getter @Setter private static int mithrilPowder = 0;
    @Getter @Setter private static int gemstonePowder = 0;
    @Getter @Setter private static int heat = 0;
    // Dungeon data
    @Getter @Setter private static DungeonFloor currentFloor = DungeonFloor.NONE;
    @Getter @Setter private static int clearedPercentage = -1;


    private SkyblockData() {}

    // Data models

    public enum Season {
        NONE,
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER;

        public static Season getByName(String name) {
            for (Season season : Season.values()) {
                if (season.name().equals(name.toUpperCase())) {
                    return season;
                }
            }

            return NONE;
        }
    }

    public static boolean isSkyblock() {
        return currentGamemode.isSkyblock();
    }

    public static void resetSlayerData() {
        setSlayerLevel(0);
        setSlayerXp(0);
        setNextLevelXp(0);
        setXpToNextLevel(0);
        setSessionBosses(0);
        setRNGesusMeter(0);
        setTotalSeconds(0);
    }

}

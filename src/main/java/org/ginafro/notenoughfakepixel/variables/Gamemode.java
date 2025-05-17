package org.ginafro.notenoughfakepixel.variables;

public enum Gamemode {

    LOBBY("FAKEPIXEL"),
    SKYWARS("SKYWARS"),
    BEDWARS("BED WARS"),
    SKYBLOCK("SKYBLOCK"),
    MLF("MY LITTLE FARM"),
    MURDERMYSTERY("MURDER MYSTERY"),
    DUELS("DUELS"),
    CATACOMBS("CATACOMB");

    private final String name;

    Gamemode(String name) {
        this.name = name;
    }

    public static Gamemode getGamemode(String input) {
        return java.util.Arrays.stream(values())
                .filter(gm -> input.contains(gm.name))
                .findFirst()
                .orElse(LOBBY);
    }

    public boolean isSkyblock() {
        return this == SKYBLOCK;
    }
}

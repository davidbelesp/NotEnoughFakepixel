package com.nef.notenoughfakepixel.variables;

import java.util.Arrays;

public enum Area {
    NONE(""),
    VILLAGE("Village"),
    CATACOMBS("The Catacombs"),
    SPRUCE("Spruce Woods"),
    DARK("Dark Thicket"),
    SAVANNA("Savanna Woodlands"),
    JUNGLE("Jungle Island"),
    BAZAAR("Bazaar Alley"),
    AUCTION("Auction House"),
    GRAVEYARD("Graveyard"),
    FOREST("Forest"),
    MOUNTAIN("Mountain"),
    HIGH_LEVEL("High Level"),
    WILDERNESS("Wilderness"),
    FARM("Farm"),
    COAL("Coal Mine"),
    BARN("The Barn"),
    DESERT("Desert Settlement"),
    OASIS("Oasis"),
    MUSHROOM("Mushroom Desert"),
    END("The End"),
    DRAGON("Dragon Nest"),
    VOID("Void Sculpture"),
    GOLD("Gold Mine"),
    DEEP("Deep Caverns"),
    IRON("Gunpowder Mines"),
    LAPIS("Lapis Quarry"),
    ISLAND("Private Island"),
    REDSTONE("Pigmen's Den"),
    EMERALD("Slimehill"),
    DIAMOND("Diamond Reserve"),
    OBSIDIAN("Obsiddian Sanctuary"),
    DWARVEN("Dwarven Village"),
    DWARVEN_MINES("Dwarven Mines"),
    BRIDGE("Palace Bridge"),
    PALACE("Royal Palace"),
    ICE_WALL("Great Ice Wall"),
    DIVAN("Divan's Gateway"),
    CLIFFSIDE("Cliffside Veins"),
    RAMPART("Rampart's Quarry"),
    UPPER("Upper Mines"),
    FORGE("Forge Basin"),
    GATE("Gates to the Mines"),
    THE_FORGE("The Forge"),
    DUNGEON_HUB("Dungeon Hub"),
    CRIMSON("Crimson Isle"),
    STRONGHOLD("Strongholdd"),
    CRIMSON_FIELDS("Crimson Fields"),
    BURNING_DESERT("Burning Desert"),
    DRAGONTAIL("Dragontail"),
    ASHFANG("Ruins of Ashfang"),
    WASTELAND("The Wasteland"),
    MARSH("Mythic Marsh"),
    SCARELTON("Scarleton"),
    VOLCANO("Blazing Volcano"),
    SPIDER("Spider's Den"),
    BIRCH_PARK("Birch Park"),

    CH_JUNGLE("Jungle"),
    CH_NUCLEUS("Crystal Nucleus"),
    CH_GOBLIN("Goblin Holdout"),
    CH_MITHRIL("Mithril Deposits"),
    CH_PRECURSOR("Precursor Remnants"),
    CH_MAGMA("Magma Fields"),

    CH_MINES_OF_DIVAN("Mines of Divan"),
    CH_JUNGLE_TEMPLE("Jungle Temple"),
    CH_GOBLIN_QUEEN("Goblin Queen's Den"),
    CH_LOST_PRECURSOR("Lost Precursor City"),

    ;

    private final String s;

    Area(String s) {
        this.s = s;
    }

    public String getArea() {
        return s;
    }

    public static Area getArea(String s) {
        return Arrays.stream(Area.values())
                .filter(l -> l.getArea().toLowerCase().contains(s.toLowerCase()))
                .findFirst()
                .orElse(NONE);
    }

    public static boolean locationExists(String s) {
        return Arrays.stream(Area.values())
                .anyMatch(l -> s.equals(l.getArea()));
    }

}


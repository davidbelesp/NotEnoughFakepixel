package com.nef.notenoughfakepixel.variables;

import java.util.Locale;

public enum Rarity {

    NONE("None"),
    COMMON("Common"),
    UNCOMMON("Uncommon"),
    RARE("Rare"),
    EPIC("Epic"),
    LEGENDARY("Legendary"),
    MYTHIC("Mythic"),
    DIVINE("Divine");

    public final String rarity;

    Rarity(String rarity) {
        this.rarity = rarity;
    }

    public static Rarity fromString(String text) {
        if (text == null) return NONE;

        String clean = text.trim().toUpperCase(Locale.ROOT);

        for (Rarity r : Rarity.values()) {
            if (clean.startsWith(r.rarity.toUpperCase(Locale.ROOT))) {
                return r;
            }
        }
        return NONE;
    }

    public static String getColor(Rarity rarity) {
        String color = "§7";
        switch (rarity) {
            case COMMON: color = "§f"; break;
            case UNCOMMON: color ="§a"; break;
            case RARE: color = "§9"; break;
            case EPIC: color = "§5"; break;
            case LEGENDARY: color = "§6"; break;
            case MYTHIC: color = "§d"; break;
            case DIVINE: color = "§b"; break;
            default: color = "§7"; break;
        }
        return color;
    }

}

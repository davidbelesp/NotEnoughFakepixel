package com.nef.notenoughfakepixel.variables;

import com.nef.notenoughfakepixel.utils.ListUtils;

import java.util.Arrays;
import java.util.List;

public enum StackingEnchant {
    NONE("", new Integer[]{}),
    CULTIVATING("CULTIVATING", new Integer[]{
        1000,
        4000,
        20000,
        75000,
        200000,
        1200000,
        3500000,
        15000000,
        80000000
    }),
    COMPACT("COMPACT", new Integer[]{
        100,
        400,
        1000,
        3500,
        10000,
        35000,
        100000,
        350000,
        500000
    }),
    EXPERTISE("EXPERTISE", new Integer[]{
        50,
        50,
        150,
        250,
        500,
        1500,
        3000,
        4500,
        5000
    }),
    CHAMPION("CHAMPION", new Integer[]{
        50000,
        50000,
        150000,
        250000,
        500000,
        500000,
        500000,
        500000,
        500000
    }),
    TOXOPHILITE("TOXOPHILITE", new Integer[]{
        50000,
        50000,
        150000,
        250000,
        500000,
        500000,
        500000,
        500000,
        500000
    });

    private final String name;
    private final List<Integer> levelTable;

    StackingEnchant(String name, Integer[] levelTable) {
        this.name = name;
        this.levelTable = ListUtils.of(levelTable);
    }

    public static Integer getNextLevel(int currentCounter, StackingEnchant enchant) {
        for (Integer level : enchant.levelTable) {
            if (currentCounter < level) {
                return level;
            }
        }
        return 0;
    }

    public static StackingEnchant fromString(String name) {
        return Arrays.stream(StackingEnchant.values())
                .filter(enchant -> enchant.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(NONE);
    }
}
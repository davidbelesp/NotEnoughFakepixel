package com.nef.notenoughfakepixel.variables;

import lombok.Getter;

@Getter
public enum Slayer {

    NONE,
    VOIDGLOOM("Voidgloom", new int[]{10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000}),
    TARANTULA("Tarantula", new int[]{5, 25, 200, 1000, 5000, 20000, 100000, 400000, 1000000}),
    SVEN("Sven", new int[]{10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000}),
    REVENANT("Revenant", new int[]{5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000}),
    INFERNO("Inferno", new int[]{10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000});

    final String name;
    final int[] xpTable;

    Slayer(String name, int[] xpTable) {
        this.name = name;
        this.xpTable = xpTable;
    }

    Slayer() {
        this.name = "NONE";
        this.xpTable = new int[0];
    }

    public int getNextLevelXp(int currentLevel) {
        int[] xpTable = this.getXpTable();
        return xpTable[currentLevel];
    }

    public int getAccumulatedXp(int level, int xpTowardsNext) {
        int[] xpTable = this.getXpTable();
        if(level > 8) return xpTable[xpTable.length - 1];

        int nextLevelXp = xpTable[level];
        return nextLevelXp - xpTowardsNext;
    }

}

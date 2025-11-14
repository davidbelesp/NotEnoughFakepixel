package com.nef.notenoughfakepixel.variables;

import java.util.Arrays;

public enum DungeonFloor {
    NONE(-1, -1),
    E0(20, -120),
    F0(20, -120),
    F1(30, -120),
    F2(40, -120),
    F3(50, -120),
    F4(60, -240),
    F5(70, -120),
    F6(85, -240),
    F7(100, -360),
    M1(30, 0),
    M2(40, 0),
    M3(50, 0),
    M4(60, 0),
    M5(70, 0),
    M6(85, -120),
    M7(100, -360);

    private final int secretPercentage;
    private final int t;

    DungeonFloor(int secretPercentage, int t) {
        this.secretPercentage = secretPercentage;
        this.t = t;
    }

    public int getSecretPercentage() {
        return secretPercentage;
    }

    public int getT() {
        return t;
    }

    public static DungeonFloor getFloor(String fromValue) {
        return Arrays.stream(DungeonFloor.values())
                .filter(floor -> floor.name().equals(fromValue))
                .findFirst()
                .orElse(DungeonFloor.NONE);
    }

}


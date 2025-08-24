package org.ginafro.notenoughfakepixel.variables;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Location {

    DWARVEN("sbm-", "sbm_sandbox-", "sbm_test-"),
    HUB("skyblock-", "skyblock_sandbox-", "skyblocktest-"),
    PRIVATE_HUB("skyblock_private-", "none", "none"),
    DUNGEON_HUB("sbdh-", "sbdh_sandbox-", "sbdh_test-"),
    BARN("sbfarms-", "sbfarms_sandbox-", "sbfarms_test"),
    PARK("sbpark-", "sbpark_sandbox-", "sbpark_test-"),
    GOLD_MINE("sbmines-", "sbmines_sandbox-", "sbmines_test-"),
    PRIVATE_ISLAND("sbi-", "sbi_sandbox-", "sbi_test-"),
    JERRY("sbj-", "sbj_sandbox-", "sbj_test-"),
    SPIDERS_DEN("sbspiders-", "sbspiders_sandbox-", "sbspiders_test-"),
    THE_END("sbend-", "sbend_sandbox-", "sbend_test-"),
    CRIMSON_ISLE("sbcris-", "sbcris_sandbox-", "sbcris_test-"),
    DUNGEON("sbdungeon-", "sbdungeon_sandbox-", "sbdungeon_test-"),
    NONE("", "", "");

    private final String main;
    private final String sandbox;
    private final String alpha;

    public static Location getLocation(String s) {
        String unformatted = StringUtils.stripControlCodes(s);
        return java.util.Arrays.stream(Location.values())
                .filter(l -> l.getMain().equals(unformatted) || l.getSandbox().equals(unformatted) || l.getAlpha().equals(unformatted))
                .findFirst()
                .orElse(NONE);
    }

    public boolean isDungeon() {
        return this == DUNGEON;
    }

    public boolean isHub() {
        return this == HUB || this == PRIVATE_HUB;
    }

    public boolean isCrimson() {
        return this == CRIMSON_ISLE;
    }

    public boolean isEnd() {
        return this == THE_END;
    }

}

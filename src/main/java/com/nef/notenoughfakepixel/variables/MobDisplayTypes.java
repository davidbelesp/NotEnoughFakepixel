package com.nef.notenoughfakepixel.variables;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MobDisplayTypes {

    BAT(-0.3, -0.3, -0.3, 0.3, 0.3, 0.3),
    FEL(-0.5, -0.5, -0.5, 0.5, 3.0, 0.5),
    FELALIVE(-0.5, -2.0, -0.5, 0.5, 1.0, 0.5),
    WOLF(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5),
    WOLF_BOSS(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5),
    SPIDER_BOSS(-0.75, -1.0, -0.75, 0.75, 0.0, 0.75),
    SPIDER(-0.75, 0.0, -0.75, 0.75, 1.0, 0.75),
    ENDERMAN_BOSS(-0.5, -2.75, -0.5, 0.5, 0.0, 0.5),
    ENDERMAN(-0.5, -2.0, -0.5, 0.5, 1.0, 0.5),
    GAIA(-0.75, 1.0, -0.75, 0.75, 4.0, 0.75),
    SIAMESE(-0.3, 0.0, -0.3, 0.3, 0.7, 0.3),
    BLAZE(-0.3, -1.0, -0.3, 0.3, 0.8, 0.3),
    BLAZINGSOUL(-0.3, 0.2, -0.3, 0.3, 0.785, 0.3),
    ITEM(-0.125, 1.0, -0.125, 0.125, 1.25, 0.125),
    ITEMBIG(-0.5, 1.0, -0.5, 0.5, 2.0, 0.5),
    WITHERESSENCE(-0.3, 2.5, -0.3, 0.3, 3.0, 0.3),
    WITHERMANCER(-0.5, -1.6, -0.5, 0.5, 0.8, 0.5),
    WITHER(-0.55, 0.6, -0.55, 0.55, 2.6, 0.55),
    M7ORBS(-.5, 1.5, -.5, .5, 2, .5),
    AUTOMATON(-0.75, -2.0, -0.75, 0.75, 1.0, 0.75),
    NONE(-0.5, -1.0, -0.5, 0.5, 1.0, 0.5);

    final double x1;
    final double y1;
    final double z1;
    final double x2;
    final double y2;
    final double z2;

}

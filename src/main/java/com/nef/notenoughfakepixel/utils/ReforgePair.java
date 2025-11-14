package com.nef.notenoughfakepixel.utils;

import lombok.Getter;

public class ReforgePair {

    @Getter
    private final float x;
    @Getter
    private final float y;
    private final String displayReforge;


    public ReforgePair(float x, float y, String displayReforge) {
        this.x = x;
        this.y = y;
        this.displayReforge = displayReforge;
    }

    public String getEnchant() {
        return displayReforge;
    }
}

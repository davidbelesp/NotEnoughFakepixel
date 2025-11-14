package com.nef.notenoughfakepixel.features.skyblock.overlays.inventory.invbuttons;

import net.minecraft.util.ResourceLocation;

public enum InvStyle {

    VANILLA(new ResourceLocation("notenoughfakepixel", "invbuttons/0.png"), 0),
    DARK(new ResourceLocation("notenoughfakepixel", "invbuttons/1.png"), 1),
    TRANSPARENT(new ResourceLocation("notenoughfakepixel", "invbuttons/2.png"), 2),
    OUTLINE(new ResourceLocation("notenoughfakepixel", "invbuttons/3.png"), 3),
    FURFSKY(new ResourceLocation("notenoughfakepixel", "invbuttons/4.png"), 4);

    public ResourceLocation tex;
    public int id;

    InvStyle(ResourceLocation location, int i) {
        tex = location;
        id = i;
    }

    public static InvStyle getStyle(int i) {
        for (InvStyle s : values()) {
            if (s.id == i) {
                return s;
            }
        }
        System.out.println("Error on id " + i);
        return VANILLA;
    }
}

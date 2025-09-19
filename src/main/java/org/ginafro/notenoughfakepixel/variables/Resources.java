package org.ginafro.notenoughfakepixel.variables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;

@Getter
@AllArgsConstructor
public enum Resources {

    BEACON(new ResourceLocation("textures/entity/beacon_beam.png")),
    MAP_ICONS(new ResourceLocation("textures/map/map_icons.png")),

    ITEM_LOCK(new ResourceLocation("notenoughfakepixel", "invbuttons/editor.png")),

    LOCK(new ResourceLocation("notenoughfakepixel:slotlocking/lock.png")),
    BOUND(new ResourceLocation("notenoughfakepixel:slotlocking/bound.png")),
    RARITY_TEXTURE(new ResourceLocation("notenoughfakepixel:skyblock/textures/gui/rarity.png")),

    DARK_AH(new ResourceLocation("notenoughfakepixel:skyblock/dark_ah.png")),
    EGG_HUNT(new ResourceLocation("notenoughfakepixel:skyblock/egg_hunt.png")),

    CRYSTAL_MAP_POINT(new ResourceLocation("notenoughfakepixel:crystalhollows/map_point.png")),
    CRYSTAL_MAP_ARROW(new ResourceLocation("notenoughfakepixel:crystalhollows/map_arrow.png")),

    CRYSTAL_MAP_ZONES(new ResourceLocation("notenoughfakepixel:crystalhollows/map.png")),
    CRYSTAL_MAP_GEMS( new ResourceLocation("notenoughfakepixel:crystalhollows/map_gems.png")),

    SCATHA(new ResourceLocation("notenoughfakepixel:crystalhollows/pets_scatha.png")),
    ;

    private final ResourceLocation resource;

}

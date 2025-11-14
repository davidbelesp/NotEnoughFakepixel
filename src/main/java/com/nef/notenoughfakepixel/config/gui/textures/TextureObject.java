package com.nef.notenoughfakepixel.config.gui.textures;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class TextureObject {

    public String displayName;

    public TextureObject(String displayName) {
        this.displayName = displayName;
    }

    public static TextureObject decode(JsonObject json) {
        TextureObject textureObject = new TextureObject(json.get("displayName").getAsString());
        Arrays
                .stream(textureObject.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(ResourceLocation.class))
                .forEach(field -> {
                    try {
                        field.set(textureObject, new ResourceLocation(json.get(field.getName()).getAsString()));
                    } catch (Exception ignored) {
                    }
                });
        return textureObject;
    }

    private static ResourceLocation resource(String path) {
        return new ResourceLocation("notenoughfakepixel", path);
    }
}

package com.nef.notenoughfakepixel.utils;

import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import com.nef.notenoughfakepixel.variables.Rarity;
import lombok.Data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PetData {

    @Data
    private static class RepoWrapper {
        private Map<String, String> pets = Collections.emptyMap();
    }

    private static final RepoWrapper STUB = new RepoWrapper();

    private static final Map<String, String> FALLBACK_PETS = new LinkedHashMap<String, String>() {{
        put("Ammonite", "LEGENDARY");
        put("Armadillo", "LEGENDARY");
        put("Baby Yeti", "LEGENDARY");
        put("Bal", "LEGENDARY");
        put("Bat", "LEGENDARY");
        put("Bee", "LEGENDARY");
        put("Black Cat", "LEGENDARY");
        put("Blaze", "LEGENDARY");
        put("Blue Whale", "LEGENDARY");
        put("Chicken", "LEGENDARY");
        put("Dolphin", "LEGENDARY");
        put("Developer's Pet", "LEGENDARY");
        put("Duck", "LEGENDARY");
        put("Frozen Bubble", "LEGENDARY");
        put("Relic Keeper", "LEGENDARY");
        put("Droplet Wisp", "LEGENDARY");
        put("Elephant", "LEGENDARY");
        put("Ender Dragon", "LEGENDARY");
        put("Enderman", "LEGENDARY");
        put("Endermite", "LEGENDARY");
        put("Flying Fish", "LEGENDARY");
        put("Ghoul", "LEGENDARY");
        put("Giraffe", "LEGENDARY");
        put("Golem", "LEGENDARY");
        put("Golden Dragon", "LEGENDARY");
        put("Griffin", "LEGENDARY");
        put("Guardian", "LEGENDARY");
        put("Horse", "LEGENDARY");
        put("Jerry", "LEGENDARY");
        put("Lion", "LEGENDARY");
        put("Megalodon", "LEGENDARY");
        put("Mithril Golem", "LEGENDARY");
        put("Monkey", "LEGENDARY");
        put("Mooshroom Cow", "LEGENDARY");
        put("Ocelot", "LEGENDARY");
        put("Parrot", "LEGENDARY");
        put("Phoenix", "LEGENDARY");
        put("Pigman", "LEGENDARY");
        put("Rabbit", "LEGENDARY");
        put("Rock", "LEGENDARY");
        put("Sheep", "LEGENDARY");
        put("Silverfish", "LEGENDARY");
        put("Skeleton Horse", "LEGENDARY");
        put("Snowman", "LEGENDARY");
        put("Spider", "LEGENDARY");
        put("Squid", "LEGENDARY");
        put("Tarantula", "LEGENDARY");
        put("Tiger", "LEGENDARY");
        put("Wither Skeleton", "LEGENDARY");
        put("Wolf", "LEGENDARY");
        put("Zombie", "LEGENDARY");
    }};

    public static Map<String, Rarity> getAllPets() {
        RepoWrapper wrapper = RepoHandler.getData("pets", RepoWrapper.class, STUB);
        Map<String, String> raw = (wrapper != null) ? wrapper.getPets() : null;
        if (raw == null || raw.isEmpty()) raw = FALLBACK_PETS;

        Map<String, Rarity> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : raw.entrySet()) {
            result.put(entry.getKey(), Rarity.fromString(entry.getValue()));
        }
        return result;
    }
}

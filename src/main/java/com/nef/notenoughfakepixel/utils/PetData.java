package com.nef.notenoughfakepixel.utils;

import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PetData {

    @Data
    private static class RepoWrapper {
        private List<String> pets = Collections.emptyList();
    }

    private static final RepoWrapper STUB = new RepoWrapper();

    private static final List<String> FALLBACK_PETS = Arrays.asList(
            "Ammonite", "Armadillo",
            "Baby Yeti", "Bal", "Bat", "Bee", "Black Cat", "Blaze", "Blue Whale",
            "Chicken", "Dolphin", "Developer's Pet",
            "Duck",
            "Frozen Bubble",
            "Relic Keeper", "Droplet wisp",
            "Elephant", "Ender Dragon", "Enderman", "Endermite",
            "Flying Fish", "Ghoul", "Giraffe", "Golem", "Golden Dragon", "Griffin", "Guardian",
            "Horse", "Jerry", "Lion", "Megalodon", "Mithril Golem", "Monkey",
            "Mooshroom Cow", "Ocelot", "Parrot", "Phoenix", "Pigman",
            "Rabbit", "Rock", "Sheep", "Silverfish", "Skeleton Horse", "Snowman",
            "Spider", "Squid", "Tarantula", "Tiger", "Wither Skeleton", "Wolf", "Zombie"
    );

    public static List<String> getAllPets() {
        RepoWrapper wrapper = RepoHandler.getData("pets", RepoWrapper.class, STUB);
        List<String> pets = wrapper != null ? wrapper.getPets() : null;
        if (pets == null || pets.isEmpty()) return FALLBACK_PETS;
        return pets;
    }
}

package com.nef.notenoughfakepixel.nefrepo;

import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import com.nef.notenoughfakepixel.utils.ListUtils;

import java.util.List;

public class NefRepo {

    private static final String BASE = "https://raw.githubusercontent.com/davidbelesp/NotEnoughFakepixel-REPO/refs/heads/main/data/";

    private static final List<String> KEYS = ListUtils.of("fairysouls", "update", "accessories");

    public static void init() {
        registerSources();
        RepoHandler.warmupAllAsync();
    }

    private static void registerSources() {
        for (String key : KEYS) {
            RepoHandler.registerSource(
                    key,
                    BASE + key + ".json",
                    24 * 60 * 60 * 1000L // 24 hours
            );
        }
    }

}

package org.ginafro.notenoughfakepixel.nefrepo;

import org.ginafro.notenoughfakepixel.events.handlers.RepoHandler;
import org.ginafro.notenoughfakepixel.utils.ListUtils;

import java.util.List;

public class NefRepo {

    private static final String BASE = "https://raw.githubusercontent.com/davidbelesp/NotEnoughFakepixel-REPO/refs/heads/main/data/";

    private static final List<String> KEYS = ListUtils.of("fairysouls", "update");

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

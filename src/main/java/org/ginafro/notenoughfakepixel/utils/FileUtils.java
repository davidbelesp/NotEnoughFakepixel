package org.ginafro.notenoughfakepixel.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.events.handlers.RepoHandler;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.FairySoulData;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {

    public static File SOULS_FILE = new File(Config.configDirectory, "gainedsouls.json");
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Map<String, List<String>> templateMap = new HashMap<>();

    public static FairySoulData getSoulData() {
        try {
            if (!Files.exists(SOULS_FILE.toPath())) {
                templateMap.put("hub", new ArrayList<>());
                templateMap.put("spider", new ArrayList<>());
                templateMap.put("crimson", new ArrayList<>());
                templateMap.put("end", new ArrayList<>());
                templateMap.put("park", new ArrayList<>());
                templateMap.put("farming", new ArrayList<>());
                templateMap.put("gold", new ArrayList<>());
                templateMap.put("dungeon_hub", new ArrayList<>());
                templateMap.put("winter", new ArrayList<>());
                FairySoulData data = new FairySoulData(
                        "Do Not Manually Change This File, It will lead to errors",
                        0,
                        templateMap
                );
                saveSoulData(data);
                return data;
            }
            FileReader reader = new FileReader(SOULS_FILE);
            return gson.fromJson(reader, FairySoulData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new FairySoulData("Could not load file", 247, new HashMap<>());
        }
    }

    public static void saveSoulData(FairySoulData soulData) {
        try (FileWriter writer = new FileWriter(SOULS_FILE)) {
            gson.toJson(soulData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FairySoulData getAllSouls() {
        String fairyData = RepoHandler.getCachedJson();
        if (fairyData != null) {
            return gson.fromJson(fairyData, FairySoulData.class);
        } else {
            return new FairySoulData("Could not load repository", 247, new HashMap<>());
        }
    }

}

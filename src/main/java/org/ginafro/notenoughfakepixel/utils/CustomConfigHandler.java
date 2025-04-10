package org.ginafro.notenoughfakepixel.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jline.internal.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomConfigHandler {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Map<String, List<String>> fairySoulsTemplate = new HashMap<>();

    public static <T> @Nullable T loadConfig(Class<T> config, File file){
        try{
            if(!file.exists()) {
                file.createNewFile();
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
                return gson.fromJson(reader,config);
        }catch(IOException e){
            new RuntimeException(
                    "Invalid config file '" + file + "'. This will reset the config to default",
                    e
            ).printStackTrace();
            makeBackup(file,".corrupted");
            return null;
        }
    }

    public static void saveConfig(Object config, File file) {
        File tempFile = new File(file.getParent(), file.getName() + ".temp");
        try {
            tempFile.createNewFile();
            try (
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile.toPath()), StandardCharsets.UTF_8))
            ) {
                writer.write(gson.toJson(config));
            }

            if (loadConfig(config.getClass(), tempFile) == null) {
                System.out.println("Config verification failed for " + tempFile + ", could not save config properly.");
                makeBackup(tempFile, ".backup");
                return;
            }

            try {
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                // If atomic move fails it could be because it isn't supported or because the implementation of it
                // doesn't overwrite the old file, in this case we will try a normal move.
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            makeBackup(tempFile, ".backup");
            e.printStackTrace();
        }
    }

    private static void makeBackup(File file, String suffix) {
        File backupFile = new File(file.getParent(), file.getName() + "-" + System.currentTimeMillis() + suffix);
        System.out.println("trying to make backup: " + backupFile.getName());

        try {
            Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception __) {
                System.out.println("neu config gone");
            }
        }
        finally {
            file.delete();
        }
    }

}

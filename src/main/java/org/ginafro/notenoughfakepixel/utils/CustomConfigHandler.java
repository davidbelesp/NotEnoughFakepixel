package org.ginafro.notenoughfakepixel.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage.StorageData;
import org.ginafro.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;

import static org.ginafro.notenoughfakepixel.utils.CustomConfigFiles.STORAGE_FOLDER;

public class CustomConfigHandler {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_DIR = Config.configDirectory.toPath();
    private static final Path SLOT_LOCK   = CONFIG_DIR.resolve("slotlocking.json");

    public static SlotLocking.SlotLockingConfig loadConfig() {
        ensureDir(CONFIG_DIR);

        if (!Files.exists(SLOT_LOCK) || isEmpty(SLOT_LOCK)) {
            SlotLocking.SlotLockingConfig defaults = new SlotLocking.SlotLockingConfig();
            safeAtomicWriteJson(SLOT_LOCK, defaults);
            return defaults;
        }

        try (InputStream in = Files.newInputStream(SLOT_LOCK);
             InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8)) {

            com.google.gson.stream.JsonReader jr = new com.google.gson.stream.JsonReader(isr);
            jr.setLenient(true);

            SlotLocking.SlotLockingConfig cfg = GSON.fromJson(jr, SlotLocking.SlotLockingConfig.class);
            if (cfg == null) throw new com.google.gson.JsonSyntaxException("Parsed config was null");


            return cfg;
        } catch (com.google.gson.JsonParseException e) {
            backup(SLOT_LOCK, ".bad.json");
            logFileHead(SLOT_LOCK);
            Logger.logErrorConsole("[NEF] Invalid JSON in slotlocking.json: " + rootMsg(e));
            SlotLocking.SlotLockingConfig defaults = new SlotLocking.SlotLockingConfig();
            safeAtomicWriteJson(SLOT_LOCK, defaults);
            return defaults;

        } catch (IOException e) {
            Logger.logErrorConsole("[NEF] IO reading slotlocking.json, using defaults: " + rootMsg(e));
            return new SlotLocking.SlotLockingConfig();
        }
    }

    private static String rootMsg(Throwable t) {
        Throwable r = t;
        while (r.getCause() != null) r = r.getCause();
        return r.getClass().getSimpleName() + ": " + r.getMessage();
    }

    private static void logFileHead(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            String head = new String(bytes, 0, Math.min(bytes.length, 512), StandardCharsets.UTF_8);
            Logger.logErrorConsole("[NEF] First 512 bytes of config: " + head.replace("\n", "\\n"));
            Logger.logErrorConsole("[NEF] File size = " + bytes.length + " bytes");
        } catch (IOException ignored) {}
    }

    public static StorageData loadStorageData(String chestName) {
        Path folder = Paths.get(STORAGE_FOLDER.path);
        Path file = folder.resolve(sanitizeName(chestName) + ".json");
        if (!Files.exists(file) || isEmpty(file)) return null;

        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return GSON.fromJson(r, StorageData.class);
        } catch (JsonParseException e) {
            backup(file, ".bad.json");
            Logger.logErrorConsole("StorageData JSON invalid for " + file.getFileName());
            return null;
        } catch (IOException e) {
            Logger.logErrorConsole("Failed reading " + file.getFileName() + ": " + e.getMessage());
            return null;
        }
    }

    private static void backup(Path file, String suffix) {
        try {
            Path backup = file.resolveSibling(file.getFileName() + "-" + Instant.now().toEpochMilli() + suffix);
            Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            Logger.logConsole("Backup created: " + backup.getFileName());
        } catch (IOException e) {
            Logger.logErrorConsole("Could not create backup for " + file.getFileName() + ": " + e.getMessage());
        }
    }

    public static void saveStorageData(StorageData data) {
        Path folder = Paths.get(STORAGE_FOLDER.path);
        ensureDir(folder);
        Path file = folder.resolve(sanitizeName(data.chestName) + ".json");
        if (Files.exists(file)) backup(file, ".bak.json");
        safeAtomicWriteJson(file, data);
    }

    public static void saveConfig(SlotLocking.SlotLockingConfig config) {
        ensureDir(CONFIG_DIR);
        if (Files.exists(SLOT_LOCK)) backup(SLOT_LOCK, ".bak.json");
        safeAtomicWriteJson(SLOT_LOCK, config);
    }


    private static String sanitizeName(String s) {
        return s.replaceAll("[^A-Za-z0-9_-]", "");
    }

    private static boolean isEmpty(Path p) {
        try { return Files.size(p) == 0L; } catch (IOException e) { return true; }
    }

    private static void ensureDir(Path dir) {
        try { Files.createDirectories(dir); } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    private static void safeAtomicWriteJson(Path target, Object value) {
        String tmpName = target.getFileName() + "." + Instant.now().toEpochMilli() + ".tmp";
        Path tmp = target.getParent().resolve(tmpName);

        try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            GSON.toJson(value, w);
            w.flush();
        } catch (IOException e) {
            try { Files.deleteIfExists(tmp); } catch (IOException __) {}
            throw new UncheckedIOException("Failed writing temp file for " + target.getFileName(), e);
        }

        try {
            Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException atomicFail) {
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException fallbackFail) {
                try { Files.deleteIfExists(tmp); } catch (IOException __) {}
                throw new UncheckedIOException("Failed replacing " + target.getFileName(), fallbackFail);
            }
        }
    }

}

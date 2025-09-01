package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RegisterEvents
public class CrystalWaypoints {

    private static final CrystalWaypoints INSTANCE = new CrystalWaypoints();
    private final List<ChWaypoint> waypoints = new ArrayList<>(128);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final AtomicBoolean dirty = new AtomicBoolean(false);
    private File file;

    private CrystalWaypoints() {}

    public static CrystalWaypoints getInstance() { return INSTANCE; }

    public void initFile() {
        if (file != null) return;
        File cfgDir = new File(Minecraft.getMinecraft().mcDataDir, "config");
        cfgDir.mkdirs();
        file = new File(cfgDir, "chwaypoints.json");
    }

    public synchronized void load() {
        initFile();
        waypoints.clear();
        if (file.exists()) {
            try (Reader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                Type listType = new TypeToken<List<ChWaypoint>>(){}.getType();
                List<ChWaypoint> loaded = gson.fromJson(r, listType);
                if (loaded != null) waypoints.addAll(loaded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dirty.set(false);
    }

    public synchronized void addWaypoint(ChWaypoint wp) {
        waypoints.add(wp);
        dirty.set(true);
    }

    public synchronized List<ChWaypoint> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(waypoints));
    }

    public synchronized boolean removeById(String id) {
        boolean removed = waypoints.removeIf(w -> w.id.equalsIgnoreCase(id));
        if (removed) dirty.set(true);
        return removed;
    }

    public synchronized void saveIfDirty() {
        if (!dirty.get()) return;
        saveForce();
    }

    public synchronized void saveForce() {
        initFile();
        try (Writer w = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            gson.toJson(waypoints, w);
            dirty.set(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized ChWaypoint findById(String id) {
        for (ChWaypoint w : waypoints) {
            if (w.id.equalsIgnoreCase(id)) return w;
        }
        return null;
    }


}

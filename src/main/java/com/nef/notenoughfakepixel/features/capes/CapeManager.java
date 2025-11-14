package com.nef.notenoughfakepixel.features.capes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CapeManager {

    private static final String CAPES_JSON_URL = "https://raw.githubusercontent.com/GinaFro1/NotEnoughFakepixel-Cape/main/capes.json";
    private static final String GITHUB_CAPE_BASE_URL = "https://raw.githubusercontent.com/GinaFro1/NotEnoughFakepixel-Cape/main/";
    private static final List<Cape> allCapes = new ArrayList<>();
    private static final Map<Integer, Cape> capesByID = new HashMap<>();
    private static final Map<Integer, ResourceLocation> dynamicTextures = new HashMap<>();
    private static Cape currentCape = null;

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Map<UUID, Integer> syncedCapes = new HashMap<>();

    // Configuration handling
    private static final File CONFIG_DIR = new File(Minecraft.getMinecraft().mcDataDir, "config/Notenoughfakepixel");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "capes.cfg");

    public static void syncCape(UUID uuid, int capeID) {
        syncedCapes.put(uuid, capeID);
    }

    public static Cape getCapeForPlayer(UUID uuid) {
        Integer id = syncedCapes.get(uuid);
        if (id != null) {
            return getCapeByID(id);
        }
        return null;
    }

    public static Cape getCapeByID(Integer id) {
        return capesByID.get(id);
    }

    public static void loadCapesFromGitHub() {
        executor.execute(() -> {
            try {
                URL url = new URL(CAPES_JSON_URL);
                Reader reader = new InputStreamReader(url.openStream());
                List<Map<String, Object>> data = new Gson().fromJson(reader, new TypeToken<List<Map<String, Object>>>() {
                }.getType());

                for (Map<String, Object> entry : data) {
                    int id = Integer.parseInt((String) entry.get("capeID"));
                    String file = (String) entry.get("file");
                    int width = ((Double) entry.get("width")).intValue();
                    int height = ((Double) entry.get("height")).intValue();
                    String name = (String) entry.get("name");

                    Cape cape = new Cape(width, height, id, file, name);
                    allCapes.add(cape);
                    capesByID.put(id, cape);
                }

                if (!allCapes.isEmpty()) {
                    currentCape = allCapes.get(0);
                }

                System.out.println("✅ Loaded cape metadata from GitHub (" + allCapes.size() + " capes).");
                loadSelectedCape();
                allCapes.forEach(CapeManager::loadCapeTextureAsync);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static Cape getCape() {
        loadCapeTextureAsync(currentCape);
        return currentCape;
    }

    public static void setCape(int id) {
        if (id == -11) {
            currentCape = null;
        } else {
            currentCape = capesByID.getOrDefault(id, currentCape);
        }
        saveSelectedCape();
    }

    public static List<Cape> getAllCapes() {
        if (capesByID.isEmpty() || allCapes.isEmpty()) {
            loadCapesFromGitHub();
        }
        return allCapes;
    }

    public static ResourceLocation getCapeTexture(Cape cape) {
        return dynamicTextures.get(cape.capeID);
    }

    public static String getCapeURL(Cape cape) {
        return GITHUB_CAPE_BASE_URL + cape.capeFile;
    }

    public static boolean hasCape() {
        return currentCape != null;
    }

    private static void loadCapeTextureAsync(Cape cape) {
        if (cape == null || cape.capeFile == null) return;
        if (dynamicTextures.containsKey(cape.capeID)) return;

        executor.execute(() -> {
            try {
                String url = getCapeURL(cape);
                System.out.println("⬇️ Downloading cape from " + url);

                BufferedImage image = ImageIO.read(new URL(url));

                if (image == null) {
                    System.out.println("❌ Failed to load image for cape: " + cape.capeFile);
                    return;
                }

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    try {
                        DynamicTexture dyn = new DynamicTexture(image);
                        ResourceLocation loc = Minecraft.getMinecraft().getTextureManager()
                                .getDynamicTextureLocation(String.valueOf(cape.capeID), dyn);
                        dynamicTextures.put(cape.capeID, loc);
                        System.out.println("✅ Cape loaded and registered: " + cape.capeFile + " → " + loc);
                    } catch (Exception e) {
                        System.err.println("❌ GL error while creating cape texture: " + cape.capeFile);
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                System.err.println("❌ Failed to download cape " + cape.capeFile);
                e.printStackTrace();
            }
        });
    }

    private static void saveSelectedCape() {
        executor.execute(() -> {
            try {
                Properties props = new Properties();
                props.setProperty("selectedCapeID",
                        currentCape != null ? String.valueOf(currentCape.capeID) : "-11");

                CONFIG_DIR.mkdirs();
                try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
                    props.store(out, "NotEnoughFakepixel Cape Settings");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void loadSelectedCape() {
        if (!CONFIG_FILE.exists()) return;

        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.load(in);
            int savedID = Integer.parseInt(props.getProperty("selectedCapeID", "-11"));

            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (savedID != -11) {
                    currentCape = capesByID.get(savedID);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCapeWidth() {
        return getCape() != null ? getCape().width : 64;
    }

    public static int getCapeHeight() {
        return getCape() != null ? getCape().height : 32;
    }
}
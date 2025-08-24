package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.handlers.RepoHandler;
import org.ginafro.notenoughfakepixel.utils.*;
import org.ginafro.notenoughfakepixel.variables.Gamemode;
import org.ginafro.notenoughfakepixel.variables.Location;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RegisterEvents
public class FairySouls {

    private String island;

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK) return;
        if (!Config.feature.qol.fairySoulWaypoints) return;
        Location currentIsland = TablistParser.currentLocation;
        List<String> souls = new ArrayList<>();
        if (currentIsland == Location.HUB) {
            souls = getAllSouls().locations.get("hub");
            island = "hub";
        }
        if (currentIsland == Location.SPIDERS_DEN) {
            souls = getAllSouls().locations.get("spider");
            island = "spider";
        }
        if (currentIsland == Location.CRIMSON_ISLE) {
            souls = getAllSouls().locations.get("crimson");
            island = "crimson";
        }
        if (currentIsland == Location.THE_END) {
            souls = getAllSouls().locations.get("end");
            island = "end";
        }
        if (currentIsland == Location.PARK) {
            souls = getAllSouls().locations.get("park");
            island = "park";
        }
        if (currentIsland == Location.BARN) {
            souls = getAllSouls().locations.get("farming");
            island = "farming";
        }
        if (currentIsland == Location.GOLD_MINE) {
            souls = getAllSouls().locations.get("gold");
            island = "gold";
        }
        if (currentIsland == Location.DUNGEON_HUB) {
            souls = getAllSouls().locations.get("dungeon_hub");
            island = "dungeon_hub";
        }
        if (currentIsland == Location.JERRY) {
            souls = getAllSouls().locations.get("winter");
            island = "winter";
        }
        if (currentIsland == Location.DWARVEN) {
            souls = getAllSouls().locations.get("dwarven");
            island = "dwarven";
        }
        List<String> renderedSouls = checkSouls(souls);
        for (String s : renderedSouls) {
            String[] coords = s.split(",");
            Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
            double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * e.partialTicks;
            double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * e.partialTicks;
            double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * e.partialTicks;
            int x = Integer.parseInt(coords[0].trim());
            int y = Integer.parseInt(coords[1].trim());
            int z = Integer.parseInt(coords[2].trim());
            GlStateManager.color(1f, 1f, 1f, 1f);
            AxisAlignedBB aab = new AxisAlignedBB(
                    x - viewerX + 0.2,
                    y - viewerY - 1,
                    z - viewerZ + 0.2,
                    x + 0.8 - viewerX,
                    y - viewerY + 256,
                    z + 0.8 - viewerZ
            ).expand(0.01f, 0.01f, 0.01f);
            Color c = ColorUtils.getColor(Config.feature.qol.fairySoulWaypointsColor);
            RenderUtils.highlightBlock(new BlockPos(x, y, z), c, true, e.partialTicks);
            GlStateManager.disableCull();
            Color fairySoulC = ColorUtils.getColor(Config.feature.qol.fairySoulWaypointsColor);
            Color fairySoulColor = new Color(fairySoulC.getRed(), fairySoulC.getGreen(), fairySoulC.getBlue(), 102);
            GlStateManager.disableDepth();
            RenderUtils.renderBeaconBeam(new BlockPos(x, y, z), fairySoulColor.getRGB(), 1.0f, e.partialTicks);
//            RenderUtils.drawFilledBoundingBox(aab, 1f, fairySoulColor);
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (StringUtils.stripControlCodes(e.message.getUnformattedText()).equalsIgnoreCase("SOUL! You found a Fairy Soul!")
                || StringUtils.stripControlCodes(e.message.getFormattedText()).equalsIgnoreCase("You already found that Fairy Soul!")
        ) {
            System.out.println("Chat Recieved");
            String soul = null;
            double closestDistSq = 5 * 5;
            FairySoulData soulData = getAllSouls();
            FairySoulData soulData1 = getSoulData();
            if (island == null || soulData1.locations == null) {
                System.out.println("Island or soulData.locations is null");
                return;
            }
            List<String> souls = soulData.locations.get(island);
            List<String> gainedSouls = soulData1.locations.get(island);
            for (String s : souls) {
                String[] s1 = s.split(",");
                BlockPos pos = new BlockPos(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]), Integer.parseInt(s1[2]));
                double distSq = pos.distanceSq(Minecraft.getMinecraft().thePlayer.getPosition());

                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    soul = s;
                }
            }
            if (soul != null && !gainedSouls.contains(soul)) {
                gainedSouls.add(soul);
                soulData1.locations.put(island, gainedSouls);
                soulData1.soulCount++;
                saveSoulData(soulData1);
            }
        }
    }

    private List<String> checkSouls(List<String> shownSouls) {
        List<String> souls = new ArrayList<>();
        FairySoulData data = getSoulData();
        if (data != null) {
            if (data.locations != null) {
                if (data.locations.get(island) != null) {
                    for (String s : shownSouls) {
                        if (!data.locations.get(island).contains(s)) {
                            souls.add(s);
                        }
                    }
                }
            }
        }
        if (souls.isEmpty()) {
            return shownSouls;
        }
        return souls;
    }

    // FAIRY SOUL DATA HANDLING

    public static class FairySoulData {

        String description;
        int soulCount;
        Map<String, List<String>> locations;

        public FairySoulData(String desc, int souls, Map<String, List<String>> locs) {
            description = desc;
            soulCount = souls;
            locations = locs;
        }

    }

    private static final FairySoulData STUB_LOAD_FAIL =
            new FairySoulData("Could not load repository", 247, java.util.Collections.emptyMap());
    private static volatile FairySoulData CACHED_SOULS = null;
    private static volatile String LAST_JSON_REF = null;

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
        final String KEY = "fairysouls";
        if (!RepoHandler.isLoaded(KEY)) {
            RepoHandler.ensureLoadedAsync(KEY);
            return CACHED_SOULS != null ? CACHED_SOULS : STUB_LOAD_FAIL;
        }

        final String json = RepoHandler.getJson(KEY);
        if (json == null) {
            return CACHED_SOULS != null ? CACHED_SOULS : STUB_LOAD_FAIL;
        }

        if (json != LAST_JSON_REF) {
            try {
                FairySoulData parsed = gson.fromJson(json, FairySoulData.class);
                if (parsed != null) {
                    CACHED_SOULS = parsed;
                    LAST_JSON_REF = json;
                }
            } catch (Exception e) {
                Logger.logErrorConsole("FairySouls parse error: " + e.getMessage());
            }
        }

        return CACHED_SOULS != null ? CACHED_SOULS : STUB_LOAD_FAIL;
    }

}

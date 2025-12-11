package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.google.gson.Gson;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.ConfigHandler;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Gamemode;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

@RegisterEvents
public class FairySouls {

    private String island;

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        final FairySoulData all = getAllSouls();
        final Map<String, List<String>> locs = (all != null) ? all.locations : null;
        List<String> souls = Collections.emptyList();
        island = null;

        if (SkyblockData.getCurrentGamemode() != Gamemode.SKYBLOCK) return;
        if (!Config.feature.waypoints.fairySoulWaypoints) return;

        Location currentIsland = SkyblockData.getCurrentLocation();
        if (locs != null && currentIsland != null) {
            switch (currentIsland) {
                case HUB:         souls = locs.getOrDefault("hub", Collections.emptyList());           island = "hub"; break;
                case SPIDERS_DEN: souls = locs.getOrDefault("spider", Collections.emptyList());        island = "spider"; break;
                case CRIMSON_ISLE:souls = locs.getOrDefault("crimson", Collections.emptyList());       island = "crimson"; break;
                case THE_END:     souls = locs.getOrDefault("end", Collections.emptyList());           island = "end"; break;
                case PARK:        souls = locs.getOrDefault("park", Collections.emptyList());          island = "park"; break;
                case BARN:        souls = locs.getOrDefault("farming", Collections.emptyList());       island = "farming"; break;
                case GOLD_MINE:   souls = locs.getOrDefault("gold", Collections.emptyList());          island = "gold"; break;
                case DUNGEON_HUB: souls = locs.getOrDefault("dungeon_hub", Collections.emptyList());   island = "dungeon_hub"; break;
                case JERRY:       souls = locs.getOrDefault("winter", Collections.emptyList());        island = "winter"; break;
                case DWARVEN:     souls = locs.getOrDefault("dwarven", Collections.emptyList());       island = "dwarven"; break;
                default: break;
            }
        }

        GlStateManager.pushMatrix();

        try {
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

                Color base = ColorUtils.getColor(Config.feature.waypoints.fairySoulWaypointsColor);
                Color beam = new Color(base.getRed(), base.getGreen(), base.getBlue(), 102);

                RenderUtils.highlightBlock(new BlockPos(x, y, z), base, true, e.partialTicks);
                GlStateManager.disableCull();
                GlStateManager.disableDepth();
                RenderUtils.renderBeaconBeam(new BlockPos(x, y, z), beam.getRGB(), 1.0f, e.partialTicks);
                GlStateManager.enableDepth();
                GlStateManager.enableCull();
                GlStateManager.enableTexture2D();
            }
        } finally {
            GlStateManager.popMatrix();
        }

    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        String msg = StringUtils.stripControlCodes(e.message.getUnformattedText()).trim();
        if (msg.equalsIgnoreCase("SOUL! You found a Fairy Soul!") || msg.equalsIgnoreCase("You already found that Fairy Soul!")) {
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
            List<String> gainedSouls = soulData1.locations.computeIfAbsent(island, k -> new ArrayList<>());
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
        FairySoulData data = getSoulData();
        if (data == null || data.locations == null || island == null) return shownSouls;

        List<String> gained = data.locations.get(island);
        if (gained == null || gained.isEmpty()) return shownSouls;

        List<String> result = new ArrayList<>(shownSouls.size());
        for (String s : shownSouls) {
            if (!gained.contains(s)) result.add(s);
        }
        return result.isEmpty() ? shownSouls : result;
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
    public static File SOULS_FILE = new File(Config.configDirectory, "gainedsouls.json");
    public static Gson gson = ConfigHandler.GSON;
    public static Map<String, List<String>> templateMap = new HashMap<>();

    public static FairySoulData getSoulData() {
        try {
            if (!Files.exists(SOULS_FILE.toPath())) {
                Map<String, List<String>> template = new HashMap<>();
                template.put("hub", new ArrayList<>());
                template.put("spider", new ArrayList<>());
                template.put("crimson", new ArrayList<>());
                template.put("end", new ArrayList<>());
                template.put("park", new ArrayList<>());
                template.put("farming", new ArrayList<>());
                template.put("gold", new ArrayList<>());
                template.put("dungeon_hub", new ArrayList<>());
                template.put("winter", new ArrayList<>());
                template.put("dwarven", new ArrayList<>());

                FairySoulData data = new FairySoulData(
                        "Do Not Manually Change This File, It will lead to errors",
                        0, template
                );
                saveSoulData(data);
                return data;
            }

            try (FileReader reader = new FileReader(SOULS_FILE)) {
                return gson.fromJson(reader, FairySoulData.class);
            }
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

    private static final FairySoulData STUB_SOULS =
            new FairySoulData("Could not load repository", 247, Collections.emptyMap());

    public static FairySoulData getAllSouls() {
        return RepoHandler.getData("fairysouls", FairySoulData.class, STUB_SOULS);
    }

}

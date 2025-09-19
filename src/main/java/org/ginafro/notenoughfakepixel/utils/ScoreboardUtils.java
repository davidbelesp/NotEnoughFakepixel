package org.ginafro.notenoughfakepixel.utils;

import jline.internal.Log;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.DragonCloseAlert;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.stats.StatBars;
import org.ginafro.notenoughfakepixel.serverdata.SkyblockData;
import org.ginafro.notenoughfakepixel.variables.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RegisterEvents
public class ScoreboardUtils {

    public static Area currentArea = Area.NONE;
    public static Gamemode currentGamemode = Gamemode.LOBBY;

    public static DungeonFloor currentFloor = DungeonFloor.NONE;
    public static int clearedPercentage = -1;

    private int lastBoardHash = 0;

    public static Slayer currentSlayer = Slayer.NONE;
    public static boolean isSlayerActive = false;

    public static int heat = 0;

    @Getter
    @Setter
    private static Pattern floorPattern = Pattern.compile(" §7⏣ §cThe Catacombs §7\\(<?floor>.{2}\\)");

    private static final int TICK_INTERVAL = 20;
    private int tickCounter = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if ((tickCounter = (tickCounter + 1) % TICK_INTERVAL) != 0) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.isSingleplayer()) return;

        final ServerData sd = mc.getCurrentServerData();
        if (sd == null ) return;
        final String serverIP = sd.serverIP == null ? "" : sd.serverIP;
        if (!Config.feature.debug.enableOutOfFakepixel && !serverIP.contains("fakepixel")) return;

        if (mc.theWorld == null) return;
        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) return;

        final ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);

        if (objective != null) {
            final String objNameClear = net.minecraft.util.StringUtils.stripControlCodes(objective.getDisplayName());
            currentGamemode = Gamemode.getGamemode(ScoreboardUtils.cleanSB(objective.getDisplayName()));

            final List<Score> scores = new ArrayList<>(scoreboard.getSortedScores(objective));
            for (int i = scores.size() - 1; i >= 0; i--) {
                final Score sc = scores.get(i);
                if (sc == null) {
                    scores.remove(i);
                    continue;
                }
                final String pn = sc.getPlayerName();
                if (pn == null || pn.startsWith("#")) {
                    scores.remove(i);
                }
            }

            final int boardHash = StringUtils.hashBoard(objNameClear, scores);
            if (boardHash != lastBoardHash) {
                lastBoardHash = boardHash;

                for (int i = 0, n = scores.size(); i < n; i++) {
                    final String raw = scores.get(i).getPlayerName();
                    if (raw == null || raw.isEmpty()) continue;

                    final String clean = net.minecraft.util.StringUtils.stripControlCodes(raw).trim();
                    if (clean.isEmpty()) continue;

                    // " §7⏣ §cThe Catacombs §7(F1)"
                    if (raw.startsWith(" §7⏣ §cThe Catacombs §7")) {
                        final int open = raw.indexOf('(');
                        final int close = raw.indexOf(')', open + 1);
                        if (open >= 0 && close > open) {
                            final String floor = raw.substring(open + 1, close);
                            currentFloor = DungeonFloor.getFloor(floor);
                        }
                    }

                    // --- Dungeon Cleared: 97% ---
                    if (StringUtils.startsWithFast(clean, "Dungeon Cleared: ")) {
                        final String num = StringUtils.sliceAfter(clean, "Dungeon Cleared: ");
                        clearedPercentage = NumberUtils.parseIntSafe(StringUtils.removeChars(num, "%"));
                        if (clearedPercentage < 0) {
                            clearedPercentage = -1;
                            Logger.log("Failed to parse cleared percentage from scoreboard: " + clean);
                        }
                    }

                    // get the heat from clean string "Heat: ♨ 9"
                    if (StringUtils.startsWithFast(clean, "Heat:")) {
                        final String num = StringUtils.sliceAfter(clean, "Heat:");
                        heat = NumberUtils.parseIntSafe(StringUtils.removeChars(num, "♨ "));
                        if (heat < 0) {
                            heat = 0;
                            Log.warn("Failed to parse heat from scoreboard: " + clean);
                        }
                    }

                    List<String> seasons = Arrays.asList("Spring", "Summer", "Autumn", "Winter");
                    for (String season : seasons) {
                        if (clean.contains(season)) {
                            SkyblockData.setSeason(SkyblockData.Season.valueOf(season.toUpperCase()));
                            break;
                        }
                    }

                    if (clean.contains("☀") || clean.contains("☽")) {
                        final String timeStr = clean.split(" ")[0];
                        final String[] parts = timeStr.split(":");
                        final boolean am = clean.contains("am");
                        if (parts.length == 2) {
                            try {
                                SkyblockData.setSbHour(Integer.parseInt(parts[0]));
                                SkyblockData.setSbMinute(Integer.parseInt(parts[1].replaceAll("am|pm", "")));
                                SkyblockData.setAm(am);
                            } catch (NumberFormatException ex) {
                                SkyblockData.setSbHour(0);
                                SkyblockData.setSbMinute(0);
                                SkyblockData.setAm(true);
                                Logger.log("Failed to parse time from scoreboard: " + clean);
                            }

                        }
                    }

                    if      (StringUtils.startsWithFast(clean, "Voidgloom Seraph"))      currentSlayer = Slayer.VOIDGLOOM;
                    else if (StringUtils.startsWithFast(clean, "Inferno Demonlord"))     currentSlayer = Slayer.INFERNO;
                    else if (StringUtils.startsWithFast(clean, "Sven Packmaster"))       currentSlayer = Slayer.SVEN;
                    else if (StringUtils.startsWithFast(clean, "Revenant Horror"))       currentSlayer = Slayer.REVENANT;
                    else if (StringUtils.startsWithFast(clean, "Tarantula Broodfather")) currentSlayer = Slayer.TARANTULA;

                    if (clean.indexOf("Slay the boss!") >= 0) isSlayerActive = true;
                    if (clean.indexOf(") Kills") >= 0 || clean.indexOf("Quest Failed") >= 0 || clean.indexOf("Boss slain!") >= 0) {
                        isSlayerActive = false;
                    }

                    for (Map.Entry<String, Color> e : DragonCloseAlert.DRAGON_COLORS.entrySet()) {
                        final String dragonName = e.getKey();
                        if (dragonName != null && !dragonName.isEmpty() && raw.contains(dragonName)) {
                            final Color color = e.getValue();
                            final Collection<EntityDragon> group = DragonCloseAlert.INSTANCE.getDragonsByColor(color);
                            if (group != null) {
                                for (EntityDragon dragon : group) {
                                    DragonCloseAlert.INSTANCE.registerDragon(dragon, raw);
                                }
                            }
                        }
                    }

                }

            }
        }


        if (currentGamemode == Gamemode.SKYBLOCK && mc.getNetHandler() != null) {
            final Collection<NetworkPlayerInfo> infos = mc.getNetHandler().getPlayerInfoMap();
            boolean gotSpeed = false;

            for (NetworkPlayerInfo npi : infos) {
                if (npi == null) continue;
                final IChatComponent disp = npi.getDisplayName();
                if (disp == null) continue;

                final String name = disp.getUnformattedText();
                if (name == null || name.isEmpty()) continue;

                // Unformatted name
                final String unformattedName = net.minecraft.util.StringUtils.stripControlCodes(name).trim();

                if (!gotSpeed) {
                    final int star = unformattedName.indexOf('✦');
                    if (star >= 0) {
                        final int val = NumberUtils.parseTrailingInt(unformattedName, star + 1);
                        if (val >= 0) {
                            StatBars.setSpeed(val);
                            gotSpeed = true;
                        }
                    } else {
                        final int idx = unformattedName.indexOf("Speed: ");
                        if (idx >= 0) {
                            final int val = NumberUtils.parseTrailingInt(unformattedName, idx + 7);
                            if (val >= 0) {
                                StatBars.setSpeed(val);
                                gotSpeed = true;
                            }
                        }
                    }
                }

                if (gotSpeed) break;

            }
        }
    }

    private static int  HUB_CACHE_VAL  = Integer.MIN_VALUE;
    private static long HUB_CACHE_TIME = 0L;

    public static int getHubNumber() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getNetHandler() == null) return -1;

        final long now = Minecraft.getSystemTime();
        if (HUB_CACHE_VAL != Integer.MIN_VALUE && (now - HUB_CACHE_TIME) < 1000L) {
            return HUB_CACHE_VAL;
        }

        int result = -1;
        final Collection<NetworkPlayerInfo> map = mc.getNetHandler().getPlayerInfoMap();
        if (map == null || map.isEmpty()) return -1;

        for (NetworkPlayerInfo npi : map) {
            if (npi == null) continue;
            final IChatComponent disp = npi.getDisplayName();
            if (disp == null) continue;
            final String text = disp.getUnformattedText();
            if (text == null || text.isEmpty()) continue;

            // Search for "Server: " and parse after it
            final int sv = text.indexOf("Server: ");
            if (sv < 0) continue;

            int pos = sv + 8;
            while (pos < text.length() && text.charAt(pos) == ' ') pos++;

            if (regionMatchesIgnoreCase(text, pos, "skyblock-")) {
                final int num = NumberUtils.parseTrailingInt(text, pos + "skyblock-".length());
                if (num >= 0) { result = num; break; }
            }

            // Fallback: first "-<digits>" after "Server: "
            final int dash = text.indexOf('-', pos);
            if (dash >= 0 && dash + 1 < text.length()) {
                final int num = NumberUtils.parseTrailingInt(text,dash + 1);
                if (num >= 0) { result = num; break; }
            }
        }

        HUB_CACHE_VAL  = result;
        HUB_CACHE_TIME = now;
        return result;
    }

    private static boolean regionMatchesIgnoreCase(String s, int offset, String pat) {
        final int n = pat.length();
        if (offset < 0 || offset + n > s.length()) return false;
        return s.regionMatches(true, offset, pat, 0, n);
    }

    public static String cleanSB(String scoreboard) {
        return net.minecraft.util.StringUtils.stripControlCodes(scoreboard).chars()
                .filter(c -> c > 20 && c < 127)
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    @SuppressWarnings({"ExecutionException", "IllegalArgumentException"})
    public static List<String> getScoreboardLines() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return Collections.emptyList();

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) return Collections.emptyList();

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return Collections.emptyList();

        List<Score> filteredScores;
        try {
            filteredScores = scoreboard.getSortedScores(objective).stream()
                    .filter(score -> score != null
                            && score.getPlayerName() != null
                            && !score.getPlayerName().startsWith("#"))
                    .collect(Collectors.toList());
        } catch (ConcurrentModificationException ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }

        int size = filteredScores.size();
        return IntStream.range(Math.max(0, size - 15), size)
                .mapToObj(i -> {
                    Score score = filteredScores.get(i);
                    String playerName = score.getPlayerName();
                    ScorePlayerTeam team = scoreboard.getPlayersTeam(playerName);
                    return ScorePlayerTeam.formatPlayerName(team, playerName);
                })
                .collect(Collectors.toList());
    }


    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        currentGamemode = Gamemode.LOBBY;
        currentArea = Area.NONE;
        currentFloor = DungeonFloor.NONE;
        clearedPercentage = -1;
    }

}
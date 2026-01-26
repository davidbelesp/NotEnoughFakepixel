package com.nef.notenoughfakepixel.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.overlays.stats.StatBars;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Area;
import com.nef.notenoughfakepixel.variables.Gamemode;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RegisterEvents
public class TablistParser {

    private static final Ordering<NetworkPlayerInfo> playerOrdering = Ordering.from(new PlayerComparator());

    public static int secretPercentage = 0;
    public static int deaths = 0;
    public static String time = "";

    public static int crypts = 0;

    public static String currentOpenChestName = "";
    public static String lastOpenChestName = "";

    private final List<String> accountInfo = new ArrayList<>();
    private final List<String> serverInfo  = new ArrayList<>();
    public static List<String> commissions = new ArrayList<>();

    @SideOnly(Side.CLIENT)
    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {
        }

        public int compare(NetworkPlayerInfo o1, NetworkPlayerInfo o2) {
            ScorePlayerTeam team1 = o1.getPlayerTeam();
            ScorePlayerTeam team2 = o2.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(
                            o1.getGameType() != WorldSettings.GameType.SPECTATOR,
                            o2.getGameType() != WorldSettings.GameType.SPECTATOR
                    )
                    .compare(
                            team1 != null ? team1.getRegisteredName() : "",
                            team2 != null ? team2.getRegisteredName() : ""
                    )
                    .compare(o1.getGameProfile().getName(), o2.getGameProfile().getName()).result();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onGuiOpen(GuiOpenEvent event) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (event.gui == null) return;

        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            ContainerChest container = (ContainerChest) chest.inventorySlots;

            currentOpenChestName = container.getLowerChestInventory().getDisplayName().getUnformattedText();
            lastOpenChestName = currentOpenChestName;
        } else {
            currentOpenChestName = "";
        }
    }

    // Parsing data from tablist
    private static final int TICK_INTERVAL = 20; // ~1s
    private int tickCounter = 0;

    private enum Section { NONE, SERVER, ACCOUNT, PLAYER }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if ((tickCounter = (tickCounter + 1) % TICK_INTERVAL) != 0) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;

        accountInfo.clear();
        serverInfo.clear();
        commissions.clear();

        final GuiPlayerTabOverlay tab = mc.ingameGUI.getTabList();

        final List<NetworkPlayerInfo> infos =
                playerOrdering.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());

        Section section = Section.NONE;
        boolean readingCommissions = false;

        for (NetworkPlayerInfo info : infos) {
            final String raw = tab.getPlayerName(info);
            if (raw == null || raw.isEmpty()) continue;

            // Headers detection
            if (raw.contains("§3§l Server Info§r")) {
                section = Section.SERVER;
                readingCommissions = false;
                continue;
            } else if (raw.contains("§6§lAccount Info§r")) {
                section = Section.ACCOUNT;
                readingCommissions = false;
                continue;
            } else if (raw.contains("§2§lPlayer Stats§r")) {
                section = Section.PLAYER;
                readingCommissions = false;
                continue;
            }

            final String line = net.minecraft.util.StringUtils.stripControlCodes(raw).trim();
            if (line.isEmpty()) {
                if (section == Section.SERVER && readingCommissions) {
                    readingCommissions = false;
                }
                continue;
            }

            switch (section) {
                case SERVER: {

                    if (StringUtils.startsWithFast(line, "Time: ")) {
                        time = line.substring(6);
                    }

                    // Server: skyblock-1
                    if (StringUtils.startsWithFast(line, "Server:")) {
                        String s = line.substring(line.indexOf("Server: ") + 8).trim();
                        final int dashDigits = StringUtils.indexOfDashDigits(s);
                        if (dashDigits >= 0) s = s.substring(0, dashDigits + 1);
                        SkyblockData.setCurrentLocation(Location.getLocation(s));
                    }

                    // Mithril Powder: 12,345
                    if (StringUtils.startsWithFast(line, "Mithril Powder: ")) {
                        final String num = StringUtils.sliceAfter(line, "Mithril Powder: ");
                        SkyblockData.setMithrilPowder(NumberUtils.parseIntSafe(StringUtils.removeChars(num, ",")));
                    }

                    // Gemstone Powder: 12,345
                    if (StringUtils.startsWithFast(line, "Gemstone Powder: ")) {
                        final String num = StringUtils.sliceAfter(line, "Gemstone Powder: ");
                        SkyblockData.setGemstonePowder(NumberUtils.parseIntSafe(StringUtils.removeChars(num, ",")));
                    }

                    // Secrets Found: 97%
                    if (StringUtils.startsWithFast(line, "Secrets Found: ")) {
                        final String num = StringUtils.sliceAfter(line, "Secrets Found: ");
                        secretPercentage = NumberUtils.parseIntSafe(StringUtils.removeChars(num, "%"));
                    }

                    // Commissions
                    if (readingCommissions) {
                        if (raw.contains("§9§l")) readingCommissions = false;
                        else commissions.add(line);
                    } else if (raw.contains("Commissions")) {
                        readingCommissions = true;
                    }

                    if (StringUtils.startsWithFast(line, "Dungeon: ")) {
                        SkyblockData.setCurrentGamemode(Gamemode.SKYBLOCK);
                        SkyblockData.setCurrentLocation(Location.DUNGEON);
                    }

                    if (StringUtils.startsWithFast(line, "Area: ")) {
                        SkyblockData.setCurrentGamemode(Gamemode.SKYBLOCK);
                        final String areaName = line.replace("Area: ", "");
                        SkyblockData.setCurrentArea(Area.getArea(areaName));
                    }

                    serverInfo.add(line);
                    break;
                }
                case PLAYER: {
                    // Deaths: X (Y)
                    if (StringUtils.startsWithFast(line, "Deaths: ")) {
                        final int open = line.indexOf('(');
                        final int close = line.indexOf(')', open + 1);
                        if (open > 0 && close > open) {
                            deaths = NumberUtils.parseIntSafe(line.substring(open + 1, close).trim());
                        }
                    }

                    // Crypts: N or "A/B" => 0
                    if (StringUtils.startsWithFast(line, "Crypts: ")) {
                        final String rest = line.substring(8).trim();
                        crypts = (rest.indexOf('/') >= 0) ? 0 : NumberUtils.parseIntSafe(rest);
                    }
                    break;
                }
                case ACCOUNT: {
                    // Speed: ✦400
                    if (StringUtils.startsWithFast(line, "Speed: ")){
                        String speedString = line.substring(7).replace("✦", "").trim();
                        int speed = NumberUtils.parseIntSafe(speedString);
                        StatBars.setSpeed(speed);
                    }

                    accountInfo.add(line);
                    break;
                }
                case NONE:
                default:
                    // Ignore lines outside sections
                    break;
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        SkyblockData.setCurrentLocation(Location.NONE);
    }


}

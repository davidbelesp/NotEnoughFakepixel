package com.nef.notenoughfakepixel.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.overlays.stats.StatBars;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.variables.Area;
import com.nef.notenoughfakepixel.variables.Gamemode;
import com.nef.notenoughfakepixel.variables.Location;
import com.nef.notenoughfakepixel.variables.Mayor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@RegisterEvents
public class TablistParser {

    private static final Ordering<NetworkPlayerInfo> playerOrdering = Ordering.from(new PlayerComparator());
    private static final Pattern PEST_PLOT_ENTRY = Pattern.compile("^(\\d+)(?:\\s*[xX]\\s*(\\d+))?$");
    private static final double PLOT_FACE_OFFSET = 0.01D;

    private static final AxisAlignedBB[] GARDEN_PLOTS = {
            new AxisAlignedBB(192, 67, 96, 288, 100, 192),
            new AxisAlignedBB(96, 67, 192, 192, 100, 288),
            new AxisAlignedBB(192, 67, 288, 288, 100, 384),
            new AxisAlignedBB(288, 67, 192, 384, 100, 288),
            new AxisAlignedBB(96, 67, 96, 192, 100, 192),
            new AxisAlignedBB(288, 67, 96, 384, 100, 192),
            new AxisAlignedBB(96, 67, 288, 192, 100, 384),
            new AxisAlignedBB(288, 67, 288, 384, 100, 384),
            new AxisAlignedBB(192, 67, 0, 288, 100, 96),
            new AxisAlignedBB(0, 67, 192, 96, 100, 288),
            new AxisAlignedBB(384, 67, 192, 480, 100, 288),
            new AxisAlignedBB(192, 67, 384, 288, 100, 480),
            new AxisAlignedBB(96, 67, 0, 192, 100, 96),
            new AxisAlignedBB(288, 67, 0, 384, 100, 96),
            new AxisAlignedBB(0, 67, 96, 96, 100, 192),
            new AxisAlignedBB(384, 67, 96, 480, 100, 192),
            new AxisAlignedBB(0, 67, 288, 96, 100, 384),
            new AxisAlignedBB(384, 67, 288, 480, 100, 384),
            new AxisAlignedBB(96, 67, 384, 192, 100, 480),
            new AxisAlignedBB(288, 67, 384, 384, 100, 480),
            new AxisAlignedBB(0, 67, 0, 96, 100, 96),
            new AxisAlignedBB(384, 67, 0, 480, 100, 96),
            new AxisAlignedBB(0, 67, 384, 96, 100, 480),
            new AxisAlignedBB(384, 67, 384, 480, 100, 480)
    };

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
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) {
            SkyblockData.setSandbox(false);
            SkyblockData.setActivePests(new HashMap<>());
            SkyblockData.setActiveVisitors(new ArrayList<>());
            SkyblockData.setCropMilestone(new ArrayList<>());
            SkyblockData.setCurrentMayor(Mayor.NONE);
            return;
        }

        accountInfo.clear();
        serverInfo.clear();
        commissions.clear();

        final GuiPlayerTabOverlay tab = mc.ingameGUI.getTabList();

        final List<NetworkPlayerInfo> infos =
                playerOrdering.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());

        Section section = Section.NONE;
        boolean readingCommissions = false;
        Map<Integer, Integer> parsedActivePests = new HashMap<>();
        boolean pestPlotsLineSeen = false;
        List<String> parsedActiveVisitors = new ArrayList<>();
        boolean readingVisitors = false;
        List<String> parsedCropMilestone = new ArrayList<>();
        boolean readingCropMilestone = false;
        Mayor parsedCurrentMayor = SkyblockData.getCurrentMayor();
        boolean mayorLineSeen = false;

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

            final String formattedLine = raw.trim();
            final String line = net.minecraft.util.StringUtils.stripControlCodes(raw).trim();
            if (line.isEmpty()) {
                readingCropMilestone = false;
                if (section == Section.SERVER && readingCommissions) {
                    readingCommissions = false;
                }
                continue;
            }

            if (StringUtils.startsWithFast(line, "Winner:")) {
                parsedCurrentMayor = Mayor.fromName(line.substring("Winner:".length()).trim());
                mayorLineSeen = true;
            } else if (StringUtils.startsWithFast(line, "Current Mayor:")) {
                parsedCurrentMayor = Mayor.fromName(line.substring("Current Mayor:".length()).trim());
                mayorLineSeen = true;
            }

            if (StringUtils.startsWithFast(line, "Visitors")) {
                readingCropMilestone = false;
                readingVisitors = true;
                continue;
            }

            if (StringUtils.startsWithFast(line, "Crop Milestone")) {
                readingCropMilestone = true;
                parsedCropMilestone.add(formattedLine);
                continue;
            }

            if (readingCropMilestone) {
                parsedCropMilestone.add(formattedLine);
                continue;
            }

            if (readingVisitors) {
                if (StringUtils.startsWithFast(line, "Next Visitor")) {
                    readingVisitors = false;
                } else {
                    // Keep the rarity color from the server's visitor line for the overlay.
                    parsedActiveVisitors.add(formattedLine);
                    continue;
                }
            }

            // Garden pests are listed as: "Plots: 1, 2 x3".
            if (StringUtils.startsWithFast(line, "Plots:")) {
                pestPlotsLineSeen = true;
                parseActivePestPlots(line.substring("Plots:".length()), parsedActivePests);
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
                        SkyblockData.setSandbox(s.toLowerCase(Locale.ROOT).contains("_sandbox"));
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
                        StatBars.getSTATS().setSpeed(speed);
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

        if (SkyblockData.getCurrentLocation() == Location.GARDEN && pestPlotsLineSeen) {
            SkyblockData.setActivePests(new HashMap<>(parsedActivePests));
        } else {
            SkyblockData.setActivePests(new HashMap<>());
        }

        if (SkyblockData.getCurrentLocation() == Location.GARDEN) {
            SkyblockData.setActiveVisitors(new ArrayList<>(parsedActiveVisitors));
            SkyblockData.setCropMilestone(new ArrayList<>(parsedCropMilestone));
        } else {
            SkyblockData.setActiveVisitors(new ArrayList<>());
            SkyblockData.setCropMilestone(new ArrayList<>());
        }

        if (mayorLineSeen) {
            SkyblockData.setCurrentMayor(parsedCurrentMayor);
        }
    }

    private static void parseActivePestPlots(String plotsText, Map<Integer, Integer> output) {
        for (String entry : plotsText.trim().split(",")) {
            Matcher matcher = PEST_PLOT_ENTRY.matcher(entry.trim());
            if (!matcher.matches()) continue;

            int plot = NumberUtils.parseIntSafe(matcher.group(1));
            int count = matcher.group(2) == null ? 1 : NumberUtils.parseIntSafe(matcher.group(2));
            if (plot >= 1 && plot <= GARDEN_PLOTS.length && count > 0) {
                output.put(plot, count);
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (SkyblockData.getCurrentLocation() != Location.GARDEN) return;
        if (SkyblockData.getActivePests() == null || SkyblockData.getActivePests().isEmpty()) return;
        if (Config.feature == null || Config.feature.garden == null
                || !Config.feature.garden.enable || !Config.feature.garden.pestPlotGrill.enabled) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.theWorld == null) return;

        net.minecraft.entity.Entity viewer = mc.getRenderViewEntity();
        if (viewer == null) return;

        double playerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks;
        double playerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks;
        double playerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks;

        Color grillColor = ColorUtils.getColor(Config.feature.garden.pestPlotGrill.color);
        float red = grillColor.getRed() / 255.0F;
        float green = grillColor.getGreen() / 255.0F;
        float blue = grillColor.getBlue() / 255.0F;
        float alpha = grillColor.getAlpha() / 255.0F;
        double gridSpacing = Math.max(2.0D, Config.feature.garden.pestPlotGrill.gridSpacing);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GL11.glLineWidth(Math.max(1.0F, Config.feature.garden.pestPlotGrill.lineWidth));

            WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
            renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            for (Integer plotNumber : SkyblockData.getActivePests().keySet()) {
                if (plotNumber == null || plotNumber < 1 || plotNumber > GARDEN_PLOTS.length) continue;
                AxisAlignedBB plot = GARDEN_PLOTS[plotNumber - 1];

                addXPlaneGrill(renderer, plot, plot.minZ - PLOT_FACE_OFFSET, gridSpacing,
                        playerX, playerY, playerZ, red, green, blue, alpha);
                addXPlaneGrill(renderer, plot, plot.maxZ + PLOT_FACE_OFFSET, gridSpacing,
                        playerX, playerY, playerZ, red, green, blue, alpha);
                addZPlaneGrill(renderer, plot, plot.minX - PLOT_FACE_OFFSET, gridSpacing,
                        playerX, playerY, playerZ, red, green, blue, alpha);
                addZPlaneGrill(renderer, plot, plot.maxX + PLOT_FACE_OFFSET, gridSpacing,
                        playerX, playerY, playerZ, red, green, blue, alpha);
            }
            Tessellator.getInstance().draw();
        } finally {
            GL11.glLineWidth(1.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
    }

    private static void addXPlaneGrill(WorldRenderer renderer, AxisAlignedBB plot, double z,
                                       double gridSpacing, double playerX, double playerY, double playerZ,
                                       float red, float green, float blue, float alpha) {
        addLine(renderer, plot.minX, plot.minY, z, plot.maxX, plot.minY, z,
                playerX, playerY, playerZ, red, green, blue, alpha);
        addLine(renderer, plot.minX, plot.maxY, z, plot.maxX, plot.maxY, z,
                playerX, playerY, playerZ, red, green, blue, alpha);
        addLine(renderer, plot.minX, plot.minY, z, plot.minX, plot.maxY, z,
                playerX, playerY, playerZ, red, green, blue, alpha);
        addLine(renderer, plot.maxX, plot.minY, z, plot.maxX, plot.maxY, z,
                playerX, playerY, playerZ, red, green, blue, alpha);

        for (double x = plot.minX + gridSpacing; x < plot.maxX; x += gridSpacing) {
            addLine(renderer, x, plot.minY, z, x, plot.maxY, z,
                    playerX, playerY, playerZ, red, green, blue, alpha);
        }
        for (double y = plot.minY + gridSpacing; y < plot.maxY; y += gridSpacing) {
            addLine(renderer, plot.minX, y, z, plot.maxX, y, z,
                    playerX, playerY, playerZ, red, green, blue, alpha);
        }
    }

    private static void addZPlaneGrill(WorldRenderer renderer, AxisAlignedBB plot, double x,
                                       double gridSpacing, double playerX, double playerY, double playerZ,
                                       float red, float green, float blue, float alpha) {
        addLine(renderer, x, plot.minY, plot.minZ, x, plot.minY, plot.maxZ,
                playerX, playerY, playerZ, red, green, blue, alpha);
        addLine(renderer, x, plot.maxY, plot.minZ, x, plot.maxY, plot.maxZ,
                playerX, playerY, playerZ, red, green, blue, alpha);
        addLine(renderer, x, plot.minY, plot.minZ, x, plot.maxY, plot.minZ,
                playerX, playerY, playerZ, red, green, blue, alpha);
        addLine(renderer, x, plot.minY, plot.maxZ, x, plot.maxY, plot.maxZ,
                playerX, playerY, playerZ, red, green, blue, alpha);

        for (double z = plot.minZ + gridSpacing; z < plot.maxZ; z += gridSpacing) {
            addLine(renderer, x, plot.minY, z, x, plot.maxY, z,
                    playerX, playerY, playerZ, red, green, blue, alpha);
        }
        for (double y = plot.minY + gridSpacing; y < plot.maxY; y += gridSpacing) {
            addLine(renderer, x, y, plot.minZ, x, y, plot.maxZ,
                    playerX, playerY, playerZ, red, green, blue, alpha);
        }
    }

    private static void addLine(WorldRenderer renderer,
                                double x1, double y1, double z1,
                                double x2, double y2, double z2,
                                double playerX, double playerY, double playerZ,
                                float red, float green, float blue, float alpha) {
        renderer.pos(x1 - playerX, y1 - playerY, z1 - playerZ).color(red, green, blue, alpha).endVertex();
        renderer.pos(x2 - playerX, y2 - playerY, z2 - playerZ).color(red, green, blue, alpha).endVertex();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        SkyblockData.setCurrentLocation(Location.NONE);
        SkyblockData.setSandbox(false);
        SkyblockData.setCurrentMayor(Mayor.NONE);
        SkyblockData.setActivePests(new HashMap<>());
        SkyblockData.setActiveVisitors(new ArrayList<>());
        SkyblockData.setCropMilestone(new ArrayList<>());
    }


}

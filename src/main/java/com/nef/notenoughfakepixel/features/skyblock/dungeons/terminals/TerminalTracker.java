package com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.variables.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class TerminalTracker {
    private static final AxisAlignedBB GLOBAL_BOUNDS = new AxisAlignedBB(3, 100, 29, 120, 159, 150);
    private static final Map<Integer, Integer> TERMINAL_MAX = new HashMap<>();
    private static final int LEVER_MAX = 2;
    private static final int DEVICE_MAX = 1;

    static {
        TERMINAL_MAX.put(1, 4);
        TERMINAL_MAX.put(2, 5);
        TERMINAL_MAX.put(3, 4);
        TERMINAL_MAX.put(4, 4);
    }

    private final Map<Integer, Integer> terminalCounts = new HashMap<>();
    private final Map<Integer, Integer> deviceCounts = new HashMap<>();
    private final Map<Integer, Integer> leverCounts = new HashMap<>();
    private int currentPhase = 1;
    private final Map<Integer, Boolean> gateOpened = new HashMap<>();
    private final Map<String, Integer> playerTerminals = new HashMap<>();
    private final Map<String, Integer> playerDevices = new HashMap<>();
    private final Map<String, Integer> playerLevers = new HashMap<>();

    // Regex patterns
    private static final Pattern DEVICE_PATTERN = Pattern.compile("^(?:\\[[^\\]]+\\] )?(\\w+) completed a device! \\(\\d+/\\d+\\)$");
    private static final Pattern TERMINAL_PATTERN = Pattern.compile("^(?:\\[[^\\]]+\\] )?(\\w+) activated a terminal! \\(\\d+/\\d+\\)$");
    private static final Pattern LEVER_PATTERN = Pattern.compile("^(?:\\[[^\\]]+\\] )?(\\w+) activated a lever! \\(\\d+/\\d+\\)$");
    private static final Pattern GATE_PATTERN = Pattern.compile("The gate will open in 5 seconds!");

    public TerminalTracker() {
        resetCounts();
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Config.feature.dungeons.dungeonsTerminalTracker) {
            return;
        }
        if (!DungeonManager.checkEssentialsF7()) return;

        String message = event.message.getUnformattedText();
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (message.startsWith("The Core entrance is opening!")) {
            for (String name : playerTerminals.keySet()) {
                int terminals = playerTerminals.getOrDefault(name, 0);
                int devices = playerDevices.getOrDefault(name, 0);
                int levers = playerLevers.getOrDefault(name, 0);

                String msg = String.format("§b[§aNEF§b] §6%s§7, Terminals: §f%d§7, Devices: §f%d§7, Levers: §f%d§7.",
                        name, terminals, devices, levers);

                Minecraft.getMinecraft().thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(msg));
            }
        }

        if (player == null || TablistParser.currentLocation != Location.DUNGEON) {
            return;
        }

        if (!isPlayerInBounds(player)) {
            return;
        }

        Matcher gateMatcher = GATE_PATTERN.matcher(message);
        if (gateMatcher.matches()) {
            gateOpened.put(currentPhase, true);
            if (currentPhase < 4) {
                currentPhase++;
            }
            return;
        }

        Matcher deviceMatcher = DEVICE_PATTERN.matcher(message);
        if (deviceMatcher.matches() && deviceCounts.get(currentPhase) < DEVICE_MAX) {
            String name = deviceMatcher.group(1);
            deviceCounts.put(currentPhase, deviceCounts.get(currentPhase) + 1);
            playerDevices.put(name, playerDevices.getOrDefault(name, 0) + 1);
            return;
        }

        Matcher terminalMatcher = TERMINAL_PATTERN.matcher(message);
        if (terminalMatcher.matches() && terminalCounts.get(currentPhase) < TERMINAL_MAX.get(currentPhase)) {
            String name = terminalMatcher.group(1);
            terminalCounts.put(currentPhase, terminalCounts.get(currentPhase) + 1);
            playerTerminals.put(name, playerTerminals.getOrDefault(name, 0) + 1);
            return;
        }

        Matcher leverMatcher = LEVER_PATTERN.matcher(message);
        if (leverMatcher.matches() && leverCounts.get(currentPhase) < LEVER_MAX) {
            String name = leverMatcher.group(1);
            leverCounts.put(currentPhase, leverCounts.get(currentPhase) + 1);
            playerLevers.put(name, playerLevers.getOrDefault(name, 0) + 1);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (!Config.feature.dungeons.dungeonsTerminalTracker) {
            return;
        }
        if (!DungeonManager.checkEssentialsF7()) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null || TablistParser.currentLocation != Location.DUNGEON) {
            return;
        }

        if (!isPlayerInBounds(player)) {
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int overlayWidth = 1;
        int overlayHeight = 1;
        float scale = Config.feature.dungeons.dungeonsTerminalTrackerScale;
        int x = Config.feature.dungeons.terminalTrackerPos.getAbsX(sr, overlayWidth);
        int y = Config.feature.dungeons.terminalTrackerPos.getAbsY(sr, overlayHeight);
        int lineHeight = (int) (10 * scale) + 4;

        String phaseLine = String.format("Phase %d", currentPhase);
        String terminalLine = String.format("Terminals: %d/%d", terminalCounts.get(currentPhase), TERMINAL_MAX.get(currentPhase));
        String deviceLine = String.format("Devices: %d/%d", deviceCounts.get(currentPhase), DEVICE_MAX);
        String leverLine = String.format("Levers: %d/%d", leverCounts.get(currentPhase), LEVER_MAX);

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        float scaledX = x / scale;
        float scaledY = y / scale;

        mc.fontRendererObj.drawStringWithShadow(phaseLine, scaledX, scaledY, ColorUtils.getColor(Config.feature.dungeons.dungeonsTerminalTrackerColor).getRGB());
        mc.fontRendererObj.drawStringWithShadow(terminalLine, scaledX, scaledY + lineHeight / scale, ColorUtils.getColor(Config.feature.dungeons.dungeonsTerminalTrackerColor).getRGB());
        mc.fontRendererObj.drawStringWithShadow(deviceLine, scaledX, scaledY + 2 * lineHeight / scale, ColorUtils.getColor(Config.feature.dungeons.dungeonsTerminalTrackerColor).getRGB());
        mc.fontRendererObj.drawStringWithShadow(leverLine, scaledX, scaledY + 3 * lineHeight / scale, ColorUtils.getColor(Config.feature.dungeons.dungeonsTerminalTrackerColor).getRGB());

        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        resetCounts();
        playerTerminals.clear();
        playerDevices.clear();
        playerLevers.clear();
    }

    private void resetCounts() {
        for (int phase = 1; phase <= 4; phase++) {
            terminalCounts.put(phase, 0);
            deviceCounts.put(phase, 0);
            leverCounts.put(phase, 0);
            gateOpened.put(phase, false);
        }
        currentPhase = 1;
    }

    private boolean isPlayerInBounds(EntityPlayerSP player) {
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        return x >= GLOBAL_BOUNDS.minX && x <= GLOBAL_BOUNDS.maxX &&
                y >= GLOBAL_BOUNDS.minY && y <= GLOBAL_BOUNDS.maxY &&
                z >= GLOBAL_BOUNDS.minZ && z <= GLOBAL_BOUNDS.maxZ;
    }
}
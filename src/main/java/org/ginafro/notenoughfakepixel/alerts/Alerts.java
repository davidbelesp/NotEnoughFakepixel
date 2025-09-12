package org.ginafro.notenoughfakepixel.alerts;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.SoundUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class Alerts {

    public static List<Alert> alerts = new ArrayList<>();
    public static HashMap<Alert, Pattern> patterns = new HashMap<>();
    public static String configFile = Config.configDirectory + "/nefalerts.json";

    private static String displayText = "";
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static long endTime = 0;

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (event.type == 2) return;

        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

        for (Alert alert : alerts) {
            if (!alert.toggled) continue;

            boolean location;
            switch (alert.location) {
                case "Skyblock":
                    location = ScoreboardUtils.currentGamemode.isSkyblock();
                    break;
                case "Dungeons":
                    location = TablistParser.currentLocation.isDungeon();
                    break;
                default:
                    location = true;
            }
            if (!location) continue;

            String alertText = alert.alert;

            if (alert.mode.equals("Regex")) {
                Matcher matcher = patterns.get(alert).matcher(message);
                if (matcher.matches()) {
                    matcher.reset();

                    int i = 0;
                    while (matcher.find()) {
                        for (int j = 0; j <= matcher.groupCount(); j++) {
                            alertText = alertText.replace("$$" + i + "$$", matcher.group(j));
                            i++;
                        }
                    }
                    SoundUtils.playSound(
                            mc.thePlayer.getPosition(),
                            "note.pling",
                            2.0F,
                            2.0F
                    );
                    showCustomOverlay(EnumChatFormatting.RED + alertText.replace("&", "§"), 2000);
                    return;
                }
            } else {
                boolean trigger;
                switch (alert.mode) {
                    case "Starts With":
                        trigger = message.startsWith(alert.message);
                        break;
                    case "Contains":
                        trigger = message.contains(alert.message);
                        break;
                    case "Ends With":
                        trigger = message.endsWith(alert.message);
                        break;
                    default:
                        continue;
                }

                if (trigger) {
                    SoundUtils.playSound(
                            mc.thePlayer.getPosition(),
                            "note.pling",
                            2.0F,
                            2.0F
                    );
                    showCustomOverlay(EnumChatFormatting.RED + alertText.replace("&", "§"), 2000);
                    return;
                }
            }
        }
    }

    private void showCustomOverlay(String text, int durationMillis) {
        displayText = text;
        endTime = System.currentTimeMillis() + durationMillis;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (System.currentTimeMillis() > endTime) return;

        FontRenderer fr = mc.fontRendererObj;

        int screenWidth = event.resolution.getScaledWidth();
        int screenHeight = event.resolution.getScaledHeight();

        GlStateManager.pushMatrix();
        GlStateManager.scale(6.0F, 6.0F, 6.0F);
        int textWidth = fr.getStringWidth(displayText);
        int x = (screenWidth / 12) - (textWidth / 2);
        int y = (screenHeight / 12) - 15;
        fr.drawStringWithShadow(displayText, x, y, 0xFF5555);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null) displayText = "";
    }

    public static void save() {
        for (Alert alert : alerts) {
            if (alert.mode.equals("Regex")) {
                Pattern pattern = Pattern.compile(alert.message);
                patterns.put(alert, pattern);
            }
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(alerts, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void load() {
        File file = new File(configFile);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                List<Alert> loadedAlerts = new GsonBuilder().create().fromJson(
                        reader,
                        new TypeToken<List<Alert>>() {
                        }.getType()
                );
                if (loadedAlerts != null) {
                    alerts.clear(); // Clear existing alerts
                    alerts.addAll(loadedAlerts); // Load new ones
                    // Rebuild patterns for Regex alerts
                    for (Alert alert : alerts) {
                        if (alert.mode.equals("Regex")) {
                            Pattern pattern = Pattern.compile(alert.message);
                            patterns.put(alert, pattern);
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Failed to load alerts from " + configFile);
            }
        } else {
            System.out.println("No alerts file found at " + configFile + ", starting with empty list");
        }
    }

    public static class Alert {
        public String mode;
        public String location;
        public String message;
        public String alert;
        public boolean toggled;

        public Alert(String mode, String location, String message, String alert, boolean toggled) {
            this.mode = mode;
            this.location = location;
            this.message = message;
            this.alert = alert;
            this.toggled = toggled;
        }

        public void toggle() {
            toggled = !toggled;
        }
    }
}
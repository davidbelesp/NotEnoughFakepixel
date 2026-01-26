package com.nef.notenoughfakepixel.features.skyblock.slayers;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.overlays.Overlay;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.Logger;
import com.nef.notenoughfakepixel.variables.Location;
import com.nef.notenoughfakepixel.variables.Slayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RegisterEvents
public class SlayerOverlay extends Overlay {

    @Override
    public boolean shouldShow() {
        if (!SkyblockData.isSkyblock()) return false;
        if (!SkyblockData.isSlayerActive()) return false;
        if (!isValidLocation()) return false;
        return Config.feature.slayer.slayerOverlay;
    }

    private boolean isValidLocation() {
        return SkyblockData.getCurrentLocation() != Location.PRIVATE_ISLAND;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!shouldShow()) return;
        if (getLines().isEmpty()) return;

        float scale = Config.feature.slayer.slayerOverlayScale;
        float width = getWidth(scale, getLines());
        float height = getHeight(scale, getLines());

        float x = Config.feature.slayer.slayerOverlayPos.getAbsX(new ScaledResolution(Minecraft.getMinecraft()), (int) width) - width/2;
        float y = Config.feature.slayer.slayerOverlayPos.getAbsY(new ScaledResolution(Minecraft.getMinecraft()), (int) height ) - height/2;
        draw(
                x,
                y,
                Config.feature.slayer.slayerOverlayScale,
                Config.feature.slayer.slayerOverlayBackgroundColor
        );
    }

    @Override
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        Slayer currentSlayer = SkyblockData.getCurrentSlayer();
        int itemCount = 0;
        if (currentSlayer != Slayer.NONE) {
            lines.add("\u00a74" + currentSlayer.getName() + "\u00a7r Slayer");
        }
        if (SkyblockData.getSlayerLevel() > 0) {
            if (SkyblockData.getSlayerLevel() == 9) {
                lines.add("\u00a77Level: \u00a7aMAX");
            } else {
                lines.add("\u00a77Level: \u00a74" + SkyblockData.getSlayerLevel());
            }

            itemCount++;
        }
        if (SkyblockData.getSlayerXp() > 0 && SkyblockData.getXpToNextLevel() > 0) {
            lines.add("\u00a77XP: \u00a7c" + SkyblockData.getSlayerXp() + " \u00a77/\u00a7a " + SkyblockData.getNextLevelXp());
            itemCount++;
        }
        if (SkyblockData.getRNGesusMeter() > 0) {
            lines.add("\u00a7dRNGesus Meter\u00a77:\u00a7d " + SkyblockData.getRNGesusMeter() + "%");
            itemCount++;
        }
        if (SkyblockData.getSessionBosses() > 0) {
            lines.add("\u00a77Bosses This Session: \u00a7e" + SkyblockData.getSessionBosses());
            itemCount++;
        }
        if (SkyblockData.getTotalSeconds() > 0 && SkyblockData.getSessionBosses() > 0) {
            double averageTime = SkyblockData.getTotalSeconds() / SkyblockData.getSessionBosses();
            int minutes = (int) averageTime / 60;
            int seconds = (int) averageTime % 60;
            lines.add("\u00a77Average Kill time: \u00a7e" + minutes + "m " + seconds + "s");
            itemCount++;
        }

        if (itemCount < 1) {
            return Collections.emptyList();
        }


        return lines;
    }

    @Override
    public float getWidth(float scale, List<String> lines) {
        return Math.max(getLongestLine(lines) * 5, MINIMUM_WIDTH) * scale;
    }

    @Override
    public float getHeight(float scale, List<String> lines) {
        return lines.size() * LINE_HEIGHT;
    }

    // Detecting slayer messages to update XP
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!shouldShow()) return;
        String message = event.message.getUnformattedText();
        if (message.contains("SLAYER QUEST COMPLETE!")) {
            SkyblockData.setSessionBosses(SkyblockData.getSessionBosses() + 1);
        }

        if (message.contains("SLAYER QUEST STARTED!") || (message.contains("» Slay ") && message.contains("XP worth of "))) {
            event.setCanceled(true);
        }

        if (message.contains("RNGesus Meter:")) {
            SkyblockData.setRNGesusMeter(Float.parseFloat(event.message.getUnformattedText().split(":")[1].trim().replace("%", "")));
            event.setCanceled(true);
        }

        if (message.contains("Slayer LVL")) {
            Slayer currentSlayer = SkyblockData.getCurrentSlayer();
            if (currentSlayer != Slayer.NONE) {
                try {

                    int slayerLevel = Integer.parseInt(message.split("Slayer LVL")[1].split("-")[0].trim());
                    SkyblockData.setSlayerLevel(slayerLevel);

                    if (slayerLevel == 9) {
                        SkyblockData.setSlayerXp(0);
                        SkyblockData.setNextLevelXp(0);
                        SkyblockData.setXpToNextLevel(0);
                    } else {
                        String nextXpPart = message.split("Next LVL in")[1].trim().split(" ")[0].replace(",", "");
                        int nextLevelXp = Integer.parseInt(nextXpPart);

                        SkyblockData.setXpToNextLevel(nextLevelXp);
                        int totalXpForCurrentLevel = currentSlayer.getAccumulatedXp(slayerLevel, nextLevelXp);

                        SkyblockData.setSlayerXp(totalXpForCurrentLevel);
                        SkyblockData.setNextLevelXp(currentSlayer.getNextLevelXp(slayerLevel));
                    }

                    event.setCanceled(true);

                } catch (NumberFormatException e) {
                    Logger.log("[SlayerOverlay] Failed to parse slayer level or XP from message: " + message);
                }
            }
        }

    }
}

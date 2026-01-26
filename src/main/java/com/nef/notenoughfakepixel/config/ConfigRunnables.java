package com.nef.notenoughfakepixel.config;

import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.WormSpawnTimer;
import com.nef.notenoughfakepixel.features.skyblock.qol.FairySouls;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.Logger;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.variables.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.awt.datatransfer.Clipboard;

public class ConfigRunnables {

    public static void runDebugRunnable(String runnableId) {
        if (equalsDebugId(runnableId, "triggerTimers")) {
            WormSpawnTimer.setGoalEpochMs(System.currentTimeMillis() + 30000);
            Logger.log("Set worm spawn to 30 seconds from now");
        }

        if (equalsDebugId(runnableId, "logLocation")) {
            Logger.log(SkyblockData.getCurrentLocation());
        }

        if (equalsDebugId(runnableId, "showAPI")) {
            String data = RepoHandler.getJson("fairysouls");
            if (data != null) {
                // copy to clipboard
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                clip.setContents(new java.awt.datatransfer.StringSelection(data), null);
                Logger.log("Copied API to clipboard! Length: " + data.length());
            }
        }

        if (equalsDebugId(runnableId, "showSBID")) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {
                ItemStack item = player.getHeldItem();
                if (item != null) {
                    Logger.log(ItemUtils.getInternalName(item));
                } else {
                    Logger.logError("No Held Item");
                }
            } else {
                Logger.logError("No Player");
            }
        }

        if (equalsDebugId(runnableId, "logScoreboard")) {
            ScoreboardUtils.getScoreboardLines().forEach(Logger::log);
        }

        if (equalsDebugId(runnableId, "logIsInSkyblock")) {
            Logger.log("Current Gamemode: " + SkyblockData.getCurrentGamemode() + " | Is in Skyblock: " +  SkyblockData.getCurrentGamemode().isSkyblock());
        }

        if (equalsDebugId(runnableId, "logSbData")) {
            Logger.log("\u00a7c=============================");
            Logger.log("Gamemode: \u00a7f" + SkyblockData.getCurrentGamemode().toString());
            Logger.log("Using Profile: \u00a7f" + SkyblockData.getCurrentProfile());
            Logger.log("\u00a72Location\u00a7f:");
            Logger.log(" - Current Location: \u00a7f" + SkyblockData.getCurrentLocation());
            Logger.log(" - Current Area: \u00a7f" + SkyblockData.getCurrentArea());
            Logger.log("\u00a74Slayer\u00a7f:");
            Logger.log(" - Has Slayer Active: \u00a7f" + SkyblockData.isSlayerActive());
            Logger.log(" - Current Slayer: \u00a7f" + SkyblockData.getCurrentSlayer());
            Logger.log(" - Slayer Level: \u00a7f" + SkyblockData.getSlayerLevel());
            Logger.log(" - Slayer XP: \u00a7f" + SkyblockData.getSlayerXp());
            Logger.log(" - Next Level XP: \u00a7f" + SkyblockData.getNextLevelXp());
            Logger.log(" - Session Bosses: \u00a7f" + SkyblockData.getSessionBosses());
            Logger.log(" - RNGesus Meter: \u00a7f" + SkyblockData.getRNGesusMeter());
            Logger.log("\u00a73Mining\u00a7f:");
            Logger.log(" - Mithril Powder: \u00a7f" + SkyblockData.getMithrilPowder());
            Logger.log(" - Gemstone Powder: \u00a7f" + SkyblockData.getGemstonePowder());
            Logger.log(" - Heat: \u00a7f" + SkyblockData.getHeat());
            Logger.log("\u00a7dTime\u00a7f:");
            Logger.log(" - Skyblock Hour: \u00a7f" + SkyblockData.getSbHour());
            Logger.log(" - Skyblock Minutes: \u00a7f" + SkyblockData.getSbMinute());
            Logger.log(" - AM/PM: \u00a7f" + (SkyblockData.isAm() ? "AM" : "PM"));
            Logger.log(" - Current Season: \u00a7f" + SkyblockData.getSeason());
            Logger.log("\u00a7c=============================");
        }
    }

    public static void runExecutableRunnable(String runnableId) {
        if (equalsExecutableId(runnableId, "fairySoulsReset")) {
            FairySouls.resetSoulData();
            Logger.log(Colors.GREEN, "Fairy Souls data has been reset.");
        }
    }

    // Helpers
    private static boolean equalsExecutableId(String id, String targetId) {
        return equalsRunnableId(id, targetId, "exec_");
    }
    private static boolean equalsDebugId(String id, String targetId) {
        return equalsRunnableId(id, targetId, "debug_");
    }
    private static boolean equalsRunnableId(String id, String targetId, String prefix) {
        return id != null && id.equals(prefix + targetId);
    }

}

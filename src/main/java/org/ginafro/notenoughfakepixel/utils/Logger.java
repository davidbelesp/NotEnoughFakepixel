package org.ginafro.notenoughfakepixel.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.variables.Constants;

import java.util.logging.Level;

public class Logger {

    private static long lastLogTime = 0;

    private Logger() {
        // Prevent instantiation
    }

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(Logger.class.getName());

    /**
     * Logs a message to the chat.
     *
     * @param message The String message to log.
     */
    public static void log(String message) {
        if (Config.feature.debug.debug && Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Constants.PREFIX + message));
        }
    }

    /**
     * Logs a message to the chat only once every second.
     *
     * @param message The String message to log.
     */
    public static void logOnlyOnce(String message) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastLogTime > 1000) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(message)
                );
                lastLogTime = currentTime;
            }
        }
    }

    /**
     * Logs an error message to the chat.
     *
     * @param message The String message to log.
     */
    public static void logError(String message) {
        if (Config.feature.debug.debug && Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Constants.ERROR_PREFIX + message));
        }
    }

    /**
     * Logs an object to the chat.
     *
     * @param object The object to log.
     */
    public static void log(Object object) {
        if (!Config.feature.debug.debug) return;
        try {
            log(object.toString());
        } catch (Exception e) {
            logConsole("Failed to log object: " + object.getClass().getName());
        }
    }

    /**
     * Logs a message to the console.
     *
     * @param message The String message to log.
     */
    public static void logConsole(String message) {
        if (!Config.feature.debug.debug) return;
        LOGGER.log(Level.INFO, message);
    }

    /**
     * Logs an error to the console with a new line.
     *
     * @param error The String message to log.
     */
    public static void logErrorConsole(String error) {
        if (!Config.feature.debug.debug) return;
        LOGGER.log(Level.WARNING, error);
    }

    /**
     * Logs an object to the console.
     *
     * @param object The object to log.
     */
    public static void logConsole(Object object) {
        if (!Config.feature.debug.debug) return;
        try {
            logConsole(object.toString());
        } catch (Exception e) {
            logConsole("Failed to log object: " + object.getClass().getName());
        }
    }

}

package com.nef.notenoughfakepixel.utils;

import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.variables.Constants;

import java.util.logging.Level;

public class Logger {

    private static final java.util.logging.Logger JUL =
            java.util.logging.Logger.getLogger(Logger.class.getName());

    private static long lastLogTime = 0;

    private Logger() {}

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(Logger.class.getName());

    @Setter
    private static volatile Boolean debugOverride = null;

    /* ----------------- Public API ----------------- */

    /**
     * Logs a message to the chat.
     *
     * @param message The String message to log.
     */
    public static void log(String message) {
        if (!isDebugEnabled()) return;
        if (!isClientChatReady()) return;
        try {
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(Constants.PREFIX + message)
            );
        } catch (Throwable ignored) {
            // If chat logging fails (e.g., headless), fall back to console
            logConsole(message);
        }
    }

    /**
     * Logs a message to the chat only once every second.
     *
     * @param message The String message to log.
     */
    public static void logOnlyOnce(String message) {
        if (!isClientChatReady()) return;
        long now = System.currentTimeMillis();
        if (now - lastLogTime > 1000L) {
            try {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Constants.PREFIX + message));
            } catch (Throwable ignored) {
                // ignore
            }
            lastLogTime = now;
        }
    }

    /**
     * Logs an error message to the chat.
     *
     * @param message The String message to log.
     */
    public static void logError(String message) {
        if (!isDebugEnabled()) return;
        if (!isClientChatReady()) return;
        try {
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(Constants.ERROR_PREFIX + message)
            );
        } catch (Throwable ignored) {
            logErrorConsole(message);
        }
    }

    /**
     * Logs an object to the chat.
     *
     * @param object The object to log.
     */
    public static void log(Object object) {
        log(object.toString());
    }

    /**
     * Logs a message to the console.
     *
     * @param message The String message to log.
     */
    public static void logConsole(String message) {
        if (!isDebugEnabled()) return;
        safeJulLog(Level.INFO, message);
    }

    /**
     * Logs an error to the console with a new line.
     *
     * @param error The String message to log.
     */
    public static void logErrorConsole(String error) {
        if (!isDebugEnabled()) return;
        safeJulLog(Level.WARNING, error);
    }

    /**
     * Logs an object to the console.
     *
     * @param object The object to log.
     */
    public static void logConsole(Object object) {
        if (!isDebugEnabled()) return;
        try {
            logConsole(String.valueOf(object));
        } catch (Throwable t) {
            safeJulLog(Level.WARNING, "Failed to log object: " + (object == null ? "null" : object.getClass().getName()));
        }
    }

    /** Never throws; if JUL has issues, fallback to stdout/stderr. */
    private static void safeJulLog(Level level, String msg) {
        String text = String.valueOf(msg);
        try {
            JUL.log(level, text);
        } catch (Throwable __) {
            if (level.intValue() >= Level.WARNING.intValue()) {
                System.err.println("[NEF][ERR] " + text);
            } else {
                System.out.println("[NEF] " + text);
            }
        }
    }

    private static boolean isDebugEnabled() {
        if (debugOverride != null) return debugOverride;
        try {
            // Guard every hop to avoid NPEs during early lifecycle
            return Config.feature.debug.debug;
        } catch (Throwable __) {
            return false;
        }
    }

    /** Safe check: only true when chat is actually usable. */
    private static boolean isClientChatReady() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            return mc != null && mc.thePlayer != null && mc.getNetHandler() != null;
        } catch (Throwable __) {
            return false;
        }
    }

}

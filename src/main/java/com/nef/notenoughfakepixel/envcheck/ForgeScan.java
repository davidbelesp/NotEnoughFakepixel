package com.nef.notenoughfakepixel.envcheck;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Objects;

public class ForgeScan {

    private ForgeScan() {
        // Prevent instantiation
    }

    /*
     * This class is responsible for checking the environment in which the mod is running.
     * It checks if the mod is running on the correct version of Forge.
     * If the mod is not running on the correct version of Forge, it will display an error message.
     */

    static boolean shouldCheckOnce = true;

    public static void checkEnvironmentOnce() {
        if (shouldCheckOnce) checkEnvironment();
    }

    static void checkEnvironment() {
        shouldCheckOnce = false;
        checkForgeEnvironment();
    }

    static Class<?> tryToGetClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    static boolean isAtLeast(Object left, int right) {
        if (left instanceof Integer) {
            return (Integer) left >= right;
        }
        return false;
    }

    static Object tryToGetField(Class<?> clazz, Object inst, String name) {
        if (clazz == null) return null;
        try {
            Field declaredField = clazz.getDeclaredField(name);
            return declaredField.get(inst);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return null;
    }

    static void missingOrOutdatedForgeError() {
        showErrorMessage(
                "You just launched NotEnoughFakepixel with the wrong (or no) modloader installed.",
                "",
                "NotEnoughFakepixel only works in Minecraft 1.8.9, with Forge 11.15.1+",
                "Please relaunch NotEnoughFakepixel in the correct environment.",
                "If you are using Minecraft 1.8.9 with Forge 11.15.1+ installed, please contact support.",
                "Click OK to launch anyways."
        );
    }

    public static void showErrorMessage(String... messages) {
        String message = String.join("\n", messages);
        System.setProperty("java.awt.headless", "false");
        JOptionPane.showMessageDialog(
                null, message, "NotEnoughFakepixel - Problematic System Configuration", JOptionPane.ERROR_MESSAGE
        );
    }

    static void checkForgeEnvironment() {
        Class<?> forgeVersion = tryToGetClass("net.minecraftforge.common.ForgeVersion");
        if (forgeVersion == null
                || !Objects.equals(tryToGetField(forgeVersion, null, "majorVersion"), 11)
                || !Objects.equals(tryToGetField(forgeVersion, null, "minorVersion"), 15)
                || !isAtLeast(tryToGetField(forgeVersion, null, "revisionVersion"), 1)
                || !Objects.equals(tryToGetField(forgeVersion, null, "mcVersion"), "1.8.9")
        ) {

            System.out.printf("Forge Version : %s%nMajor : %s%nMinor : %s%nRevision : %s%nMinecraft : %s%n",
                    forgeVersion,
                    tryToGetField(forgeVersion, null, "majorVersion"),
                    tryToGetField(forgeVersion, null, "minorVersion"),
                    tryToGetField(forgeVersion, null, "revisionVersion"),
                    tryToGetField(forgeVersion, null, "mcVersion")
            );
            missingOrOutdatedForgeError();
        }
    }
}
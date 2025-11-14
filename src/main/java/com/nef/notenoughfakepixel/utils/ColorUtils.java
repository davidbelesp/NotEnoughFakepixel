package com.nef.notenoughfakepixel.utils;

import com.nef.notenoughfakepixel.config.gui.core.ChromaColour;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ColorUtils {
    private static final String DEFAULT_COLOR_STRING = "0:255:255:255:255"; // Opaque white as fallback
    private static final Color DEFAULT_COLOR = new Color(255, 255, 255, 255);

    /**
     * Converts a chroma color string to a Color object.
     * Expects format "chromaSpeed:alpha:r:g:b" (e.g., "0:0:0:0:0").
     * Falls back to opaque white if the string is invalid.
     *
     * @param colorString The chroma color string
     * @return A Color object representing the parsed ARGB value
     */
    public static @NotNull Color getColor(String colorString) {
        // Handle null or malformed input
        if (colorString == null || colorString.split(":").length != 5) {
            System.err.println("Invalid color string: " + colorString + ". Expected 'chromaSpeed:alpha:r:g:b'. Using default: " + DEFAULT_COLOR_STRING);
            return DEFAULT_COLOR;
        }

        try {
            int argb = ChromaColour.specialToChromaRGB(colorString);
            return new Color(argb, true); // ARGB format
        } catch (Exception e) {
            System.err.println("Error parsing color string: " + colorString + ". Error: " + e.getMessage() + ". Using default: " + DEFAULT_COLOR_STRING);
            return DEFAULT_COLOR;
        }
    }

    public static String cleanColor(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    /**
     * Converts a Color object to a hexadecimal string.
     *
     * @param color        The Color object to convert
     * @param includeAlpha Whether to include the alpha channel in the hex string
     * @return A hex string (e.g., "#FF0000" or "#FF0000FF")
     */
    public static String colorToHex(Color color, boolean includeAlpha) {
        if (color == null) {
            return includeAlpha ? "#FFFFFFFF" : "#FFFFFF"; // Default to white
        }
        if (includeAlpha) {
            return String.format("#%08X", color.getRGB()); // ARGB format
        } else {
            return String.format("#%06X", color.getRGB() & 0xFFFFFF); // RGB only
        }
    }
}
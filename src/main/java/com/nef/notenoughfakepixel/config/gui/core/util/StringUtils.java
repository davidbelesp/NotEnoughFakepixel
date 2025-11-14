package com.nef.notenoughfakepixel.config.gui.core.util;

public class StringUtils {

    public static String cleanColour(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    public static String joinStrings(String[] arr, int start) {
        if (start >= arr.length) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            if (i > start) sb.append(' ');
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String joinRange(String[] arr, int startInclusive, int endExclusive) {
        StringBuilder sb = new StringBuilder();
        for (int i = startInclusive; i < endExclusive; i++) {
            if (i > startInclusive) sb.append(' ');
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String clean(String s) {
        return s.replace('\u00A0', ' ')
                .replace('\u2007', ' ')
                .replace('\u202F', ' ')
                .trim();
    }

}

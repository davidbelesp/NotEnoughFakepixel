package com.nef.notenoughfakepixel.utils;

import java.util.Locale;

public class NumberUtils {

    public static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static int parseTrailingInt(String s, int start) {
        int i = start, n = s.length(), val = 0, digits = 0;
        while (i < n) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') break;
            val = (val * 10) + (c - '0');
            i++; digits++;
            if (digits > 6) break;
        }
        return digits == 0 ? -1 : val;
    }

    public static double parseDoubleFlexible(String s) {
        return Double.parseDouble(s.replace(',', '.'));
    }

    public static String fmt1(double d) {
        return String.format(Locale.ROOT, "%.1f", d);
    }

    public static boolean basicallyEqual(double num1, double num2, double dist) {
        return Math.abs(num1 - num2) < dist;
    }

    public static double truncateTwoDecimalPlaces(double value) {
        return Math.floor(value * 100) / 100;
    }

}

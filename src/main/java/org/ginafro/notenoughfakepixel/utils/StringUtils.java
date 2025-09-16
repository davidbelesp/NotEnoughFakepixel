package org.ginafro.notenoughfakepixel.utils;

import net.minecraft.scoreboard.Score;

import java.util.*;

public class StringUtils {

    public static boolean isNumeric(String string) {
        return string != null && !string.isEmpty() && string.chars().allMatch(Character::isDigit);
    }

    public static <T> Map<String, T> subMapWithKeysThatAreSuffixes(String prefix, NavigableMap<String, T> map) {
        return "".equals(prefix) ? map : map.subMap(prefix, true, createLexicographicallyNextStringOfTheSameLength(prefix), false);
    }

    public static String createLexicographicallyNextStringOfTheSameLength(String input) {
        return input.substring(0, input.length() - 1) + (char) (input.charAt(input.length() - 1) + 1);
    }

    public static boolean containsSubstring(String[] keywords, String itemName) {
        return Arrays.stream(keywords).anyMatch(itemName::contains);
    }

    public static boolean startsWithFast(String s, String prefix) {
        return s.length() >= prefix.length() && s.startsWith(prefix);
    }

    public static String sliceAfter(String s, String prefix) {
        return (s.length() > prefix.length()) ? s.substring(prefix.length()) : "";
    }

    public static String removeChars(String s, String chars) {
        if (s.isEmpty() || chars.isEmpty()) return s;
        StringBuilder sb = new StringBuilder(s.length());
        outer:
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            for (int j = 0; j < chars.length(); j++) {
                if (c == chars.charAt(j)) continue outer;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static int indexOfDashDigits(String s) {
        int idx = s.indexOf('-');
        if (idx < 0 || idx + 1 >= s.length()) return -1;
        int i = idx + 1; boolean hasDigit = false;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') { hasDigit = true; i++; }
            else break;
        }
        return hasDigit ? idx : -1;
    }

    public static int hashBoard(String display, List<Score> scores) {
        int h = 17;
        h = 31 * h + (display == null ? 0 : display.hashCode());
        final int limit = Math.min(20, scores.size());
        for (int i = 0; i < limit; i++) {
            final String pn = scores.get(i).getPlayerName();
            h = 31 * h + (pn == null ? 0 : pn.hashCode());
        }
        h = 31 * h + scores.size();
        return h;
    }

    public static String stripFormattingFast(final String in) {
        if (in == null) return "";
        final int n = in.length();
        final StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            char c = in.charAt(i);
            if (c == '§' || c == '&') {
                if (i + 1 < n) i++;
                continue;
            }
            sb.append(c);
        }
        return sb.toString().toLowerCase(java.util.Locale.ROOT);
    }


}

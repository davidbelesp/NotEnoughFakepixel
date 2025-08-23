package org.ginafro.notenoughfakepixel.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;

public class StringUtils {

    public static String cleanColor(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

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

}

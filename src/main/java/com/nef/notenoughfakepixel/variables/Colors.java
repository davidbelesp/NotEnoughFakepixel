package com.nef.notenoughfakepixel.variables;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Colors {

    // This is a list of colors that are used in minecraft

    BLACK("\u00A70"),
    DARK_BLUE("\u00A71"),
    DARK_GREEN("\u00A72"),
    DARK_AQUA("\u00A73"),
    DARK_RED("\u00A74"),
    DARK_PURPLE("\u00A75"),
    GOLD("\u00A76"),
    GRAY("\u00A77"),
    DARK_GRAY("\u00A78"),
    BLUE("\u00A79"),
    GREEN("\u00A7a"),
    AQUA("\u00A7b"),
    RED("\u00A7c"),
    LIGHT_PURPLE("\u00A7d"),
    YELLOW("\u00A7e"),
    WHITE("\u00A7f"),

    // This is a list of formatters
    BOLD("\u00A7l"),
    STRIKETHROUGH("\u00A7m"),
    UNDERLINE("\u00A7n"),
    ITALIC("\u00A7o"),
    RESET("\u00A7r");

    private final String code;

    @Override
    public String toString() {
        return code;
    }

}


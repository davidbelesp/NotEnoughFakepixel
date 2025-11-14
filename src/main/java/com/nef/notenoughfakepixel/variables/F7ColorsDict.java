package com.nef.notenoughfakepixel.variables;

public enum F7ColorsDict {

    NONE("none", -1, -1),
    WHITE("white", 0, 15),
    ORANGE("orange", 1, 14),
    MAGENTA("magenta", 2, 13),
    LIGHT_BLUE("light blue", 3, 12),
    YELLOW("yellow", 4, 11),
    LIME("lime", 5, 10),
    PINK("pink", 6, 9),
    GRAY("gray", 7, 8),
    LIGHT_GRAY("silver", 8, 7),
    CYAN("cyan", 9, 6),
    PURPLE("purple", 10, 5),
    BLUE("blue", 11, 4),
    BROWN("brown", 12, 3),
    GREEN("green", 13, 2),
    RED("red", 14, 1),
    BLACK("black", 15, 0);

    final String color;
    final int main;
    final int dye;

    F7ColorsDict(String color, int main, int dye) {
        this.color = color;
        this.main = main;
        this.dye = dye;
    }

    public static F7ColorsDict getColorFromMain(int main) {
        for (F7ColorsDict color : F7ColorsDict.values()) {
            if (color.main == main) {
                return color;
            }
        }
        return NONE;
    }

    public static F7ColorsDict getColorFromDye(int dye) {
        for (F7ColorsDict color : F7ColorsDict.values()) {
            if (color.dye == dye) {
                return color;
            }
        }
        return NONE;
    }

    @Override
    public String toString() {
        return this.color;
    }

}

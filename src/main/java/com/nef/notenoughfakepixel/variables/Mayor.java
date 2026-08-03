package com.nef.notenoughfakepixel.variables;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum Mayor {
    NONE("None"),
    AATROX("Aatrox"),
    COLE("Cole"),
    DIANA("Diana"),
    DIAZ("Diaz"),
    FINNEGAN("Finnegan"),
    MARINA("Marina"),
    DERPY("Derpy"),
    FOXY("Foxy"),
    SCORPIUS("Scorpius"),
    JERRY("Jerry"),
    PAUL("Paul");

    private final String name;

    Mayor(String name) {
        this.name = name;
    }

    public static Mayor fromName(String value) {
        if (value == null) return NONE;

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (Mayor mayor : values()) {
            if (mayor.name.toLowerCase(Locale.ROOT).equals(normalized)) {
                return mayor;
            }
        }
        return NONE;
    }
}

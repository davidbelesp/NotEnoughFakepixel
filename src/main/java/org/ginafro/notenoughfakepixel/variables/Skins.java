package org.ginafro.notenoughfakepixel.variables;

import java.util.Arrays;

public enum Skins {

    ENDERMAN_HEAD("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdhNTliYjBhN2EzMjk2NWIzZDkwZDhlYWZhODk5ZDE4MzVmNDI0NTA5ZWFkZDRlNmI3MDlhZGE1MGI5Y2YifX19"),

    EASTER_EGG_BREAKFAST("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQ5MzMzZDg1YjhhMzE1ZDAzMzZlYjJkZjM3ZDhhNzE0Y2EyNGM1MWI4YzYwNzRmMWI1YjkyN2RlYjUxNmMyNCJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E0OTMzM2Q4NWI4YTMxNWQwMzM2ZWIyZGYzN2Q4YTcxNGNhMjRjNTFiOGM2MDc0ZjFiNWI5MjdkZWI1MTZjMjQifX19"),
    EASTER_EGG_LUNCH("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FlNmQyZDMxZDgxNjdiY2FmOTUyOTNiNjhhNGFjZDg3MmQ2NmU3NTFkYjVhMzRmMmNiYzY3NjZhMDM1NmQwYSJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdhZTZkMmQzMWQ4MTY3YmNhZjk1MjkzYjY4YTRhY2Q4NzJkNjZlNzUxZGI1YTM0ZjJjYmM2NzY2YTAzNTZkMGEifX19"),
    EASTER_EGG_DINNER("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJjZDVkZjlkN2YxZmE4MzQxZmNjZTJmM2MxMThlMmY1MTdlNGQyZDk5ZGYyYzUxZDYxZDkzZWQ3ZjgzZTEzIn19fQ==", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IyY2Q1ZGY5ZDdmMWZhODM0MWZjY2UyZjNjMTE4ZTJmNTE3ZTRkMmQ5OWRmMmM1MWQ2MWQ5M2VkN2Y4M2UxMyJ9fX0="),

    FLAMING_FIST("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzcxNzkzM2M0MGZiZjkzNmFhOTI4ODUxM2VmZTE5YmRhNDYwMWVmYzBlNGVjYWQyZTAyM2IwYzFkMjg0NDRiIn19fQ==", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzc3MTc5MzNjNDBmYmY5MzZhYTkyODg1MTNlZmUxOWJkYTQ2MDFlZmMwZTRlY2FkMmUwMjNiMGMxZDI4NDQ0YiJ9fX0="),

    RED_RELIC("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIwZWYwNmRkNjA0OTk3NjZhYzhjZTE1ZDJiZWE0MWQyODEzZmU1NTcxODg2NGI1MmRjNDFjYmFhZTFlYTkxMyJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyMGVmMDZkZDYwNDk5NzY2YWM4Y2UxNWQyYmVhNDFkMjgxM2ZlNTU3MTg4NjRiNTJkYzQxY2JhYWUxZWE5MTMifX19"),
    GREEN_RELIC("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQwNjNiYTViMTZiNzAzMGEyMGNlNmYwZWE5NmRjZDI0YjA2NDgzNmY1NzA0NTZjZGJmYzllODYxYTc1ODVhNSJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM0MDYzYmE1YjE2YjcwMzBhMjBjZTZmMGVhOTZkY2QyNGIwNjQ4MzZmNTcwNDU2Y2RiZmM5ZTg2MWE3NTg1YTUifX19"),
    ORANGE_RELIC("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWFjZTZiYjNhYTRjY2FjMDMxMTY4MjAyZjZkNDUzMjU5N2JjYWM2MzUxMDU5YWJkOWQxMGIyODYxMDQ5M2FlYiJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FhY2U2YmIzYWE0Y2NhYzAzMTE2ODIwMmY2ZDQ1MzI1OTdiY2FjNjM1MTA1OWFiZDlkMTBiMjg2MTA0OTNhZWIifX19"),
    PURPLE_RELIC("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZiMTBkMDkzMzBlMGY3MDA4MTVlNGQwNmU2NmI1ZDc2Nzc2NThiOWNhZDA4NTU1YmE3ZDg3YzEzYjI4OGQwZCJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU2YjEwZDA5MzMwZTBmNzAwODE1ZTRkMDZlNjZiNWQ3Njc3NjU4YjljYWQwODU1NWJhN2Q4N2MxM2IyODhkMGQifX19"),
    BLUE_RELIC("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRlNzE2NzFkYjVmNjlkMmM0NmEwZDcyNzY2YjI0OWMxMjM2ZDcyNjc4MmMwMGEwZTIyNjY4ZGY1NzcyZDRiOSJ9fX0=", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U0ZTcxNjcxZGI1ZjY5ZDJjNDZhMGQ3Mjc2NmIyNDljMTIzNmQ3MjY3ODJjMDBhMGUyMjY2OGRmNTc3MmQ0YjkifX19");

    private final String https;
    private final String http;

    Skins(String http, String https) {
        this.http = http;
        this.https = https;
    }

    public String getHttp() {
        return this.http;
    }

    public String getHttps() {
        return this.https;
    }

    public static Skins getSkinByName(String name) {
        return Arrays.stream(Skins.values())
                .filter(skin -> skin.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static Skins getSkinByValue(String value) {
        return Arrays.stream(Skins.values())
                .filter(skin -> skin.getHttps().equals(value) || skin.getHttp().equals(value))
                .findFirst()
                .orElse(null);
    }

    public static boolean equalsSkin(String value, Skins skin) {
        return skin != null && (skin.getHttps().equals(value) || skin.getHttp().equals(value));
    }

}

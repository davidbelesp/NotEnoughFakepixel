package org.ginafro.notenoughfakepixel.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static Field findField(Class<?> c, String... names) {
        for (String n : names) {
            try {
                Field f = c.getDeclaredField(n);
                f.setAccessible(true);
                return f;
            } catch (Throwable ignored) {}
        }
        return null;
    }

    public static int getInt(Field f, Object o, int def) {
        try { return f != null ? f.getInt(o) : def; } catch (Throwable t) { return def; }
    }

    public static final class Ref {
        public static int intField(Class<?> owner, String name, Object instance) {
            try {
                Field f = owner.getDeclaredField(name);
                f.setAccessible(true);
                return f.getInt(instance);
            } catch (Exception e) {
                return 0;
            }
        }
    }

}

package com.nef.notenoughfakepixel.utils;

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

    public static int getInt(Field f, Object o, int fallback) {
        try { return f.getInt(o); } catch (Throwable t) { return fallback; }
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

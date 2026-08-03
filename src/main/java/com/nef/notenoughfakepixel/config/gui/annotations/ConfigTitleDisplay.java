package com.nef.notenoughfakepixel.config.gui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Displays a title-like value in the MoulConfig editor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigTitleDisplay {

    Type type() default Type.VERSION;

    String text() default "";

    int color() default 0xFFFFFF;

    boolean shadow() default false;

    Size size() default Size.BIG;

    enum Type {
        VERSION,
        TEXT,
        MODNAME
    }

    enum Size {
        BIG,
        MEDIUM,
        SMALL
    }
}

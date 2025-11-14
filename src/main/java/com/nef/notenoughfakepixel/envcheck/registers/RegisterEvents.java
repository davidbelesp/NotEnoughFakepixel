package com.nef.notenoughfakepixel.envcheck.registers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterEvents {
    // This is a marker annotation for classes that should be registered as events
}

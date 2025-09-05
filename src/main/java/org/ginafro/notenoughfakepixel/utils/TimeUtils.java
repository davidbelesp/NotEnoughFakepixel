package org.ginafro.notenoughfakepixel.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtils {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static long nextXx55UtcEpochMs() {
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance(UTC);
        cal.setTimeInMillis(now);

        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE, 55);

        if (now >= cal.getTimeInMillis()) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
            cal.set(Calendar.MINUTE, 55);
        }
        return cal.getTimeInMillis();
    }

    private TimeUtils() {}

}

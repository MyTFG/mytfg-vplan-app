package de.mytfg.apps.mytfg.tools;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.mytfg.apps.mytfg.R;

/**
 * Collection of TimeUtils
 */

public class TimeUtils {
    public static String timeDiff(long start, long end, Context context) {
        long diff = end - start;

        int secs = (int)(diff / 1000);

        if (secs < 10) {
            return context.getString(R.string.widget_just_now);
        }

        if (secs < 60) {
            return secs + "s";
        }

        int min = secs / 60;
        if (min < 60) {
            return min + "m";
        }

        int hours = min / 60;
        if (hours < 10) {
            return hours + "h";
        }

        return context.getString(R.string.widget_too_long);
    }

    public static long now() {
        return new Date().getTime();
    }

    public static String format(long timestamp) {
        return format(timestamp, "EEE, dd.MM.yyyy");
    }

    public static String format(long timestamp, String format) {
        Date in = new Date();
        in.setTime(timestamp);
        return new SimpleDateFormat(format, Locale.GERMANY).format(in);
    }
}

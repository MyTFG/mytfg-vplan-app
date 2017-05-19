package de.mytfg.apps.mytfg.tools;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.Date;

import de.mytfg.apps.mytfg.R;

/**
 * Created by bader on 19.05.2017.
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
}

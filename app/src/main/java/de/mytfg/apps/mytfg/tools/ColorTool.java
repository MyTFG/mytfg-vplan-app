package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import android.util.Log;

import de.mytfg.apps.mytfg.R;

public class ColorTool {
    private static int currentColor = 0;

    private static int[] colors = {
            R.color.colorBlue,
            R.color.colorRed,
            R.color.colorGreen,
            R.color.colorOrange,
            R.color.colorIndigo,
            R.color.colorYellow,
            R.color.colorAmber
    };


    public static int getNextColor() {
        int color = colors[currentColor];
        currentColor = (currentColor + 1) % colors.length;
        return color;
    }

    public static int getColor(long index, Context context) {
        long color = Math.max(0, index % colors.length);
        return context.getResources().getColor(colors[(int) color]);
    }
}
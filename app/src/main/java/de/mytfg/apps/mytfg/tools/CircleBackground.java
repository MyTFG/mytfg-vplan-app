package de.mytfg.apps.mytfg.tools;

import de.mytfg.apps.mytfg.R;

public class CircleBackground {
    public static int getResource(String color) {
        if (color == null) {
            return R.drawable.circle_background_blue;
        }

        switch (color) {
            case "red":
                return R.drawable.circle_background_red;
            case "green":
                return R.drawable.circle_background_green;
            case "yellow":
                return R.drawable.circle_background_yellow;
            case "orange":
                return R.drawable.circle_background_orange;
            case "amber":
                return R.drawable.circle_background_amber;
            case "purple":
                return R.drawable.circle_background_purple;
            case "indigo":
                return R.drawable.circle_background_indigo;
            case "blue":
            default:
                return R.drawable.circle_background_blue;
        }
    }
}

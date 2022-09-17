package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Manages storage of settings for easy access
 */

public class Settings {
    private Context context;
    private SharedPreferences preferences;

    public Settings(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void save(String option, String value) {
        preferences.edit()
                .putString(option, value)
                .apply();
    }

    public void save(String option, boolean value) {
        preferences.edit()
                .putBoolean(option, value)
                .apply();
    }

    public void save(String option, int value) {
        preferences.edit()
                .putInt(option, value)
                .apply();
        Log.d("SAVE", option + ": " + value);
    }

    public boolean getBool(String option) {
        return preferences.getBoolean(option, false);
    }

    public boolean getBool(String option, boolean def) {
        return preferences.getBoolean(option, def);
    }

    public String getString(String option) {
        return preferences.getString(option, "");
    }

    public void clear(String option) {
        preferences.edit()
                .remove(option)
                .apply();
    }

    public int getInt(String option) {
        return preferences.getInt(option, 0);
    }
}

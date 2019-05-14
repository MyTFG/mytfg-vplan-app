package de.mytfg.apps.mytfg.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import de.mytfg.apps.mytfg.R;

/**
 * The configuration screen for the {@link VplanWidget VplanWidget} AppWidget.
 */
public class VplanWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "de.mytfg.apps.mytfg.widgets.VplanWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREF_POSTFIX_NM = "_nightmode";

    public VplanWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveDayPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadDayPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String day = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (day != null) {
            return day;
        } else {
            return "today";
        }
    }

    static void deleteDayPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    static void saveNightmodePref(Context context, int appWidgetId, int nightmode) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_NM, nightmode);
        prefs.apply();
    }

    static int getNightmodePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_NM, VplanWidget.Nightmode.OFF);
    }

    static void deleteNightmodePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_NM);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.vplan_widget_configure);
        findViewById(R.id.button_widget_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = VplanWidgetConfigureActivity.this;

                // When the button is clicked, store the string locally
                saveDayPref(context, mAppWidgetId, "today");
                saveNightmodePref(context, mAppWidgetId, getNightmode());

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                VplanWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        findViewById(R.id.button_widget_tomorrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = VplanWidgetConfigureActivity.this;

                // When the button is clicked, store the string locally
                saveDayPref(context, mAppWidgetId, "tomorrow");
                saveNightmodePref(context, mAppWidgetId, getNightmode());

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                VplanWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    private int getNightmode() {
        RadioGroup group = findViewById(R.id.widget_radiogroup);
        switch (group.getCheckedRadioButtonId()) {
            default:
            case R.id.widget_nightmode_off:
                return VplanWidget.Nightmode.OFF;
            case R.id.widget_nightmode_on:
                return VplanWidget.Nightmode.ON;
            case R.id.widget_nightmode_app:
                return VplanWidget.Nightmode.APP;
            case R.id.widget_nightmode_auto:
                return VplanWidget.Nightmode.AUTO;
        }
    }
}


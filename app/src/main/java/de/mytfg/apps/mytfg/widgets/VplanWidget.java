package de.mytfg.apps.mytfg.widgets;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.objects.Vplan;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link VplanWidgetConfigureActivity VplanWidgetConfigureActivity}
 */
public class VplanWidget extends AppWidgetProvider {
    // 4 hours
    private static long plan_update = 4 * 60 * 60 * 1000;
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";
    private static int randomNum = 1;

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {
        updateAppWidget(context, appWidgetManager, appWidgetId, false);
    }

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId, final boolean force) {

        Log.d("WIDGET", "UPDATE FOR WIDGET " + appWidgetId);

        final CharSequence day = VplanWidgetConfigureActivity.loadDayPref(context, appWidgetId);
        CharSequence daystr;
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.vplan_widget);

        views.setOnClickPendingIntent(R.id.widget_fab, getPendingSelfIntent(context, SYNC_CLICKED, appWidgetId));

        if (day.equals("today")) {
            daystr = context.getString(R.string.plan_today);
        } else {
            daystr = context.getString(R.string.plan_tomorrow);
        }

        final Vplan plan = new Vplan(context, day.toString());
        views.setTextViewText(R.id.widget_title, daystr);

        MyTFGApi api = new MyTFGApi(context);

        if (!api.isLoggedIn()) {
            views.setTextViewText(R.id.widget_plan_empty, context.getString(R.string.widget_login_required));
            views.setViewVisibility(R.id.widget_plan_empty, View.VISIBLE);
            views.setViewVisibility(R.id.widget_list_view, View.GONE);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }
        //views.setViewVisibility(R.id.widget_fab, View.GONE);

        long updateInt = force ? 0 : plan_update;
        Log.d("WIDGET", "UPDATE FORCE: " + force);

        views.setTextViewText(R.id.widget_plan_empty, context.getString(R.string.plan_empty));

        views.setTextViewText(R.id.widget_title, context.getString(R.string.plan_loading));
        appWidgetManager.updateAppWidget(appWidgetId, views);

        plan.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                //views.setViewVisibility(R.id.widget_fab, View.VISIBLE);
                Log.d("WIDGET", "UPDATE COMPLETED: " + success);
                if (success) {
                    Date update = new Date();
                    update.setTime(plan.getTimestamp());
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM. HH:mm", Locale.GERMANY);
                    String diff = format.format(update);
                    views.setTextViewText(R.id.widget_changed, context.getString(R.string.widget_last_update) + " " + diff);

                    views.setTextViewText(R.id.widget_title, plan.formatDate());
                    if (plan.getEntries().size() == 0) {
                        views.setViewVisibility(R.id.widget_plan_empty, View.VISIBLE);
                        views.setViewVisibility(R.id.widget_list_view, View.GONE);
                    } else {
                        views.setViewVisibility(R.id.widget_plan_empty, View.GONE);
                        views.setViewVisibility(R.id.widget_list_view, View.VISIBLE);
                    }
                    setRemoteAdapter(context, views, day.toString());
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);
                }
                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }, force, updateInt);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetId = intent.getExtras().getInt("appWidgetId");
            updateAppWidget(context, appWidgetManager, appWidgetId, true);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            VplanWidgetConfigureActivity.deleteDayPref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views, String day) {
        Intent intent;
        switch (day) {
            default:
            case "today":
                intent = new Intent(context, VplanWidgetServiceToday.class);
                break;
            case "tomorrow":
                intent = new Intent(context, VplanWidgetServiceTomorrow.class);
                break;
        }
        intent.putExtra("day", day);
        intent.putExtra("random", randomNum);
        randomNum++;
        views.setRemoteAdapter(R.id.widget_list_view, intent);

    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        Intent intent = new Intent(context, VplanWidget.class);
        intent.setAction(action);
        intent.putExtra("appWidgetId", appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, 0);
    }
}


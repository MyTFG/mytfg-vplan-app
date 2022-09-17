package de.mytfg.apps.mytfg.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.objects.Vplan;
import de.mytfg.apps.mytfg.objects.VplanEntry;

/**
 * Adapter for Widget ListView
 */

public class VplanWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "VplanWidgetDataProvider";
    private Vplan plan;
    private String day;
    private int widgetId;
    private boolean nightmode;

    private List<VplanEntry> mCollection = new ArrayList<>();
    private Context mContext = null;

    public VplanWidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        day = intent.getStringExtra("day");
        widgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        nightmode = VplanWidget.useNightmode(context, widgetId);
        Log.d("WIDGET", "VplanWidgetDataProvider DAY: " + day + "; Nightmode: " + nightmode);
        plan = new Vplan(mContext, day);
    }

    @Override
    public void onCreate() {
        onDataSetChanged();
    }

    @Override
    public void onDataSetChanged() {
        plan = new Vplan(mContext, day);
        plan.loadFromCache();
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("WIDGET", "COUNT ("+day+"): " + mCollection.size());
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCollection.size() <= position) {
            return null;
        }

        VplanEntry entry = mCollection.get(position);
        nightmode = VplanWidget.useNightmode(mContext, widgetId);

        int layout = nightmode ? R.layout.vplan_widget_entry_dark : R.layout.vplan_widget_entry;

        RemoteViews view = new RemoteViews(mContext.getPackageName(), layout);
        view.setTextViewText(R.id.widget_plan_plan, entry.getPlan());
        view.setTextViewText(R.id.widget_plan_comment, entry.getComment());
        view.setTextViewText(R.id.widget_plan_substitution, entry.getSubstitution());
        view.setTextViewText(R.id.widget_plan_lesson, entry.getLesson());
        view.setTextViewText(R.id.widget_plan_cls, entry.getCls());

        if (entry.getComment().isEmpty()) {
            view.setViewVisibility(R.id.widget_plan_comment, View.GONE);
        } else {
            view.setViewVisibility(R.id.widget_plan_comment, View.VISIBLE);
        }
        if (entry.getSubstitution().isEmpty()) {
            view.setViewVisibility(R.id.widget_plan_substitution, View.GONE);
        } else {
            view.setViewVisibility(R.id.widget_plan_substitution, View.VISIBLE);
        }

        if (entry.getSubstitution().isEmpty() && entry.getComment().isEmpty()) {
            view.setViewVisibility(R.id.widget_arrow, View.GONE);
        } else {
            view.setViewVisibility(R.id.widget_arrow, View.VISIBLE);
        }

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        //return new RemoteViews(mContext.getPackageName(),
        //       R.layout.vplan_appwidget_loading);
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void initData() {
        Log.d("WIDGET", "INIT DATA: " + plan.getDay());
        nightmode = VplanWidget.useNightmode(mContext, widgetId);
        mCollection.clear();

        if (plan.isLoaded() && plan.upToDate()) {
            Log.d("WIDGET", "Data up to date (Data PRovider)");
        } else {
            Log.d("WIDGET", "Data too old (Data Provider)");
        }

        mCollection.clear();
        for (VplanEntry entry : plan.getEntries()) {
            mCollection.add(entry);
        }
        Log.d("WIDGET", "Entries: " + mCollection.size());
        Log.d("WIDGET", "Day: " + plan.getDayString());
        Log.d("WIDGET", "Date: " + plan.formatDate());
        Log.d("WIDGET", "NOTIFY");
        AppWidgetManager awm = AppWidgetManager.getInstance(mContext);
        awm.notifyAppWidgetViewDataChanged(awm.getAppWidgetIds(new ComponentName(mContext, VplanWidgetServiceToday.class)), R.id.widget_list_view);
        awm.notifyAppWidgetViewDataChanged(awm.getAppWidgetIds(new ComponentName(mContext, VplanWidgetServiceTomorrow.class)), R.id.widget_list_view);

        /*else {
            Log.d("WIDGET", "Loading started");
            plan.load(new SuccessCallback() {
                @Override
                public void callback(boolean success) {
                    Log.d("WIDGET", "Loading finished: " + success);
                    if (success) {
                        onDataSetChanged();
                    }
                }
            });
        }*/

    }
}

package de.mytfg.apps.vplan.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.api.SuccessCallback;
import de.mytfg.apps.vplan.objects.Vplan;
import de.mytfg.apps.vplan.objects.VplanEntry;

/**
 * Created by bader on 23.01.2017.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "WidgetDataProvider";
    private Vplan plan;

    private List<VplanEntry> mCollection = new ArrayList<>();
    private Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        plan = new Vplan(mContext, "today");
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d("WIDGET", "getViewAt "  + position);
        VplanEntry entry = mCollection.get(position);
        Log.d("WIDGET", "entry plan: " + entry.getPlan());

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        String text = entry.getCls() + " " + entry.getLesson() + ":\n"
                + entry.getPlan() +"\n"
                + entry.getSubstitution() + "\n"
                + entry.getComment();
        view.setTextViewText(android.R.id.text1, text);
        //view.setTextViewText(R.id.plan_entry_lesson, entry.getLesson());

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
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollection.clear();
        if (plan.isLoaded() && plan.upToDate()) {
            mCollection.clear();
            Log.d("WIDGET", "Data present");
            for (VplanEntry entry : plan.getEntries()) {
                mCollection.add(entry);
            }
            Log.d("WIDGET", "Entries: " + mCollection.size());
            AppWidgetManager awm = AppWidgetManager.getInstance(mContext);
            awm.notifyAppWidgetViewDataChanged(awm.getAppWidgetIds(new ComponentName(mContext, WidgetService.class)), R.id.widgetListView);
        } else {
            Log.d("WIDGET", "Loading started");
            plan.load(new SuccessCallback() {
                @Override
                public void callback(boolean success) {
                    Log.d("WIDGET", "Loading finished: " + success);
                    if (success) {
                        initData();
                    }
                }
            });
        }

    }
}

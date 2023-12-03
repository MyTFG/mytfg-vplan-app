package de.mytfg.apps.mytfg.logic;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecylcerEventsAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.fragments.FragmentHolderLogic;
import de.mytfg.apps.mytfg.objects.TfgEvents;
import de.mytfg.apps.mytfg.objects.TfgEventsEntry;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;

/**
 * Handles plan loading and displaying for one day
 */

public class EventsLogic implements FragmentHolderLogic {
    private TfgEvents events;
    private Context context;
    private RecylcerEventsAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private Parcelable layoutState;

    private int scrollPos = 0;

    private boolean forceReload = true;

    private String current_filter = null;
    public EventsLogic(TfgEvents events) {
        this.events = events;
    }
    public EventsLogic(TfgEvents events, Context context) {
        this.events = events;
        this.context = context;
    }

    @Override
    public void init(final Context context, View view, Bundle args) {
        this.context = context;
        Log.d("Init", context.toString());
        recyclerView = (RecyclerView) view.findViewById(R.id.events_recylcerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(context, R.dimen.cardview_spacing));
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                layoutState = recyclerView.getLayoutManager().onSaveInstanceState();
                if (dy > 2) {
                    ((MainActivity) context).getToolbarManager().hideFab();
                } else if (dy < -2) {
                    ((MainActivity) context).getToolbarManager().showFab();
                }
            }
        });

        loadEvents(false);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents(true);
            }
        });
    }

    private void display() {
        if (!forceReload && adapter != null) {
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            recyclerView.getLayoutManager().onRestoreInstanceState(layoutState);
            return;
        }

        forceReload = false;

        if (adapter == null) {
            adapter = new RecylcerEventsAdapter(context);
        }
        Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();
        adapter.clear();
        for (TfgEventsEntry entry : this.events.filter(this.current_filter)) {
            adapter.addItem(entry);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //TabLayout.Tab tab = getTab();
        ViewPager pager = (ViewPager)((MainActivity)context).findViewById(R.id.tfg_pager);
        TabLayout tabLayout = ((MainActivity)context).getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(pager);
        }
        recyclerView.getLayoutManager().onRestoreInstanceState(state);
        /*
        if (tab != null) {
            //tab.setText("Test, sas  - asd");
            Log.d("TAB TEXT", tab.getText().toString());
            Log.d("TAB TEXT LEN", tab.getText().toString().length() + "");
        }*/
    }

    @Override
    public void saveInstanceState() {

    }

    @Override
    public void restoreInstanceState() {

    }

    private void loadEvents(boolean reload) {
        forceReload = reload;
        refreshLayout.setRefreshing(true);
        this.events.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                refreshLayout.setRefreshing(false);
                if (success) {
                    // Display results
                    display();
                } else {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((MainActivity) context).findViewById(R.id.coordinator_layout);
                    // Show snackbar since user input is invalid
                    String message = context.getString(R.string.news_failed);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout,
                                    message,
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }, reload);
    }

    private TabLayout.Tab getTab() {
        TabLayout tabLayout = ((MainActivity)context).getToolbarManager().getTabs();
        TabLayout.Tab tab;
        if (tabLayout == null) {
            return null;
        }
        tab = tabLayout.getTabAt(0);
        return tab;
    }

    @Override
    public String getTabTitle() {
        return context.getString(R.string.menutitle_calendar);
    }

    public void update() {
        this.display();
    }

    public void filter(String filter) {
        this.current_filter = filter;
        forceReload = true;
        this.display();
    }

    public void resetFilter() {
        this.current_filter = null;
        forceReload = true;
        this.display();
    }


}

package de.mytfg.apps.mytfg.logic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecylcerExamsAdapter;
import de.mytfg.apps.mytfg.adapters.RecylcerPlanAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.fragments.FragmentHolderLogic;
import de.mytfg.apps.mytfg.objects.ExamEntry;
import de.mytfg.apps.mytfg.objects.Exams;
import de.mytfg.apps.mytfg.objects.Vplan;
import de.mytfg.apps.mytfg.objects.VplanEntry;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;

/**
 * Handles plan loading and displaying for one day
 */

public class ExamLogic implements FragmentHolderLogic {
    private Exams exams;
    private String cls;
    private View view;
    private Context context;
    private RecylcerExamsAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private Parcelable recyclerViewState;

    private String current_filter = null;
    public ExamLogic(Exams exams, String cls) {
        this.exams = exams;
        this.cls = cls;
    }

    @Override
    public void init(final Context context, View view, Bundle args) {
        this.context = context;
        this.view = view;
        recyclerView = (RecyclerView) view.findViewById(R.id.base_recylcerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        adapter = new RecylcerExamsAdapter(context);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(context, R.dimen.cardview_spacing));
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 2) {
                    ((MainActivity) context).getToolbarManager().hideFab();
                } else if (dy < -2) {
                    ((MainActivity) context).getToolbarManager().showFab();
                }
            }
        });

        MainActivity c = (MainActivity) context;
        if (this.exams.isLoaded()) {
            display();
        } else {
            loadExams(false);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadExams(true);
            }
        });
    }

    private void display() {
        adapter = new RecylcerExamsAdapter(context);
        recyclerView.setAdapter(adapter);
        Map<String, List<ExamEntry>> map = this.exams.filter(this.current_filter);
        List<ExamEntry> examEntries = new LinkedList<>();
        if (map.containsKey(cls)) {
            examEntries = map.get(cls);
        }
        for (ExamEntry entry : examEntries) {
            adapter.addItem(entry);
        }
        adapter.notifyDataSetChanged();
        if (recyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
        //TabLayout.Tab tab = getTab();
        ViewPager pager = (ViewPager)((MainActivity)context).findViewById(R.id.exam_pager);

        ((MainActivity)context).getToolbarManager().getTabs().setupWithViewPager(pager);/*
        if (tab != null) {
            //tab.setText("Test, sas  - asd");
            Log.d("TAB TEXT", tab.getText().toString());
            Log.d("TAB TEXT LEN", tab.getText().toString().length() + "");
        }*/
    }


    @Override
    public void saveInstanceState() {
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }


    @Override
    public void restoreInstanceState() {
        if (recyclerViewState != null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.base_recylcerview);
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
    }

    private void loadExams(boolean reload) {
        refreshLayout.setRefreshing(true);
        this.exams.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                Log.d("EXAMS", "Loading callback for " + cls + " - Success: " + success);
                refreshLayout.setRefreshing(false);
                if (success) {
                    // Display results
                    display();
                } else {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((MainActivity) context).findViewById(R.id.coordinator_layout);
                    // Show snackbar since user input is invalid
                    int code = exams.getLastCode();
                    String message; // = context.getString(R.string.plan_failed);
                    switch (code) {
                        case -1:
                            message = context.getString(R.string.api_offline);
                            break;
                        case 403:
                            message = context.getString(R.string.api_noauth);
                            break;
                        default:
                            message = context.getString(R.string.api_serverfault);
                            break;
                    }
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout,
                                    message,
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }, reload);
    }

    @Override
    public String getTabTitle() {
        return exams.getContext().getString(R.string.exams_tab_title, cls);
    }

    public void filter(String filter) {
        this.current_filter = filter;
        this.display();
    }

    public void resetFilter() {
        this.current_filter = null;
        this.display();
    }

    public Exams getExams() {
        return exams;
    }

    public String getCls() {
        return cls;
    }
}

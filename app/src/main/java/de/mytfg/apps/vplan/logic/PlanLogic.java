package de.mytfg.apps.vplan.logic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.adapters.RecylcerPlanAdapter;
import de.mytfg.apps.vplan.api.SuccessCallback;
import de.mytfg.apps.vplan.fragments.FragmentHolderLogic;
import de.mytfg.apps.vplan.objects.Vplan;
import de.mytfg.apps.vplan.objects.VplanEntry;
import de.mytfg.apps.vplan.tools.ItemOffsetDecoration;

/**
 * Handles plan loading and displaying for one day
 */

public class PlanLogic implements FragmentHolderLogic {
    private Vplan plan;
    private Context context;
    private RecylcerPlanAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressDialog dialog;

    private String current_filter = null;
    public PlanLogic(Vplan plan) {
        this.plan = plan;
    }

    @Override
    public void init(final Context context, View view, Bundle args) {
        this.context = context;
        recyclerView = (RecyclerView) view.findViewById(R.id.base_recylcerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        adapter = new RecylcerPlanAdapter(context);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(context, R.dimen.cardview_spacing));

        if (this.plan.isLoaded()) {
            display();
        } else {
            loadPlan(false);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPlan(true);
            }
        });
    }

    private void display() {
        adapter = new RecylcerPlanAdapter(context);
        recyclerView.setAdapter(adapter);
        for (VplanEntry entry : this.plan.filter(this.current_filter)) {
            adapter.addItem(entry);
        }
        adapter.notifyDataSetChanged();
        //TabLayout.Tab tab = getTab();
        ViewPager pager = (ViewPager)((MainActivity)context).findViewById(R.id.plan_pager);
        ((MainActivity)context).getToolbarManager().getTabs().setupWithViewPager(pager);/*
        if (tab != null) {
            //tab.setText("Test, sas  - asd");
            Log.d("TAB TEXT", tab.getText().toString());
            Log.d("TAB TEXT LEN", tab.getText().toString().length() + "");
        }*/
    }

    private void loadPlan(boolean reload) {
        refreshLayout.setRefreshing(true);
        this.plan.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                refreshLayout.setRefreshing(false);
                if (success) {
                    // Display results
                    display();
                } else {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((MainActivity) context).findViewById(R.id.coordinator_layout);
                    // Show snackbar since user input is invalid
                    int code = plan.getLastCode();
                    String message = context.getString(R.string.plan_failed);
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

    private TabLayout.Tab getTab() {
        TabLayout tabLayout = ((MainActivity)context).getToolbarManager().getTabs();
        TabLayout.Tab tab;
        if (tabLayout == null) {
            return null;
        }
        if (this.plan.getDay().equals("today")) {
            tab = tabLayout.getTabAt(0);
        } else {
            tab = tabLayout.getTabAt(1);
        }
        return tab;
    }

    @Override
    public String getTabTitle() {
        return plan.getDayString();
    }

    public void filter(String filter) {
        this.current_filter = filter;
        this.display();
    }

    public void resetFilter() {
        this.current_filter = null;
        this.display();
    }


}

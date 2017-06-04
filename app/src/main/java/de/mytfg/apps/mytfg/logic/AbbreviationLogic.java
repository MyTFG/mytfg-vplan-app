package de.mytfg.apps.mytfg.logic;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecylcerAbbreviationsAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.fragments.FragmentHolderLogic;
import de.mytfg.apps.mytfg.objects.Abbreviation;
import de.mytfg.apps.mytfg.objects.Abbreviations;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;

/**
 * Handles plan loading and displaying for one day
 */

public class AbbreviationLogic implements FragmentHolderLogic {
    private Abbreviations abbreviations;
    private Context context;
    private RecylcerAbbreviationsAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private String current_filter = null;
    public AbbreviationLogic(Abbreviations abbreviations) {
        this.abbreviations = abbreviations;
    }
    public AbbreviationLogic(Abbreviations abbreviations, Context context) {
        this.abbreviations = abbreviations;
        this.context = context;
    }

    @Override
    public void init(final Context context, View view, Bundle args) {
        this.context = context;
        recyclerView = (RecyclerView) view.findViewById(R.id.abbr_recylcerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        adapter = new RecylcerAbbreviationsAdapter(context);
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

        loadAbbreviations(false);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAbbreviations(true);
            }
        });
    }

    private void display() {
        adapter = new RecylcerAbbreviationsAdapter(context);
        recyclerView.setAdapter(adapter);
        for (Abbreviation entry : this.abbreviations.filter(this.current_filter)) {
            adapter.addItem(entry);
        }
        adapter.notifyDataSetChanged();
        //TabLayout.Tab tab = getTab();
        ViewPager pager = (ViewPager)((MainActivity)context).findViewById(R.id.abbr_pager);
        TabLayout tabLayout = ((MainActivity)context).getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(pager);
        }
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

    private void loadAbbreviations(boolean reload) {
        refreshLayout.setRefreshing(true);
        this.abbreviations.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                refreshLayout.setRefreshing(false);
                if (success) {
                    // Display results
                    display();
                } else {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((MainActivity) context).findViewById(R.id.coordinator_layout);
                    // Show snackbar since user input is invalid
                    String message = context.getString(R.string.abbr_failed);
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
        switch (abbreviations.getType()) {
            default:
            case "teachers":
                return context.getResources().getString(R.string.abbr_teacher);
            case "subjects":
                return context.getResources().getString(R.string.abbr_subject);
        }
    }

    public void filter(String filter) {
        this.current_filter = filter;
        this.display();
    }

    public void resetFilter() {
        this.current_filter = null;
        this.display();
    }

    public Abbreviations getAbbreviations() {
        return abbreviations;
    }
}

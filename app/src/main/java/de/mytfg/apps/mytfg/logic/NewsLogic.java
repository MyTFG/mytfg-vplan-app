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
import android.util.Log;
import android.view.View;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecylcerNewsAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.fragments.FragmentHolderLogic;
import de.mytfg.apps.mytfg.objects.TfgNews;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;

/**
 * Handles plan loading and displaying for one day
 */

public class NewsLogic implements FragmentHolderLogic {
    private TfgNews news;
    private Context context;
    private RecylcerNewsAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private String current_filter = null;
    public NewsLogic(TfgNews news) {
        this.news = news;
    }
    public NewsLogic(TfgNews news, Context context) {
        this.news = news;
        this.context = context;
    }

    @Override
    public void init(final Context context, View view, Bundle args) {
        this.context = context;
        Log.d("Init", context.toString());
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recylcerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        adapter = new RecylcerNewsAdapter(context);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(context, R.dimen.cardview_spacing));

        loadNews(false);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNews(true);
            }
        });
    }


    @Override
    public void saveInstanceState() {

    }

    @Override
    public void restoreInstanceState() {

    }

    private void display() {
        adapter = new RecylcerNewsAdapter(context);
        recyclerView.setAdapter(adapter);
        for (TfgNewsEntry entry : this.news.filter(this.current_filter)) {
            adapter.addItem(entry);
        }
        adapter.notifyDataSetChanged();
        //TabLayout.Tab tab = getTab();
        ViewPager pager = (ViewPager)((MainActivity)context).findViewById(R.id.tfg_pager);
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

    private void loadNews(boolean reload) {
        refreshLayout.setRefreshing(true);
        this.news.load(new SuccessCallback() {
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
        return context.getString(R.string.menutitle_news);
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

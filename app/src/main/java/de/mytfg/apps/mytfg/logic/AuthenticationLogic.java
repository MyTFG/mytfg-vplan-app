package de.mytfg.apps.mytfg.logic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecylcerAuthAdapter;
import de.mytfg.apps.mytfg.api.SimpleCallback;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.fragments.FragmentHolderLogic;
import de.mytfg.apps.mytfg.objects.Authentication;
import de.mytfg.apps.mytfg.objects.Authentications;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;

/**
 * Handles plan loading and displaying for one day
 */

public class AuthenticationLogic implements FragmentHolderLogic {
    private Authentications auths;
    private View view;
    private Context context;
    private RecylcerAuthAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressDialog dialog;
    private Parcelable recyclerViewState;
    private boolean authType = false; // false = Active, true = Timedout

    public AuthenticationLogic(Authentications auths, boolean authType) {
        this.auths = auths;
        this.authType = authType;
    }

    @Override
    public void init(Context context, View view, Bundle args) {
        this.context = context;
        this.view = view;
        recyclerView = (RecyclerView) view.findViewById(R.id.base_recylcerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);
        refreshLayout.setRefreshing(true);

        adapter = new RecylcerAuthAdapter(context);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(context, R.dimen.cardview_spacing));

        MainActivity c = (MainActivity) context;

        if (auths == null) {
            auths = new Authentications(context);
        }

        auths.addOnChangeListener(new SimpleCallback() {
            @Override
            public void callback() {
                Log.d("AUTH", "ONCHANGE");
                display();
            }
        });

        if (!auths.isLoaded()) {
            auths.load(new SuccessCallback() {
                @Override
                public void callback(boolean success) {
                    display();
                }
            });
        } else {
            this.display();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                auths.load(new SuccessCallback() {
                    @Override
                    public void callback(boolean success) {
                        display();
                    }
                });
            }
        });
    }

    private void display() {
        refreshLayout.setRefreshing(false);
        adapter = new RecylcerAuthAdapter(context);
        recyclerView.setAdapter(adapter);
        adapter.clear();

        List<Authentication> authentications = !authType ? auths.getActive() : auths.getTimedout();
        for (Authentication authentication : authentications) {
            adapter.addItem(authentication);
        }

        adapter.notifyDataSetChanged();
        if (recyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
        //TabLayout.Tab tab = getTab();
        ViewPager pager = (ViewPager)((MainActivity)context).findViewById(R.id.authmanage_pager);

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

    private TabLayout.Tab getTab() {
        TabLayout tabLayout = ((MainActivity)context).getToolbarManager().getTabs();
        TabLayout.Tab tab;
        if (tabLayout == null) {
            return null;
        }
        if (!this.authType) {
            tab = tabLayout.getTabAt(0);
        } else {
            tab = tabLayout.getTabAt(1);
        }
        return tab;
    }

    @Override
    public String getTabTitle() {
        if (context == null) {
            return "";
        }
        return !this.authType ? context.getString(R.string.manage_authentications_active) : context.getString(R.string.manage_authentications_timedout);
    }
}

package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecylcerVrrAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.objects.Vrr;
import de.mytfg.apps.mytfg.objects.VrrEntry;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;
import de.mytfg.apps.mytfg.tools.Settings;

public class VrrFragment extends AuthenticationFragment {
    private View view;
    private RecylcerVrrAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean autoRefresh;
    private MainActivity context;
    private Handler handler;
    private Runnable updateRunner;
    private boolean timerActive;

    private static final int updateInterval = 10000;

    public VrrFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vrr, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);
        context = (MainActivity)this.getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.vrr_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));

        Settings settings = new Settings(context);
        autoRefresh = settings.getBool("vrr_auto_refresh");

        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.vrr_header_s)
                .setTitle(getString(R.string.menutitle_vrr))
                .setExpandable(true, true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayVrr();
            }
        });

        handler = new Handler();

        updateRunner = new Runnable() {
            @Override
            public void run() {
                try {
                    displayVrr();
                } finally {
                    // 100% guarantee that this always happens, even if
                    // your update method throws an exception
                    handler.postDelayed(updateRunner, updateInterval);
                }
            }
        };


        this.displayVrr();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.vrr_menu, menu);
        menu.getItem(0).setChecked(autoRefresh);
        if (autoRefresh) {
            menu.getItem(0).setIcon(R.drawable.ic_action_vrr_enabled_autorefresh);
            startTimer();
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_action_vrr_disabled_autorefresh);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vrr_auto_refresh:
                item.setChecked(!item.isChecked());
                autoRefresh = item.isChecked();
                Settings settings = new Settings(context);
                settings.save("vrr_auto_refresh", autoRefresh);
                if (autoRefresh) {
                    context.getNavi().snackbar(context.getString(R.string.vrr_autorefresh_enabled));
                    item.setIcon(R.drawable.ic_action_vrr_enabled_autorefresh);
                    startTimer();
                } else {
                    context.getNavi().snackbar(context.getString(R.string.vrr_autorefresh_disabled));
                    item.setIcon(R.drawable.ic_action_vrr_disabled_autorefresh);
                    stopTimer();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void startTimer() {
        if (!timerActive) {
            updateRunner.run();
            timerActive = true;
        }
    }

    private void stopTimer() {
        timerActive = false;
        handler.removeCallbacks(updateRunner);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (autoRefresh) {
            startTimer();
        }
    }

    @Override
    public void onPause() {
        if (autoRefresh) {
            stopTimer();
        }
        super.onPause();
    }

    public void displayVrr() {
        recyclerView = (RecyclerView) view.findViewById(R.id.vrr_recylcerview);
        swipeRefreshLayout.setRefreshing(true);

        final Vrr vrr = new Vrr(getContext());
        vrr.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                swipeRefreshLayout.setRefreshing(false);
                if (success) {
                    if (adapter == null) {
                        adapter = new RecylcerVrrAdapter(getContext());
                        recyclerView.setAdapter(adapter);
                    }
                    adapter.clear();
                    for (VrrEntry entry : vrr.getEntries()) {
                        adapter.addItem(entry);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    ((MainActivity)getActivity()).getNavi().snackbar(getString(R.string.api_vrr_error));
                }
            }
        }, true);
    }
}

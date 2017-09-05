package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
    private Vrr vrr;
    private boolean expand = true;

    private static final int updateInterval = 30000;

    public VrrFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("VRR", "CreateView");
        view = inflater.inflate(R.layout.fragment_vrr, container, false);
        context = (MainActivity)this.getActivity();
        vrr = new Vrr(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.vrr_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        Settings settings = new Settings(context);
        autoRefresh = settings.getBool("vrr_auto_refresh");

        Log.d("VRR", "expand: " + expand);
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.vrr_header_s, !expand)
                .setTitle(getString(R.string.menutitle_vrr));

        if (expand) {
            context.getToolbarManager().setExpandable(true, true);
        }
        expand = false;
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
            handler.postDelayed(updateRunner, updateInterval);
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
        Log.d("VRR", "Resume!");
        Log.d("VRR", "RecylcerView: " + recyclerView);
        Log.d("VRR", "swipe: " + swipeRefreshLayout);
        Log.d("VRR", "adapter: " + adapter);
        Log.d("VRR", "vrr: " + vrr);
        reshow();
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

    private void reshow() {
        recyclerView = (RecyclerView) view.findViewById(R.id.vrr_recylcerview);
        if (adapter != null) {
            Log.d("VRR", "notify");
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } else {
            displayVrr();
        }
    }

    public void displayVrr() {
        recyclerView = (RecyclerView) view.findViewById(R.id.vrr_recylcerview);
        swipeRefreshLayout.setRefreshing(true);
        Log.d("VRR", "DisplayVrr");

        final Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();

        vrr.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                swipeRefreshLayout.setRefreshing(false);
                if (success) {
                    if (adapter == null) {
                        adapter = new RecylcerVrrAdapter(getContext());
                    }
                    adapter.clear();
                    for (VrrEntry entry : vrr.getEntries()) {
                        adapter.addItem(entry);
                    }
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.getLayoutManager().onRestoreInstanceState(state);
                } else {
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).getNavi().snackbar(getString(R.string.api_vrr_error));
                    }
                }
            }
        }, true);
    }
}

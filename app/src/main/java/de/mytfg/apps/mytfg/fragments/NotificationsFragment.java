package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecyclerNotificationsAdapter;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

public class NotificationsFragment extends AuthenticationFragment {
    private View view;
    private RecyclerNotificationsAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity context;

    public NotificationsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        context = (MainActivity)this.getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.notifications_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.notifications_header)
                .showBottomScrim()
                .setExpandable(true, true)
                .showFab(R.drawable.delete_fab)
                .setTitle(getString(R.string.menutitle_notifications));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayNotifications();
            }
        });


        FloatingActionButton fab = context.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonFileManager.clear("notifications.json", context);
                displayNotifications();
            }
        });
        fab.show();

        displayNotifications();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void reshow() {
        recyclerView = (RecyclerView) view.findViewById(R.id.notifications_recylcerview);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } else {
            displayNotifications();
        }
    }

    public void displayNotifications() {
        recyclerView = (RecyclerView) view.findViewById(R.id.notifications_recylcerview);
        swipeRefreshLayout.setRefreshing(true);

        final Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();

        JSONObject logObj = JsonFileManager.read("notifications.json", context);
        JSONArray log = logObj.optJSONArray("notifications");
        if (log == null) {
            log = new JSONArray();
        }

        Log.d("Notifications", log.toString());
        swipeRefreshLayout.setRefreshing(false);
        if (adapter == null) {
            adapter = new RecyclerNotificationsAdapter(getContext());
        }
        adapter.clear();
        for (int i = log.length() - 1; i >= 0; --i) {
            JSONObject obj = log.optJSONObject(i);
            if (obj != null) {
                adapter.addItem(obj);
            }
        }

        if (adapter.getCount() == 0) {
            context.getNavi().snackbar(getResources().getString(R.string.notifications_empty));
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.getLayoutManager().onRestoreInstanceState(state);
    }
}

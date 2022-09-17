package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecyclerLostAndFoundAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.objects.LostAndFound;
import de.mytfg.apps.mytfg.objects.LostAndFoundEntry;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;

public class LostAndFoundFragment extends AuthenticationFragment {
    private View view;
    private RecyclerLostAndFoundAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MainActivity context;

    private LostAndFound lostAndFound;
    //private Vrr vrr;

    public LostAndFoundFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("LostAndFound", "CreateView");
        view = inflater.inflate(R.layout.fragment_lostandfound, container, false);
        context = (MainActivity)this.getActivity();

        lostAndFound = new LostAndFound(context);

        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.lostandfound_header_s)
                .setExpanded(true, true)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_lostandfound));


        recyclerView = view.findViewById(R.id.laf_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));
        swipeRefreshLayout = view.findViewById(R.id.base_refreshLayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayLostAndFound();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reshow();
    }

    private void reshow() {
        recyclerView = view.findViewById(R.id.laf_recylcerview);
        if (adapter != null) {
            Log.d("LostAndFound", "notify");
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } else {
            displayLostAndFound();
        }
    }

    public void displayLostAndFound() {
        recyclerView = view.findViewById(R.id.laf_recylcerview);
        swipeRefreshLayout.setRefreshing(true);
        Log.d("LostAndFound", "DisplayLostAndFound");

        final Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();

        lostAndFound.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                swipeRefreshLayout.setRefreshing(false);
                if (success) {
                    if (adapter == null) {
                        adapter = new RecyclerLostAndFoundAdapter(getContext());
                    }

                    adapter.clear();
                    for (LostAndFoundEntry entry : lostAndFound.getEntries()) {
                        adapter.addItem(entry);
                    }
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.getLayoutManager().onRestoreInstanceState(state);
                } else {
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).getNavi().snackbar(getString(R.string.api_laf_error));
                    }
                }
            }
        });
    }
}

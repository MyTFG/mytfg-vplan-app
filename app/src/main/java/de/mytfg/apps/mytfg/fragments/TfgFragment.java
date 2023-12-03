package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.FragmentHolder;
import de.mytfg.apps.mytfg.adapters.RecylcerEventsAdapter;
import de.mytfg.apps.mytfg.adapters.RecylcerVrrAdapter;
import de.mytfg.apps.mytfg.adapters.ViewPagerAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.logic.EventsLogic;
import de.mytfg.apps.mytfg.logic.NewsLogic;
import de.mytfg.apps.mytfg.objects.TfgEvents;
import de.mytfg.apps.mytfg.objects.TfgEventsEntry;
import de.mytfg.apps.mytfg.objects.TfgNews;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;

public class TfgFragment extends AuthenticationFragment {
    private View view;
    private MainActivity context;

    private EventsLogic eventsLogic;
    private TfgEvents events;
    private FragmentHolder eventsFragment;

    private RecylcerEventsAdapter adapter;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean expand = true;

    public TfgFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_events, container, false);
        }
        final MainActivity context = (MainActivity)this.getActivity();
        events = new TfgEvents(context);
        eventsLogic = new EventsLogic(events, context);
        this.context = context;
        eventsLogic.init(context, view, new Bundle());

        /*recyclerView = (RecyclerView) view.findViewById(R.id.events_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);*/

        setHasOptionsMenu(true);

        context.getToolbarManager()
                .clear(false)
                .setTitle(getString(R.string.menutitle_tfg_events))
                .clearMenu()
                .setImage(R.drawable.events_header_s)
                .showBottomScrim()
                .showFab();

        if (expand) {
            context.getToolbarManager().setExpandable(true, true);
        }

        expand = false;

        final FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getToolbarManager().showSearchBar();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tfg_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchView searchView = context.getToolbarManager().getSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                eventsLogic.filter(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                eventsLogic.resetFilter();
                return true;
            }
        });
    }

    public void displayEvents() {
        if (recyclerView == null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.events_recylcerview);
        }
        swipeRefreshLayout.setRefreshing(true);
        final Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();
        events.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                swipeRefreshLayout.setRefreshing(false);
                if (success) {
                    if (adapter == null) {
                        adapter = new RecylcerEventsAdapter(getContext());
                    }
                    adapter.clear();
                    for (TfgEventsEntry entry: events.getEntries()) {
                        adapter.addItem(entry);
                    }
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.getLayoutManager().onRestoreInstanceState(state);
                } else {
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).getNavi().snackbar(getString(R.string.api_news_error));
                    }
                }
            }
        }, true);
    }

    private void reshow() {
        recyclerView = (RecyclerView) view.findViewById(R.id.events_recylcerview);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } else {
            displayEvents();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        eventsLogic.update();
    }
}


package de.mytfg.apps.vplan.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.adapters.RecylcerNewsAdapter;
import de.mytfg.apps.vplan.adapters.RecylcerVrrAdapter;
import de.mytfg.apps.vplan.api.SuccessCallback;
import de.mytfg.apps.vplan.objects.TfgNews;
import de.mytfg.apps.vplan.objects.TfgNewsEntry;
import de.mytfg.apps.vplan.objects.Vrr;
import de.mytfg.apps.vplan.objects.VrrEntry;
import de.mytfg.apps.vplan.tools.ItemOffsetDecoration;

public class NewsFragment extends AuthenticationFragment {
    private View view;
    private RecylcerNewsAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public NewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);
        MainActivity context = (MainActivity)this.getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.news_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));

        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .showBottomScrim()
                .setImage(R.drawable.news_header_s)
                .setTitle(getString(R.string.menutitle_news))
                .setExpandable(true, true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayNews();
            }
        });


        this.displayNews();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void displayNews() {
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recylcerview);
        swipeRefreshLayout.setRefreshing(true);

        final TfgNews news = new TfgNews(getContext());
        news.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                swipeRefreshLayout.setRefreshing(false);
                if (success) {
                    adapter = new RecylcerNewsAdapter(getContext());
                    recyclerView.setAdapter(adapter);
                    for (TfgNewsEntry entry : news.getEntries()) {
                        adapter.addItem(entry);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    ((MainActivity)getActivity()).getNavi().snackbar(getString(R.string.api_news_error));
                }
            }
        });
    }
}

package de.mytfg.apps.vplan.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.adapters.RecylcerNewsAdapter;
import de.mytfg.apps.vplan.adapters.RecylcerVrrAdapter;
import de.mytfg.apps.vplan.api.MyTFGApi;
import de.mytfg.apps.vplan.api.SuccessCallback;
import de.mytfg.apps.vplan.objects.TfgNews;
import de.mytfg.apps.vplan.objects.TfgNewsEntry;
import de.mytfg.apps.vplan.objects.User;
import de.mytfg.apps.vplan.objects.Vrr;
import de.mytfg.apps.vplan.objects.VrrEntry;
import de.mytfg.apps.vplan.tools.CustomViewTarget;
import de.mytfg.apps.vplan.tools.ItemOffsetDecoration;
import de.mytfg.apps.vplan.tools.ShowCaseManager;

public class VrrFragment extends AuthenticationFragment {
    private View view;
    private RecylcerVrrAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public VrrFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vrr, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);
        MainActivity context = (MainActivity)this.getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.vrr_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));

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


        this.displayVrr();

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

    public void displayVrr() {
        recyclerView = (RecyclerView) view.findViewById(R.id.vrr_recylcerview);
        swipeRefreshLayout.setRefreshing(true);

        final Vrr vrr = new Vrr(getContext());
        vrr.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                swipeRefreshLayout.setRefreshing(false);
                if (success) {
                    adapter = new RecylcerVrrAdapter(getContext());
                    recyclerView.setAdapter(adapter);
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

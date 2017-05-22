package de.mytfg.apps.mytfg.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.RecylcerVrrAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.objects.MytfgPage;
import de.mytfg.apps.mytfg.objects.Vrr;
import de.mytfg.apps.mytfg.objects.VrrEntry;
import de.mytfg.apps.mytfg.tools.ItemOffsetDecoration;
import de.mytfg.apps.mytfg.tools.Settings;

public class PageFragment extends AuthenticationFragment {
    private View view;
    private TextView content;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity context;

    private String path;
    private String title;

    private MytfgPage page;

    public PageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_page, container, false);
        context = (MainActivity)this.getActivity();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refreshLayout);

        path = getArguments().getString("path");
        title = getArguments().getString("title");

        int image = R.drawable.page_header;
        if (getArguments().containsKey("image")) {
            image = getArguments().getInt("image");
        }

        context.getToolbarManager()
                .clear()
                .setImage(image, true)
                .showBottomScrim()
                .setExpandable(true, true)
                .setTitle(title);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage(true);
            }
        });

        loadPage(false);

        return view;
    }

    public void loadPage(boolean clearCache) {
        swipeRefreshLayout.setRefreshing(true);
        content = (TextView) view.findViewById(R.id.page_text);
        page = new MytfgPage(context, path);
        page.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                if (success) {
                    content.setText(Html.fromHtml(page.getHtml()));
                } else {
                    content.setText(getString(R.string.page_failed));
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, clearCache);

    }
}

package de.mytfg.apps.mytfg.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.activities.PdfActivity;

public class PagesFragment extends AuthenticationFragment {
    public PagesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pages, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.page_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_pages))
                .setExpandable(true, true);


        CardView card = (CardView) view.findViewById(R.id.page_datapolicy);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage("apppolicy", getString(R.string.page_datapolicy));
            }
        });

        card = (CardView) view.findViewById(R.id.page_faq);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage("faq", getString(R.string.page_faq));
            }
        });

        card = (CardView) view.findViewById(R.id.page_changelog);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage("appchangelog", getString(R.string.page_changelog));
            }
        });

        return view;
    }

    private void openPage(String page, String title) {
        AuthenticationFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        if (title != null) {
            args.putString("title", title);
        }
        args.putString("path", page);
        fragment.setArguments(args);
        ((MainActivity)getActivity()).getNavi().navigate(fragment, R.id.fragment_container);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}

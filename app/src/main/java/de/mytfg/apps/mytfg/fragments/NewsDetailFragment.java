package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.objects.VrrEntry;

import static android.R.attr.delay;
import static android.R.attr.direction;
import static android.R.attr.type;

public class NewsDetailFragment extends AuthenticationFragment {
    private TfgNewsEntry newsEntry;

    public static NewsDetailFragment newInstance(TfgNewsEntry entry) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.newsEntry = entry;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        TextView title = (TextView) view.findViewById(R.id.news_detail_title);
        TextView date = (TextView) view.findViewById(R.id.news_detail_date);
        TextView text = (TextView) view.findViewById(R.id.news_detail_text);

        if (newsEntry == null) {
            newsEntry = new TfgNewsEntry();
            Log.e("NULL", "NEWS ENTRY IS NULL!");
            // Try to load from saved instance
            if (savedInstanceState != null && savedInstanceState.containsKey("entryJson")) {
                try {
                    JSONObject json = new JSONObject(savedInstanceState.getString("entryJson"));
                    if (!newsEntry.load(json)) {
                        return view;
                    }
                } catch (JSONException ex) {
                    return view;
                }
            } else {
                return view;
            }
        }

        title.setText(newsEntry.getTitle());
        date.setText(newsEntry.getDateString());
        text.setText(newsEntry.getText());

        ((MainActivity)getActivity()).getToolbarManager()
                .clear()
                .setTitle(getContext().getString(R.string.news_title))
                .setImage(R.drawable.news_detail_header).showBottomScrim()
                .setExpandable(true, true);

        ViewCompat.setTransitionName(view, getArguments().getString("frame"));
        ViewCompat.setTransitionName(title, getArguments().getString("title"));
        ViewCompat.setTransitionName(date, getArguments().getString("date"));

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("entryJson", newsEntry.getJson());
    }
}

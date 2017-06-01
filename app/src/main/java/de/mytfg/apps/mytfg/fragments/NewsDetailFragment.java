package de.mytfg.apps.mytfg.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.ViewFlipperIndicator;

public class NewsDetailFragment extends AuthenticationFragment {

    private static final String BASE_URL = "https://mytfg.de/api_tfg_image.x?src=";

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

        ((MainActivity)getActivity()).getToolbarManager()
                .clear()
                .setTitle(getContext().getString(R.string.news_title))
                .setImage(R.drawable.news_detail_header).showBottomScrim()
                .setExpandable(true, true);

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

        final ViewFlipperIndicator flipper = (ViewFlipperIndicator) view.findViewById(R.id.news_detail_flipper);
        flipper.setInAnimation(getContext(), R.anim.slide_in_right);
        flipper.setOutAnimation(getContext(), R.anim.slide_out_left);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.accent));
        flipper.setPaintCurrent(paint);
        paint = new Paint();
        paint.setColor((getResources().getColor(R.color.colorIconsDark)));
        flipper.setRadius(15);
        flipper.setMargin(15);
        flipper.setPaintNormal(paint);
        flipper.setFlipInterval(4000);

        final View flipperCard = view.findViewById(R.id.news_detail_flipper_card);
        final View flipperProgress = view.findViewById(R.id.news_detail_progress_bar);

        if(newsEntry.getImages().length == 0) {
            flipperCard.setVisibility(View.GONE);
        } else {
            final MyTFGApi api = new MyTFGApi(getContext());

            for(String path : newsEntry.getImages()) {
                api.startLoading();
                ImageView imageView = new ImageView(getContext());
                Picasso.with(getContext()).load(BASE_URL + path)
                        .error(R.mipmap.ic_error)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                api.stopLoading();
                                if(!api.isLoading()) {
                                    flipper.setVisibility(View.VISIBLE);
                                    flipperProgress.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError() {
                                api.stopLoading();
                                if(!api.isLoading()) {
                                    flipper.setVisibility(View.VISIBLE);
                                    flipperProgress.setVisibility(View.GONE);
                                }
                            }
                        });
                flipper.addView(imageView);
            }

            if(newsEntry.getImages().length > 1) {
                float density = getResources().getDisplayMetrics().density;
                flipper.setPadding(flipper.getPaddingLeft(),
                        flipper.getPaddingTop(),
                        flipper.getPaddingRight(),
                        (int) (25 * density));
                flipper.setAutoStart(true);
                flipper.startFlipping();
            }
        }

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

package de.mytfg.apps.mytfg.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.ImageActivity;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.ViewFlipperIndicator;

public class NewsDetailFragment extends AuthenticationFragment {

    private static final String TAG = "NewsDetailFragment";
    private static final String BASE_URL = "https://mytfg.de/api_tfg_image.x?src=";

    private TfgNewsEntry newsEntry;
    private final ArrayList<Target> downloadTargets = new ArrayList<>();
    private String unique;
    private Context context;
    private int lastFlipperPos = 0;
    private ViewFlipperIndicator flipper;

    public  NewsDetailFragment() {
        unique = UUID.randomUUID().toString();
    }

    public static NewsDetailFragment newInstance(TfgNewsEntry entry) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.newsEntry = entry;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_news_detail, container, false);
        setHasOptionsMenu(true);

        context = getContext();

        ((MainActivity) getActivity()).getToolbarManager()
                .clear()
                .setTitle(getContext().getString(R.string.news_title))
                .setImage(R.drawable.news_detail_header).showBottomScrim()
                .setExpandable(true, true);

        TextView title = (TextView) view.findViewById(R.id.news_detail_title);
        TextView date = (TextView) view.findViewById(R.id.news_detail_date);
        TextView text = (TextView) view.findViewById(R.id.news_detail_text);

        if (newsEntry == null) {
            newsEntry = new TfgNewsEntry();
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text.setText(Html.fromHtml(newsEntry.getHtml(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            text.setText(Html.fromHtml(newsEntry.getHtml()));
        }
        text.setMovementMethod(LinkMovementMethod.getInstance());

        final ViewFlipperIndicator flipper = (ViewFlipperIndicator) view.findViewById(R.id.news_detail_flipper);
        this.flipper = flipper;
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.accent));
        flipper.setPaintCurrent(paint);
        paint = new Paint();
        paint.setColor((getResources().getColor(R.color.colorIconsDark)));
        flipper.setRadius(15);
        flipper.setMargin(15);
        flipper.setPaintNormal(paint);
        flipper.setFlipInterval(4000);
        lastFlipperPos = 0;

        ViewCompat.setTransitionName(view, getArguments().getString("frame"));
        ViewCompat.setTransitionName(title, getArguments().getString("title"));
        ViewCompat.setTransitionName(date, getArguments().getString("date"));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.news_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.news_detail_browser:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsEntry.getLink()));
                startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("entryJson", newsEntry.getJson());
    }

    @Override
    public void onResume() {
        super.onResume();

        final ViewFlipperIndicator flipper = this.flipper;
        final View flipperCard = getView().findViewById(R.id.news_detail_flipper_card);
        final View flipperProgress = getView().findViewById(R.id.news_detail_progress_bar);

        downloadTargets.clear();
        downloadTargets.ensureCapacity(newsEntry.getImages().length);

        flipper.removeAllViews();

        if (newsEntry.getImages().length == 0) {
            flipperCard.setVisibility(View.GONE);
        } else {
            final MyTFGApi api = new MyTFGApi(getContext());
            Picasso.with(getContext()).setLoggingEnabled(true);

            for (String path : newsEntry.getImages()) {
                api.startLoading();
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d(TAG, "Succesfully loaded image from " + from.name());
                        ImageView imageView = new ImageView(context);
                        imageView.setImageBitmap(bitmap);
                        insertView(imageView);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.e(TAG, "Error while downloading image");
                        ImageView imageView = new ImageView(context);
                        imageView.setImageDrawable(errorDrawable);
                        insertView(imageView);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}

                    private void insertView(View view) {
                        ViewCompat.setTransitionName(view, "image_" + flipper.getChildCount() + "_" + unique);
                        view.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                enlargeImage(view);
                                return true;
                            }
                        });
                        flipper.addView(view);
                        api.stopLoading();
                        if (!api.isLoading()) {
                            flipper.setInAnimation(null);
                            flipper.setOutAnimation(null);
                            flipper.setDisplayedChild(lastFlipperPos);
                            flipper.setInAnimation(getContext(), R.anim.slide_in_right);
                            flipper.setOutAnimation(getContext(), R.anim.slide_out_left);
                            flipper.setVisibility(View.VISIBLE);
                            flipperProgress.setVisibility(View.GONE);
                        }
                    }
                };
                downloadTargets.add(target);
                Picasso.with(context)
                        .load(BASE_URL + path)
                        .error(R.drawable.download_error)
                        .into(target);
            }

            if (newsEntry.getImages().length > 1) {
                float density = getResources().getDisplayMetrics().density;
                flipper.setPadding(flipper.getPaddingLeft(),
                        flipper.getPaddingTop(),
                        flipper.getPaddingRight(),
                        (int) (25 * density));
                flipper.startFlipping();
            }
        }
    }

    private void enlargeImage(View view) {
        lastFlipperPos = flipper.getDisplayedChild();
        Intent intent = new Intent(context, ImageActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, ViewCompat.getTransitionName(view));
        intent.putExtra("image", BASE_URL + newsEntry.getImages()[flipper.getDisplayedChild()]);
        intent.putExtra("transition_name", ViewCompat.getTransitionName(view));
        startActivity(intent, options.toBundle());
    }
}

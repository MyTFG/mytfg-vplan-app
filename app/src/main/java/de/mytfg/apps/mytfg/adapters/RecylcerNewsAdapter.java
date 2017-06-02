package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.fragments.NewsDetailFragment;
import de.mytfg.apps.mytfg.fragments.VrrDetailFragment;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.Settings;

public class RecylcerNewsAdapter extends RecyclerView.Adapter<NewsEntryHolder> {
    private Context context;
    private ArrayList<TfgNewsEntry> elements = new ArrayList<>();

    private String unique;
    private int lastPosition = -1; // None

    public RecylcerNewsAdapter(Context c) {
        this.context = c;
        unique = UUID.randomUUID().toString();
    }

    public void addItem(TfgNewsEntry item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public NewsEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_newsentry, null);
        return new NewsEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewsEntryHolder holder, final int position) {
        final TfgNewsEntry entry = elements.get(position);
        holder.update(entry);

        Settings settings = new Settings(context);
        if (!settings.getBool("news_browser")) {
            final TextView title = holder.getTitle();
            final TextView date = holder.getDate();
            final CardView cardView = holder.getCardView();

            final String uniqueTitle = "title_" + position + "_" + unique;
            final String uniqueDate = "date_" + position + "_" + unique;
            final String uniqueCardview = "frame_" + position + "_" + unique;

            ViewCompat.setTransitionName(title, uniqueTitle);
            ViewCompat.setTransitionName(date, uniqueDate);
            ViewCompat.setTransitionName(cardView, uniqueCardview);

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Pair<String, View>> sharedElements = new HashMap<>();
                    sharedElements.put("title", new Pair<String, View>(uniqueTitle, title));
                    sharedElements.put("date", new Pair<String, View>(uniqueDate, date));
                    sharedElements.put("frame", new Pair<String, View>(uniqueCardview, cardView));

                    NewsDetailFragment fragment = NewsDetailFragment.newInstance(entry);
                    ((MainActivity) context).getNavi().navigate(
                            fragment,
                            R.id.fragment_container,
                            sharedElements
                    );
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }
}

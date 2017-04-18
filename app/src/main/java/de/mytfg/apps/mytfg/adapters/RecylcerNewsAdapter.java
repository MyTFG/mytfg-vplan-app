package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.Settings;

public class RecylcerNewsAdapter extends RecyclerView.Adapter<NewsEntryHolder> {
    private Context context;
    private ArrayList<TfgNewsEntry> elements = new ArrayList<>();

    private String unique;
    private int lastPosition = -1; // None
    private int expandedPosition = -1;

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
        NewsEntryHolder newsEntryHolder = new NewsEntryHolder(view);
        return newsEntryHolder;
    }

    @Override
    public void onBindViewHolder(final NewsEntryHolder holder, final int position) {
        final TfgNewsEntry entry = elements.get(position);
        holder.update(entry, position == expandedPosition);

        Settings settings = new Settings(context);
        if (!settings.getBool("news_browser")) {
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (expandedPosition >= 0) {
                        notifyItemChanged(expandedPosition);
                    }
                    if (holder.getAdapterPosition() == expandedPosition) {
                        expandedPosition = -1;
                        notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        expandedPosition = holder.getAdapterPosition();
                        notifyItemChanged(expandedPosition);
                    }
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

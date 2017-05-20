package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.TfgEventsEntry;

public class RecylcerEventsAdapter extends RecyclerView.Adapter<EventsEntryHolder> {
    private Context context;
    private ArrayList<TfgEventsEntry> elements = new ArrayList<>();

    private String unique;

    public RecylcerEventsAdapter(Context c) {
        this.context = c;
        unique = UUID.randomUUID().toString();
    }

    public void addItem(TfgEventsEntry item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public EventsEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_eventsentry, null);
        return new EventsEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventsEntryHolder holder, final int position) {
        holder.update(elements.get(position));
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }
}

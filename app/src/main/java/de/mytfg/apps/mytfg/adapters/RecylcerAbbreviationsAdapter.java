package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.Abbreviation;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.Settings;

public class RecylcerAbbreviationsAdapter extends RecyclerView.Adapter<AbbreviationEntryHolder> {
    private Context context;
    private ArrayList<Abbreviation> elements = new ArrayList<>();

    private String unique;
    private int lastPosition = -1; // None
    private int expandedPosition = -1;

    public RecylcerAbbreviationsAdapter(Context c) {
        this.context = c;
        unique = UUID.randomUUID().toString();
    }

    public void addItem(Abbreviation item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public AbbreviationEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_abbreviationentry, null);
        AbbreviationEntryHolder newsEntryHolder = new AbbreviationEntryHolder(view);
        return newsEntryHolder;
    }

    @Override
    public void onBindViewHolder(final AbbreviationEntryHolder holder, final int position) {
        final Abbreviation entry = elements.get(position);
        holder.update(entry);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }
}

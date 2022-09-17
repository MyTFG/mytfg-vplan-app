package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.LostAndFoundEntry;

public class RecyclerLostAndFoundAdapter extends RecyclerView.Adapter<LostAndFoundEntryHolder> {
    private Context context;
    private ArrayList<LostAndFoundEntry> elements = new ArrayList<>();

    private String unique;

    public RecyclerLostAndFoundAdapter(Context c) {
        this.context = c;
        unique = UUID.randomUUID().toString();
    }

    public void addItem(LostAndFoundEntry item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public LostAndFoundEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_lafentry, null);
        return new LostAndFoundEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(final LostAndFoundEntryHolder holder, int position) {
        final LostAndFoundEntry entry = elements.get(position);
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

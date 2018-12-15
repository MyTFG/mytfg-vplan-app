package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.fragments.VrrDetailFragment;
import de.mytfg.apps.mytfg.objects.LostAndFound;
import de.mytfg.apps.mytfg.objects.LostAndFoundEntry;
import de.mytfg.apps.mytfg.objects.VrrEntry;

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

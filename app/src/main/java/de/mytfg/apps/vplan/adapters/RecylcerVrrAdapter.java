package de.mytfg.apps.vplan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.objects.TfgNewsEntry;
import de.mytfg.apps.vplan.objects.VrrEntry;

public class RecylcerVrrAdapter extends RecyclerView.Adapter<VrrEntryHolder> {
    private Context context;
    private ArrayList<VrrEntry> elements = new ArrayList<>();

    private String unique;

    public RecylcerVrrAdapter(Context c) {
        this.context = c;
        unique = UUID.randomUUID().toString();
    }

    public void addItem(VrrEntry item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public VrrEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_vrrentry, null);
        VrrEntryHolder vrrEntryHolder = new VrrEntryHolder(view);
        return vrrEntryHolder;
    }

    @Override
    public void onBindViewHolder(final VrrEntryHolder holder, final int position) {
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

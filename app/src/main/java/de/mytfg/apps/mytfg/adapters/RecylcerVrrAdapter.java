package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.fragments.VrrDetailFragment;
import de.mytfg.apps.mytfg.objects.VrrEntry;

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
    public void onBindViewHolder(final VrrEntryHolder holder, int position) {
        final VrrEntry entry = elements.get(position);
        holder.update(entry);

        final TextView direction = holder.getDirection();
        final CardView cardView = holder.getCardView();
        final ImageView typeImg = holder.getTypeImg();
        final TextView type = holder.getType();
        final TextView line = holder.getLine();
        final TextView arrival = holder.getArrival();

        final String uniqueDirection = "direction_" + position + "_" + unique;
        final String uniquetypeImg = "typeImg" + position + "_" + unique;
        final String uniquetype = "type_" + position + "_" + unique;
        final String uniqueline = "line_" + position + "_" + unique;
        final String uniquearrival = "arrival_" + position + "_" + unique;
        final String uniqueCardview = "frame_" + position + "_" + unique;

        ViewCompat.setTransitionName(direction, uniqueDirection);
        ViewCompat.setTransitionName(cardView, uniqueCardview);
        ViewCompat.setTransitionName(typeImg, uniquetypeImg);
        ViewCompat.setTransitionName(type, uniquetype);
        ViewCompat.setTransitionName(line, uniqueline);
        ViewCompat.setTransitionName(arrival, uniquearrival);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Pair<String, View>> sharedElements = new HashMap<>();
                sharedElements.put("direction", new Pair<String, View>(uniqueDirection, direction));
                sharedElements.put("typeImg", new Pair<String, View>(uniquetypeImg, typeImg));
                sharedElements.put("type", new Pair<String, View>(uniquetype, type));
                sharedElements.put("line", new Pair<String, View>(uniqueline, line));
                sharedElements.put("arrival", new Pair<String, View>(uniquearrival, arrival));
                sharedElements.put("frame", new Pair<String, View>(uniqueCardview, cardView));

                VrrDetailFragment fragment = VrrDetailFragment.newInstance(entry);
                ((MainActivity) context).getNavi().navigate(
                        fragment,
                        R.id.fragment_container,
                        sharedElements
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }
}

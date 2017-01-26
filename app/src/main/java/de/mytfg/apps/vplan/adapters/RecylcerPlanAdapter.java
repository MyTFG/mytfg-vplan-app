package de.mytfg.apps.vplan.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.objects.VplanEntry;

public class RecylcerPlanAdapter extends RecyclerView.Adapter<PlanEntryHolder> {
    private Context context;
    private ArrayList<VplanEntry> elements = new ArrayList<>();
    private PlanEntryAdapterEvents events;

    private int lastPosition = -1; // None

    private String unique;

    private int endOffset;

    public RecylcerPlanAdapter(Context c) {
        this.context = c;
        this.endOffset = endOffset;
        unique = UUID.randomUUID().toString();
    }

    public void setBaseObjectAdapterEvents(PlanEntryAdapterEvents events) {
        this.events = events;
    }

    public void addItem(VplanEntry item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public PlanEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_planentry, null);
        PlanEntryHolder planEntryHolder = new PlanEntryHolder(view);
        return planEntryHolder;
    }

    @Override
    public void onBindViewHolder(final PlanEntryHolder holder, final int position) {
        holder.update(elements.get(position));
        setAnimation(holder.getCardView(), position);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public interface PlanEntryAdapterEvents {
        void onEndReached(ArrayList<VplanEntry> elements);
    }

    public void clear() {
        elements.clear();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(PlanEntryHolder holder) {
        holder.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }
}

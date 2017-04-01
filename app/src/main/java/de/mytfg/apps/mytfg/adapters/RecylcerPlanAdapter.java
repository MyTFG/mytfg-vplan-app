package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.fragments.PlanDetailFragment;
import de.mytfg.apps.mytfg.fragments.VrrDetailFragment;
import de.mytfg.apps.mytfg.objects.VplanEntry;

public class RecylcerPlanAdapter extends RecyclerView.Adapter<PlanEntryHolder> {
    private Context context;
    private ArrayList<VplanEntry> elements = new ArrayList<>();

    private int lastPosition = -1; // None
    private int expandedPosition = -1;

    private String unique;

    private int endOffset;

    public RecylcerPlanAdapter(Context c) {
        this.context = c;
        this.endOffset = endOffset;
        unique = UUID.randomUUID().toString();
    }

    public void setBaseObjectAdapterEvents(PlanEntryAdapterEvents events) {

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
                inflate(R.layout.cardview_planentry_detail, null);
        return new PlanEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlanEntryHolder holder, int position) {
        final VplanEntry entry = elements.get(position);
        holder.update(entry, position == expandedPosition);

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

        /*final CardView cardView = holder.getCardView();
        final TextView plan = holder.getPlan();
        final TextView subst = holder.getSubst();
        final TextView comment = holder.getComment();

        final String uniqueCardview = "frame_" + position + "_" + unique;
        final String uniquePlan = "fplan_" + position + "_" + unique;
        final String uniqueSubst = "subst_" + position + "_" + unique;
        final String uniqueComment = "comment_" + position + "_" + unique;
        ViewCompat.setTransitionName(cardView, uniqueCardview);
        ViewCompat.setTransitionName(plan, uniquePlan);
        ViewCompat.setTransitionName(subst, uniqueSubst);
        ViewCompat.setTransitionName(comment, uniqueComment);


        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Pair<String, View>> sharedElements = new HashMap<>();
                sharedElements.put("frame", new Pair<String, View>(uniqueCardview, cardView));
                sharedElements.put("plan", new Pair<String, View>(uniquePlan, plan));
                sharedElements.put("subst", new Pair<String, View>(uniqueSubst, subst));
                sharedElements.put("comment", new Pair<String, View>(uniqueComment, comment));

                PlanDetailFragment fragment = PlanDetailFragment.newInstance(entry.getVplan(), entry);
                ((MainActivity) context).getNavi().navigate(
                        fragment,
                        R.id.fragment_container,
                        sharedElements
                );
            }
        });*/
        //setAnimation(holder.getCardView(), position);
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
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
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

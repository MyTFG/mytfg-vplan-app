package de.mytfg.apps.vplan.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.objects.VplanEntry;

import static android.view.View.GONE;

public class PlanEntryHolder extends RecyclerView.ViewHolder {
    private TextView lesson;
    private TextView cls;
    private TextView plan;
    private TextView subst;
    private TextView substHeader;
    private TextView comment;
    private TextView commentHeader;

    private Context context;
    private CardView cardView;

    public PlanEntryHolder(View view) {
        super(view);
        context = view.getContext();
        cardView = (CardView) view;
        lesson = (TextView) view.findViewById(R.id.plan_entry_lesson);
        cls    = (TextView) view.findViewById(R.id.plan_entry_class);
        plan   = (TextView) view.findViewById(R.id.plan);
        subst  = (TextView) view.findViewById(R.id.subst);
        comment = (TextView) view.findViewById(R.id.comment);
        substHeader  = (TextView) view.findViewById(R.id.subst_header);
        commentHeader = (TextView) view.findViewById(R.id.comment_header);
    }

    public void update(VplanEntry planEntry) {
        //titleView.setText(baseObject.getName());
        lesson.setText(planEntry.getLesson());
        plan.setText(planEntry.getPlan());
        if (planEntry.getSubstitution().isEmpty()) {
            substHeader.setVisibility(GONE);
            subst.setVisibility(GONE);
        } else {
            subst.setText(planEntry.getSubstitution());
            subst.setVisibility(View.VISIBLE);
            substHeader.setVisibility(View.VISIBLE);
        }
        if (planEntry.getComment().isEmpty()) {
            comment.setVisibility(GONE);
            commentHeader.setVisibility(GONE);
        } else {
            comment.setText(planEntry.getComment());
            commentHeader.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
        }
        cls.setText(planEntry.getCls());
        if (planEntry.isOwn()) {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        } else {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorSecondaryLight));
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}

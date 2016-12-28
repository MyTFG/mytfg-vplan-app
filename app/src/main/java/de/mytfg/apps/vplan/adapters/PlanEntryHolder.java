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

public class PlanEntryHolder extends RecyclerView.ViewHolder {
    private TextView lesson;
    private TextView cls;
    private TextView plan;
    private TextView subst;
    private TextView comment;

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
    }

    public void update(VplanEntry planEntry) {
        //titleView.setText(baseObject.getName());
        lesson.setText(planEntry.getLesson());
        plan.setText(planEntry.getPlan());
        subst.setText(planEntry.getSubstitution());
        comment.setText(planEntry.getComment());
        cls.setText(planEntry.getCls());
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}

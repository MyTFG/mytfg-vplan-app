package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.VplanEntry;
import de.mytfg.apps.mytfg.tools.CircleBackground;
import de.mytfg.apps.mytfg.tools.Settings;

import static android.view.View.GONE;

public class PlanEntryHolder extends RecyclerView.ViewHolder {
    private TextView lesson;
    private TextView cls;
    private TextView plan;
    private TextView planHeader;
    private TextView subst;
    private TextView substHeader;
    private ImageView arrow;
    private LinearLayout linearLayout;
    private TextView emptyText;
    private FrameLayout clsFrame;

    private Context context;
    private CardView cardView;

    public PlanEntryHolder(View view) {
        super(view);
        context = view.getContext();
        cardView = (CardView) view;
        lesson = view.findViewById(R.id.plan_entry_lesson);
        cls    = view.findViewById(R.id.plan_entry_class);
        plan   = view.findViewById(R.id.plan);
        subst  = view.findViewById(R.id.subst);
        arrow = view.findViewById(R.id.arrow);
        planHeader = view.findViewById(R.id.plan_header);
        substHeader = view.findViewById(R.id.subst_header);
        linearLayout = view.findViewById(R.id.plan_entry_layout);
        emptyText = view.findViewById(R.id.plan_entry_empty);
        clsFrame = view.findViewById(R.id.plan_entry_class_frame);
    }

    public void update(VplanEntry planEntry) {
        update(planEntry, false);
    }

    public void update(VplanEntry planEntry, boolean expanded) {
        if (planEntry == null) {
            linearLayout.setVisibility(GONE);
            emptyText.setVisibility(View.VISIBLE);
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            return;
        }

        emptyText.setVisibility(GONE);
        linearLayout.setVisibility(View.VISIBLE);

        Settings settings = new Settings(context);

        if (expanded) {
            substHeader.setVisibility(View.VISIBLE);
            planHeader.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.GONE);
        } else {
            substHeader.setVisibility(View.GONE);
            planHeader.setVisibility(View.GONE);
            arrow.setVisibility(View.VISIBLE);
        }

        //titleView.setText(baseObject.getName());
        lesson.setText(planEntry.getLesson());
        String planText = settings.getBool("plan_fulltext") ? planEntry.getPlanText() : planEntry.getPlan();
        String substText = settings.getBool("plan_fulltext") ? planEntry.getSubstText() : planEntry.getSubstitution();
        plan.setText(planText);
        if (planEntry.getSubstitution().isEmpty()) {
            //substHeader.setVisibility(GONE);
            subst.setVisibility(GONE);
        } else {
            subst.setText(substText);
            subst.setVisibility(View.VISIBLE);
            //substHeader.setVisibility(View.VISIBLE);
        }


        if (planEntry.getComment().isEmpty() && planEntry.getSubstitution().isEmpty()) {
            arrow.setVisibility(GONE);
        } else if (!expanded) {
            arrow.setVisibility(View.VISIBLE);
        }

        cls.setText(planEntry.getCls());
        if (planEntry.isOwn()) {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        } else {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorSecondaryLight));
        }

        clsFrame.setBackgroundResource(CircleBackground.getResource(planEntry.getColor()));
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public TextView getPlan() {
        return plan;
    }

    public TextView getSubst() {
        return subst;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void clearAnimation()
    {
        cardView.clearAnimation();
    }
}

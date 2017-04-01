package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.auto_fit_textview.AutoResizeTextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.VplanEntry;
import de.mytfg.apps.mytfg.tools.Settings;

import static android.view.View.GONE;

public class PlanEntryHolder extends RecyclerView.ViewHolder {
    private TextView lesson;
    private TextView cls;
    private TextView plan;
    private TextView planHeader;
    private TextView subst;
    private TextView substHeader;
    private TextView comment;
    private TextView commentHeader;
    private AutoResizeTextView summary;
    private ImageView arrow;
    private LinearLayout linearLayout;

    private Context context;
    private CardView cardView;

    public PlanEntryHolder(View view) {
        super(view);
        context = view.getContext();
        cardView = (CardView) view;
        lesson = (TextView) view.findViewById(R.id.plan_entry_lesson);
        cls    = (TextView) view.findViewById(R.id.plan_entry_class);
        summary = (AutoResizeTextView) view.findViewById(R.id.plan_entry_summary);
        plan   = (TextView) view.findViewById(R.id.plan);
        subst  = (TextView) view.findViewById(R.id.subst);
        comment = (TextView) view.findViewById(R.id.comment);
        arrow = (ImageView) view.findViewById(R.id.arrow);
        planHeader = (TextView) view.findViewById(R.id.plan_header);
        substHeader = (TextView) view.findViewById(R.id.subst_header);
        commentHeader = (TextView) view.findViewById(R.id.comment_header);
        //linearLayout = (LinearLayout) view.findViewById(R.id.plan_layout);
    }

    public void update(VplanEntry planEntry) {
        update(planEntry, false);
    }

    public void update(VplanEntry planEntry, boolean expanded) {
        Settings settings = new Settings(context);

        if (expanded) {
            commentHeader.setVisibility(View.VISIBLE);
            substHeader.setVisibility(View.VISIBLE);
            planHeader.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.GONE);
            summary.setVisibility(View.GONE);
        } else {
            commentHeader.setVisibility(View.GONE);
            substHeader.setVisibility(View.GONE);
            planHeader.setVisibility(View.GONE);
            arrow.setVisibility(View.VISIBLE);
            summary.setVisibility(View.VISIBLE);
        }
        summary.setText(planEntry.getSummary());

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
        if (planEntry.getComment().isEmpty()) {
            comment.setVisibility(GONE);
            //commentHeader.setVisibility(GONE);
        } else {
            comment.setText(planEntry.getComment());
            //commentHeader.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
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

    public TextView getComment() {
        return comment;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void clearAnimation()
    {
        cardView.clearAnimation();
    }
}

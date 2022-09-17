package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.objects.Vplan;
import de.mytfg.apps.mytfg.objects.VplanEntry;

/**
 * Provices detailed related to a VRR connection.
 */

public class PlanDetailFragment extends AuthenticationFragment {
    private Vplan vplan;
    private VplanEntry entry;

    public static PlanDetailFragment newInstance(Vplan plan, VplanEntry entry) {
        PlanDetailFragment fragment = new PlanDetailFragment();
        fragment.vplan = plan;
        fragment.entry = entry;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan_detail, container, false);

        TextView day = (TextView) view.findViewById(R.id.plan_detail_day);
        TextView lesson = (TextView) view.findViewById(R.id.plan_detail_lesson);
        TextView cls = (TextView) view.findViewById(R.id.plan_detail_class);

        TextView plan = (TextView) view.findViewById(R.id.plan_detail_plan);
        TextView subst = (TextView) view.findViewById(R.id.plan_detail_subst);
        TextView comment = (TextView) view.findViewById(R.id.plan_detail_comment);

        day.setText(vplan.getDayString());
        lesson.setText(entry.getLesson() + " Std.");
        cls.setText(entry.getCls());
        plan.setText(entry.getPlan());
        subst.setText(entry.getSubstitution());
        comment.setText(entry.getComment());

        ((MainActivity)getActivity()).getToolbarManager()
                .clear()
                .setTitle(entry.getSummary())
                .setExpandable(true, true)
                .setImage(R.drawable.vplan_header_s)
                .showBottomScrim();

        ViewCompat.setTransitionName(view, getArguments().getString("frame"));
        ViewCompat.setTransitionName(plan, getArguments().getString("plan"));
        ViewCompat.setTransitionName(subst, getArguments().getString("subst"));
        ViewCompat.setTransitionName(comment, getArguments().getString("comment"));

        return view;
    }
}

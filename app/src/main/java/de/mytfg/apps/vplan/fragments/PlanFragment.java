package de.mytfg.apps.vplan.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.adapters.FragmentHolder;
import de.mytfg.apps.vplan.adapters.ViewPagerAdapter;
import de.mytfg.apps.vplan.logic.PlanLogic;
import de.mytfg.apps.vplan.objects.Vplan;

public class PlanFragment extends Fragment {
    private View view;
    private PlanLogic today;
    private PlanLogic tomorrow;
    private Vplan todayPlan;
    private Vplan tomorrowPlan;
    private MainActivity context;
    private ViewPagerAdapter viewPagerAdapter;


    public PlanFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plan, container, false);
        MainActivity context = (MainActivity)this.getActivity();
        this.context = context;
        context.getToolbarManager()
                .setTitle(getString(R.string.menutitle_plan))
                .setExpandable(true, true)
                .setTabs(true);

        // Create logics
        todayPlan = new Vplan(context, "today");
        tomorrowPlan = new Vplan(context, "tomorrow");

        today = new PlanLogic(todayPlan);
        tomorrow = new PlanLogic(tomorrowPlan);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.plan_pager);

        // Create Pager elements if not existent
        if (today == null || tomorrow == null || viewPagerAdapter == null) {
            todayPlan = new Vplan(context, "today");
            tomorrowPlan = new Vplan(context, "tomorrow");

            today = new PlanLogic(todayPlan);
            tomorrow = new PlanLogic(tomorrowPlan);


            viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

            viewPagerAdapter.addFragment(
                    FragmentHolder.newInstance(
                            R.layout.fragment_base_list,
                            today
                    ),
                    getResources().getString(R.string.plan_today)
            );
            viewPagerAdapter.addFragment(
                    FragmentHolder.newInstance(
                            R.layout.fragment_base_list,
                            tomorrow
                    ),
                    getResources().getString(R.string.plan_tomorrow)
            );
            viewPager.setAdapter(viewPagerAdapter);
        }

        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }
}
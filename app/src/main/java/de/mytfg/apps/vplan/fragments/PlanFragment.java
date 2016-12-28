package de.mytfg.apps.vplan.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.plan_details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TabLayout tabLayout = context.getToolbarManager().getTabs();
        Vplan plan = tabLayout.getSelectedTabPosition() == 0 ? todayPlan : tomorrowPlan;
        if (!plan.isLoaded()) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) context.findViewById(R.id.coordinator_layout);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,
                            getString(R.string.plan_not_loaded),
                            Snackbar.LENGTH_LONG);
            snackbar.show();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.show_absent:
                AlertDialog.Builder absentDialog = new AlertDialog.Builder(context);
                absentDialog.setTitle(getString(R.string.plan_absent));
                String absent = null;
                for (String msg : plan.getAbsentStrings()) {
                    if (absent == null) {
                        absent = msg;
                    } else {
                        absent += "\n\n" + msg;
                    }
                }
                absentDialog.setMessage(absent);
                absentDialog.setIcon(R.drawable.ic_menu_absent);
                AlertDialog abs = absentDialog.create();
                abs.show();
                return true;
            case R.id.show_marquee:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(getString(R.string.plan_marquee));
                String message = null;
                for (String msg : plan.getMarquee()) {
                    if (message == null) {
                        message = msg;
                    } else {
                        message += "\n" + msg;
                    }
                }
                alertDialog.setMessage(message);
                alertDialog.setIcon(R.drawable.ic_menu_about);
                AlertDialog alert = alertDialog.create();
                alert.show();
                return true;
            case R.id.show_time:
                AlertDialog.Builder timeDialog = new AlertDialog.Builder(context);
                timeDialog.setTitle(getString(R.string.plan_time));
                String time = plan.getChanged();
                timeDialog.setMessage(time);
                timeDialog.setIcon(R.drawable.ic_menu_clock);
                AlertDialog timeDlg = timeDialog.create();
                timeDlg.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

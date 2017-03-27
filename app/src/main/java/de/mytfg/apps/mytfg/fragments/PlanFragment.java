package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.FragmentHolder;
import de.mytfg.apps.mytfg.adapters.ViewPagerAdapter;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.logic.PlanLogic;
import de.mytfg.apps.mytfg.objects.User;
import de.mytfg.apps.mytfg.objects.Vplan;
import de.mytfg.apps.mytfg.toolbar.ToolbarManager;
import de.mytfg.apps.mytfg.tools.CustomViewTarget;
import de.mytfg.apps.mytfg.tools.ShowCaseManager;

public class PlanFragment extends AuthenticationFragment {
    private View view;
    private PlanLogic today;
    private PlanLogic tomorrow;
    private Vplan todayPlan;
    private Vplan tomorrowPlan;
    private MainActivity context;

    public PlanFragment() {
    }

    @Override
    public boolean needsAuthentication() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plan, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.plan_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this.getChildFragmentManager()));
        final MainActivity context = (MainActivity)this.getActivity();

        this.context = context;
        context.getToolbarManager()
                .clear(false)
                .setTitle(getString(R.string.menutitle_plan))
                .clearMenu()
                .setImage(R.drawable.vplan_header_s)
                .showBottomScrim()
                .setExpandable(true, true)
                .setTabs(true)
                .showFab()
                .setTabOutscroll(true);

        setHasOptionsMenu(true);

        final FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getToolbarManager().showSearchBar();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.plan_details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MyTFGApi api = new MyTFGApi(context);
        final User user = api.getUser();

        if (user.getRights() < 2) {
            menu.getItem(1).setVisible(false);
        }

        SearchView searchView = context.getToolbarManager().getSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                today.filter(newText);
                tomorrow.filter(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                today.resetFilter();
                tomorrow.resetFilter();
                return true;
            }
        });
    }

    private Vplan getTodayPlan() {
        return todayPlan;
    }

    private Vplan getTomorrowPlan() {
        return tomorrowPlan;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TabLayout tabLayout = context.getToolbarManager().getTabs();
        Vplan plan = tabLayout.getSelectedTabPosition() == 0 ? getTodayPlan() : getTomorrowPlan();
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
                if (absent == null) {
                    absent = "";
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
                if (message == null) {
                    message = "";
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
        if (today == null || tomorrow == null || true) {
            todayPlan = new Vplan(context, "today");
            tomorrowPlan = new Vplan(context, "tomorrow");

            today = new PlanLogic(todayPlan);
            tomorrow = new PlanLogic(tomorrowPlan);

            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

            viewPagerAdapter.addFragment(
                    FragmentHolder.newInstance(
                            R.layout.fragment_base_list,
                            today
                    )
            );
            viewPagerAdapter.addFragment(
                    FragmentHolder.newInstance(
                            R.layout.fragment_base_list,
                            tomorrow
                    )
            );
            viewPager.setAdapter(viewPagerAdapter);
        }

        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        this.showcase();
    }

    private void showcase() {
        ShowCaseManager scm = new ShowCaseManager(getContext());

        ToolbarManager tbm = ((MainActivity)getActivity()).getToolbarManager();

        Target tab1 = new CustomViewTarget(tbm.getTabs(), 0.25, 0.5);
        Target tab2 = new CustomViewTarget(tbm.getTabs(), 0.75, 0.5);
        Target fab = new ViewTarget(R.id.fab, getActivity());
        Target menu = new CustomViewTarget(tbm.getToolbar(), 200, 0, CustomViewTarget.Type.ABS_MID_R);

        scm.createChain(this)
                .add(tab1, R.string.sc_plan_today_title, R.string.sc_plan_today_text)
                .add(tab2, R.string.sc_plan_next_title, R.string.sc_plan_next_text)
                .add(menu, R.string.sc_plan_menu_title, R.string.sc_plan_menu_text)
                .add(fab, R.string.sc_plan_search_title, R.string.sc_plan_search_text, true)
                .showChain();

    }

}


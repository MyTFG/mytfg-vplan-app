package de.mytfg.apps.mytfg.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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


import org.json.JSONObject;

import java.net.HttpURLConnection;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.FragmentHolder;
import de.mytfg.apps.mytfg.adapters.ViewPagerAdapter;
import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.logic.PlanLogic;
import de.mytfg.apps.mytfg.objects.User;
import de.mytfg.apps.mytfg.objects.Vplan;

public class PlanFragment extends AuthenticationFragment {
    private View view;
    private PlanLogic today;
    private PlanLogic tomorrow;
    private Vplan todayPlan;
    private Vplan tomorrowPlan;
    private MainActivity context;
    private int tab = 0;
    private boolean expand = true;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private Parcelable viewPagerState;
    private boolean firstOpen = true;

    public PlanFragment() {
    }

    @Override
    public boolean needsAuthentication() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_plan, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.plan_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this.getChildFragmentManager()));
        if (viewPagerState != null) {
            viewPager.onRestoreInstanceState(viewPagerState);
        }

        final MainActivity context = (MainActivity)this.getActivity();

        this.context = context;
        context.getToolbarManager()
                .clear(false)
                .setTitle(getString(R.string.menutitle_plan))
                .clearMenu()
                .setImage(R.drawable.vplan_header_s)
                .showBottomScrim()
                .setTabs(true)
                .showFab()
                .setTabOutscroll(true);

        if (expand) {
            context.getToolbarManager().setExpandable(true, true);
        }
        expand = false;

        if (savedInstanceState != null && savedInstanceState.containsKey("planTab")) {
            tab = savedInstanceState.getInt("planTab");
        }

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
                    absent = getString(R.string.plan_no_teachers);
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
                        message += "\n\n" + msg;
                    }
                }
                if (message == null) {
                    message = getString(R.string.plan_no_announcements);
                }
                alertDialog.setMessage(message);
                alertDialog.setIcon(R.drawable.ic_menu_about_old);
                AlertDialog alert = alertDialog.create();
                alert.show();
                return true;
            case R.id.show_time:
                AlertDialog.Builder timeDialog = new AlertDialog.Builder(context);
                timeDialog.setTitle(getString(R.string.plan_time));
                String time = plan.getChanged();
                if (time.isEmpty() || time.equals("0")) {
                    timeDialog.setMessage(getString(R.string.plan_no_update));
                } else {
                    timeDialog.setMessage(time);
                }
                timeDialog.setIcon(R.drawable.ic_menu_clock);
                AlertDialog timeDlg = timeDialog.create();
                timeDlg.show();
                return true;
            case R.id.show_exams:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.link_examplan_url)));
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPagerState = viewPager.onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (viewPager == null) {
            viewPager = (ViewPager) view.findViewById(R.id.plan_pager);
        }


        todayPlan = new Vplan(context, "today");
        tomorrowPlan = new Vplan(context, "tomorrow");

        today = new PlanLogic(todayPlan);
        tomorrow = new PlanLogic(tomorrowPlan);

        viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                context.getToolbarManager().showFab();
                tab = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        if (firstOpen) {
            firstOpen = false;

            if (context.getIntent().getExtras() != null && context.getIntent().getExtras().containsKey("type")) {
                if ("vplan_update".equals(context.getIntent().getExtras().getString("type"))) {
                    tab = context.getIntent().getExtras().getInt("tab");
                }
            }
        }

        viewPager.setCurrentItem(tab);

        /*if (viewPagerState != null) {
            viewPager.onRestoreInstanceState(viewPagerState);
        }*/

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setupViewpager();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 10);

        //this.showcase();

        MyTFGApi api = new MyTFGApi(context);

        ApiParams params = new ApiParams();
        api.addAuth(params);

        api.call("api/account/checkMail.x", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    if (result != null) {
                        String mail = result.optString("mail", "");
                        Boolean isMyTFGMail = result.optBoolean("isMyTFGMail", false);
                        Boolean mytfgMailWarningDisabled = result.optBoolean("warningDisabled", false);

                        if (isMyTFGMail && !mytfgMailWarningDisabled) {
                            new AlertDialog.Builder(context)
                                    .setCancelable(true)
                                    .setTitle(R.string.mytfg_mail)
                                    .setMessage(getResources().getString(R.string.mytfg_mail_warning))
                                    .setPositiveButton(R.string.mytfg_mail_update, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            context.getNavi().toWebView(MyTFGApi.URL_SETTINGS, context);
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.mytfg_mail_disable, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            context.getNavi().toWebView(MyTFGApi.URL_SETTINGS, context);
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNeutralButton(R.string.mytfg_mail_ignore, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            }
        });
    }

    private void setupViewpager() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.plan_pager);

        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private int getTab() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.plan_pager);
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }

    private void showcase() {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("planTab", getTab());
    }
}


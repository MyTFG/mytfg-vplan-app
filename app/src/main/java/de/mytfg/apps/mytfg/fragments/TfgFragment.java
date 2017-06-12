package de.mytfg.apps.mytfg.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.amlcurran.showcaseview.targets.Target;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.FragmentHolder;
import de.mytfg.apps.mytfg.adapters.ViewPagerAdapter;
import de.mytfg.apps.mytfg.logic.EventsLogic;
import de.mytfg.apps.mytfg.logic.NewsLogic;
import de.mytfg.apps.mytfg.objects.TfgEvents;
import de.mytfg.apps.mytfg.objects.TfgNews;
import de.mytfg.apps.mytfg.toolbar.ToolbarManager;
import de.mytfg.apps.mytfg.tools.CustomViewTarget;
import de.mytfg.apps.mytfg.tools.ShowCaseManager;

public class TfgFragment extends AuthenticationFragment {
    private View view;
    private MainActivity context;

    private NewsLogic newsLogic;
    private TfgNews news;
    private FragmentHolder newsFragment;

    private EventsLogic eventsLogic;
    private TfgEvents events;
    private FragmentHolder eventsFragment;

    private ViewPagerAdapter viewPagerAdapter;

    private int setTab = 0;
    private boolean expand = true;

    public TfgFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("NEWS", "createView");
        Log.d("NEWS", "createView " + view);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_tfg, container, false);
        }
        final MainActivity context = (MainActivity)this.getActivity();

        this.context = context;
        context.getToolbarManager()
                .clear(false)
                .setTitle(getString(R.string.menutitle_tfg))
                .clearMenu()
                .setImage(R.drawable.news_header_s)
                .showBottomScrim()
                .setTabs(true)
                .showFab()
                .setTabOutscroll(true);

        if (expand) {
            context.getToolbarManager()
                    .setExpandable(true, true);
        }

        expand = false;

        final FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getToolbarManager().showSearchBar();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey("tfgTab")) {
            setTab = savedInstanceState.getInt("tfgTab");
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tfg_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchView searchView = context.getToolbarManager().getSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newsLogic.filter(newText);
                eventsLogic.filter(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                newsLogic.resetFilter();
                eventsLogic.resetFilter();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.tfg_pager);
        news = new TfgNews(context);
        if (newsLogic == null) {
            newsLogic = new NewsLogic(news, context);
        }
        if (newsFragment == null) {
            newsFragment = FragmentHolder.newInstance(
                    R.layout.fragment_news,
                    newsLogic
            );
        }

        events = new TfgEvents(context);
        if (eventsLogic == null) {
            eventsLogic = new EventsLogic(events, context);
        }
        if (eventsFragment == null) {
            eventsFragment = FragmentHolder.newInstance(
                    R.layout.fragment_events,
                    eventsLogic
            );
        }

        if (viewPagerAdapter == null) {
            viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());
            viewPagerAdapter.addFragment(
                    newsFragment
            );
            viewPagerAdapter.addFragment(
                    eventsFragment
            );
        }

        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTab = position;
                switch (position) {
                    case 0:
                        context.getToolbarManager().setImage(R.drawable.news_header_s, true);
                        break;
                    case 1:
                        context.getToolbarManager().setImage(R.drawable.events_header_s, true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setupViewpager();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 10);

        showcase();
    }



    private int getTab() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.tfg_pager);
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }

    private void setupViewpager() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.tfg_pager);


        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
        viewPager.setCurrentItem(setTab);
    }



    private void showcase() {
        ShowCaseManager scm = new ShowCaseManager(getContext());

        ToolbarManager tbm = ((MainActivity)getActivity()).getToolbarManager();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
            && (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

            // Don't show the navigation hint on large landscape devices (tablets)
            return;
        }

        scm.createChain(this)
                .add(new CustomViewTarget(tbm.getToolbar(), 50, 0, CustomViewTarget.Type.ABS_MID_L), R.string.sc_home_navi_title, R.string.sc_home_navi_text)
                .showChain();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tfgTab", getTab());
    }
}


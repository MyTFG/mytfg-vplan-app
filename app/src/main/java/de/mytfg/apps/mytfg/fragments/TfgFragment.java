package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
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

    private EventsLogic eventsLogic;
    private TfgEvents events;

    private int setTab = 0;

    public TfgFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tfg, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.tfg_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this.getChildFragmentManager()));
        final MainActivity context = (MainActivity)this.getActivity();

        this.context = context;
        context.getToolbarManager()
                .clear(false)
                .setTitle(getString(R.string.menutitle_tfg))
                .clearMenu()
                .setImage(R.drawable.news_header_s)
                .showBottomScrim()
                .setExpandable(true, true)
                .setTabs(true)
                .showFab()
                .setTabOutscroll(true);

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
        newsLogic = new NewsLogic(news, context);

        events = new TfgEvents(context);
        eventsLogic = new EventsLogic(events, context);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

        viewPagerAdapter.addFragment(
                FragmentHolder.newInstance(
                        R.layout.fragment_news,
                        newsLogic
                )
        );

        viewPagerAdapter.addFragment(
                FragmentHolder.newInstance(
                        R.layout.fragment_events,
                        eventsLogic
                )
        );
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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

        //showcase();
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

        Target tab1 = new CustomViewTarget(tbm.getTabs(), 0.25, 0.5);
        Target tab2 = new CustomViewTarget(tbm.getTabs(), 0.75, 0.5);

        scm.createChain(this)
                .add(tab1, R.string.sc_home_news, R.string.sc_home_news_text)
                .add(tab2, R.string.sc_home_events, R.string.sc_home_events_text)
                .showChain();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tfgTab", getTab());
    }
}


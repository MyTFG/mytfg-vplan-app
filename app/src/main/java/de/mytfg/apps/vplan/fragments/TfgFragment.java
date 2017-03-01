package de.mytfg.apps.vplan.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.adapters.FragmentHolder;
import de.mytfg.apps.vplan.adapters.ViewPagerAdapter;
import de.mytfg.apps.vplan.logic.EventsLogic;
import de.mytfg.apps.vplan.logic.NewsLogic;
import de.mytfg.apps.vplan.logic.PlanLogic;
import de.mytfg.apps.vplan.objects.TfgEvents;
import de.mytfg.apps.vplan.objects.TfgNews;
import de.mytfg.apps.vplan.objects.Vplan;
import de.mytfg.apps.vplan.toolbar.ToolbarManager;
import de.mytfg.apps.vplan.tools.CustomViewTarget;
import de.mytfg.apps.vplan.tools.ShowCaseManager;

public class TfgFragment extends AuthenticationFragment {
    private View view;
    private MainActivity context;

    private NewsLogic newsLogic;
    private TfgNews news;

    private EventsLogic eventsLogic;
    private TfgEvents events;

    public TfgFragment() {
    }

    @Override
    public boolean needsAuthentication() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tfg, container, false);
        final MainActivity context = (MainActivity)this.getActivity();

        this.context = context;
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_tfg))
                .clearMenu()
                .setImage(R.drawable.news_header_s)
                .showBottomScrim()
                .setExpandable(true, true)
                .setTabs(true)
                .setTabOutscroll(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.tfg_pager);

        // Create Pager elements if not existent
        if (newsLogic == null || eventsLogic == null) {
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
        }

        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }
}


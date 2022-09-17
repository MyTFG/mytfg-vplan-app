package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.FragmentHolder;
import de.mytfg.apps.mytfg.adapters.ViewPagerAdapter;
import de.mytfg.apps.mytfg.logic.AbbreviationLogic;
import de.mytfg.apps.mytfg.objects.Abbreviations;

public class AbbreviationsFragment extends AuthenticationFragment {
    private View view;
    private MainActivity context;

    private AbbreviationLogic teacherLogic;

    private AbbreviationLogic subjectLogic;

    private int setTab = 0;

    public AbbreviationsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_abbreviations, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.abbr_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this.getChildFragmentManager()));
        final MainActivity context = (MainActivity)this.getActivity();

        this.context = context;
        context.getToolbarManager()
                .clear(false)
                .setTitle(getString(R.string.menutitle_abbreviations))
                .clearMenu()
                .setImage(R.drawable.abbreviation_teacher_header)
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

        setHasOptionsMenu(true);

        if (savedInstanceState != null && savedInstanceState.containsKey("abbrTab")) {
            setTab = savedInstanceState.getInt("abbrTab");
        }

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
                teacherLogic.filter(newText);
                subjectLogic.filter(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                teacherLogic.resetFilter();
                subjectLogic.resetFilter();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.abbr_pager);
        // Create Pager elements if not existent
        Abbreviations teachers = new Abbreviations(context, "teachers");
        teacherLogic = new AbbreviationLogic(teachers, context);

        Abbreviations subjects = new Abbreviations(context, "subjects");
        subjectLogic = new AbbreviationLogic(subjects, context);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

        viewPagerAdapter.addFragment(
                FragmentHolder.newInstance(
                        R.layout.fragment_abbreviation_list,
                        teacherLogic
                )
        );

        viewPagerAdapter.addFragment(
                FragmentHolder.newInstance(
                        R.layout.fragment_abbreviation_list,
                        subjectLogic
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
                        context.getToolbarManager().setImage(R.drawable.abbreviation_teacher_header, true);
                        break;
                    case 1:
                        context.getToolbarManager().setImage(R.drawable.abbreviation_subjects_header, true);
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
    }


    private void setupViewpager() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.abbr_pager);

        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        viewPager.setCurrentItem(setTab);
    }

    private int getTab() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.abbr_pager);
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("abbrTab", getTab());
    }
}


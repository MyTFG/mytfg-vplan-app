package de.mytfg.apps.mytfg.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.FragmentHolder;
import de.mytfg.apps.mytfg.adapters.ViewPagerAdapter;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.logic.ExamLogic;
import de.mytfg.apps.mytfg.objects.Exams;

public class ExamFragment extends AuthenticationFragment {
    private View view;

    private Exams exams;

    private MainActivity context;
    private int tab = 0;
    private boolean expand = true;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private Parcelable viewPagerState;
    private boolean firstOpen = true;
    private ExamLogic[] logics = new ExamLogic[3];

    public ExamFragment() {
    }

    @Override
    public boolean needsAuthentication() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_exams, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.exam_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this.getChildFragmentManager()));
        if (viewPagerState != null) {
            viewPager.onRestoreInstanceState(viewPagerState);
        }

        final MainActivity context = (MainActivity)this.getActivity();

        this.context = context;
        context.getToolbarManager()
                .clear(false)
                .setTitle(getString(R.string.menutitle_exams))
                .clearMenu()
                .setImage(R.drawable.exam_header)
                .showBottomScrim()
                .setTabs(true)
                .showFab()
                .setTabOutscroll(true);

        if (expand) {
            context.getToolbarManager().setExpandable(true, true);
        }
        expand = false;

        if (savedInstanceState != null && savedInstanceState.containsKey("examTab")) {
            tab = savedInstanceState.getInt("examTab");
        } else {
            // Test if user is part of grade 10,11 or 12 and select tab accordingly
            MyTFGApi api = new MyTFGApi(context);
            int gradenum = api.getUser().getGradeNum();
            if (10 <= gradenum && gradenum <= 12) {
                tab = gradenum - 10;
            }
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
        inflater.inflate(R.menu.exam_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchView searchView = context.getToolbarManager().getSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                for (ExamLogic logic : logics) {
                    logic.filter(newText);
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                for (ExamLogic logic : logics) {
                    logic.resetFilter();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TabLayout tabLayout = context.getToolbarManager().getTabs();

        switch (item.getItemId()) {
            case R.id.show_exams:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.link_examplan_url)));
                startActivity(i);
                return true;
            case R.id.show_exam_title:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(getString(R.string.exams_info));

                String msg;
                if (!exams.isLoaded()) {
                    msg = getString(R.string.exams_not_loaded);
                } else {
                    msg = exams.getTitle() + "\n\n" + exams.getTimeText() + "\n\n" + exams.getTimeSpan();
                }

                alertDialog.setMessage(msg);
                alertDialog.setIcon(R.drawable.ic_menu_about_old);
                AlertDialog alert = alertDialog.create();
                alert.show();
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
            viewPager = (ViewPager) view.findViewById(R.id.exam_pager);
        }

        exams = new Exams(context);

        for (int i = 0; i < logics.length; ++i) {
            String cls = "" + (10 + i);
            logics[i] = new ExamLogic(exams, cls);
        }

        viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

        for (ExamLogic logic : logics) {
            viewPagerAdapter.addFragment(
                    FragmentHolder.newInstance(
                            R.layout.fragment_base_list,
                            logic
                    )
            );
        }

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
    }

    private void setupViewpager() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.exam_pager);

        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private int getTab() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.exam_pager);
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("examTab", getTab());
    }
}


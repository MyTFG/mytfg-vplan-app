package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.FragmentHolder;
import de.mytfg.apps.mytfg.adapters.ViewPagerAdapter;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.logic.AuthenticationLogic;
import de.mytfg.apps.mytfg.objects.Authentications;

/**
 * Fragment to manage MyTFG Logins / App Logins.
 */

public class AuthmanageFragment extends AuthenticationFragment {
    private View view;
    private MainActivity context;
    private int tab;
    private ViewPager viewPager;
    private Parcelable viewPagerState;
    private ViewPagerAdapter viewPagerAdapter;

    private AuthenticationLogic active;
    private AuthenticationLogic timedout;

    private Authentications auths;

    @Override
    public boolean needsAuthentication() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_authmanage, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        this.context = context;
        viewPager = (ViewPager) view.findViewById(R.id.authmanage_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this.getChildFragmentManager()));
        if (viewPagerState != null) {
            viewPager.onRestoreInstanceState(viewPagerState);
        }
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.authmanage_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_authmanage))
                .setTabs(true)
                .setExpandable(true, true);

        if (savedInstanceState != null && savedInstanceState.containsKey("tab")) {
            tab = savedInstanceState.getInt("tab");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (viewPager == null) {
            viewPager = (ViewPager) view.findViewById(R.id.plan_pager);
        }

        auths = new Authentications(context);
        auths.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
            }
        });

        active = new AuthenticationLogic(auths, false);
        timedout = new AuthenticationLogic(auths, true);

        viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

        viewPagerAdapter.addFragment(
                FragmentHolder.newInstance(
                        R.layout.fragment_base_list,
                        active
                )
        );
        viewPagerAdapter.addFragment(
                FragmentHolder.newInstance(
                        R.layout.fragment_base_list,
                        timedout
                )
        );
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupViewpager() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.authmanage_pager);

        TabLayout tabLayout = context.getToolbarManager().getTabs();
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private int getTab() {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.authmanage_pager);
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getTab());
    }
}

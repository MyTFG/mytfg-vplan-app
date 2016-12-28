package de.mytfg.apps.vplan.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.api.MyTFGApi;
import de.mytfg.apps.vplan.fragments.AboutFragment;
import de.mytfg.apps.vplan.fragments.AccountFragment;
import de.mytfg.apps.vplan.fragments.LoginFragment;
import de.mytfg.apps.vplan.fragments.PlanFragment;
import de.mytfg.apps.vplan.fragments.StartFragment;
import de.mytfg.apps.vplan.navigation.Navigation;
import de.mytfg.apps.vplan.toolbar.ToolbarManager;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Navigation navi;
    // private ApiManager apiManager;
    public static SharedPreferences sharedPreferences;
    private ToolbarManager toolbarManager;
    private MainActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        navi = new Navigation(this);
        context = this;

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbarManager = new ToolbarManager(this, drawerLayout);
        toolbarManager.setExpandable(false, false);

        navigationView.inflateMenu(R.menu.navigation_menu);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                MyTFGApi api = new MyTFGApi(context);
                if (!api.isLoggedIn()) {
                    navi.clear();
                    navi.navigate(new LoginFragment(), R.id.fragment_container);
                    return true;
                }
                switch (item.getItemId()) {
                    default:
                        return false;
                    case R.id.mainmenu_start:
                        navi.clear();
                        navi.navigate(new StartFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_plan:
                        navi.clear();
                        navi.navigate(new PlanFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_account:
                        navi.clear();
                        navi.navigate(new AccountFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_about:
                        navi.clear();
                        navi.navigate(new AboutFragment(), R.id.fragment_container);
                        return true;
                }
            }
        });


        Fragment fragment;

        MyTFGApi api = new MyTFGApi(context);
        if (!api.isLoggedIn()) {
            fragment = new LoginFragment();
        } else {
            if (savedInstanceState != null) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState,
                        "fragmentInstanceSaved");
            } else {
                fragment = new StartFragment();
            }
        }
        navi.navigate(fragment, R.id.fragment_container);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public void onBackPressed() {
        navi.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState,
                "fragmentInstanceSaved",
                getSupportFragmentManager().findFragmentById(R.id.fragment_container));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.cast, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public ToolbarManager getToolbarManager() {
        return toolbarManager;
    }

    public Navigation getNavi() {
        return this.navi;
    }
}


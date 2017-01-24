package de.mytfg.apps.vplan.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentContainer;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Set;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.api.MyTFGApi;
import de.mytfg.apps.vplan.fragments.AboutFragment;
import de.mytfg.apps.vplan.fragments.AuthenticationFragment;
import de.mytfg.apps.vplan.fragments.LinksFragment;
import de.mytfg.apps.vplan.fragments.OfficeFragment;
import de.mytfg.apps.vplan.fragments.SettingsFragment;
import de.mytfg.apps.vplan.fragments.LoginFragment;
import de.mytfg.apps.vplan.fragments.PlanFragment;
import de.mytfg.apps.vplan.fragments.StartFragment;
import de.mytfg.apps.vplan.fragments.VrrFragment;
import de.mytfg.apps.vplan.navigation.Navigation;
import de.mytfg.apps.vplan.toolbar.ToolbarManager;
import de.mytfg.apps.vplan.tools.Settings;

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
                    case R.id.mainmenu_vrr:
                        navi.clear();
                        navi.navigate(new VrrFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_account:
                        navi.clear();
                        navi.navigate(new SettingsFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_links:
                        navi.clear();
                        navi.navigate(new LinksFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_about:
                        navi.clear();
                        navi.navigate(new AboutFragment(), R.id.fragment_container);
                        return true;

                    // SUBMENU "PARTNERS"
                    case R.id.mainmenu_partners:
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.submenu_partners);
                        return true;
                    case R.id.submenu_partners_back:
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.navigation_menu);
                        return true;
                    case R.id.submenu_partners_secretary:
                        navi.clear();
                        navi.navigate(new OfficeFragment(), R.id.fragment_container);
                        return true;
                }
            }
        });


        AuthenticationFragment fragment;
        Settings settings = new Settings(context);

        MyTFGApi api = new MyTFGApi(context);
        if (!api.isLoggedIn() && !settings.getBool("first_login_hint")) {
            settings.save("first_login_hint", true);
            fragment = new LoginFragment();
        } else {
            if (savedInstanceState != null) {
                fragment = (AuthenticationFragment)getSupportFragmentManager().getFragment(savedInstanceState,
                        "fragmentInstanceSaved");
            } else {
                // Read Landing Page from settings
                String fragName = settings.getString("landing_page");
                try {
                    fragName = "de.mytfg.apps.vplan.fragments." + fragName;
                    Class c = Class.forName(fragName);
                    fragment = (AuthenticationFragment)c.newInstance();
                } catch (Exception ex) {
                    fragment = new StartFragment();
                }
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


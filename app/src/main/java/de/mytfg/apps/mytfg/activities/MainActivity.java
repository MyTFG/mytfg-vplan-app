package de.mytfg.apps.mytfg.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.fragments.AboutFragment;
import de.mytfg.apps.mytfg.fragments.AuthenticationFragment;
import de.mytfg.apps.mytfg.fragments.FeedbackFragment;
import de.mytfg.apps.mytfg.fragments.LinksFragment;
import de.mytfg.apps.mytfg.fragments.OfficeFragment;
import de.mytfg.apps.mytfg.fragments.SettingsFragment;
import de.mytfg.apps.mytfg.fragments.LoginFragment;
import de.mytfg.apps.mytfg.fragments.PlanFragment;
import de.mytfg.apps.mytfg.fragments.TfgFragment;
import de.mytfg.apps.mytfg.fragments.VrrFragment;
import de.mytfg.apps.mytfg.navigation.Navigation;
import de.mytfg.apps.mytfg.toolbar.ToolbarManager;
import de.mytfg.apps.mytfg.tools.Settings;

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
                    case R.id.mainmenu_news:
                        //navi.clear();
                        navi.navigate(new TfgFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_plan:
                        //navi.clear();
                        navi.navigate(new PlanFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_vrr:
                        //navi.clear();
                        navi.navigate(new VrrFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_account:
                        //navi.clear();
                        navi.navigate(new SettingsFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_about:
                        //navi.clear();
                        navi.navigate(new AboutFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_feedback:
                        //navi.clear();
                        navi.navigate(new FeedbackFragment(), R.id.fragment_container);
                        return true;

                    // SUBMENU "PARTNERS"
                    case R.id.mainmenu_more:
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.submenu_more);
                        return true;
                    case R.id.submenu_partners_back:
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.navigation_menu);
                        return true;
                    case R.id.mainmenu_links:
                        //navi.clear();
                        navi.navigate(new LinksFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_partners_secretary:
                        //navi.clear();
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
                    fragName = "de.mytfg.apps.mytfg.fragments." + fragName;
                    Class c = Class.forName(fragName);
                    fragment = (AuthenticationFragment)c.newInstance();
                } catch (Exception ex) {
                    fragment = new TfgFragment();
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


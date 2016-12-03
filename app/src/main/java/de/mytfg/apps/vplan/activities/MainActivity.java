package de.mytfg.apps.vplan.activities;

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
import android.widget.ProgressBar;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.fragments.AboutFragment;
import de.mytfg.apps.vplan.navigation.Navigation;
import de.mytfg.apps.vplan.toolbar.ToolbarManager;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Navigation navi;
    public static Context context;
    // private ApiManager apiManager;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        // apiManager = ApiManager.getInstance();

        context = this;

        //TmdbApi.init((ProgressBar) findViewById(R.id.loadingBar));

        // ToolbarManager.init(this, drawerLayout);

        navi = new Navigation(this);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ToolbarManager.init(this, drawerLayout);

        navigationView.inflateMenu(R.menu.navigation_menu);

        /*
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    default:
                        return false;
                    case R.id.mainmenu_start:
                        navi.clear();
                        navi.navigate(new StartFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_discover:
                        navi.clear();
                        navi.navigate(new DiscoverFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_movies:
                        navi.clear();
                        navi.navigate(new MoviesFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_series:
                        navi.clear();
                        navi.navigate(new TvShowsFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_persons:
                        navi.clear();
                        navi.navigate(new PersonsFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_search:
                        navi.clear();
                        navi.navigate(new SearchFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_watchlist:
                        navi.clear();
                        navi.navigate(new WatchlistFragment(), R.id.fragment_container);
                        return true;
                    case R.id.mainmenu_favorites:
                        navi.clear();
                        navi.navigate(new FavoritesFragment(), R.id.fragment_container);
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
        });*/


        Fragment fragment;
        if (savedInstanceState != null) {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState,
                    "fragmentInstanceSaved");
        } else {
            fragment = new AboutFragment();
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
}


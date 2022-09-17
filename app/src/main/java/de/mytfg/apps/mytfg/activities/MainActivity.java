package de.mytfg.apps.mytfg.activities;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.FirebaseApp;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.adapters.MyTFGWebView;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.firebase.FbApi;
import de.mytfg.apps.mytfg.fragments.AbbreviationsFragment;
import de.mytfg.apps.mytfg.fragments.AboutFragment;
import de.mytfg.apps.mytfg.fragments.AuthenticationFragment;
import de.mytfg.apps.mytfg.fragments.BoosterFragment;
import de.mytfg.apps.mytfg.fragments.ExamFragment;
import de.mytfg.apps.mytfg.fragments.FeedbackFragment;
import de.mytfg.apps.mytfg.fragments.FoundationFragment;
import de.mytfg.apps.mytfg.fragments.LinksFragment;
import de.mytfg.apps.mytfg.fragments.LostAndFoundFragment;
import de.mytfg.apps.mytfg.fragments.NotificationsFragment;
import de.mytfg.apps.mytfg.fragments.OfficeFragment;
import de.mytfg.apps.mytfg.fragments.PagesFragment;
import de.mytfg.apps.mytfg.fragments.ParentsFragment;
import de.mytfg.apps.mytfg.fragments.SettingsFragment;
import de.mytfg.apps.mytfg.fragments.PlanFragment;
import de.mytfg.apps.mytfg.fragments.TfgFragment;
import de.mytfg.apps.mytfg.fragments.VrrFragment;
import de.mytfg.apps.mytfg.fragments.WifiFragment;
import de.mytfg.apps.mytfg.navigation.Navigation;
import de.mytfg.apps.mytfg.toolbar.ToolbarManager;
import de.mytfg.apps.mytfg.tools.Settings;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Navigation navi;
    private ToolbarManager toolbarManager;
    // private ApiManager apiManager;
    public static SharedPreferences sharedPreferences;
    private MainActivity context;
    private Menu menu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings settings = new Settings(this);
        if (settings.getBool("nightmode", false)) {
            setTheme(R.style.DarkTheme_Base);
        } else {
            setTheme(R.style.AppTheme_Base);
        }

        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        navi = new Navigation(this);
        context = this;

        navigationView = findViewById(R.id.navigation_view);

        View header = navigationView.inflateHeaderView(R.layout.navigation_header);

        navi.setNavigationView(navigationView);
        navi.setNavigationHeader(header);

        drawerLayout = findViewById(R.id.drawer_layout);
        /*if (drawerLayout == null) {
            DrawerLayout alternativeDrawer = (DrawerLayout) findViewById(R.id.drawer_layout_replacement);
            if (alternativeDrawer != null) {
                alternativeDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                alternativeDrawer.setDrawerShadow(R.drawable.no_shadow, 0);
                alternativeDrawer.setScrimColor(getResources().getColor(R.color.transparent));
            }
        }*/
        toolbarManager = new ToolbarManager(this, drawerLayout);
        toolbarManager.setExpandable(false, false);

        navigationView.inflateMenu(R.menu.navigation_menu);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FbApi.updateFirebase(context);

                Bundle args = new Bundle();

                // Notify if Login timedout
                final MyTFGApi api = new MyTFGApi(context);
                api.checkLoginDialog(context);

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
                    case R.id.mainmenu_exams:
                        //navi.clear();
                        navi.navigate(new ExamFragment(), R.id.fragment_container);
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
                    case R.id.mainmenu_notification:
                        navi.navigate(new NotificationsFragment(), R.id.fragment_container);
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
                    case R.id.mainmenu_pages:
                        //navi.clear();
                        navi.navigate(new PagesFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_partners_secretary:
                        //navi.clear();
                        navi.navigate(new OfficeFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_partners_parents:
                        //navi.clear();
                        navi.navigate(new ParentsFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_partners_boosters:
                        //navi.clear();
                        navi.navigate(new BoosterFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_partners_foundation:
                        //navi.clear();
                        navi.navigate(new FoundationFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_partners_abbreviations:
                        //navi.clear();
                        navi.navigate(new AbbreviationsFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_partners_lostandfound:
                        //navi.clear();
                        navi.navigate(new LostAndFoundFragment(), R.id.fragment_container);
                        return true;
                    case R.id.submenu_wifi:
                        //navi.clear();
                        navi.navigate(new WifiFragment(), R.id.fragment_container);
                        return true;

                    // SUBMENU "MyTFG"
                    case R.id.mainmenu_mytfg_open:
                        navi.toWebView(MyTFGApi.URL_HOME, context);
                        return true;
                    case R.id.mainmenu_mytfg:
                        navigationView.getMenu().clear();
                        navi.setMyTFGMenu(navigationView.getMenu());
                        return true;
                    case R.id.submenu_mytfg_back:
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.navigation_menu);
                        return true;
                    case R.id.submenu_mytfg_home:
                        navi.toWebView(MyTFGApi.URL_HOME, context);
                        return true;
                    case R.id.submenu_mytfg_supportcenter:
                        navi.toWebView(MyTFGApi.URL_SUPPORTCENTER, context);
                        return true;
                    case R.id.submenu_mytfg_supportcenter_tickets:
                        navi.toWebView(MyTFGApi.URL_SUPPORTCENTER_TICKETS, context);
                        return true;
                    case R.id.submenu_mytfg_purchases:
                        navi.toWebView(MyTFGApi.URL_PURCHASES, context);
                        return true;
                    case R.id.submenu_mytfg_settings:
                        navi.toWebView(MyTFGApi.URL_SETTINGS, context);
                        return true;
                    case R.id.submenu_mytfg_accounts:
                        navi.toWebView(MyTFGApi.URL_ACCOUNTS, context);
                        return true;
                    case R.id.submenu_mytfg_accounts_search:
                        navi.toWebView(MyTFGApi.URL_ACCOUNTS_SEARCH, context);
                        return true;
                    case R.id.submenu_mytfg_accounts_create:
                        navi.toWebView(MyTFGApi.URL_ACCOUNTS_CREATE, context);
                        return true;
                }
            }
        });


        AuthenticationFragment fragment = null;

        if (savedInstanceState != null) {
            Log.d("Intent", "Saved Instance");
            fragment = (AuthenticationFragment)getSupportFragmentManager().getFragment(savedInstanceState,
                    "fragmentInstanceSaved");
        } else {
            Intent intent = getIntent();
            if (getIntent().getData() != null) {
                Log.d("Intent", "Intent data received (MyTFG Link)");
                Uri data = getIntent().getData();
                // URI to String, Replace http to https
                String url = data.toString().replace("http://", "https://");

                Log.d("URL", url);
                if (!MyTFGWebView.catchAppUrls(url, this)) {
                    getNavi().toWebView(url, this);
                }
                return;
            } else if (intent.getExtras() != null && intent.getExtras().containsKey("type")) {
                String type = intent.getExtras().getString("type");

                Log.d("Intent", "Intent received: " + type);

                switch (type != null ? type : "") {
                    case "open-webview":
                    case "mytfg-notification":
                        String url = intent.getExtras().getString("url", "");
                        Log.d("URL", url);
                        if (url.length() > 0) {
                            url = "https://mytfg.de/" + url;
                            navi.toWebView(url, this);
                            return;
                        }
                        break;
                    case "vplan_update":
                        fragment = new PlanFragment();
                        break;
                    case "message":
                    case "show_message":
                        fragment = new NotificationsFragment();

                        navi.navigate(fragment, R.id.fragment_container);

                        Log.d("FB", "Notification should be shown");

                        AlertDialog.Builder notify = new AlertDialog.Builder(context);
                        notify.setTitle(intent.getExtras().getString("title"));
                        notify.setMessage(intent.getExtras().getString("text"));
                        notify.setIcon(R.drawable.tfg2_round);
                        notify.show();
                        return;
                    default:
                        Log.d("Intent-Type", type);
                        break;
                }
            }
            if (fragment == null) {
                // Read Landing Page from settings
                String fragName = settings.getString("landing_page");
                try {
                    fragName = "de.mytfg.apps.mytfg.fragments." + fragName;
                    Class c = Class.forName(fragName);
                    fragment = (AuthenticationFragment) c.newInstance();
                } catch (Exception ex) {
                    fragment = new TfgFragment();
                }
            }
        }

        navi.navigate(fragment, R.id.fragment_container);

        final MyTFGApi api = new MyTFGApi(context);
        api.checkLoginDialog(context);
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
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null) {
            getSupportFragmentManager().putFragment(outState,
                    "fragmentInstanceSaved",
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.cast, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public ToolbarManager getToolbarManager() {
        if (toolbarManager == null) {
            toolbarManager = new ToolbarManager(this, drawerLayout);
        }
        return toolbarManager;
    }

    public Navigation getNavi() {
        return this.navi;
    }

    public Menu getMenu() {
        return menu;
    }
}


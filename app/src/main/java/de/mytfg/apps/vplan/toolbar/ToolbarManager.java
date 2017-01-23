package de.mytfg.apps.vplan.toolbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.tools.DisableableAppBarLayoutBehavior;

public class ToolbarManager {
    private MainActivity context;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout holder;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TabLayout tabs;
    private SearchView searchView;

    private Toolbar toolbar;

    public ToolbarManager(MainActivity c, DrawerLayout drawer) {
        this.context = c;
        this.drawerLayout = drawer;
        this.holder = (CoordinatorLayout) context.findViewById(R.id.coordinator_layout);

        appBarLayout = (AppBarLayout) holder.findViewById(R.id.toolbarDefault);
        collapsingToolbarLayout = (CollapsingToolbarLayout) holder.findViewById(R.id.collapsingToolbarLayout);

        toolbar = (Toolbar) holder.findViewById(R.id.toolbar);
        context.setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                context,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public CoordinatorLayout coordinatorLayout() {
        return holder;
    }

    public ToolbarManager clear() {
        return this.clear(true);
    }

    public ToolbarManager clear(boolean hideFab) {
        this.setTitle(context.getString(R.string.app_name));
        if (hideFab) {
            this.hideFab();
        }
        this.clearMenu();
        this.setTabs(false);
        this.setTabOutscroll(false);
        return this;
    }

    public ToolbarManager setTabOutscroll(boolean outscroll) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        // TODO: Allow outscroll of Toolbar without tabs
        return this;
    }

    public ToolbarManager setTitle(@NonNull String title) {
        toolbar.setTitle(title);
        collapsingToolbarLayout.setTitle(title);
        return this;
    }

    public ToolbarManager clearMenu() {
        toolbar.getMenu().clear();
        return this;
    }

    public ToolbarManager setExpandable(boolean expanded, boolean animate) {
        appBarLayout.setExpanded(expanded, animate);
        appBarLayout.setEnabled(expanded);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        ((DisableableAppBarLayoutBehavior) layoutParams.getBehavior()).setEnabled(expanded);
        return this;
    }

    public ToolbarManager setImage(@DrawableRes int drawable) {
        ImageView image = (ImageView)holder.findViewById(R.id.toolbar_image);
        image.setImageResource(drawable);
        return this;
    }

    public ToolbarManager setTabs(boolean show) {
        tabs = (TabLayout) appBarLayout.findViewById(R.id.tablayout);
        if (show) {
            if (tabs == null) {
                tabs = (TabLayout) LayoutInflater.from(context).inflate(R.layout.tabs, null);
                appBarLayout.addView(tabs);
            }
        } else {
            if (tabs != null) {
                appBarLayout.removeViewAt(1);
            }
        }
        return this;
    }

    public TabLayout getTabs() {
        return tabs == null ? (TabLayout) appBarLayout.findViewById(R.id.tablayout) : tabs;
    }

    public ToolbarManager hideFab() {
        FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab);
        if (fab.getVisibility() != View.GONE) {
            fab.hide();
        }
        return this;
    }

    public ToolbarManager showFab() {
        FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab);
        if (fab.getVisibility() != View.VISIBLE) {
            fab.show();
        }
        return this;
    }

    public ToolbarManager showSearchBar() {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.expandActionView();
            searchItem.getActionView().requestFocus();
            context.getNavi().showKeyboard();
        }
        return this;
    }

    public ToolbarManager hideSeachBar() {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.setVisible(false);
        }
        return this;
    }

    public SearchView getSearchView() {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        if (searchItem != null) {
            return (SearchView) searchItem.getActionView();
        }
        return null;
    }

    public void setToolbar(@LayoutRes int layout, String title) {
        setToolbar(layout, title, false);
    }

    private void setToolbar(@LayoutRes int layout, String title, boolean translucent) {

        holder.removeViewAt(0);

        AppBarLayout appBarLayout;
        LayoutInflater inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        appBarLayout = (AppBarLayout) inflater.inflate(layout, null);

        appBarLayout.setLayoutParams(
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        holder.addView(appBarLayout, 0);
        //holder.setFitsSystemWindows(translucent);

        toolbar = (Toolbar) holder.findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        context.setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                context,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }
}

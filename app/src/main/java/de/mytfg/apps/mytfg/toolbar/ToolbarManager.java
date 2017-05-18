package de.mytfg.apps.mytfg.toolbar;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.tools.DisableableAppBarLayoutBehavior;

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
        this.setImage(R.mipmap.front);
        this.clearMenu();
        this.setTabs(false);
        this.hideBottomScrim();
        this.hideCornerImage();
        this.setTabOutscroll(false);
        return this;
    }

    public ToolbarManager hideCornerImage() {
        final ImageView corner = (ImageView)holder.findViewById(R.id.toolbar_image_corner);
        corner.setVisibility(View.GONE);
        return this;
    }

    public ToolbarManager setCornerImage(@DrawableRes int res) {
        final ImageView corner = (ImageView)holder.findViewById(R.id.toolbar_image_corner);
        corner.setImageResource(res);
        corner.setVisibility(View.VISIBLE);
        return this;
    }

    public ToolbarManager setTabOutscroll(boolean outscroll) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        //Toolbar.LayoutParams toolbarParams = (Toolbar.LayoutParams) toolbar.getLayoutParams();
        // TODO: Allow outscroll of Toolbar without tabs
        if (outscroll) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        } else {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        }
        collapsingToolbarLayout.setLayoutParams(params);
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

    public ToolbarManager setExpandable(boolean expandable, boolean animate) {
        return setExpandable(expandable, animate, expandable);
    }

    public ToolbarManager setExpandable(boolean expandable, boolean animate, boolean expanded) {
        if (expanded) {
            appBarLayout.setExpanded(true, animate);
        }
        appBarLayout.setEnabled(expandable);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        ((DisableableAppBarLayoutBehavior) layoutParams.getBehavior()).setEnabled(expanded);
        return this;
    }

    public ToolbarManager setImage(@DrawableRes int drawable) {
        return this.setImage(drawable, false);
    }

    public ToolbarManager setImage(@DrawableRes int drawable, boolean fade) {
        final ImageView image = (ImageView)holder.findViewById(R.id.toolbar_image);
        if (fade) {
            Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            image.startAnimation(fadeIn);
            /*fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                    image.startAnimation(fadeOut);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });*/
        }
        image.setImageResource(drawable);
        return this;
    }

    public ToolbarManager setTabs(boolean show) {
        tabs = (TabLayout) appBarLayout.findViewById(R.id.tablayout);
        if (show) {
            tabs.setVisibility(View.VISIBLE);
        } else {
            tabs.setVisibility(View.GONE);
        }
        return this;
        /*
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
        return this;*/
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

    public ToolbarManager showBottomScrim() {
        View scrim = collapsingToolbarLayout.findViewById(R.id.toolbar_scrim_bottom);
        scrim.setVisibility(View.VISIBLE);
        return this;
    }

    public ToolbarManager hideBottomScrim() {
        View scrim = collapsingToolbarLayout.findViewById(R.id.toolbar_scrim_bottom);
        scrim.setVisibility(View.GONE);
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

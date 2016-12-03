package de.mytfg.apps.vplan.toolbar;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;

public class ToolbarManager {
    private static MainActivity context;
    private static DrawerLayout drawerLayout;
    private static CoordinatorLayout holder;

    public static Toolbar toolbar;



    public static void init(MainActivity c, DrawerLayout drawer) {
        context = c;
        drawerLayout = drawer;
        holder = (CoordinatorLayout) context.findViewById(R.id.coordinator_layout);
    }

    public static CoordinatorLayout coordinatorLayout() {
        return holder;
    }

    public static void setToolbar(@LayoutRes int layout, String title) {
        setToolbar(layout, title, false);
    }

    public static void setToolbar(@LayoutRes int layout, String title, boolean translucent) {

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
        // holder.setFitsSystemWindows(translucent);

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
}

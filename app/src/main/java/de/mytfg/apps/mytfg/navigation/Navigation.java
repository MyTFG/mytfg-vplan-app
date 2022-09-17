package de.mytfg.apps.mytfg.navigation;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.fragments.AuthenticationFragment;
import de.mytfg.apps.mytfg.fragments.LoginFragment;
import de.mytfg.apps.mytfg.fragments.WebViewFragment;
import de.mytfg.apps.mytfg.objects.User;
import de.mytfg.apps.mytfg.tools.CopyDrawableImageTransform;
import de.mytfg.apps.mytfg.tools.Settings;
import de.mytfg.apps.mytfg.widgets.VplanWidget;

public class Navigation {
    private Context context;

    private boolean finishOnBack = false;
    private boolean nightmodeState = false;

    private NavigationView navigationView;
    private View navigationHeader;

    public Navigation(Context context) {
        this.context = context;
    }

    public void navigate(AuthenticationFragment fragment, int container, HashMap<String, Pair<String, View>> sharedElements) {
        navigate(fragment, container, sharedElements, false);
    }

    public void navigate(AuthenticationFragment fragment, int container, HashMap<String, Pair<String, View>> sharedElements, boolean add) {
        // Need to accept Terms of Use first
        /*if (!MainActivity.sharedPreferences.getBoolean("tou_accepted", false) && !(fragment instanceof AboutFragment)) {
            navigate(new AboutFragment(), container);
            return;
        }*/
        updateHeader();

        finishOnBack = false;

        MyTFGApi api = new MyTFGApi(context);
        api.clearOverrideLoading();
        if (fragment.needsAuthentication() && !api.isLoggedIn()) {
            navigate(new LoginFragment(), container);
            return;
        }

        this.hideKeyboard();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet transitionSet = new TransitionSet();

            CopyDrawableImageTransform copyDrawableImageTransform = new CopyDrawableImageTransform();
            ChangeTransform changeTransform = new ChangeTransform();
            ChangeBounds changeBounds = new ChangeBounds();

            changeBounds.setDuration(200);
            copyDrawableImageTransform.setDuration(200);


            transitionSet.addTransition(copyDrawableImageTransform);
            transitionSet.addTransition(changeBounds);
            transitionSet.addTransition(changeTransform);
            transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);

            fragment.setSharedElementEnterTransition(transitionSet);
            fragment.setSharedElementReturnTransition(transitionSet);
            /*if (sharedElements.size() > 0) {
                fragment.setEnterTransition(new Fade());
                fragment.setExitTransition(new Fade());
            }*/
        }

        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        //long transId = System.currentTimeMillis();


        FragmentTransaction ft = ((MainActivity)context).getSupportFragmentManager().beginTransaction();

        for (String transitionName : sharedElements.keySet()) {
            String unique = sharedElements.get(transitionName).first;

            ViewCompat.setTransitionName(sharedElements.get(transitionName).second, unique);

            ft.addSharedElement(sharedElements.get(transitionName).second, unique);

            args.putString(transitionName, unique);
        }

        if (!fragment.isAdded()) {
            fragment.setArguments(args);
        }

        if (add) {
            ft.add(container, fragment);
        } else {
            ft.replace(container, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();
        if (((MainActivity)context).getDrawerLayout() != null) {
            ((MainActivity) context).getDrawerLayout().closeDrawers();
        }
    }

    public void navigate(AuthenticationFragment fragment, int container) {
        this.navigate(fragment, container, new HashMap<String, Pair<String, View>>());
    }

    public void closeDrawer() {
        if (((MainActivity)context).getDrawerLayout() != null) {
            ((MainActivity) context).getDrawerLayout().closeDrawers();
        }
    }

    public void toWebView(String url, MainActivity context) {
        WebView webView = context.findViewById(R.id.webview);
        if (webView != null) {
            webView.loadUrl(url);
            this.closeDrawer();
        } else {
            AuthenticationFragment fragment = new WebViewFragment();
            Bundle args = new Bundle();
            args.putString("url", url);
            fragment.setArguments(args);
            navigate(fragment, R.id.fragment_container);
        }
    }

    public void onBackPressed() {
        WebView webView = ((MainActivity)context).findViewById(R.id.webview);

        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        }

        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            if (finishOnBack) {
                ((MainActivity) context).finish();
            } else {
                finishOnBack = true;
                snackbar(context.getString(R.string.next_close));
            }
        }
    }

    public void clear() {
        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void hideKeyboard() {
        View view = ((MainActivity)context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void snackbar(String text) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((MainActivity)context).findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout,
                        text,
                        Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }

    public void setNavigationHeader(View navigationHeader) {
        this.navigationHeader = navigationHeader;
    }

    public void updateHeader() {
        MyTFGApi api = new MyTFGApi(context);
        TextView usernameTV = navigationHeader.findViewById(R.id.navigation_user_name);
        TextView mailTV = navigationHeader.findViewById(R.id.navigation_user_mail);
        CircleImageView userImg = navigationHeader.findViewById(R.id.navigation_user_image);
        ImageView nightmodeToggler = navigationHeader.findViewById(R.id.toggleNightmode);
        ImageView background = navigationHeader.findViewById(R.id.imageView);

        final Settings settings = new Settings(context);

        nightmodeToggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean nm = settings.getBool("nightmode");
                settings.save("nightmode", !nm);
                VplanWidget.updateAllWidgets((MainActivity)context);
                ((MainActivity)context).recreate();
            }
        });

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toWebView(MyTFGApi.URL_AVATAR, (MainActivity) context);
            }
        });

        usernameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toWebView(MyTFGApi.URL_SETTINGS, (MainActivity) context);
            }
        });
        mailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toWebView(MyTFGApi.URL_SETTINGS, (MainActivity) context);
            }
        });

        if (settings.getBool("nightmode") != nightmodeState) {
            if (settings.getBool("nightmode")) {
                nightmodeToggler.setImageDrawable(context.getResources().getDrawable(R.drawable.disable_nightmode));
                background.setImageDrawable(context.getResources().getDrawable(R.drawable.nightmode_navi));
            } else {
                nightmodeToggler.setImageDrawable(context.getResources().getDrawable(R.drawable.enable_nightmode));
                background.setImageDrawable(context.getResources().getDrawable(R.drawable.school_tfg_navi));
            }
            nightmodeState = !nightmodeState;
        }

        if (mailTV == null) {
            return;
        }

        if (!api.isLoggedIn()) {
            userImg.setImageResource(R.drawable.person_white80);
            mailTV.setVisibility(View.GONE);
            usernameTV.setVisibility(View.GONE);
        } else {
            mailTV.setVisibility(View.VISIBLE);
            usernameTV.setVisibility(View.VISIBLE);
            mailTV.setText(api.getUser().getMail());
            usernameTV.setText(api.getUser().getFirstname() + " " + api.getUser().getLastname());

            if (api.getUser().getAvatar().equals("")) {
                userImg.setImageResource(R.drawable.person_white);
            } else {
                try {
                    Log.d("Avatar", api.getUser().getAvatar(true));
                    Picasso.with(context)
                            .load(api.getUser().getAvatar(true))
                            .error(R.drawable.person_white80)
                            .into(userImg);
                } catch (Exception ex) {
                    Log.d("Picasso", ex.toString());
                }
            }
        }
    }

    public void setMyTFGMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(context);
        MyTFGApi api = new MyTFGApi(context);
        User user = api.getUser();

        inflater.inflate(R.menu.submenu_mytfg, menu);
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            String permission = null;
            switch (item.getItemId()) {
                case R.id.submenu_mytfg_supportcenter_tickets:
                    permission = "supportcenter/visit";
                    break;
                case R.id.submenu_mytfg_supportcenter:
                    permission = "supportcenter/visit";
                    break;
                case R.id.submenu_mytfg_accounts:
                    permission = "account/manage";
                    break;
                case R.id.submenu_mytfg_accounts_create:
                    permission = "account/create";
                    break;
                case R.id.submenu_mytfg_accounts_search:
                    permission = "account/search";
                    break;
                case R.id.submenu_mytfg_purchases:
                    permission = "purchases";
                    break;
                case R.id.submenu_mytfg_settings:
                    permission = "loggedin";
                    break;
            }
            if (permission != null) {
                if (user.hasPermission(permission)) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }
            }
        }
    }
}

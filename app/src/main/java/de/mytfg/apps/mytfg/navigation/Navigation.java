package de.mytfg.apps.mytfg.navigation;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.fragments.AuthenticationFragment;
import de.mytfg.apps.mytfg.fragments.LoginFragment;
import de.mytfg.apps.mytfg.tools.CopyDrawableImageTransform;

public class Navigation {
    private Context context;

    private boolean finishOnBack = false;

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

        finishOnBack = false;

        MyTFGApi api = new MyTFGApi(context);
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
        long transId = System.currentTimeMillis();


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
        ((MainActivity)context).getDrawerLayout().closeDrawers();
    }

    public void navigate(AuthenticationFragment fragment, int container) {
        this.navigate(fragment, container, new HashMap<String, Pair<String, View>>());
    }

    public void onBackPressed() {
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
}

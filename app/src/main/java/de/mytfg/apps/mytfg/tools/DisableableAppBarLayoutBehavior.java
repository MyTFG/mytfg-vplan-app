package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Allows to enable and disable the collapsing toolbar layout.
 */

public class DisableableAppBarLayoutBehavior extends AppBarLayout.Behavior {
    private boolean mEnabled = false;

    public DisableableAppBarLayoutBehavior() {
        super();
    }

    public DisableableAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return mEnabled && super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    public boolean isEnabled() {
        return mEnabled;
    }
}

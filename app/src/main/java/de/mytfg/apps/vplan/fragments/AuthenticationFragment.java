package de.mytfg.apps.vplan.fragments;

import android.support.v4.app.Fragment;

/**
 * Adds a method for authentication check to the Fragment
 */

public abstract class AuthenticationFragment extends Fragment {
    public boolean needsAuthentication() {
        return false;
    };
}

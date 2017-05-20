package de.mytfg.apps.mytfg.fragments;

import android.support.v4.app.Fragment;

/**
 * Adds a method for authentication check to the Fragment
 */

public abstract class AuthenticationFragment extends Fragment {
    public boolean needsAuthentication() {
        return false;
    }
}

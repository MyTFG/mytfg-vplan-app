package de.mytfg.apps.mytfg.fragments;

import androidx.fragment.app.Fragment;

/**
 * Adds a method for authentication check to the Fragment
 */

public abstract class AuthenticationFragment extends Fragment {
    public boolean needsAuthentication() {
        return false;
    }
}

package de.mytfg.apps.mytfg.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface FragmentHolderLogic {
    void init(Context context, View view, Bundle args);

    void saveInstanceState();

    void restoreInstanceState();

    String getTabTitle();
}

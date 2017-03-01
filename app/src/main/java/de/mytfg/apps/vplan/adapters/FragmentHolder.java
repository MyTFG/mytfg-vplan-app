package de.mytfg.apps.vplan.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.fragments.FragmentHolderLogic;

public class FragmentHolder extends Fragment {
    int resId;
    private FragmentHolderLogic logic;

    public static FragmentHolder newInstance(int layout) {
        return FragmentHolder.newInstance(layout, null);
    }


    public static FragmentHolder newInstance(int layout, FragmentHolderLogic logic) {
        return FragmentHolder.newInstance(layout, logic, new Bundle());
    }

    public static FragmentHolder newInstance(int layout, FragmentHolderLogic logic, Bundle args) {
        FragmentHolder fragmentHolder = new FragmentHolder();
        args.putInt("fraglayout", layout);
        fragmentHolder.setArguments(args);
        fragmentHolder.logic = logic;
        return fragmentHolder;
    }

    public FragmentHolder() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        resId = getArguments().getInt("fraglayout");
        View view = inflater.inflate(resId, container, false);;
        Log.d("FH", "onCreateView");

        if (logic != null) {
            Log.d("FH", "Logic exists");
            logic.init(getContext(), view, getArguments());
        }
        return view;
    }

    public FragmentHolderLogic getLogic() {
        return logic;
    }
}

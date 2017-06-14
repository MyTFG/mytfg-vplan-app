package de.mytfg.apps.mytfg.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.mytfg.fragments.FragmentHolderLogic;
import de.mytfg.apps.mytfg.logic.AbbreviationLogic;
import de.mytfg.apps.mytfg.logic.ExamLogic;
import de.mytfg.apps.mytfg.logic.LogicFactory;
import de.mytfg.apps.mytfg.logic.PlanLogic;

public class FragmentHolder extends Fragment {
    int resId;
    private FragmentHolderLogic logic;
    private View view;

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
        Log.d("FRAG", "newInstance");
        return fragmentHolder;
    }

    public FragmentHolder() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        resId = getArguments().getInt("fraglayout");
        view = inflater.inflate(resId, container, false);
        Log.d("FRAG", "onCreateView");
        Log.d("FRAG", "logic " + logic);
        Log.d("FRAG", "resid " + resId);
        Log.d("FRAG", "this " + this);

        if (logic == null) {
            if (savedInstanceState != null && savedInstanceState.containsKey("logic")) {
                String logicType = savedInstanceState.getString("logic");
                String param = "";
                if (savedInstanceState.containsKey("logicparam")) {
                    param = savedInstanceState.getString("logicparam");
                }
                logic = LogicFactory.createLogic(logicType, getContext(), param);
            }
        }

        Log.d("FRAG", "logic2 " + logic);

        if (logic != null) {
            logic.init(getContext(), view, getArguments());
        }
        return view;
    }

    public FragmentHolderLogic getLogic() {
        return logic;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("logic", logic.getClass().getSimpleName());
        Log.d("FRAG", "type: " + logic.getClass().getSimpleName());
        if (logic instanceof PlanLogic) {
            outState.putString("logicparam", ((PlanLogic)logic).getPlan().getDay());
        } else if (logic instanceof AbbreviationLogic) {
            outState.putString("logicparam", ((AbbreviationLogic)logic).getAbbreviations().getType());
        } else if (logic instanceof ExamLogic) {
            outState.putString("logicparam", ((ExamLogic)logic).getCls());
        }
        //logic.saveInstanceState();
    }

    public void init() {
        logic.init(getContext(), view, getArguments());
    }


}

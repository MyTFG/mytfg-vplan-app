package  de.mytfg.apps.vplan.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import  de.mytfg.apps.vplan.R;
import  de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.api.SuccessCallback;
import de.mytfg.apps.vplan.objects.Vplan;
import  de.mytfg.apps.vplan.toolbar.ToolbarManager;

public class AboutFragment extends Fragment {
    private View view;


    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);
        MainActivity context = (MainActivity)this.getActivity();
        context.getToolbarManager()
                .setTitle(getString(R.string.menutitle_about))
                .setExpandable(true, true)
                .setTabs(false);

        /*Vplan plan = new Vplan(getContext(), "today");
        plan.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {

            }
        });*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
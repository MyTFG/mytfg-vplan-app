package de.mytfg.apps.vplan.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.api.MyTFGApi;
import de.mytfg.apps.vplan.api.SuccessCallback;
import de.mytfg.apps.vplan.objects.User;
import de.mytfg.apps.vplan.objects.Vplan;
import de.mytfg.apps.vplan.toolbar.ToolbarManager;

public class StartFragment extends Fragment {
    private View view;


    public StartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_home))
                .setExpandable(true, true);

        MyTFGApi api = new MyTFGApi(context);
        if (api.isLoggedIn()) {
            User user = api.getUser();
            TextView infotext = (TextView) view.findViewById(R.id.home_login_status);
            String text = String.format(
                    getString(R.string.home_welcome_text),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getLevel(),
                    user.getUsername(),
                    user.getGrade(),
                    api.getExpireString()
            );
            infotext.setText(Html.fromHtml(text));
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
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

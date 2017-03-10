package  de.mytfg.apps.mytfg.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import  de.mytfg.apps.mytfg.R;
import  de.mytfg.apps.mytfg.activities.MainActivity;

public class AboutFragment extends AuthenticationFragment {
    private final String githubUrl = "https://github.com/MyTFG/mytfg-vplan-app";
    private final String mytfgUrl = "https://mytfg.de";


    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_about))
                .setExpandable(true, true);

        ImageView github = (ImageView) view.findViewById(R.id.github);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(githubUrl));
                startActivity(i);
            }
        });



        ImageView mytfg = (ImageView) view.findViewById(R.id.mytfg);
        mytfg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mytfgUrl));
                startActivity(i);
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}

package de.mytfg.apps.mytfg.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;

public class LinksFragment extends AuthenticationFragment {
    private final String mytfgUrl = "https://mytfg.de";
    private final String helpUrl = "https://help.mytfg.de";
    private final String moodleUrl = "https://tfgym-duesseldorf.lms.schulon.org/";
    private final String tfgUrl = "http://tfg-duesseldorf.de";


    public LinksFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String examUrl = getString(R.string.link_examplan_url);

        View view = inflater.inflate(R.layout.fragment_links, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.links_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_links))
                .setExpandable(true, true);


        CardView mytfg = (CardView) view.findViewById(R.id.link_mytfg);
        mytfg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mytfgUrl));
                startActivity(i);
            }
        });


        CardView mytfgHelp = (CardView) view.findViewById(R.id.link_mytfg_help);
        mytfgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(helpUrl));
                startActivity(i);
            }
        });

        final CardView moodle = (CardView) view.findViewById(R.id.link_moodle);
        moodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(moodleUrl));
                startActivity(i);
            }
        });

        CardView tfg = (CardView) view.findViewById(R.id.link_tfg_web);
        tfg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tfgUrl));
                startActivity(i);
            }
        });

        final CardView exam = (CardView) view.findViewById(R.id.link_tfg_exams);
        exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(examUrl));
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

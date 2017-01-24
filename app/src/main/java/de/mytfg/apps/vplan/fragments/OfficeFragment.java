package de.mytfg.apps.vplan.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.net.URLEncoder;
import java.util.ArrayList;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;

public class OfficeFragment extends AuthenticationFragment {
    private final String addressString = "Kalkumer Schlossallee 28 40489 Düsseldorf";


    public OfficeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_office, container, false);
        MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_secretary))
                .setExpandable(true, true);

        CardView address = (CardView) view.findViewById(R.id.office_address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:0,0?q=%s",
                                URLEncoder.encode(addressString))));
                startActivity(i);
            }
        });

        final String num = getString(R.string.office_phone_val).replace("/", "");

        CardView phone = (CardView) view.findViewById(R.id.office_phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + num));
                startActivity(i);
            }
        });

        CardView mail = (CardView) view.findViewById(R.id.office_mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:" + getString(R.string.office_mail_val)));
                startActivity(i);
            }
        });

        /*CardView mytfg = (CardView) view.findViewById(R.id.link_mytfg);
        mytfg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mytfgUrl));
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
        */
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}

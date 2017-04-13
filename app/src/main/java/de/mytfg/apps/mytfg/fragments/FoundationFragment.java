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

import java.net.URLEncoder;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;

public class FoundationFragment extends AuthenticationFragment {
    //private final String addressString = "Kalkumer Schlossallee 28 40489 Düsseldorf";


    public FoundationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foundation, container, false);
        MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.foundation_header)
                .setCornerImage(R.drawable.schulstiftung_logo)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_foundation))
                .setExpandable(true, true);

        final String addressString = context.getString(R.string.foundation_address_val);
        CardView address = (CardView) view.findViewById(R.id.foundation_address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:0,0?q=%s",
                                URLEncoder.encode(addressString))));
                startActivity(i);
            }
        });

        final String url = "http://" + getString(R.string.foundation_web_val);
        final String donateurl = getString(R.string.foundation_donation_url);

        CardView donate = (CardView) view.findViewById(R.id.foundation_donation);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(donateurl));
                startActivity(i);
            }
        });

        CardView web = (CardView) view.findViewById(R.id.parents_web);
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        CardView mail = (CardView) view.findViewById(R.id.foundation_mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:" + getString(R.string.foundation_mail_val)));
                startActivity(i);
            }
        });

        CardView phone = (CardView) view.findViewById(R.id.foundation_phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = getString(R.string.foundation_phone_val);
                Intent i = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + num));
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

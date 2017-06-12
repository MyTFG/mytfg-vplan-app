package de.mytfg.apps.mytfg.fragments;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Paint;
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
import de.mytfg.apps.mytfg.tools.ViewFlipperIndicator;

public class BoosterFragment extends AuthenticationFragment {
    private final String addressString = "Kalkumer Schlossallee 28 40489 Düsseldorf";


    public BoosterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boosters, container, false);

        ViewFlipperIndicator flipper = (ViewFlipperIndicator) view.findViewById(R.id.boosters_flipper);
        flipper.setInAnimation(getContext(), R.anim.slide_in_right);
        flipper.setOutAnimation(getContext(), R.anim.slide_out_left);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.accent));
        flipper.setPaintCurrent(paint);
        paint = new Paint();
        paint.setColor((getResources().getColor(R.color.colorIconsDark)));
        flipper.setRadius(15);
        flipper.setMargin(15);
        flipper.setPaintNormal(paint);
        flipper.setAutoStart(true);
        flipper.setFlipInterval(4000);
        flipper.startFlipping();

        MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.booster_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_boosters))
                .setExpandable(true, true);

        CardView address = (CardView) view.findViewById(R.id.boosters_address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:0,0?q=%s",
                                URLEncoder.encode(addressString))));
                startActivity(i);
            }
        });

        final String url = "http://" + getString(R.string.boosters_web_val);

        CardView web = (CardView) view.findViewById(R.id.parents_web);
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        CardView mail = (CardView) view.findViewById(R.id.boosters_mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:" + getString(R.string.boosters_mail_val)));
                startActivity(i);
            }
        });


        CardView pdf = (CardView) view.findViewById(R.id.boosters_pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.boosters_dl_url)));
                //i.setType("application/pdf");
                //Intent choose = Intent.createChooser(i, getString(R.string.chooser_title));
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

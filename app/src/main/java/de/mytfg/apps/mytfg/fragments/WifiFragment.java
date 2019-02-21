package de.mytfg.apps.mytfg.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skyfishjy.library.RippleBackground;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;

public class WifiFragment extends AuthenticationFragment {
    private final String SSID = "MyTFG";

    private RippleBackground rippleBackground;
    private FloatingActionButton fab;

    public WifiFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.wifi_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_wifi))
                .showFab(R.drawable.ic_wifi_black_24dp)
                .setExpandable(false, false);


        rippleBackground = view.findViewById(R.id.wifi_loader);
        fab = context.findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });

        return view;
    }

    private void startScan() {
        rippleBackground.startRippleAnimation();
        fab.hide();
    }

    private void scanDone() {
        rippleBackground.stopRippleAnimation();
        fab.show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}

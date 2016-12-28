package de.mytfg.apps.vplan.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.api.MyTFGApi;

public class AccountFragment extends Fragment {
    private View view;


    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        context.getToolbarManager()
                .setTitle(getString(R.string.menutitle_account))
                .setExpandable(true, true)
                .setTabs(false);

        MyTFGApi api = new MyTFGApi(context);
        String username = api.getUsername();
        String device = api.getStoredDevice();
        long expirets = api.getExpire();
        Timestamp stamp = new Timestamp(expirets * 1000);
        Date date = new Date(stamp.getTime());
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String expire = df.format(date);

        TextView title = (TextView) view.findViewById(R.id.account_name);
        TextView tv_username = (TextView) view.findViewById(R.id.account_info_username);
        TextView tv_device = (TextView) view.findViewById(R.id.account_info_device);
        TextView tv_expire = (TextView) view.findViewById(R.id.account_info_expire);
        title.setText(username);
        tv_username.setText(String.format(getString(R.string.account_info_username), username));
        tv_device.setText(String.format(getString(R.string.account_info_device), device));
        tv_expire.setText(String.format(getString(R.string.account_info_expire), expire));

        Button logoutbtn = (Button) view.findViewById(R.id.logout_button);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTFGApi api = new MyTFGApi(context);
                api.logout(false);

                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) context.findViewById(R.id.coordinator_layout);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout,
                                getString(R.string.account_logged_out),
                                Snackbar.LENGTH_LONG);
                snackbar.show();
                context.getNavi().clear();
                context.getNavi().navigate(new LoginFragment(), R.id.fragment_container);
            }
        });

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

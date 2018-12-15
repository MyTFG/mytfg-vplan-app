package de.mytfg.apps.mytfg.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;

public class SupportcenterFragment extends AuthenticationFragment {
    private final String catchUrl = "https://mytfg.de/supportcenter/";
    private final String ticketsUrl = "https://mytfg.de/supportcenter/tickets.x";

    private WebView webView;

    public SupportcenterFragment() {
    }

    @Override
    public boolean needsAuthentication() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_mytfg_supportcenter))
                .setExpandable(false, true);

        webView = view.findViewById(R.id.webview);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();

        MyTFGApi api = new MyTFGApi(getContext());

        cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_username=" + api.getUsername() + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_device=" + api.getDevice() + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_token=" + api.getToken() + ";path=/");


        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(ticketsUrl);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


}

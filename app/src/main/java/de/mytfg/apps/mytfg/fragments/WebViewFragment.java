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

public class WebViewFragment extends AuthenticationFragment {
    private final String defaultUrl = "https://mytfg.de/";
    private final String catchUrl = "https://mytfg.de/supportcenter/";
    private final String ticketsUrl = "https://mytfg.de/supportcenter/tickets.x";

    private WebView webView;
    private String url;

    public WebViewFragment() {
    }

    @Override
    public boolean needsAuthentication() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        Bundle args = this.getArguments();
        url = args.getString("url", defaultUrl);

        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_mytfg))
                .setExpandable(false, true);



        MyTFGApi api = new MyTFGApi(getContext());
        if (!api.isLoggedIn()) {
            context.getNavi().navigate(new LoginFragment(), R.id.fragment_container);
            return view;
        }

        webView = view.findViewById(R.id.webview);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_username=" + api.getUsername() + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_device=" + api.getDevice() + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_token=" + api.getToken() + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_app=true" + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_app_os=android" + ";path=/");


        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


}

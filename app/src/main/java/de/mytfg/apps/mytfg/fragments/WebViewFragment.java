package de.mytfg.apps.mytfg.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import de.mytfg.apps.mytfg.adapters.MyTFGWebAppInterface;
import de.mytfg.apps.mytfg.adapters.MyTFGWebView;
import de.mytfg.apps.mytfg.api.MyTFGApi;

public class WebViewFragment extends AuthenticationFragment {
    private final String defaultUrl = "https://mytfg.de/";

    private WebView webView;
    private String url;
    private MainActivity context;

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

        context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_mytfg))
                .setExpandable(false, true);



        MyTFGApi api = new MyTFGApi(getContext());

        webView = view.findViewById(R.id.webview);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.webview_refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();

        if (api.isLoggedIn()) {
            cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_username=" + api.getUsername() + ";path=/");
            cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_device=" + api.getDevice() + ";path=/");
            cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_token=" + api.getToken() + ";path=/");
        }

        cookieManager.setCookie("https://mytfg.de", "mytfg_app=true" + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_app_os=android" + ";path=/");


        webView.setWebViewClient(new MyTFGWebView(context, swipeRefreshLayout));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyTFGWebAppInterface(context), "MyTFGAppAndroid");

        webView.loadUrl(url);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.webview_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications:
                context.getNavi().toWebView(MyTFGApi.URL_NOTIFICATIONS, context);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

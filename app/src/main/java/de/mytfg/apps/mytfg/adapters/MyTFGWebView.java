package de.mytfg.apps.mytfg.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;

public class MyTFGWebView extends WebViewClient {
    private MyTFGApi api;
    private MainActivity context;

    public MyTFGWebView(MainActivity context) {
        api = new MyTFGApi(context);
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri parsed = Uri.parse(url);

        if (url.startsWith("mailto:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, parsed);
            context.startActivity(intent);
            return true;
        } else if (!url.contains("https://mytfg.de")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, parsed);
            context.startActivity(intent);
            return true;
        } else {
            view.loadUrl(url);
            return false;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        api.stopLoading();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        api.startLoading();
    }
}

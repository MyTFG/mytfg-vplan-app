package de.mytfg.apps.mytfg.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.fragments.AuthenticationFragment;
import de.mytfg.apps.mytfg.fragments.ExamFragment;
import de.mytfg.apps.mytfg.fragments.LoginFragment;
import de.mytfg.apps.mytfg.fragments.LostAndFoundFragment;
import de.mytfg.apps.mytfg.fragments.PlanFragment;
import de.mytfg.apps.mytfg.fragments.TfgFragment;
import de.mytfg.apps.mytfg.fragments.VrrFragment;

public class MyTFGWebView extends WebViewClient {
    private MyTFGApi api;
    private MainActivity context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MyTFGWebView(MainActivity context, SwipeRefreshLayout swipeRefreshLayout) {
        api = new MyTFGApi(context);
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public static boolean catchAppUrls(String url, MainActivity context) {
        Log.d("URL", url);

        switch (url) {
            case "https://mytfg.de/login.x":
                context.getNavi().navigate(new LoginFragment(), R.id.fragment_container);
                break;
            case "https://mytfg.de/lostandfound.x":
                context.getNavi().navigate(new LostAndFoundFragment(), R.id.fragment_container);
                break;
            case "https://mytfg.de/tfg/vrr.x":
                context.getNavi().navigate(new VrrFragment(), R.id.fragment_container);
                break;
            case "https://mytfg.de/exams.x":
                context.getNavi().navigate(new ExamFragment(), R.id.fragment_container);
                break;
            case "https://mytfg.de/tfg/news.x":
                context.getNavi().navigate(new TfgFragment(), R.id.fragment_container);
                break;
            case "https://mytfg.de/tfg/events.x":
                AuthenticationFragment fragment = new TfgFragment();
                Bundle args = new Bundle();
                args.putInt("tfgTab", 1);
                fragment.setArguments(args);
                context.getNavi().navigate(fragment, R.id.fragment_container);
                break;
            case "https://mytfg.de/vplan.x":
                context.getNavi().navigate(new PlanFragment(), R.id.fragment_container);
                break;
            default:
                return false;
        }

        return true;
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
            if (MyTFGWebView.catchAppUrls(url, context)) {
                return true;
            }
            view.loadUrl(url);
            return false;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        api.stopLoading();
        swipeRefreshLayout.setRefreshing(false);
    }



    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        api.startLoading();
        swipeRefreshLayout.setRefreshing(true);
    }
}

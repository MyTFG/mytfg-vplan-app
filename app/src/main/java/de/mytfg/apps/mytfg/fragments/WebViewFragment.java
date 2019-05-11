package de.mytfg.apps.mytfg.fragments;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.adapters.MyTFGWebAppInterface;
import de.mytfg.apps.mytfg.adapters.MyTFGWebView;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.tools.Settings;

import static android.app.Activity.RESULT_OK;

public class WebViewFragment extends AuthenticationFragment {
    private final String defaultUrl = "https://mytfg.de/";

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageArr;

    private final static int FILECHOOSER_RESULTCODE = 1;
    private final static int REQUEST_SELECT_FILE = 2;

    private WebView webView;
    private String url;
    private MainActivity context;

    public WebViewFragment() {
    }

    @Override
    public boolean needsAuthentication() {
        return false;
    }

    @SuppressLint("SetJavaScriptEnabled")
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

        Settings settings = new Settings(context);
        String nightmode = settings.getBool("nightmode") ? "true" : "false";

        if (api.isLoggedIn()) {
            cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_username=" + api.getUsername() + ";path=/");
            cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_device=" + api.getDevice() + ";path=/");
            cookieManager.setCookie("https://mytfg.de", "mytfg_api_login_token=" + api.getToken() + ";path=/");
        }

        cookieManager.setCookie("https://mytfg.de", "mytfg_app=true" + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "mytfg_app_os=android" + ";path=/");
        cookieManager.setCookie("https://mytfg.de", "nightmode=" + nightmode + ";path=/");


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.addJavascriptInterface(new MyTFGWebAppInterface(context), "MyTFGAppAndroid");

        webView.setWebViewClient(new MyTFGWebView(context, swipeRefreshLayout));

        webView.setWebChromeClient(new WebChromeClient()
        {
            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                Log.d("FILE", "open 1");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                getActivity().startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
                Log.d("FILE", "open 2");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                getActivity().startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                Log.d("FILE", "open 3");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                getActivity().startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE);

            }

            // For Android 5.0+
            @SuppressLint("NewApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUploadMessageArr != null) {
                    mUploadMessageArr.onReceiveValue(null);
                    mUploadMessageArr = null;
                }

                mUploadMessageArr = filePathCallback;

                Intent intent = fileChooserParams.createIntent();

                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    mUploadMessageArr = null;
                    Log.e("FILE", e.getMessage());
                    return false;
                }

                return true;
            }
        });

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



    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            Log.d("FILE", "Filechooser result " + resultCode);

            if (null == mUploadMessage) return;

            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == REQUEST_SELECT_FILE) {
            Log.d("FILE", "Filechooser result (new) " + resultCode);
            if (mUploadMessageArr == null) return;
            mUploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            mUploadMessageArr = null;
        }
    }
}

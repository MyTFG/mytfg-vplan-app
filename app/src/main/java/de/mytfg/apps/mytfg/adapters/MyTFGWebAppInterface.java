package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;

public class MyTFGWebAppInterface {
    private static int lastNotificationCount = 0;

    MainActivity mContext;

    public MyTFGWebAppInterface(MainActivity c) {
        mContext = c;
    }

    @JavascriptInterface
    public void unseenNotifications(final boolean bool, final int count) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mContext.getMenu() == null || mContext.getMenu().size() == 0) {
                    return;
                }
                MenuItem item = mContext.getMenu().getItem(0);
                if (item != null) {
                    if (bool) {
                        item.setIcon(R.drawable.ic_notifications_unseen);
                        if (count > lastNotificationCount) {
                            if (count > 1) {
                                mContext.getNavi().snackbar(mContext.getResources().getString(R.string.new_notifications, count + ""));
                            } else {
                                mContext.getNavi().snackbar(mContext.getResources().getString(R.string.new_notification));
                            }
                        }
                    } else {
                        item.setIcon(R.drawable.ic_notifications_none);
                    }

                    lastNotificationCount = count;
                }
            }
        });
    }

    @JavascriptInterface
    public void urlOpened(final String url) {
        Log.d("URL", url);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyTFGWebView.catchAppUrls(url, mContext);
            }
        });
    }

}

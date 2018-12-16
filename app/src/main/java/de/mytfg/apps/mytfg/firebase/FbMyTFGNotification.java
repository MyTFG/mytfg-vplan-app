package de.mytfg.apps.mytfg.firebase;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

import de.mytfg.apps.mytfg.tools.Settings;

/**
 * Handles Firebase messages related to the VPlan
 */

class FbMyTFGNotification {
    private Context context;
    public FbMyTFGNotification(Context c) {
        this.context = c;
    }

    void handle(Map<String, String> data) {
        String text = data.get("text");
        String title = data.get("title");
        String url = data.get("url");

        Settings settings = new Settings(context);
        int nextId;
        if (settings.getBool("group-mytfg-notifications", false)) {
            nextId = url.hashCode();
        } else {
            nextId = data.get("timestamp").hashCode();
        }


        Bundle extras = new Bundle();
        extras.putString("title", title);
        extras.putString("text", text);
        extras.putString("url", url);
        FbNotify.notifyMyTFGNotification(context, title, text, url, nextId, extras);
    }
}

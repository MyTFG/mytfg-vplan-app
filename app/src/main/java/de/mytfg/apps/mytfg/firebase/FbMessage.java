package de.mytfg.apps.mytfg.firebase;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.User;
import de.mytfg.apps.mytfg.tools.TimeUtils;

/**
 * Handles Firebase messages related to the VPlan
 */

class FbMessage {
    private Context context;
    public FbMessage(Context c) {
        this.context = c;
    }

    void handle(Map<String, String> data) {
        String text = data.get("text");
        String title = data.get("title");

        int nextId = data.get("timestamp").hashCode();

        Bundle extras = new Bundle();
        extras.putString("title", title);
        extras.putString("text", text);
        extras.putString("type", "message");
        FbNotify.notifyMessage(context, title, text, nextId, extras);
        FbMessagingService.logNotification(context, title, text, TimeUtils.now(), extras);
    }
}

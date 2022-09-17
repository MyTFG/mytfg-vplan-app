package de.mytfg.apps.mytfg.firebase;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.JsonFileManager;
import de.mytfg.apps.mytfg.tools.Settings;
import de.mytfg.apps.mytfg.tools.TimeUtils;

public class FbMessagingService extends FirebaseMessagingService {
    private static int MaxNotificationLogSize = 100;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String TAG = "FBASE";
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // Notify or update view
            Map<String, String> data = remoteMessage.getData();
            String type = data.get("type");
            if (type != null) {
                switch (type) {
                    default:
                        break;
                    case "vplan_change":
                        FbVplan fbVplan = new FbVplan(this);
                        fbVplan.handle(data);
                        break;
                    case "mytfg-notification":
                        FbMyTFGNotification fbMyTFGNotification = new FbMyTFGNotification(this);
                        fbMyTFGNotification.handle(data);
                        break;
                    case "message":
                        FbMessage fbMessage = new FbMessage(this);
                        fbMessage.handle(data);
                        break;
                    case "mytfg-ack":
                        // Delete Notifications
                        String ids = data.get("ids");
                        String urls = data.get("urls");
                        Log.d("Firebase ACK IDs", ids);
                        Log.d("Firebase ACK URL", urls);
                        break;
                }
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FB-MS", "onNewToken");
        Log.d("FB-MS", "New token: " + token);

        Settings settings = new Settings(this.getApplicationContext());
        settings.save("firebaseToken", token);
        settings.save("firebaseTokenUpdated", true);

        FbApi fbApi = new FbApi(getApplicationContext());
        fbApi.sendFbToken(token, new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                Log.d("FB-ID-Server", "" + success);
            }
        });
    }

    public static void logNotification(Context context, String title, String text) {
        logNotification(context, title, text, TimeUtils.now());
    }

    public static void logNotification(Context context, String title, String text, long timestamp) {
        logNotification(context, title, text, timestamp, new Bundle());
    }

    public static void logNotification(Context context, String title, String text, long timestamp, Bundle args) {
        JSONObject logObj = JsonFileManager.read("notifications.json", context);
        JSONArray log = logObj.optJSONArray("notifications");
        if (log == null) {
            log = new JSONArray();
        }

        JSONObject entry = new JSONObject();
        try {
            JSONObject bundle = new JSONObject();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Set<String> keys = args.keySet();
                for (String key : keys) {
                    try {
                        // json.put(key, bundle.get(key)); see edit below
                        bundle.put(key, JSONObject.wrap(args.get(key)));
                    } catch (JSONException e) {
                        //Handle exception here
                        Log.e("JSON", e.getMessage());
                    }
                }
            }

            entry.put("title", title);
            entry.put("text", text);
            entry.put("date", timestamp);
            entry.put("bundle", bundle);
            log.put(entry);
        } catch (JSONException ex) {
            Log.e("JSON", ex.getMessage());
        }

        JSONArray trimmedLog = FbMessagingService.trimLog(log);

        logObj = new JSONObject();
        try {
            logObj.put("notifications", trimmedLog);
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        JsonFileManager.write(logObj, "notifications.json", context);
    }

    private static JSONArray trimLog(JSONArray log) {
        if (log.length() > MaxNotificationLogSize) {
            JSONArray trimmed = new JSONArray();
            for (int i = log.length() - MaxNotificationLogSize; i < log.length(); ++i) {
                trimmed.put(log.opt(i));
            }
            return trimmed;
        }

        return log;
    }
}

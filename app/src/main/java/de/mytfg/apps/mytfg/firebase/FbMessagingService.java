package de.mytfg.apps.mytfg.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.Settings;

public class FbMessagingService extends FirebaseMessagingService {
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
}

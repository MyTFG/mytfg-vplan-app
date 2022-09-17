package de.mytfg.apps.mytfg.firebase;

import android.content.Context;
import android.widget.Switch;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.navigation.Navigation;
import de.mytfg.apps.mytfg.tools.Settings;

/**
 * Used to manage the Firebase Token and access the MyTFG API.
 */

public class FbApi {
    private Context context;

    public static void updateFirebase(final Context context) {
        MyTFGApi api = new MyTFGApi(context);
        FbApi fbApi = new FbApi(context);
        FirebaseMessaging.getInstance().subscribeToTopic("tfg_news");
        FirebaseMessaging.getInstance().subscribeToTopic("tfg_events");
        // Manual messages send from MyTFG
        FirebaseMessaging.getInstance().subscribeToTopic("mytfg_app_messages");

        if (!api.isLoggedIn()) {
            return;
        }

        FirebaseMessaging.getInstance().subscribeToTopic("vplan_general");
        FirebaseMessaging.getInstance().subscribeToTopic("vplan_" + api.getUser().getGrade());
        for (String cls : api.getAdditionalClasses()) {
            FirebaseMessaging.getInstance().subscribeToTopic("vplan_" + cls);
        }

        if (fbApi.tokenNeedsRefresh()) {
            Settings settings = new Settings(context);
            String token = settings.getString("firebaseToken");
            fbApi.sendFbToken(token, new SuccessCallback() {
                @Override
                public void callback(boolean success) {
                    if (!success) {
                        Navigation navi = new Navigation(context);
                        navi.snackbar(context.getString(R.string.firebase_failed));
                    }
                }
            });
        }
    }

    public static void unsubscribeAll(Context context) {
        Settings settings = new Settings(context);
        settings.save("firebaseTokenUpdated", false);
        FirebaseMessaging.getInstance().deleteToken();
    }

    public FbApi(Context context) {
        this.context = context;
    }

    public boolean tokenNeedsRefresh() {
        Settings settings = new Settings(context);
        return settings.getBool("firebaseTokenUpdated");
    }

    public void sendFbToken(String token, final SuccessCallback callback) {
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        params.addParam("push_token", token);
        params.addParam("service", "firebase");
        api.addAuth(params);
        api.call("api_firebase_token", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    callback.callback(true);
                } else {
                    callback.callback(false);
                }
            }
        });
    }

    public void updateSubscription(String topic, String action, final SuccessCallback callback) {
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        params.addParam("topic", topic);
        api.addAuth(params);
        api.call("api/firebase/" + action, params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    callback.callback(true);
                } else {
                    callback.callback(false);
                }
            }
        });
    }

    public void isSubscribed(String topic, final Switch uiSwitch, final SuccessCallback callback) {
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        params.addParam("topic", topic);
        api.addAuth(params);
        api.call("api/firebase/isSubscribed", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                boolean subscribed = result.optJSONObject("abonnement").optBoolean("isSubscribed", false);

                if (uiSwitch != null) {
                    uiSwitch.setChecked(subscribed);
                }

                callback.callback(subscribed);
            } else {
                callback.callback(false);
            }
            }
        });
    }
}

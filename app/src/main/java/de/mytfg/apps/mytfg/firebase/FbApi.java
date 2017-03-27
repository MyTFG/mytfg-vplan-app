package de.mytfg.apps.mytfg.firebase;

import android.content.Context;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Used to manage the Firebase Token and access the MyTFG API.
 */

public class FbApi {
    private Context context;

    public FbApi(Context context) {
        this.context = context;
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
}

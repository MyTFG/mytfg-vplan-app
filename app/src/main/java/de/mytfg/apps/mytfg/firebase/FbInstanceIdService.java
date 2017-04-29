package de.mytfg.apps.mytfg.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.Settings;

public class FbInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        Log.d("FB-ID", "OnTokenRefresh");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FB-ID", "Refreshed token: " + refreshedToken);

        Settings settings = new Settings(this.getApplicationContext());
        settings.save("firebaseToken", refreshedToken);
        settings.save("firebaseTokenUpdated", true);

        FbApi fbApi = new FbApi(getApplicationContext());
        fbApi.sendFbToken(refreshedToken, new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                Log.d("FB-ID-Server", "" + success);
            }
        });
    }
}

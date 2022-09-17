package de.mytfg.apps.mytfg.objects;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SimpleCallback;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Used to handle Authentications
 */

public class Authentications extends MytfgObject {
    private Context context;

    private List<Authentication> active = new LinkedList<>();
    private List<Authentication> timedout = new LinkedList<>();
    private Authentication current = null;
    private boolean loaded = false;

    private List<SimpleCallback> onChangeListeners = new LinkedList<>();

    public Authentications(Context context) {
        this.context = context;
    }

    @Override
    public void load(final SuccessCallback callback) {
        loaded = false;
        // Otherwise request new data
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        api.addAuth(params);
        api.call("api_auth_list", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                if (responseCode == 200) {
                    loaded = true;
                    if (parse(result)) {
                        callback.callback(true);
                    } else {
                        callback.callback(false);
                    }
                } else {
                    callback.callback(false);
                }
            }
        });
    }

    private boolean parse(JSONObject result) {
        this.active = new LinkedList<>();
        this.timedout = new LinkedList<>();
        try {
            JSONArray entries = result.getJSONArray("active");
            for (int i = 0; i < entries.length(); ++i) {
                Authentication entry = new Authentication(this);
                entry.load(entries.getJSONObject(i));
                this.active.add(entry);
            }
            entries = result.getJSONArray("timedout");
            for (int i = 0; i < entries.length(); ++i) {
                Authentication entry = new Authentication(this);
                entry.load(entries.getJSONObject(i));
                this.timedout.add(entry);
            }
            if (result.optJSONObject("current") != null) {
                this.current = new Authentication(this);
                this.current.load(result.getJSONObject("current"));
            }
            loaded = true;
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public void remove(Authentication auth) {
        Log.d("AUTHS", "remove, Size: " + onChangeListeners.size());
        if (active.contains(auth)) {
            active.remove(auth);
            Log.d("AUTHS", "Deleted active");
            onChange();
        } else if (timedout.contains(auth)) {
            Log.d("AUTHS", "Deleted timedout");
            timedout.remove(auth);
            onChange();
        }
    }


    public List<Authentication> getActive() {
        return active;
    }

    public List<Authentication> getTimedout() {
        return timedout;
    }

    public Authentication getCurrent() {
        return current;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void addOnChangeListener(SimpleCallback callback) {
        this.onChangeListeners.add(callback);
    }

    private void onChange() {
        for (SimpleCallback callback : onChangeListeners) {
            if (callback != null) {
                callback.callback();
            }
        }
    }

    public Context getContext() {
        return context;
    }
}

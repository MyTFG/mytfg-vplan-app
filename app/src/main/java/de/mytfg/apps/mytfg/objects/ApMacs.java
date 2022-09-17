package de.mytfg.apps.mytfg.objects;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

/**
 * Manages VRR Entries returned by MyTFG
 */

public class ApMacs extends MytfgObject {
    private Context context;
    private List<ApMac> entries = new LinkedList<>();

    private int timeout = 5 * 60 * 1000;
    private long timestamp = 0;
    private boolean cacheAvail;
    private boolean loaded;
    private int lastCode;

    public ApMacs(Context context) {
        this.context = context;
    }

    @Override
    public void load(final SuccessCallback callback) {
        this.load(callback, false);
    }

    public void load(final SuccessCallback callback, boolean clearCache) {
        this.load(callback, clearCache, timeout);
    }

    public void load(final SuccessCallback callback, boolean clearCache, long outdate_millis) {
        // Try to load from cache
        cacheAvail = false;
        if (!clearCache && loadFromCache()) {
            if (upToDate(outdate_millis)) {
                callback.callback(true);
                return;
            }

        }
        loaded = false;
        // Otherwise request new data
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        api.addAuth(params);
        api.call("api/aps/getmacs", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                lastCode = responseCode;
                if (responseCode == 200) {
                    loaded = true;
                    if (parse(result)) {
                        saveToCache(result);
                        callback.callback(true);
                    } else {
                        callback.callback(false);
                    }
                } else {
                    if (responseCode == -1 && loadFromCache()) {
                        // No internet connection -> use cache even when cache timed out
                        callback.callback(true);
                    } else {
                        callback.callback(false);
                    }
                }
            }
        });
    }


    private boolean parse(JSONObject result) {
        this.entries = new LinkedList<>();
        try {
            JSONArray macs = result.getJSONArray("data");
            timestamp = result.optLong("api_time", 0);
            for (int i = 0; i < macs.length(); ++i) {
                ApMac entry = new ApMac();
                entry.load(macs.getJSONObject(i));
                entries.add(entry);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public List<ApMac> getEntries() {
        return entries;
    }

    public boolean upToDate() {
        return upToDate(timeout);
    }

    private boolean upToDate(long outdate) {
        return (timestamp + outdate) >= System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    private void saveToCache(JSONObject json) {
        try {
            json.put("api_time", System.currentTimeMillis());
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        JsonFileManager.write(json, "ap_macs", context);
    }

    public boolean loadFromCache() {
        JSONObject json = JsonFileManager.read("ap_macs", context);
        return parse(json);
    }

    public static void clearCache(Context context) {
        JsonFileManager.clear("ap_macs", context);
    }

    public ApMac matchAp(String mac) {
        for (ApMac e : this.getEntries()) {
            if (e.matchMac(mac)) {
                return e;
            }
        }
        return null;
    }
}

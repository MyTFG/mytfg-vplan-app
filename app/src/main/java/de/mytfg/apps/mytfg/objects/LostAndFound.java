package de.mytfg.apps.mytfg.objects;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Manages LostAndFound Entries returned by MyTFG
 */

public class LostAndFound extends MytfgObject {
    private Context context;
    private List<LostAndFoundEntry> entries = new LinkedList<>();

    public LostAndFound(Context context) {
        this.context = context;
    }

    public void load(SuccessCallback callback) {
        this.load(callback, false);
    }

    public void load(final SuccessCallback callback, boolean clearCache) {
        // Always request fresh data
        MyTFGApi api = new MyTFGApi(context);
        api.call("api/lostandfound/list.x", new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                Log.d("RESP", "" + responseCode);
                if (responseCode == 200) {
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
        this.entries = new LinkedList<>();
        try {
            JSONArray entries = result.getJSONArray("elements");
            for (int i = 0; i < entries.length(); ++i) {
                LostAndFoundEntry entry = new LostAndFoundEntry();
                entry.load(entries.getJSONObject(i));
                if (entry.isOpen()) {
                    this.entries.add(entry);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public List<LostAndFoundEntry> getEntries() {
        return entries;
    }
}

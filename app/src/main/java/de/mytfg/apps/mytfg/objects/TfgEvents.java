package de.mytfg.apps.mytfg.objects;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

/**
 * Represents a set of News from the TFG Webpage
 */
public class TfgEvents extends MytfgObject {
    private Context context;

    private List<TfgEventsEntry> entries = new LinkedList<>();
    private boolean loaded = false;
    private long timestamp;
    private final long timeout = 60 * 60 * 1000; // 60 minutes

    public TfgEvents(Context context) {
        this.context = context;
    }

    @Override
    public void load(SuccessCallback callback) {
        this.load(callback, false);
    }

    public void load(final SuccessCallback callback, boolean clearCache) {
        // Try to load from cache
        if (!clearCache && loadFromCache()) {
            if (upToDate()) {
                callback.callback(true);
                return;
            }
        }
        loaded = false;
        // Otherwise request new data
        MyTFGApi api = new MyTFGApi(context);
        api.call("api_tfg_events", new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
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
            JSONArray entries = result.getJSONArray("events");
            for (int i = 0; i < entries.length(); ++i) {
                TfgEventsEntry entry = new TfgEventsEntry();
                entry.load(entries.getJSONObject(i));
                this.entries.add(entry);
            }
            timestamp = result.optLong("api_time", System.currentTimeMillis());
            loaded = true;
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    private void saveToCache(JSONObject json) {
        try {
            json.put("api_time", System.currentTimeMillis());
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        JsonFileManager.write(json, "tfg_events", context);
    }

    private boolean loadFromCache() {
        JSONObject json = JsonFileManager.read("tfg_events", context);
        return parse(json);
    }

    public List<TfgEventsEntry> getEntries() {
        return entries;
    }

    public boolean upToDate() {
        return (timestamp + timeout) >= System.currentTimeMillis();
    }

    public List<TfgEventsEntry> filter(String filter) {
        if (filter == null) {
            return getEntries();
        }
        filter = filter.toLowerCase();

        List<TfgEventsEntry> results = new LinkedList<>();
        for (TfgEventsEntry entry : getEntries()) {
            if (entry.filter(filter)) {
                results.add(entry);
            }
        }
        return results;
    }
}

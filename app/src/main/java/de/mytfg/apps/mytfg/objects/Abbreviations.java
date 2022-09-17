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
 * API Wrapper for abbreviations.
 */

public class Abbreviations extends MytfgObject {
    private Context context;

    private List<Abbreviation> entries = new LinkedList<>();
    private boolean loaded = false;
    private long timestamp;
    private final long timeout = 7 * 24 * 60 * 60 * 1000; // 1 week
    private String type;

    public Abbreviations(Context context, String type) {
        this.context = context;
        this.type = type;
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
        api.call("api_abbreviation_" + type, new ApiCallback() {
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

    public List<Abbreviation> getEntries() {
        return entries;
    }

    private boolean parse(JSONObject result) {
        this.entries = new LinkedList<>();
        try {
            JSONArray entries = result.getJSONArray(type);
            for (int i = 0; i < entries.length(); ++i) {
                Abbreviation entry = new Abbreviation();
                entry.load(entries.getJSONObject(i), type);
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
        JsonFileManager.write(json, "abbr_"+type, context);
    }

    private boolean loadFromCache() {
        JSONObject json = JsonFileManager.read("abbr_" + type, context);
        return parse(json);
    }

    public String getType() {
        return type;
    }

    public boolean upToDate() {
        return (timestamp + timeout) >= System.currentTimeMillis();
    }

    public List<Abbreviation> filter(String filter) {
        if (filter == null) {
            return getEntries();
        }
        filter = filter.toLowerCase();

        List<Abbreviation> results = new LinkedList<>();
        for (Abbreviation entry : getEntries()) {
            if (entry.filter(filter)) {
                results.add(entry);
            }
        }
        return results;
    }
}

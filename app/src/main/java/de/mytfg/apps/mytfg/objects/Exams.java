package de.mytfg.apps.mytfg.objects;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SimpleCallback;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

/**
 * Manages the Exams returned from the Server.
 * Supports multiple concurrent loaders
 */

public class Exams extends MytfgObject {
    private Context context;

    private Map<String, List<ExamEntry>> entries = new HashMap<>();
    private String[] classes;
    private String title;
    private String timeText;
    private String timeSpan;


    private boolean loading;
    private List<SuccessCallback> callbacks = new LinkedList<>();

    private int lastCode;

    private boolean loaded = false;
    private long timestamp;
    private final long timeout = 60 * 60 * 1000; // 60 minutes

    public Exams(Context context) {
        this.context = context;
    }

    @Override
    public void load(SuccessCallback callback) {
        this.load(callback, false);
    }

    public void load(final SuccessCallback callback, boolean clearCache) {
        callbacks.add(callback);

        if (loading) {
            return;
        }

        loading = true;

        // Try to load from cache
        if (!clearCache && loadFromCache()) {
            if (upToDate()) {
                callCallbacks(true);
                return;
            }
        }
        loaded = false;
        // Otherwise request new data
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        api.call("api_exams_linear", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                lastCode = responseCode;
                if (responseCode == 200) {
                    loaded = true;
                    if (parse(result)) {
                        saveToCache(result);
                        callCallbacks(true);
                    } else {
                        callCallbacks(false);
                    }
                } else {
                    if (responseCode == -1 && loadFromCache()) {
                        // No internet connection -> use cache even when cache timed out
                        callCallbacks(true);
                    } else {
                        callCallbacks(false);
                    }
                }
            }
        });
    }

    private boolean parse(JSONObject result) {
        this.entries = new HashMap<>();
        try {
            JSONObject exams = result.getJSONObject("exams");
            JSONObject entries = exams.getJSONObject("entries");
            JSONArray header = exams.getJSONArray("header");
            title = exams.getString("title");
            timeText = exams.getString("time_text");
            timeSpan = exams.getString("time_span");
            classes = new String[header.length() - 1];
            for (int i = 1; i < header.length(); ++i) {
                classes[i-1] = header.getString(i);
            }

            for (String cls : classes) {
                JSONArray clsEntries = entries.getJSONArray(cls);
                List<ExamEntry> list = new LinkedList<>();
                for (int i = 0; i < clsEntries.length(); ++i) {
                    ExamEntry entry = new ExamEntry();
                    if (entry.load(clsEntries.getJSONObject(i))) {
                        list.add(entry);
                    }
                }
                this.entries.put(cls, list);
            }
            timestamp = result.optLong("api_time", System.currentTimeMillis());
            loaded = true;
        } catch (JSONException ex) {
            ex.printStackTrace();
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
        JsonFileManager.write(json, "exams", context);
    }

    private boolean loadFromCache() {
        JSONObject json = JsonFileManager.read("exams", context);
        return parse(json);
    }

    public Map<String, List<ExamEntry>> filter(String filter) {
        if (filter == null) {
            return getEntries();
        }
        filter = filter.toLowerCase();

        Map<String, List<ExamEntry>> filtered = new HashMap<>();

        for (String key : entries.keySet()) {
            List<ExamEntry> clsEntries = entries.get(key);
            List<ExamEntry> filteredEntries = new LinkedList<>();
            for (ExamEntry entry : clsEntries) {
                if (entry.filter(filter)) {
                    filteredEntries.add(entry);
                }
            }
            filtered.put(key, filteredEntries);
        }

        return filtered;
    }

    private void callCallbacks(boolean success) {
        loading = false;
        for (SuccessCallback callback : callbacks) {
            callback.callback(success);
        }
        callbacks.clear();
    }

    public Map<String, List<ExamEntry>> getEntries() {
        return entries;
    }

    public String[] getClasses() {
        return classes;
    }

    public String getTitle() {
        return title;
    }

    public String getTimeText() {
        return timeText;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean upToDate() {
        return (timestamp + timeout) >= System.currentTimeMillis();
    }

    public int getLastCode() {
        return lastCode;
    }

    public Context getContext() {
        return context;
    }
}

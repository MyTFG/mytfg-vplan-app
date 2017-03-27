package de.mytfg.apps.mytfg.objects;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

/**
 * Represents the Vplan for one day
 * Contains multiple entries for different classes and lessons.
 */

public class Vplan extends MytfgObject {
    private Context context;

    private String day;
    private String date;
    private List<VplanEntry> entries = new LinkedList<>();
    private String mode;
    private String cls_string;
    private String day_str;
    private String changed;
    private List<String> marquee = new LinkedList<>();
    private List<Pair<String, String>> absent_teachers = new LinkedList<>();
    private List<String> absent_strings = new LinkedList<>();
    private long timestamp = 0;
    private boolean cacheAvail = false;

    private int lastCode;
    private boolean loaded;
    // 3 Minutes
    private final long timeout = 3 * 60 * 1000;

    /**
     * Instanciates a new Vplan
     * @param day The day to use when using the <code>load</code>-method.
     *            (<code>today</code> or <code>tomorrow</code>)
     */
    public Vplan(Context context, String day) {
        this.day = day;
        this.context = context;
        if ("today".equals(day)) {
            this.day_str = context.getString(R.string.plan_today);
        } else {
            this.day_str = context.getString(R.string.plan_tomorrow);
        }
    }

    @Override
    public void load(final SuccessCallback callback) {
        this.load(callback, false);
    }

    public void load(final SuccessCallback callback, boolean clearCache) {
        // Try to load from cache
        cacheAvail = false;
        if (!clearCache && loadFromCache()) {
            if (upToDate()) {
                callback.callback(true);
                return;
            }

        }
        loaded = false;
        // Otherwise request new data
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        params.addParam("day", this.day);
        params.addParam("additionals", TextUtils.join(",", api.getAdditionalClasses()));
        api.addAuth(params);
        api.call("api_vplan_get", params, new ApiCallback() {
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

    public int getLastCode() {
        return this.lastCode;
    }

    public boolean isLoaded() {
        return loaded;
    }

    private boolean parse(JSONObject result) {
        this.entries = new LinkedList<>();
        this.marquee = new LinkedList<>();
        this.absent_teachers = new LinkedList<>();
        this.absent_strings = new LinkedList<>();
        try {
            JSONObject plan = result.getJSONObject("plan");
            this.day_str = plan.getString("day_str");
            date = plan.getString("date");

            JSONArray entries = plan.getJSONArray("entries");
            for (int i = 0; i < entries.length(); ++i) {
                VplanEntry vplanEntry = new VplanEntry(true);
                vplanEntry.load(this, entries.getJSONObject(i));
                this.entries.add(vplanEntry);
            }

            JSONArray additionals = plan.getJSONArray("additional_entries");
            for (int i = 0; i < additionals.length(); ++i) {
                VplanEntry vplanEntry = new VplanEntry(false);
                vplanEntry.load(this, additionals.getJSONObject(i));
                this.entries.add(vplanEntry);
            }
            JSONArray marquee = plan.getJSONArray("marquee");
            for (int i = 0; i < marquee.length(); ++i) {
                this.marquee.add(marquee.getString(i));
            }
            JSONArray absent = plan.getJSONArray("absent_teachers");
            JSONArray absentlessons = plan.getJSONArray("absent_lessons");
            for (int i = 0; i < absent.length(); ++i) {
                Pair<String, String> p = new Pair<>(absent.getString(i), absentlessons.getString(i));
                this.absent_teachers.add(p);
                this.absent_strings.add(p.first + ": " + p.second);
            }
            changed = plan.getString("changed");
            timestamp = result.optLong("api_time", System.currentTimeMillis());
            loaded = true;
        } catch (JSONException ex) {
            //ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean upToDate() {
        return (timestamp + timeout) >= System.currentTimeMillis();
    }

    public String getDayString() {
        return this.day_str;
    }

    public String getDay() {
        return day;
    }

    public List<String> getMarquee() {
        return marquee;
    }

    public List<String> getAbsentStrings() {
        return absent_strings;
    }

    public String getChanged() {
        return changed;
    }

    private void saveToCache(JSONObject json) {
        try {
            json.put("api_time", System.currentTimeMillis());
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        JsonFileManager.write(json, "vplan_" + getDay(), context);
    }

    private boolean loadFromCache() {
        JSONObject json = JsonFileManager.read("vplan_" + getDay(), context);
        return parse(json);
    }

    public List<VplanEntry> getEntries() {
        return entries;
    }

    public List<VplanEntry> filter(String filter) {
        if (filter == null) {
            return getEntries();
        }
        filter = filter.toLowerCase();

        List<VplanEntry> results = new LinkedList<>();
        for (VplanEntry entry : getEntries()) {
            if (entry.filter(filter)) {
                results.add(entry);
            }
        }
        return results;
    }

    public static void clearCache(String day, Context context) {
        JsonFileManager.clear("vplan_" + day, context);
    }
}

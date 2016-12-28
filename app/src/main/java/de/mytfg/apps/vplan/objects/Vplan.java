package de.mytfg.apps.vplan.objects;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.apps.vplan.api.ApiCallback;
import de.mytfg.apps.vplan.api.ApiParams;
import de.mytfg.apps.vplan.api.MyTFGApi;
import de.mytfg.apps.vplan.api.SuccessCallback;

/**
 * Represents the Vplan for one day
 * Contains multiple entries for different classes and lessons.
 */

public class Vplan extends MytfgObject {
    private Context context;

    private String day;
    private List<VplanEntry> entries = new LinkedList<>();
    private String mode;
    private String cls_string;
    private String day_str;
    private String changed;
    private List<String> marquee = new LinkedList<>();
    private List<Pair<String, String>> absent_teachers = new LinkedList<>();
    private List<String> absent_strings = new LinkedList<>();

    private int lastCode;
    private boolean loaded;

    /**
     * Instanciates a new Vplan
     * @param day The day to use when using the <code>load</code>-method.
     *            (<code>today</code> or <code>tomorrow</code>)
     */
    public Vplan(Context context, String day) {
        this.day = day;
        this.context = context;
    }

    @Override
    public void load(final SuccessCallback callback) {
        MyTFGApi api = new MyTFGApi(context);
        ApiParams params = new ApiParams();
        params.addParam("day", this.day);
        api.addAuth(params);
        api.call("api_vplan_get", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                lastCode = responseCode;
                if (responseCode == 200) {
                    loaded = true;
                    parse(result);
                    callback.callback(true);
                } else {
                    callback.callback(false);
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
            JSONArray entries = plan.getJSONArray("entries");
            for (int i = 0; i < entries.length(); ++i) {
                VplanEntry vplanEntry = new VplanEntry();
                vplanEntry.load(this, entries.getJSONObject(i));
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
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
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

    public List<VplanEntry> getEntries() {
        return entries;
    }
}

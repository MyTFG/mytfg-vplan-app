package de.mytfg.apps.mytfg.objects;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents a single News Entry from the RSS Feed of TFG.
 */
public class TfgEventsEntry extends MytfgObject {
    private long timestamp;
    private String title;
    private String date;
    private String time;
    private String location;
    /**
     * Do not use this method. Use the load method and pass a JSON Object.
     * @param callback Always called with false
     */
    @Override
    public void load(SuccessCallback callback) {
        callback.callback(false);
    }

    public boolean load(JSONObject data) {
        if (data == null) {
            return false;
        }
        try {
            title = data.getString("title");
            date = data.getString("date");
            time = data.getString("time");
            location = data.getString("location");
            timestamp = data.getLong("timestamp");
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

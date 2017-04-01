package de.mytfg.apps.mytfg.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents a VRR Entry
 */

public class VrrEntry extends MytfgObject {
    private String line;
    private String direction;
    private String arrival;
    private String type;
    private int delay;
    private ArrayList<String> route;
    private String platform;
    private String sched;
    private String date;
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
            line = data.getString("line");
            direction = data.getString("destination");
            arrival = data.getString("arrival");
            delay = data.getInt("delay");
            type = data.getString("type");
            route = new ArrayList<>();
            platform = data.getString("platform");
            for (int i = 0; i < data.getJSONArray("route").length(); ++i) {
                route.add(data.getJSONArray("route").getString(i));
            }
            sched = data.getString("sched");
            date = data.getString("real");
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public String getLine() {
        return line;
    }

    public String getDirection() {
        return direction;
    }

    public String getArrival() {
        return arrival;
    }

    public String getType() {
        return type;
    }

    public int getDelay() {
        return delay;
    }

    public ArrayList<String> getRoute() {
        return route;
    }

    public String getPlatform() {
        return platform;
    }

    public String getSched() {
        return sched;
    }

    public String getDate() {
        return date;
    }

    public String getDelayText() {
        return getDelay() + " Min";
    }
}

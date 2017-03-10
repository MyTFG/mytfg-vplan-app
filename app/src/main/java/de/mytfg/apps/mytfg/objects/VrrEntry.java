package de.mytfg.apps.mytfg.objects;

import org.json.JSONArray;
import org.json.JSONException;

import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents a VRR Entry
 */

public class VrrEntry extends MytfgObject {
    private String line;
    private String direction;
    private String arrival;
    /**
     * Do not use this method. Use the load method and pass a JSON Object.
     * @param callback Always called with false
     */
    @Override
    public void load(SuccessCallback callback) {
        callback.callback(false);
    }

    public boolean load(JSONArray data) {
        if (data == null) {
            return false;
        }
        try {
            line = data.getString(0);
            direction = data.getString(1);
            arrival = data.getString(2);
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
}

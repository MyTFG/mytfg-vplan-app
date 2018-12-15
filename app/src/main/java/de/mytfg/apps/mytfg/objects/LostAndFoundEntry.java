package de.mytfg.apps.mytfg.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents a VRR Entry
 */

public class LostAndFoundEntry extends MytfgObject {
    private int id;
    private String description;
    private String registerdate;
    private boolean open;
    private long registerdateTS;

    private String json;

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
            this.json = data.toString();
            id = data.getInt("id");
            description = data.getJSONObject("properties").getString("description");
            registerdate = data.getJSONObject("properties").getString("registerdate");
            open = data.getJSONObject("properties").getString("state").equals("open");
            registerdateTS = data.getJSONObject("properties").getLong("registerdateTS");
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getRegisterdate() {
        return registerdate;
    }

    public boolean isOpen() {
        return open;
    }

    public long getRegisterdateTS() {
        return registerdateTS;
    }

    public String getJson() {
        return json;
    }
}

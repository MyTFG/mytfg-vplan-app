package de.mytfg.apps.mytfg.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents an AP <-> MAC mapping
 */

public class ApMac extends MytfgObject {
    private String apname;
    private String mac;
    private String mac_id;

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
            apname = data.getString("ap");
            mac = data.getString("mac");
            mac_id = data.getString("mac_id");
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public String getApName() {
        return apname;
    }

    public String getMac() {
        return mac;
    }

    public String getMacId() {
        return mac_id;
    }

    public void setApName(String apname) {
        this.apname = apname;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setMacId(String mac_id) {
        this.mac_id = mac_id;
    }

    /**
     * Checks if the MAC matches the given MAC up to the last two digits
     * @param mac The mac to check against
     * @return true iff the first 5 parts match
     */
    public boolean matchMac(String mac) {
        if (mac.length() >= 12) {
            return this.mac_id.equals(ApMac.getMacId(mac));
        }

        return false;
    }

    public String getJson() {
        return json;
    }

    public static String getMacId(String mac) {
        return mac.substring(0, mac.length() - 2).trim().replace("-", " ").replace(" ", ":").toLowerCase();
    }
}

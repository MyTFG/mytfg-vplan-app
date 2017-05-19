package de.mytfg.apps.mytfg.objects;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Created by bader on 18.05.2017.
 */

public class Abbreviation extends MytfgObject {
    private String abbreviation;
    private String fulltext;
    private String addon;
    private String type;

    @Override
    public void load(SuccessCallback callback) {
        callback.callback(false);
    }

    public boolean load(JSONObject data, String type) {
        if (data == null) {
            return false;
        }
        this.type = type;
        abbreviation = data.optString("abbr", "");
        fulltext = data.optString("name", "");
        addon = data.optString("addon", "");

        return true;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getFulltext() {
        return fulltext;
    }

    public String getAddon() {
        return addon;
    }

    public String getType() {
        return type;
    }

    public boolean filter(String filter) {
        filter = filter.toLowerCase();
        return getAbbreviation().toLowerCase().contains(filter)
                || getFulltext().toLowerCase().contains(filter)
                || getAddon().toLowerCase().contains(filter);
    }
}

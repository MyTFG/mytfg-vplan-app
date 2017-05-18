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
    private String type;

    @Override
    public void load(SuccessCallback callback) {
        callback.callback(false);
    }

    public boolean load(JSONObject data, String type) {
        if (data == null) {
            return false;
        }
        try {
            abbreviation = data.getString("abbr");
            fulltext = data.getString("name");
            this.type = type;
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getFulltext() {
        return fulltext;
    }

    public String getType() {
        return type;
    }

    public boolean filter(String filter) {
        filter = filter.toLowerCase();
        return getAbbreviation().toLowerCase().contains(filter)
                || getFulltext().toLowerCase().contains(filter);
    }
}

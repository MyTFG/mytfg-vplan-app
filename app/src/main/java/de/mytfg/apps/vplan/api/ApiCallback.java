package de.mytfg.apps.vplan.api;

import org.json.JSONObject;

public interface ApiCallback {
    void callback(JSONObject result, int responseCode);
}

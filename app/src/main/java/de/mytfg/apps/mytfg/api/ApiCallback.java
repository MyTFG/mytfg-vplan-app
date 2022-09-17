package de.mytfg.apps.mytfg.api;

import org.json.JSONObject;

public interface ApiCallback {
    void callback(JSONObject result, int responseCode);
}

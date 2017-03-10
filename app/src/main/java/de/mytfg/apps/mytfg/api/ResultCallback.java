package de.mytfg.apps.mytfg.api;

import org.json.JSONObject;

public interface ResultCallback {
    void success(JSONObject result);
    void error();
}

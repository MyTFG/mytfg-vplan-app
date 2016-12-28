package de.mytfg.apps.vplan.api;

import org.json.JSONObject;

public interface ResultCallback {
    void success(JSONObject result);
    void error();
}

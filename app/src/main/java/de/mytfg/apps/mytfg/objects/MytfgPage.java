package de.mytfg.apps.mytfg.objects;

import android.content.Context;

import org.acra.util.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

/**
 * Used to load static content pages with HTML
 */

public class MytfgPage extends MytfgObject {
    private Context context;
    private String title;
    private String path;
    private String html;
    private long timestamp;

    private static final long timeout = 7 * 24 * 60 * 60 * 1000; // 1 Week

    public MytfgPage(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    @Override
    public void load(final SuccessCallback callback) {
        load(callback, false);
    }

    public void load(final SuccessCallback callback, boolean clearCache) {
        // Try to load from cache
        if (!clearCache && loadFromCache()) {
            if (upToDate()) {
                callback.callback(true);
                return;
            }
        }

        MyTFGApi api = new MyTFGApi(context);
        api.call("api_page_html_" + path, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    if (parse(result)) {
                        try {
                            result.put("timestamp", timestamp);
                            saveToCache(result);
                            callback.callback(true);
                        } catch (JSONException ex) {
                            callback.callback(true);
                        }
                    } else {
                        callback.callback(false);
                    }
                } else {
                    callback.callback(false);
                }
            }
        });
    }

    private boolean parse(JSONObject object) {
        try {
            title = object.getString("title");
            html = object.getString("html");
            timestamp = System.currentTimeMillis();
            return true;
        } catch (JSONException ex) {
            return false;
        }
    }

    private boolean saveToCache(JSONObject object) {
        return JsonFileManager.write(object, "page_" + path, context);
    }

    private boolean upToDate() {
        return timestamp + timeout > System.currentTimeMillis();
    }

    private boolean loadFromCache() {
        JSONObject object = JsonFileManager.read("page_" + path, context);
        if (object != null) {
            if (parse(object)) {
                return true;
            }
        }
        return false;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getHtml() {
        return html;
    }
}

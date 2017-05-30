package de.mytfg.apps.mytfg.objects;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents a single News Entry from the RSS Feed of TFG.
 */
public class TfgNewsEntry extends MytfgObject {



    private long timestamp;
    private String json;
    private String link;
    private String title;
    private String text;
    private String html;
    private String summary;
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
            json = data.toString();
            timestamp = data.getLong("ts");
            link = data.getString("link");
            title = data.getString("title");
            text = data.getString("text");
            html = data.getString("html");
            summary = data.getString("summary");

            for(String imgTag : html.)
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public String getJson() {
        return json;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDateString() {
        return MyTFGApi.tsToString(timestamp);
    }

    public String getLink() {
        return link;
    }

    public String getText() {
        return text;
    }

    public String getHtml() {
        return html;
    }

    public boolean filter(String filter) {
        filter = filter.toLowerCase();
        return getSummary().toLowerCase().contains(filter)
                || getLink().toLowerCase().contains(filter)
                || getText().toLowerCase().contains(filter)
                || getTitle().toLowerCase().contains(filter);
    }
}

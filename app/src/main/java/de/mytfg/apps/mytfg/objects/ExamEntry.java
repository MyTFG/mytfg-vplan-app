package de.mytfg.apps.mytfg.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents a single Exam Entry.
 */
public class ExamEntry extends MytfgObject {
    private long timestamp;
    private String json;
    private String text;
    private String subject;
    private String teacher;
    private String lessons;
    private String block;
    private boolean crossed;
    private String type;
    private int grade;


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
        text = "";
        teacher = "";
        lessons = "";
        subject = "";
        try {
            json = data.toString();
            timestamp = data.getLong("timestamp");
            type = data.getString("type");
            grade = data.getInt("grade");
            if ("spanned".equals(type)) {
                JSONArray lines = data.getJSONArray("lines");
                for (int i = 0; i < lines.length(); ++i) {
                    text += lines.getString(i).trim() + "\n";
                }
                text = text.trim();
                block = "spanned";
            } else {
                block = data.getString("block");
                subject = data.getString("subject");
                teacher = data.getString("teacher");
                crossed = data.getBoolean("crossed");
                lessons = data.getString("lessons");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String getJson() {
        return json;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getLessons() {
        return lessons;
    }

    public String getBlock() {
        return block;
    }

    public boolean isCrossed() {
        return crossed;
    }

    public String getType() {
        return type;
    }

    public int getGrade() {
        return grade;
    }

    public String getDateString() {
        return MyTFGApi.tsToString(timestamp);
    }

    public String getDayName() {
        return MyTFGApi.getDayname(timestamp);
    }

    public String getDay() {
        return MyTFGApi.getDay(timestamp);
    }

    public String getMonth() {
        return MyTFGApi.getMonth(timestamp);
    }

    public String getYear() {
        return MyTFGApi.getYear(timestamp);
    }

    public String getText() {
        return text;
    }

    public boolean filter(String filter) {
        filter = filter.toLowerCase();
        return getTeacher().toLowerCase().contains(filter)
                || getSubject().toLowerCase().contains(filter)
                || getLessons().toLowerCase().contains(filter)
                || getType().toLowerCase().contains(filter)
                || getBlock().toLowerCase().contains(filter)
                || getDateString().toLowerCase().contains(filter)
                || getText().toLowerCase().contains(filter);
    }
}

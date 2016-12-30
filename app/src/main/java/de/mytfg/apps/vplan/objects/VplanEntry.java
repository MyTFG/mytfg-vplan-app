package de.mytfg.apps.vplan.objects;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.vplan.api.SuccessCallback;

/**
 * Represents a single entry from the MyTFG VPlan
 */

public class VplanEntry extends MytfgObject {
    private String lesson;
    private String cls;
    private String plan;
    private String substitution;
    private String comment;
    private String teacher;

    private boolean own;

    private Vplan day;

    public VplanEntry(boolean own) {
        this.own = own;
    }

    /**
     * Loading is done locally. Use <code>load(JSONObject data)</code> instead.
     * @param callback Called when loading finished.
     */
    @Override
    public void load(SuccessCallback callback) {
        callback.callback(false);
    }

    /**
     * Loads the entry from the given JSONObject.
     * @param data The JSONObject to process
     * @return true iff loading was successful, i.e. the given is was valid
     */
    public boolean load(Vplan day, JSONObject data) {
        if (data == null) {
            return false;
        }
        this.day = day;
        try {
            lesson = data.getString("lesson");
            cls = data.getString("class");
            plan = data.getString("plan");
            substitution = data.getString("substitution");
            comment = data.getString("comment");
            teacher = data.getString("teacher");
        } catch (JSONException ex) {
            // TODO: Remove this line:
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String getLesson() {
        return lesson;
    }

    public String getCls() {
        return cls;
    }

    public String getPlan() {
        return plan;
    }

    public String getSubstitution() {
        return substitution;
    }

    public String getComment() {
        return comment;
    }

    public String getTeacher() {
        return teacher;
    }

    /**
     * Checks wheter this entry contains information matching the given filter.
     * @param filter The filter to apply
     * @return True iff filter matches this entry
     */
    public boolean filter(String filter) {
        return getCls().toLowerCase().contains(filter)
                || getComment().toLowerCase().contains(filter)
                || getLesson().toLowerCase().contains(filter)
                || getPlan().toLowerCase().contains(filter)
                || getSubstitution().toLowerCase().contains(filter);
    }

    public boolean isOwn() {
        return own;
    }
}

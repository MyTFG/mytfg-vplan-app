package de.mytfg.apps.mytfg.objects;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

/**
 * Represents a user.
 * Used to store information about the logged in user on the device.
 * Information obtained during the login
 */

public class User extends MytfgObject {
    private int id;
    private String username;
    private String firstname;
    private String lastname;
    private String grade;
    private int rights;

    private Context context;

    public static final int USER_RIGHTS_NORIGHTS = 0;
    public static final int USER_RIGHTS_PUPIL = 1;
    public static final int USER_RIGHTS_TEACHER = 2;
    public static final int USER_RIGHTS_MANAGEMENT = 3;
    public static final int USER_RIGHTS_SYSTEM = 4;
    public static final int USER_RIGHTS_ADMIN = 5;

    public User(Context context) {
        this.context = context;
    }

    /**
     * Loading is done via JSON only. No API request present to request the user information.
     * @param callback Called when loading finished. Always called with <code>false</code>
     */
    @Override
    public void load(SuccessCallback callback) {
        callback.callback(false);
    }

    public boolean load(JSONObject json) {
        return this.parse(json);
    }

    public boolean load(String username) {
        JSONObject json = JsonFileManager.read("user_" + username, context);
        return parse(json);
    }

    private boolean parse(JSONObject json) {
        try {
            id = json.getInt("id");
            rights = json.getInt("rights");
            username  = json.getString("username");
            firstname = json.getString("firstname");
            lastname  = json.getString("lastname");
            grade     = json.getString("grade");
            this.save(json);
            return true;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean save(JSONObject json) {
        return JsonFileManager.write(json, "user_" + getUsername(), context);
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGrade() {
        return grade;
    }

    public int getGradeNum() {
        String digits = getGrade().replaceAll("[^0-9]", "");
        try {
            int grade = Integer.valueOf(digits);
            if (grade <= 12 && grade >= 5) {
                return grade;
            }
        } catch (Exception ex) {
            return -1;
        }
        return -1;
    }

    public int getRights() {
        return rights;
    }

    public String getLevel() {
        switch (getRights()) {
            default:
            case USER_RIGHTS_NORIGHTS:
                return context.getString(R.string.user_rights_norights);
            case USER_RIGHTS_PUPIL:
                return context.getString(R.string.user_rights_pupil);
            case USER_RIGHTS_TEACHER:
                return context.getString(R.string.user_rights_teacher);
            case USER_RIGHTS_MANAGEMENT:
                return context.getString(R.string.user_rights_management);
            case USER_RIGHTS_SYSTEM:
                return context.getString(R.string.user_rights_system);
            case USER_RIGHTS_ADMIN:
                return context.getString(R.string.user_rights_admin);
        }
    }

    public String getShortage() {
        return getUsername().substring(2);
    }
}

package de.mytfg.apps.mytfg.objects;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
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
    private String mail;
    private String usertype = null;
    private String avatar;
    private int rights;
    private JSONArray permissions;

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
            usertype  = json.getString("usertype");
            mail      = json.optString("mail", "");
            avatar    = json.optString("avatar", "");
            permissions = json.optJSONArray("permissions");
            if (permissions == null) {
                permissions = new JSONArray();
            }
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

    public boolean remove() {
        return JsonFileManager.clear("user_" + getUsername(), context);
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

    public String getMail() { return mail; }

    public String getAvatar() {
        return this.getAvatar(false);
    }

    public String getAvatar(boolean withAuthentication) {
        if (avatar == null) {
            return "";
        } else {
            if (withAuthentication) {
                MyTFGApi api = new MyTFGApi(context);
                ApiParams params = new ApiParams();
                api.addAuth(params);

                return "https://mytfg.de/" + avatar + "&" + params.toUrlString();
            } else {
                return "https://mytfg.de/" + avatar;
            }
        }
    }

    public String getUserType() {
        return usertype == null ? "" : usertype;
    }

    public boolean hasPermission(String permission) {
        if (this.permissions != null) {
            for (int i = 0; i < permissions.length(); ++i) {
                JSONObject perm = permissions.optJSONObject(i);
                if (perm != null) {
                    if (perm.optString("perm", "").equals(permission)) {
                        if (perm.optBoolean("total", false)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> perms = new ArrayList<>();
        if (this.permissions != null) {
            for (int i = 0; i < permissions.length(); ++i) {
                JSONObject perm = permissions.optJSONObject(i);
                if (perm != null) {
                    if (perm.optBoolean("total", false)) {
                        perms.add(perm.optString("perm", ""));
                    }
                }
            }
        }
        return perms;
    }

    public int getGradeNum() {
        if (getGrade() == null) {
            return -1;
        }
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
        switch (getUserType()) {
            default:
            case "logged_out":
                return context.getString(R.string.user_rights_norights);
            case "pupil":
                return context.getString(R.string.user_rights_pupil);
            case "parent":
                return context.getString(R.string.user_rights_parent);
            case "teacher":
                return context.getString(R.string.user_rights_teacher);
            case "management":
                return context.getString(R.string.user_rights_management);
            case "system":
                return context.getString(R.string.user_rights_system);
            case "admin":
                return context.getString(R.string.user_rights_admin);
        }
    }

    public String getShortage() {
        return getUsername().substring(2);
    }
}

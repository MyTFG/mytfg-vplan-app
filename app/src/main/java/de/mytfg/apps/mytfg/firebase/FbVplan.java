package de.mytfg.apps.mytfg.firebase;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.User;

/**
 * Handles Firebase messages related to the VPlan
 */

class FbVplan {
    private Context context;
    public FbVplan(Context c) {
        this.context = c;
    }

    void handle(Map<String, String> data) {
        MyTFGApi api = new MyTFGApi(context);
        if (!api.isLoggedIn()) {
            return;
        }

        String date = data.get("date");
        String day = data.get("daystr");
        String datestr = data.get("datestr");
        ArrayList<String> classes = new ArrayList<>();
        ArrayList<String> teachers = new ArrayList<>();
        try {
            JSONArray jsoncls = new JSONArray(data.get("changed_classes"));
            JSONArray jsonteachers = new JSONArray(data.get("changed_teachers"));
            for (int i = 0; i < jsoncls.length(); ++i) {
                classes.add(jsoncls.getString(i).toLowerCase());
            }
            for (int i = 0; i < jsonteachers.length(); ++i) {
                teachers.add(jsonteachers.getString(i));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        List<String> subscribtions = api.getAdditionalClasses();
        String userclass = api.getUser().getGrade();
        int userrights = api.getUser().getRights();
        String text = "";

        // Calculate ID based on date and day
        String toHash = MyTFGApi.tsToString(System.currentTimeMillis() / 1000) + day;

        int nextId = -1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // New devices: Update existing notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nextId = toHash.hashCode();
            for (StatusBarNotification notification : notificationManager.getActiveNotifications()) {
                if (notification.getId() == nextId) {
                    // Last notification still active
                    ArrayList<String> oldClasses = notification.getNotification().extras.getStringArrayList("classes");
                    ArrayList<String> oldTeachers = notification.getNotification().extras.getStringArrayList("teachers");
                    if (oldClasses != null) {
                        for (String cls : oldClasses) {
                            if (!classes.contains(cls)) {
                                classes.add(cls);
                            }
                        }
                    }
                    if (oldTeachers != null) {
                        for (String teacher : oldTeachers) {
                            if (!teachers.contains(teacher)) {
                                teachers.add(teacher);
                            }
                        }
                    }
                }
            }
        }

        Bundle extras = new Bundle();
        extras.putStringArrayList("teachers", teachers);
        extras.putStringArrayList("classes", classes);


        switch (userrights) {
            case User.USER_RIGHTS_ADMIN:
                if (classes.size() > 0) {
                    // Notification!
                    String cls = TextUtils.join(", ", classes);
                    FbNotify.notifyVplan(context, "MyTFG VPlan für " + datestr , "Klassen: " + cls, day, nextId, extras);
                }
                break;
            case User.USER_RIGHTS_TEACHER:
                String krz = api.getUser().getShortage();
                if (teachers.contains(krz)) {
                    // Notification
                    text = "Änderungen, die " + api.getUser().getFirstname() + " " + api.getUser().getLastname() + " betreffen!";
                }
            default:
                subscribtions.add(userclass);
                classes.retainAll(subscribtions);
                String text2 = "";
                if (classes.size() > 0) {
                    String cls = TextUtils.join(", ", classes);
                    text2 = "Klassen: " + cls;
                }
                String t = text2;
                if (text.length() > 0) {
                    t = text;
                    if (text2.length() > 0) {
                        t += "\n" + text2;
                    }
                }
                if (t.length() > 0) {
                    FbNotify.notifyVplan(context, "MyTFG VPlan für " + datestr, t, day, nextId, extras);
                }
                break;
        }
    }
}

package de.mytfg.apps.mytfg.api;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.collection.ArraySet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.firebase.FbApi;
import de.mytfg.apps.mytfg.fragments.LoginFragment;
import de.mytfg.apps.mytfg.objects.User;
import de.mytfg.apps.mytfg.objects.Vplan;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

/**
 * Wrapper for the MyTFG API. Handles Login tokens, authentication errors and result parsing.
 */

public class MyTFGApi {
    public static final String URL_HOME = "https://mytfg.de/start.x";
    public static final String URL_SUPPORTCENTER = "https://mytfg.de/supportcenter.x";
    public static final String URL_SUPPORTCENTER_TICKETS = "https://mytfg.de/supportcenter/tickets.x";
    public static final String URL_PURCHASES = "https://mytfg.de/purchases.x";
    public static final String URL_SETTINGS = "https://mytfg.de/settings.x";
    public static final String URL_AVATAR = "https://mytfg.de/account/picture.x";
    public static final String URL_NOTIFICATIONS = "https://mytfg.de/notifications.x";
    public static final String URL_ACCOUNTS = "https://mytfg.de/account.x";
    public static final String URL_ACCOUNTS_SEARCH = "https://mytfg.de/account/search.x";
    public static final String URL_ACCOUNTS_CREATE = "https://mytfg.de/account/create.x";
    public static final String URL_FORGOT_PASSWORD = "https://mytfg.de/account/pwreset.x";
    public static final String URL_AUTHS = "https://mytfg.de/account/authentications.x";


    private ProgressBar loadingBar;
    private Context context;
    private SharedPreferences preferences;
    private static int overrideLoad = 0;

    public MyTFGApi(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
        if (context instanceof MainActivity) {
            View rootview = ((Activity) context).getWindow().getDecorView()
                    .findViewById(android.R.id.content);
            loadingBar = (ProgressBar) rootview.findViewById(R.id.loadingBar);
            loadingBar.setVisibility(View.GONE);
        }
    }

    /**
     * Checks whether valid login information is stored.
     * Does NOT check if the stored information is still valid!
     * @return True iff user is logged in as far as known.
     */
    public boolean isLoggedIn() {
        String token = preferences.getString("token", null);
        long date = preferences.getLong("expire", 0);
        long timestamp = System.currentTimeMillis()/1000;
        return (token != null && date > timestamp);
    }

    /**
     * Queries the server for information about the current Login.
     * This is done only once every two hours, otherwise, cached information is used.
     */
    public void updateAuthInfo(final SimpleCallback callback) {
        long lastupdate = preferences.getLong("lastupdate", 0);
        this.updateAuthInfo(lastupdate, callback);
    }

    public void updateAuthInfo(long lastupdate, final SimpleCallback callback) {
        long timestamp = System.currentTimeMillis() / 1000;
        long reloadTimeout = 60 * 5; // 5 minutes

        if (lastupdate < timestamp - reloadTimeout) {
            preferences.edit().putLong("lastupdate", timestamp).apply();
            if (getToken().equals("")) {
                callback.callback();
                return;
            }

            ApiParams params = new ApiParams();
            this.addAuth(params);
            this.call("api/auth/info", params, new ApiCallback() {
                @Override
                public void callback(JSONObject result, int responseCode) {
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        // Login is still valid, update expire from response
                        long expire = 0;
                        try {
                            expire = result.getLong("expires");
                        } catch (JSONException ex) {
                            Log.d("AUTH", ex.getMessage());
                        }

                        JSONObject juser = result.optJSONObject("currentuser");
                        if (juser != null) {
                            User user = new User(context);
                            user.load(juser);
                        }

                        preferences.edit()
                                .putLong("expire", expire)
                                .putBoolean("expired", false)
                                .apply();
                        callback.callback();
                    } else {
                        if (responseCode >= 500 || responseCode < 0) {
                            // API down?
                            callback.callback();
                            return;
                        }
                        // Seems not to be a valid login anymore, logout
                        // and set flag that the login has expired
                        logout(false);
                        preferences.edit()
                                .putBoolean("expired", true)
                                .apply();

                        callback.callback();
                    }
                }
            });
        } else {
            callback.callback();
        }
    }

    public void checkLoginDialog(final MainActivity context) {
        checkLogin(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                if (!success) {
                    if (isExpired()) {
                        logout(false);
                        new AlertDialog.Builder(context)
                                .setCancelable(true)
                                .setTitle(R.string.mytfg_login_timeout_header)
                                .setMessage(context.getResources().getString(R.string.mytfg_login_timeout_text))
                                .setPositiveButton(R.string.mytfg_login_renew, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        context.getNavi().navigate(new LoginFragment(), R.id.fragment_container);
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.mytfg_login_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        });
    }

    /**
     * Adds authentication information to the parameter set.
     * @param params The parameter set the authentication information should be added to.
     */
    public void addAuth(ApiParams params) {
        String token = preferences.getString("token", "");
        String username = preferences.getString("username", "");
        String device = preferences.getString("device", "");
        params.addParam("mytfg_api_login_token", token);
        params.addParam("mytfg_api_login_username", username);
        params.addParam("mytfg_api_login_device", device);
    }

    /**
     * Checks wheter the stored login information (if any) is still valid by actively performing
     * a test query.
     * As soon as the information is present, the callback is called with the boolean result as
     * parameter (true iff login information is still valid)
     * @param callback The callback to use
     */
    public void checkLogin(final SuccessCallback callback) {
        this.updateAuthInfo(new SimpleCallback() {
            @Override
            public void callback() {
                callback.callback(isLoggedIn());
            }
        });
    }

    /**
     * Checks whether the currently saved login is expired.
     * This should be combined with "checkLogin".
     */
    public boolean isExpired() {
        if (!isLoggedIn()) {
            return preferences.getBoolean("expired", false);
        }

        return false;
    }

    public void setExpired(boolean expired) {
        preferences.edit()
                .putBoolean("expired", expired)
                .apply();
    }

    public User getUser() {
        User user = new User(context);
        user.load(getUsername());
        return user;
    }

    public List<String> getAdditionalClasses() {
        Set<String> set = preferences.getStringSet("additional_classes", new ArraySet<String>());
        List<String> list = new LinkedList<>();
        for (String cls : set) {
            if (cls != null) {
                list.add(cls);
            }
        }
        return list;
    }

    public void setAdditionalClasses(List<String> classes) {
        Set<String> classSet = new ArraySet<>(classes.size());
        for (String cls : classes) {
            if (cls != null) {
                classSet.add(cls);
            }
        }
        preferences.edit()
                .putStringSet("additional_classes", classSet)
                .apply();

        Vplan.clearCache("today", context);
        Vplan.clearCache("tomorrow", context);
    }

    public void clearAdditionalClasses() {
        preferences.edit()
                .remove("additional_classes")
                .apply();

        Vplan.clearCache("today", context);
        Vplan.clearCache("tomorrow", context);
    }

    /**
     * Generates the device ID passed to the API.
     * @return The device ID.
     */
    public String getDevice() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public String getUsername() {
        return preferences.getString("username", "");
    }

    public String getStoredDevice() {
        return preferences.getString("device", "");
    }

    public String getToken() {
        return preferences.getString("token", "");
    }

    public long getExpire() {
        return preferences.getLong("expire", 0);
    }

    public String getExpireString() {
        Timestamp stamp = new Timestamp(getExpire() * 1000);
        Date date = new Date(stamp.getTime());
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return df.format(date);
    }

    /**
     * Saves the given login data for future use.
     * @param username The username
     * @param token The API token (password replacement)
     * @param device The device ID
     * @param expire The expire date as Timestamp for the token
     */
    public void saveLogin(String username, String token, String device, String devicename, long expire) {
        preferences.edit()
                .putString("token", token)
                .putString("username", username)
                .putString("device", device)
                .putString("devicename", devicename)
                .putLong("expire", expire)
                .putBoolean("expired", false)
                .apply();
    }

    /**
     * Deletes authentication information from the device.
     * @param deleteUsername Specifies if the username should be deleted, too
     */
    public void logout(boolean deleteUsername) {
        preferences.edit()
                .remove("token")
                .remove("device")
                .remove("devicename")
                .remove("expire")
                .remove("additional_classes")
                .putBoolean("expired", false)
                .apply();

        getUser().remove();

        if (deleteUsername) {
            preferences.edit()
                    .remove("username")
                    .apply();
        }
        Vplan.clearCache("today", context);
        Vplan.clearCache("tomorrow", context);
        JsonFileManager.clear("notifications.json", context);
        FbApi.unsubscribeAll(context);
    }

    /**
     * Sends a request to the MyTFG API
     * @param apiFunction The URL for the API function
     * @param params Parameters to pass
     * @param callback The callback to invoke when result was received.
     */
    public void call(String apiFunction, ApiParams params, ApiCallback callback) {
        call(apiFunction, params, null, callback);
    }

    public void call(String apiFunction, ApiParams params, JSONObject body, ApiCallback callback) {
        if (params == null) {
            callback.callback(null, -1);
        } else {
            new MyTFGApi.RequestTask(this.loadingBar, apiFunction, params, callback).addBody(body).setContext(context).execute("");
        }
    }

    public void call(String apiFunction, ApiCallback callback) {
        call(apiFunction, new ApiParams(), callback);
    }

    public void startLoading() {
        overrideLoad++;
        RequestTask.activeCalls++;

        if (RequestTask.activeCalls > 0 && loadingBar != null) {
            loadingBar.setVisibility(View.VISIBLE);
        }
    }

    public void stopLoading() {
        if (overrideLoad == 0) {
            return;
        }
        overrideLoad--;
        RequestTask.activeCalls--;

        if (RequestTask.activeCalls <= 0 && loadingBar != null) {
            loadingBar.setVisibility(View.GONE);
        }
    }

    public boolean isLoading() {
        return RequestTask.activeCalls > 0;
    }

    public void clearOverrideLoading() {
        RequestTask.activeCalls-= overrideLoad;
        overrideLoad = 0;

        if (RequestTask.activeCalls <= 0 && loadingBar != null) {
            loadingBar.setVisibility(View.GONE);
        }
    }

    /**
     * Performs an API request in background.
     */
    private static class RequestTask extends AsyncTask<String, String, String> {
        private String path;
        private ApiParams args;
        private ApiCallback callback;
        private ProgressBar loadingBar;
        private static int activeCalls = 0;

        private String baseUrl = "https://mytfg.de/";

        private Context context;

        private int responseCode = -1;
        private String responseMessage = "";
        private JSONObject body;

        private RequestTask(ProgressBar pb, String path, ApiParams params, ApiCallback callback) {
            this.path = path;
            this.args = params;
            this.callback = callback;
            this.loadingBar = pb;
            if (loadingBar != null) {
                loadingBar.setVisibility(View.VISIBLE);
            }
        }

        private RequestTask setContext(Context context) {
            this.context = context;
            return this;
        }

        private RequestTask addBody(JSONObject body) {
            this.body = body;
            return this;
        }

        private String getPostDataString() throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : args.getMap().entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected String doInBackground(String... useless) {
            activeCalls++;
            try {
                URL url = new URL(baseUrl + path + ".x");

                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                Log.d("API Call", url.toString());

                connection.setReadTimeout(8000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString());
                writer.flush();
                writer.close();
                responseCode = connection.getResponseCode();
                responseMessage = connection.getResponseMessage();

                String response = "";
                String line;
                try {
                    BufferedReader br;
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    } else {
                        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    }
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    return response;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.e("API IO", ex.getMessage() == null ? "" : ex.getMessage());
                    return null;
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("API", "Malformed URL: " + ex.getMessage());
                return null;
            } catch (SocketTimeoutException ex) {
                Log.e("API Timeout", ex.getMessage() == null ? "" : ex.getMessage());
                return null;
            } catch (Exception ex) {
                Log.e("API Exception", ex.getMessage() == null ? "" : ex.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            activeCalls--;
            if (activeCalls <= 0 && loadingBar != null) {
                loadingBar.setVisibility(View.GONE);
            }

            Log.d("API ResponseCode", "" + responseCode);
            if (result == null)
                Log.d("API Result", "null");
            else
                Log.d("API Result", result);

            if (responseCode == 403) {
                // Check for deleted login
                try {
                    boolean login_canceled = false;
                    JSONObject tmp = new JSONObject(result);
                    if (tmp.optString("login_error", "").equals("deleted")) {
                        // Login deleted
                        login_canceled = true;
                    } else if (tmp.optString("login_error", "").equals("timedout")) {
                        login_canceled = true;
                    }
                    if (login_canceled && context instanceof MainActivity) {
                        Log.d("API", "Login canceled");
                        MainActivity activity = (MainActivity)context;
                        activity.getNavi().snackbar(context.getString(R.string.login_deleted));
                        MyTFGApi api = new MyTFGApi(context);
                        api.logout(false);
                        activity.getNavi().navigate(new LoginFragment(), R.id.fragment_container);
                        return;
                    } else {
                        callback.callback(new JSONObject(), responseCode);
                        return;
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    callback.callback(new JSONObject(), responseCode);
                    return;
                }
            }

            if (result == null) {
                callback.callback(new JSONObject(), responseCode);
            } else {
                try {
                    JSONObject obj = new JSONObject(result);
                    // Update the currentuser if present
                    if (obj.has("currentuser")) {
                        JSONObject currentuser = obj.optJSONObject("currentuser");
                        if (currentuser != null) {
                            User user = new User(context);
                            user.load(currentuser);
                        }
                    }
                    callback.callback(obj, responseCode);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.e("API Inv JSON", result);
                    callback.callback(new JSONObject(), responseCode);
                }
            }
        }
    }

    public static String tsToString(long timestamp) {
        timestamp = timestamp * 1000;
        try{
            DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "--";
        }
    }

    public static String getDay(long timestamp) {
        timestamp = timestamp * 1000;
        try{
            DateFormat sdf = new SimpleDateFormat("dd", Locale.GERMAN);
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "--";
        }
    }

    public static String getMonth(long timestamp) {
        timestamp = timestamp * 1000;
        try{
            DateFormat sdf = new SimpleDateFormat("MMM", Locale.GERMAN);
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "--";
        }
    }

    public static String getDayname(long timestamp) {
        timestamp = timestamp * 1000;
        try{
            DateFormat sdf = new SimpleDateFormat("EEE", Locale.GERMAN);
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "--";
        }
    }

    public static String getDayType(long timestamp) {
        timestamp = timestamp * 1000;
        try{
            DateFormat sdf = new SimpleDateFormat("u", Locale.GERMAN);
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "--";
        }
    }

    public static String getYear(long timestamp) {
        timestamp = timestamp * 1000;
        try{
            DateFormat sdf = new SimpleDateFormat("yyyy", Locale.GERMAN);
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "--";
        }
    }
}

package de.mytfg.apps.vplan.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.vplan.R;

/**
 * Wrapper for the MyTFG API. Handles Login tokens, authentication errors and result parsing.
 */

public class MyTFGApi {
    private ProgressBar loadingBar;
    private Context context;

    public MyTFGApi(Context context) {
        this.context = context;
        View rootview = ((Activity)context).getWindow().getDecorView()
                .findViewById(android.R.id.content);
        loadingBar = (ProgressBar) rootview.findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.GONE);
    }

    /**
     * Checks whether valid login information is stored.
     * Does NOT check if the stored information is still valid!
     * @return True iff user is logged in as far as known.
     */
    public boolean isLoggedIn() {
        SharedPreferences preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
        String token = preferences.getString("token", null);
        long date = preferences.getLong("expire", 0);
        long timestamp = System.currentTimeMillis()/1000;
        return (token != null && date > timestamp);
    }

    /**
     * Adds authentication information to the parameter set.
     * @param params The parameter set the authentication information should be added to.
     */
    public void addAuth(ApiParams params) {
        SharedPreferences preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
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
    public void checkLogin(SuccessCallback callback) {

    }

    /**
     * Generates the device ID passed to the API.
     * @return The device ID.
     */
    public String getDevice() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getUsername() {
        SharedPreferences preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
        return preferences.getString("username", "");
    }

    public String getStoredDevice() {
        SharedPreferences preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
        return preferences.getString("device", "");
    }

    public String getToken() {
        SharedPreferences preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
        return preferences.getString("token", "");
    }

    public long getExpire() {
        SharedPreferences preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
        return preferences.getLong("expire", 0);
    }

    /**
     * Saves the given login data for future use.
     * @param username The username
     * @param token The API token (password replacement)
     * @param device The device ID
     * @param expire The expire date as Timestamp for the token
     */
    public void saveLogin(String username, String token, String device, long expire) {
        SharedPreferences preferences = context.getSharedPreferences("authmanager", Context.MODE_PRIVATE);
        preferences.edit()
                .putString("token", token)
                .putString("username", username)
                .putString("device", device)
                .putLong("expire", expire)
                .apply();
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
            new MyTFGApi.RequestTask(this.loadingBar, apiFunction, params, callback).addBody(body).execute("");
        }
    }

    public void call(String apiFunction, ApiCallback callback) {
        call(apiFunction, new ApiParams(), callback);
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

        private int responseCode = -1;
        private String responseMessage = "";
        private JSONObject body;

        private RequestTask(ProgressBar pb, String path, ApiParams params, ApiCallback callback) {
            this.path = path;
            this.args = params;
            this.callback = callback;
            this.loadingBar = pb;
            loadingBar.setVisibility(View.VISIBLE);
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

                connection.setReadTimeout(5000);
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
                    Log.e("API IO", ex.getMessage());
                    return null;
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("API", "Malformed URL: " + ex.getMessage());
                return null;
            } catch (Exception ex) {
                Log.e("API Exception", ex.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            activeCalls--;
            if (activeCalls <= 0) {
                loadingBar.setVisibility(View.GONE);
            }

            // TODO: Remove this:
            Log.d("API ResponseCode", "" + responseCode);
            Log.d("API ResponseMsg ", responseMessage);
            if (result == null)
                Log.d("API Result", "null");
            else
                Log.d("API Result", result);


            if (result == null) {
                callback.callback(new JSONObject(), responseCode);
            } else {
                try {
                    JSONObject obj = new JSONObject(result);
                    callback.callback(obj, responseCode);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    callback.callback(new JSONObject(), responseCode);
                }
            }
        }
    }
}
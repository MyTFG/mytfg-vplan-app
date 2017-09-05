package de.mytfg.apps.mytfg.objects;

import org.json.JSONObject;

import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Represents an abbreviation for a teacher or a subject.
 */

public class Authentication extends MytfgObject {
    private String device;
    private String devicename;
    private boolean timedout;
    private String timeoutDate;
    private long timeout;
    private String lastUsedDate;
    private String lastIp;
    private String lastGeo;
    private String deviceType;
    private String deviceOS;
    private String deviceOSVersion;
    private String deviceInfo;

    private Authentications parent;

    public Authentication(Authentications auths) {
        this.parent = auths;
    }

    @Override
    public void load(SuccessCallback callback) {
        callback.callback(false);
    }

    public void remove(final SuccessCallback callback) {
        MyTFGApi api = new MyTFGApi(parent.getContext());
        final Authentication that = this;
        ApiParams params = new ApiParams();
        api.addAuth(params);
        params.addParam("device", this.getDevice());
        api.call("api_auth_remove", params, new ApiCallback() {
            @Override
            public void callback(JSONObject result, int responseCode) {
                if (responseCode == 200) {
                    parent.remove(that);
                    callback.callback(true);
                } else {
                    callback.callback(false);
                }
            }
        });
    }

    public boolean load(JSONObject data) {
        if (data == null) {
            return false;
        }
        device = data.optString("device", "");
        devicename = data.optString("devicename", "");
        timedout = data.optBoolean("timedout");
        timeoutDate = data.optString("timeout_date");
        timeout = data.optLong("timeout", 0);
        lastUsedDate = data.optString("lastused_date");
        deviceType = data.optString("devicetype", "phone");
        deviceOS = data.optString("deviceos", "android");
        deviceOSVersion = data.optString("deviceosversion", "Unbekannt");
        deviceInfo = data.optString("deviceinfo", "Unbekannt");

        return true;
    }

    public String getDevice() {
        return device;
    }

    public String getDevicename() {
        return devicename;
    }

    public boolean isTimedout() {
        return timedout;
    }

    public String getTimeoutDate() {
        return timeoutDate;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getLastUsedDate() {
        return lastUsedDate;
    }

    public String getLastIp() {
        return lastIp;
    }

    public String getLastGeo() {
        return lastGeo;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public String getDeviceOSVersion() {
        return deviceOSVersion;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public boolean filter(String filter) {
        filter = filter.toLowerCase();
        return getDevice().toLowerCase().contains(filter)
                || getDevicename().toLowerCase().contains(filter);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Authentication && this.getDevice().equals(((Authentication) obj).getDevice());
    }
}

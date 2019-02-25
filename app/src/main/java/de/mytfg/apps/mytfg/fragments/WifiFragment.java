package de.mytfg.apps.mytfg.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.objects.ApMac;
import de.mytfg.apps.mytfg.objects.ApMacs;

public class WifiFragment extends AuthenticationFragment {
    private final static String SSID = "MyTFG";
    private final static int scanFrequency = 30; // Every 30 Seconds scanning is allowed


    private RippleBackground rippleBackground;
    private FloatingActionButton fab;
    private NestedScrollView resultsView;

    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private MainActivity context;

    private ApMacs macs;

    private enum WIFI_STATE {
        PENDING,
        ERROR,
        WARNING,
        GOOD
    }
    private Date lastScan = new Date();
    private WIFI_STATE lastState = WIFI_STATE.PENDING;

    private class ApMatch {
        String apName;
        String macId;
        String ssid;
        boolean enabled24;
        boolean enabled50;
        int quality24;
        int quality50;
        int rssi24 = -100;
        int rssi50 = -100;
        int channel24;
        int channel50;
    }

    private List<ApMatch> apMatches;


    public WifiFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.wifi_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_wifi))
                .showFab(R.drawable.ic_wifi_black_24dp)
                .setExpandable(false, false);

        lastScan.setTime(0);

        rippleBackground = view.findViewById(R.id.wifi_loader);
        resultsView = view.findViewById(R.id.wifi_results);
        fab = context.findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
        fab.show();

        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        this.context = context;
        this.macs = new ApMacs(context);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    private void startScan() {

        if (!wifiManager.isWifiEnabled()) {
            context.getNavi().snackbar("Enabling Wifi...");
            wifiManager.setWifiEnabled(true);
        }


        BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                if (intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                    scanDone();
                } else {
                    scanFailed();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiReceiver, intentFilter);
        if (wifiManager.startScan()) {
            showScanning();
            lastScan = new Date();
            rippleBackground.startRippleAnimation();
            fab.hide();
        } else {
            context.unregisterReceiver(wifiReceiver);
            scanFailed();
        }
    }

    private void scanDone() {
        scanResults = wifiManager.getScanResults();
        Log.d("WifiScan", scanResults.toString());

        for (ScanResult result : scanResults) {
            Log.d("WifiScan", result.SSID + " : " + result.level + " (Freq " + result.frequency + ")");
        }

        macs.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                rippleBackground.stopRippleAnimation();
                fab.show();
                showResults();
            }
        });
    }

    private void scanFailed() {
        scanResults = wifiManager.getScanResults();
        context.getNavi().snackbar(context.getString(R.string.wifi_scan_forbidden));
        showResults();
    }

    private void showScanning() {
        resultsView.setVisibility(View.GONE);
        rippleBackground.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        rippleBackground.setVisibility(View.GONE);
        resultsView.setVisibility(View.VISIBLE);
        updateState();
    }

    private void updateState() {
        lastState = WIFI_STATE.ERROR;
        int maxRSSI = -100;
        int warnRSSI = -85;
        int goodRSSI = -60;

        apMatches = new ArrayList<>();

        for (ScanResult r : scanResults) {
            if (r.SSID.equals(SSID)) {
                maxRSSI = Math.max(r.level, maxRSSI);
                matchAp(r);
            }
        }

        maxRSSI = Math.max(Math.min(maxRSSI, -50), -100);

        if (maxRSSI >= warnRSSI) {
            if (maxRSSI >= goodRSSI) {
                lastState = WIFI_STATE.GOOD;
            } else {
                lastState = WIFI_STATE.WARNING;
            }
        }


        Collections.sort(apMatches, new Comparator<ApMatch>() {
            @Override
            public int compare(ApMatch a, ApMatch b) {
                int rssiA = Math.max(a.rssi24, a.rssi50);
                int rssiB = Math.max(b.rssi24, b.rssi50);

                return rssiB - rssiA;
            }
        });

        displayResults();
    }

    private void matchAp(ScanResult result) {
        String mac = result.BSSID;
        String macId = ApMac.getMacId(mac);
        String ssid = result.SSID;
        String apName = getResources().getString(R.string.wifi_ap_unknown);
        int band = getBand(result);
        int channel = getChannel(result);
        int quality = rssiToQuality(result.level);
        int rssi = result.level;

        ApMac apMac = macs.matchAp(mac);

        if (apMac != null) {
            apName = apMac.getApName();
        }

        ApMatch apMatch = null;

        for (int i = 0; i < apMatches.size(); i++) {
            apMatch = apMatches.get(i);
            if (apMatch.macId.equals(macId) && apMatch.ssid.equals(ssid)) {
                // Already found: Update
                break;
            }
            apMatch = null;
        }

        // Not found: Insert new one
        if (apMatch == null) {
            apMatch = new ApMatch();
            apMatch.apName = apName.replace("_", " ");
            apMatch.macId = macId;
            apMatch.ssid = ssid;
            apMatches.add(apMatch);
        }

        if (band == 24) {
            apMatch.channel24 = channel;
            apMatch.enabled24 = true;
            apMatch.quality24 = quality;
            apMatch.rssi24 = rssi;
        } else if (band == 50) {
            apMatch.channel50 = channel;
            apMatch.enabled50 = true;
            apMatch.quality50 = quality;
            apMatch.rssi50 = rssi;
        }
    }

    private int getBand(ScanResult result) {
        if (result.frequency >= 2400 && result.frequency <= 2500) {
            return 24;
        } else if (result.frequency >= 5000 && result.frequency <= 6000) {
            return 50;
        }

        return 0;
    }

    private int getChannel(ScanResult result) {
        int baseFreq = 2412;
        int baseChannel = 1;
        int freqWidth = 5;
        if (getBand(result) == 24) {
            int freqDif = result.frequency - baseFreq;
            int channelDif = -1;
            if (freqDif >= 0) {
                channelDif = freqDif / freqWidth;
            }
            return baseChannel + channelDif;
        } else if (getBand(result) == 50) {
            baseFreq = 5170;
            baseChannel = 34;
            int freqDif = result.frequency - baseFreq;
            int channelDif = -34;
            if (freqDif >= 0) {
                channelDif = freqDif / freqWidth;
            }
            return baseChannel + channelDif;
        }

        return 0;
    }

    private int rssiToQuality(int rssi) {
        rssi = Math.max(Math.min(rssi, -50), -100);
        return (rssi + 100) * 2;
    }

    private void displayResults() {
        CircleImageView border = context.findViewById(R.id.wifi_status_border);
        ImageView symbol = context.findViewById(R.id.wifi_status);
        TextView statusTitle = context.findViewById(R.id.wifi_status_title);
        TextView statusSub = context.findViewById(R.id.wifi_status_sub);

        switch (lastState) {
            case PENDING:
                symbol.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.wifi_status_pending));
                border.setBorderColor(ContextCompat.getColor(context, R.color.wifiPending));
                statusTitle.setText(R.string.wifi_scan_pending);
                statusSub.setText(R.string.wifi_scan_pending_sub);
                break;
            case ERROR:
                symbol.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.wifi_status_error));
                border.setBorderColor(ContextCompat.getColor(context, R.color.wifiError));
                statusTitle.setText(R.string.wifi_scan_error);
                statusSub.setText(R.string.wifi_scan_error_sub);
                break;
            case WARNING:
                symbol.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.wifi_status_warning));
                border.setBorderColor(ContextCompat.getColor(context, R.color.wifiWarning));
                statusTitle.setText(R.string.wifi_scan_warning);
                statusSub.setText(R.string.wifi_scan_warning_sub);
                break;
            case GOOD:
                symbol.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.wifi_status_good));
                border.setBorderColor(ContextCompat.getColor(context, R.color.wifiGood));
                statusTitle.setText(R.string.wifi_scan_good);
                statusSub.setText(R.string.wifi_scan_good_sub);
                break;
        }

        LinearLayout apLayout = context.findViewById(R.id.wifi_status_aps);
        apLayout.removeAllViews();

        for (ApMatch match : apMatches) {
            View cardView = context.getLayoutInflater().inflate(R.layout.cardview_ap, null);
            TableRow row24 = cardView.findViewById(R.id.row_24);
            TableRow row50 = cardView.findViewById(R.id.row_50);
            TextView apName = cardView.findViewById(R.id.ap_name);
            TextView c24 = cardView.findViewById(R.id.channel24);
            TextView c50 = cardView.findViewById(R.id.channel50);
            TextView q24 = cardView.findViewById(R.id.quality24);
            TextView q50 = cardView.findViewById(R.id.quality50);

            apName.setText(match.apName);

            if (match.enabled24) {
                c24.setText("Ch. " + match.channel24);
                q24.setText(match.quality24 + " %");
            } else {
                row24.setVisibility(View.GONE);
            }

            if (match.enabled50) {
                c50.setText("Ch. " + match.channel50);
                q50.setText(match.quality50 + " %");
            } else {
                row50.setVisibility(View.GONE);
            }

            apLayout.addView(cardView);
        }
    }

    private void requestPermissions() {
        final String CoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        final String AccessWifi = Manifest.permission.ACCESS_WIFI_STATE;
        final String ChangeWifi = Manifest.permission.CHANGE_WIFI_STATE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(CoarseLocation) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
            if (context.checkSelfPermission(AccessWifi) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE}, 123);
            }
            if (context.checkSelfPermission(ChangeWifi) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE}, 123);
            }
        }

        if (!locationEnabled()) {
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private boolean checkPermissions() {
        final String CoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        final String AccessWifi = Manifest.permission.ACCESS_WIFI_STATE;
        final String ChangeWifi = Manifest.permission.CHANGE_WIFI_STATE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(CoarseLocation) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            if (context.checkSelfPermission(AccessWifi) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            if (context.checkSelfPermission(ChangeWifi) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return locationEnabled();
    }

    private boolean locationEnabled() {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled || network_enabled;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}

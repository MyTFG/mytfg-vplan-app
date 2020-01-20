package de.mytfg.apps.mytfg.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.firebase.FbApi;
import de.mytfg.apps.mytfg.objects.User;
import de.mytfg.apps.mytfg.tools.JsonFileManager;
import de.mytfg.apps.mytfg.tools.Settings;

public class SettingsFragment extends AuthenticationFragment {
    private View view;
    private MainActivity context;


    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        this.context = context;
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.settings_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_settings))
                .setExpandable(true, true);

        updateViews();

        return view;
    }

    private void updateViews() {
        CardView details = view.findViewById(R.id.account_loggedin);
        CardView login = view.findViewById(R.id.account_goto_login);
        CardView additional = view.findViewById(R.id.account_additional_card);


        TextView deviceId = view.findViewById(R.id.account_nerd_device);
        TextView firebaseToken = view.findViewById(R.id.account_nerd_fb);
        TextView fileSizes = view.findViewById(R.id.account_nerd_filesize);
        TextView cache = view.findViewById(R.id.account_nerd_cache);

        MyTFGApi api = new MyTFGApi(context);
        deviceId.setText(api.getDevice());
        deviceId.setTextIsSelectable(true);

        Settings settings = new Settings(context);
        String token = settings.getString("firebaseToken");
        firebaseToken.setText(token);
        firebaseToken.setTextIsSelectable(true);

        long sizeFiles = JsonFileManager.fileDirSize(context);
        long sizeCache = JsonFileManager.getFileSize(context.getCacheDir());

        fileSizes.setText(JsonFileManager.humanReadableByteCount(sizeFiles, true));
        cache.setText(JsonFileManager.humanReadableByteCount(sizeCache,true));

        if (api.isLoggedIn()) {
            login.setVisibility(View.GONE);
            details.setVisibility(View.VISIBLE);
            additional.setVisibility(View.VISIBLE);
            String device = api.getStoredDevice();
            String deviceName = api.getDeviceName();
            //long expires = api.getExpire();
            String expire = api.getExpireString();
            final User user = api.getUser();

            TextView username = view.findViewById(R.id.account_name);
            username.setText(user.getUsername());
            TextView text = view.findViewById(R.id.account_info_text);
            text.setText(Html.fromHtml(
                    String.format(getString(R.string.account_info_text),
                            user.getFirstname(),
                            user.getLastname(),
                            user.getUsername(),
                            device,
                            deviceName,
                            expire,
                            user.getLevel(),
                            user.getGrade()
                    )
            ));

            Button logoutbtn = view.findViewById(R.id.logout_button);
            logoutbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyTFGApi api = new MyTFGApi(context);


                    ApiParams params = new ApiParams();
                    api.addAuth(params);

                    api.logout(false);
                    api.clearAdditionalClasses();

                    context.getNavi().updateHeader();



                    CoordinatorLayout coordinatorLayout = context.findViewById(R.id.coordinator_layout);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout,
                                    getString(R.string.account_logged_out),
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();

                    api.call("api/auth/logout.x", params, new ApiCallback() {
                        @Override
                        public void callback(JSONObject result, int responseCode) {
                            if (responseCode != 200) {
                                context.getNavi().snackbar(getResources().getString(R.string.logout_failed));
                            }
                        }
                    });

                    updateViews();
                }
            });

            Button personalSettings = view.findViewById(R.id.personal_settings_button);
            personalSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.getNavi().toWebView(MyTFGApi.URL_SETTINGS, context);
                }
            });

            Button manageLogins = view.findViewById(R.id.authentications_button);
            manageLogins.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.getNavi().toWebView(MyTFGApi.URL_AUTHS, context);
                }
            });

            updateAdditionalClasses();
        } else {
            login.setVisibility(View.VISIBLE);
            details.setVisibility(View.GONE);
            additional.setVisibility(View.GONE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).getNavi().navigate(new LoginFragment(), R.id.fragment_container);
                }
            });
        }


        Button wlan = view.findViewById(R.id.account_wlan_connect);
        final EditText wlan_user_text = view.findViewById(R.id.account_wlan_username);
        final EditText wlan_user_pw = view.findViewById(R.id.account_wlan_password);

        wlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    String user = wlan_user_text.getText().toString();
                    String password = wlan_user_pw.getText().toString();
                    if (user.length() == 0 || password.length() == 0) {
                        context.getNavi().snackbar(context.getString(R.string.account_wlan_empty));
                    } else {
                        WifiConfiguration wfc = new WifiConfiguration();
                        wfc.SSID = "\"MyTFG\"";
                        wfc.status = WifiConfiguration.Status.DISABLED;
                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                        wfc.enterpriseConfig.setIdentity(user);
                        wfc.enterpriseConfig.setPassword(password);
                        wfc.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
                        wfc.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            wfc.enterpriseConfig.setDomainSuffixMatch("mytfg.de");
                        }


                        WifiManager wfMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        int code = wfMgr.addNetwork(wfc);
                        if (code != -1) {
                            context.getNavi().snackbar(context.getString(R.string.account_wlan_saved));
                            wlan_user_pw.setText("");
                            wlan_user_text.setText("");
                        } else {
                            context.getNavi().snackbar(context.getString(R.string.account_wlan_failed));
                        }
                    }
                } else {
                    context.getNavi().snackbar(context.getString(R.string.account_wlan_version));
                }
            }
        });

        updateSettings();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void showDialog(List<String> classes, List<String> preselect, final int max) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialog));
        // Set the dialog title
        builder.setTitle(R.string.account_additional_title);
        final String[] class_arr = new String[classes.size()];
        int x = 0;
        for (String str : classes) {
            class_arr[x] = str;
            x++;
        }
        final boolean[] selected = new boolean[class_arr.length];
        for (int i = 0; i < class_arr.length; ++i) {
            if (preselect.contains(class_arr[i])) {
                selected[i] = true;
            }
        }
        builder.setMultiChoiceItems(class_arr, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    selected[i] = true;
                    if (max >= 0) {
                        int count = 0;
                        for (boolean bool : selected) {
                            if (bool) {
                                count++;
                            }
                        }
                        if (count > max) {
                            selected[i] = false;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, false);
                        }
                    }
                } else {
                    selected[i] = false;
                }
            }
        });
        builder.setPositiveButton(R.string.account_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<String> cls = new LinkedList<>();
                for (int j = 0; j < class_arr.length; ++j) {
                    if (selected[j]) {
                        cls.add(class_arr[j]);
                    }
                }
                MyTFGApi api = new MyTFGApi(context);
                api.setAdditionalClasses(cls);
                dialogInterface.dismiss();
                updateAdditionalClasses();
            }
        });
        builder.setNegativeButton(R.string.account_discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void updateAdditionalClasses() {
        MyTFGApi api = new MyTFGApi(context);
        final User user = api.getUser();
        // Additional classes
        TextView explanation = view.findViewById(R.id.account_additional_explanation);
        TextView additional = view.findViewById(R.id.account_additional_classes);
        Button selectBtn = view.findViewById(R.id.account_additional_button);
        String additionals = TextUtils.join(", ", api.getAdditionalClasses());
        if (api.getAdditionalClasses().size() == 0) {
            additionals = getString(R.string.account_addional_classes_none);
        }
        switch (user.getRights()) {
            case 1:
                explanation.setText(Html.fromHtml(
                        String.format(getString(R.string.account_additional_explanation_pupil),
                                user.getGrade())
                ));
                selectBtn.setVisibility(View.VISIBLE);
                additional.setText(Html.fromHtml(
                        String.format(getString(R.string.account_addional_classes),
                                additionals
                        )
                ));
                break;
            case 2:
                explanation.setText(Html.fromHtml(
                        getString(R.string.account_additional_explanation_pupil)
                ));
                selectBtn.setVisibility(View.VISIBLE);
                additional.setText(Html.fromHtml(
                        String.format(getString(R.string.account_addional_classes),
                                additionals
                        )
                ));
                break;
            default:
                explanation.setText(Html.fromHtml(
                        String.format(getString(R.string.account_additional_explanation_highlevel),
                                user.getLevel())
                ));
                break;
        }
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = ProgressDialog.show(view.getContext(),
                        getString(R.string.please_wait),
                        getString(R.string.account_process),
                        true);
                final MyTFGApi api = new MyTFGApi(context);
                ApiParams params = new ApiParams();
                api.addAuth(params);
                api.call("api_vplan_classes", params, new ApiCallback() {
                    @Override
                    public void callback(JSONObject result, int responseCode) {
                        dialog.dismiss();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                            List<String> classes = new LinkedList<>();
                            try {
                                JSONArray extra_classes = result.getJSONArray("extra_classes");
                                String ownClass = user.getGrade().toLowerCase();
                                for (int i = 0; i < extra_classes.length(); ++i) {
                                    if (!extra_classes.get(i).toString().toLowerCase().equals(ownClass)) {
                                        classes.add(extra_classes.get(i).toString());
                                    }
                                }
                                showDialog(classes, api.getAdditionalClasses(), user.getRights() == 1 ? 3 : -1);
                            } catch (JSONException ex) {
                                CoordinatorLayout coordinatorLayout = context.getToolbarManager().coordinatorLayout();
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout,
                                                getString(R.string.api_response),
                                                Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        } else {
                            CoordinatorLayout coordinatorLayout = context.getToolbarManager().coordinatorLayout();
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout,
                                            getString(R.string.api_serverfault),
                                            Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }
        });
    }

    private void updateSettings() {
        Switch fulltext = view.findViewById(R.id.switch_fulltext);
        Switch toolbar = view.findViewById(R.id.switch_toolbar);
        Switch vplanNotifications = view.findViewById(R.id.switch_vplan_notifications);
        Switch mytfgNotifications = view.findViewById(R.id.switch_mytfg_notifications);
        Switch groupNotifications = view.findViewById(R.id.switch_group_mytfg_notifications);
        Spinner landing = view.findViewById(R.id.spinner_landing);
        Button testNotification = view.findViewById(R.id.button_test_notification);

        TextView test = view.findViewById(R.id.textview_test);

        final Settings settings = new Settings(context);

        fulltext.setChecked(settings.getBool("plan_fulltext"));
        fulltext.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.save("plan_fulltext", b);
            }
        });

        toolbar.setChecked(settings.getBool("hide_toolbar"));
        toolbar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.save("hide_toolbar", b);
            }
        });



        groupNotifications.setChecked(settings.getBool("group-mytfg-notifications"));
        groupNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.save("group-mytfg-notifications", b);
            }
        });

        vplanNotifications.setChecked(settings.getBool("vplan-notifications", true));
        vplanNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                settings.save("vplan-notifications", b);
                FbApi fbApi = new FbApi(context);
                fbApi.updateSubscription("vplan-notifications", b ? "subscribe" : "unsubscribe", new SuccessCallback() {
                    @Override
                    public void callback(boolean success) {
                        if (!success) {
                            context.getNavi().snackbar(getResources().getString(R.string.api_settings_error));
                        }
                    }
                });
            }
        });

        mytfgNotifications.setChecked(settings.getBool("mytfg-notifications"));
        mytfgNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                settings.save("mytfg-notifications", b);
                FbApi fbApi = new FbApi(context);
                fbApi.updateSubscription("mytfg-notifications", b ? "subscribe" : "unsubscribe", new SuccessCallback() {
                    @Override
                    public void callback(boolean success) {
                        if (!success) {
                            context.getNavi().snackbar(getResources().getString(R.string.api_settings_error));
                        }
                    }
                });
            }
        });

        testNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTFGApi api = new MyTFGApi(context);
                ApiParams params = new ApiParams();
                params.addParam("token", settings.getString("firebaseToken"));
                api.addAuth(params);
                api.call("api/firebase/validate", params, new ApiCallback() {
                    @Override
                    public void callback(JSONObject result, int responseCode) {
                        if (responseCode == 200) {
                            String error = result.optString("error", "");
                            boolean success = result.optBoolean("success", false);
                            if (success) {
                                context.getNavi().snackbar(getResources().getString(R.string.firebase_test_successful));
                            } else {
                                context.getNavi().snackbar(error);
                            }
                        } else {
                            context.getNavi().snackbar(getResources().getString(R.string.api_serverfault));
                        }
                    }
                });
            }
        });


        FbApi fbApi = new FbApi(context);
        fbApi.isSubscribed("vplan-notifications", vplanNotifications, new SuccessCallback() {
            @Override
            public void callback(boolean success) {

            }
        });
        fbApi.isSubscribed("mytfg-notifications", mytfgNotifications, new SuccessCallback() {
            @Override
            public void callback(boolean success) {

            }
        });


        String selection = settings.getString("landing_page");
        int selectedId = 0;
        String[] arr = getResources().getStringArray(R.array.settings_opt_landing_page_fragments);
        String[] names = getResources().getStringArray(R.array.settings_opt_landing_page_entries);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i].equals(selection)) {
                selectedId = i;
            }
            list.add(names[i]);
        }
        landing.setAdapter(new ArrayAdapter<String>(context, R.layout.spinner_item, list));
        landing.setSelection(selectedId);
        landing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settings.save("landing_page", getResources().getStringArray(R.array.settings_opt_landing_page_fragments)[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}

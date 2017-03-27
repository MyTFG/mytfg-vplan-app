package de.mytfg.apps.mytfg.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.User;
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
        CardView details = (CardView) view.findViewById(R.id.account_loggedin);
        CardView login = (CardView) view.findViewById(R.id.account_goto_login);
        CardView additional = (CardView) view.findViewById(R.id.account_additional_card);

        MyTFGApi api = new MyTFGApi(context);
        if (api.isLoggedIn()) {
            login.setVisibility(View.GONE);
            details.setVisibility(View.VISIBLE);
            additional.setVisibility(View.VISIBLE);
            String device = api.getStoredDevice();
            long expirets = api.getExpire();
            String expire = api.getExpireString();
            final User user = api.getUser();

            TextView username = (TextView) view.findViewById(R.id.account_name);
            username.setText(user.getUsername());
            TextView text = (TextView) view.findViewById(R.id.account_info_text);
            text.setText(Html.fromHtml(
                    String.format(getString(R.string.account_info_text),
                            user.getFirstname(),
                            user.getLastname(),
                            user.getUsername(),
                            device,
                            expire,
                            user.getLevel(),
                            user.getGrade()
                    )
            ));

            Button logoutbtn = (Button) view.findViewById(R.id.logout_button);
            logoutbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyTFGApi api = new MyTFGApi(context);
                    api.logout(false);
                    api.clearAdditionalClasses();


                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) context.findViewById(R.id.coordinator_layout);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout,
                                    getString(R.string.account_logged_out),
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                    updateViews();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        TextView explanation = (TextView)view.findViewById(R.id.account_additional_explanation);
        TextView additional = (TextView)view.findViewById(R.id.account_additional_classes);
        Button selectBtn = (Button)view.findViewById(R.id.account_additional_button);
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
        Switch fulltext = (Switch) view.findViewById(R.id.switch_fulltext);
        Switch newsbrowser = (Switch) view.findViewById(R.id.switch_news_browser);
        Spinner landing = (Spinner) view.findViewById(R.id.spinner_landing);

        final Settings settings = new Settings(context);

        fulltext.setChecked(settings.getBool("plan_fulltext"));
        fulltext.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.save("plan_fulltext", b);
            }
        });
        newsbrowser.setChecked(settings.getBool("news_browser"));
        newsbrowser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.save("news_browser", b);
            }
        });

        String selection = settings.getString("landing_page");
        int selectedId = 0;
        String[] arr = getResources().getStringArray(R.array.settings_opt_landing_page_fragments);
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i].equals(selection)) {
                selectedId = i;
                break;
            }
        }

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

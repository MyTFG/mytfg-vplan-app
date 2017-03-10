package de.mytfg.apps.mytfg.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.User;
import de.mytfg.apps.mytfg.toolbar.ToolbarManager;
import de.mytfg.apps.mytfg.tools.CustomViewTarget;
import de.mytfg.apps.mytfg.tools.ShowCaseManager;

public class LoginFragment extends AuthenticationFragment {
    private View view;


    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_login))
                .setExpandable(true, true);

        MyTFGApi api = new MyTFGApi(context);
        EditText username = (EditText) view.findViewById(R.id.login_username);
        username.setText(api.getUsername());

        TextView loginText = (TextView) view.findViewById(R.id.login_text);
        loginText.setText(Html.fromHtml(getString(R.string.login_text)));

        // Set Login Button listener
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText user = (EditText) context.findViewById(R.id.login_username);
                String login = user.getText().toString();
                EditText pw = (EditText) context.findViewById(R.id.login_password);
                String password = pw.getText().toString();

                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) context.findViewById(R.id.coordinator_layout);

                if (login.length() == 0 || password.length() == 0) {
                    // Show snackbar since user input is invalid
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout,
                                    getString(R.string.login_empty),
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    // Try login
                    final ProgressDialog dialog = ProgressDialog.show(view.getContext(),
                            getString(R.string.please_wait),
                            getString(R.string.login_process),
                            true);
                    final MyTFGApi api = new MyTFGApi(context);
                    ApiParams params = new ApiParams();
                    params.addParam("user", login);
                    params.addParam("password", password);
                    params.addParam("device", api.getDevice());
                    api.call("api_auth_login", params, new ApiCallback() {
                        @Override
                        public void callback(JSONObject result, int responseCode) {
                            dialog.dismiss();
                            String snackText;
                            if (responseCode == HttpsURLConnection.HTTP_OK) {
                                // Login worked
                                snackText = getString(R.string.login_successful);
                                // Save token, timeout, username and device
                                try {
                                    String uname = result.getString("username");
                                    String token = result.getString("token");
                                    String device = result.getString("device");
                                    long expire = result.getLong("expires");
                                    api.saveLogin(uname, token, device, expire);
                                    User user = new User(context);
                                    JSONObject jsonuser = result.getJSONObject("user");
                                    if (!user.load(jsonuser)) {
                                        Log.e("LOGIN", "Could not load user during login");
                                    }
                                    context.getNavi().clear();
                                    context.getNavi().navigate(new PlanFragment(), R.id.fragment_container);
                                } catch (JSONException ex) {
                                    snackText = getString(R.string.login_badresponse);
                                }
                            } else if (responseCode == -1) {
                                // Timeout
                                snackText = getString(R.string.login_timeout);
                            } else if (responseCode == 403) {
                                // Invalid login
                                snackText = getString(R.string.login_invalid);
                            } else if (responseCode == 400) {
                                // Invalid request
                                snackText = getString(R.string.login_badrequest);
                            } else {
                                // Server Error
                                snackText = getString(R.string.login_serverfault);
                            }
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout,
                                            snackText,
                                            Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        showcase();
        super.onResume();
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void showcase() {
        ShowCaseManager scm = new ShowCaseManager(getContext());

        ToolbarManager tbm = ((MainActivity)getContext()).getToolbarManager();

        scm.createChain(this)
                .add(new CustomViewTarget(tbm.getToolbar(), 50, 0, CustomViewTarget.Type.ABS_MID_L), R.string.sc_home_navi_title, R.string.sc_home_navi_text)
                .showChain();
    }
}

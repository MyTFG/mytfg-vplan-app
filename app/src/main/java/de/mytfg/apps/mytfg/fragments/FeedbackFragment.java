package de.mytfg.apps.mytfg.fragments;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.ApiCallback;
import de.mytfg.apps.mytfg.api.ApiParams;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.tools.JsonFileManager;

public class FeedbackFragment extends AuthenticationFragment {
    RatingBar ratingBar;
    EditText likeText;
    EditText improveText;
    EditText nameText;
    EditText mailText;

    public FeedbackFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        final MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setImage(R.drawable.feedback_header)
                .showBottomScrim()
                .setTitle(getString(R.string.menutitle_feedback))
                .setExpandable(true, true);

        ratingBar = (RatingBar) view.findViewById(R.id.feedback_rating);
        likeText = (EditText) view.findViewById(R.id.feedback_like);
        improveText = (EditText) view.findViewById(R.id.feedback_improve);
        nameText = (EditText) view.findViewById(R.id.feedback_name);
        mailText = (EditText) view.findViewById(R.id.feedback_mail);
        final Button sendButton = (Button) view.findViewById(R.id.feedback_button);

        JSONObject savedFeedback = JsonFileManager.read("feedback", context);
        if (savedFeedback != null) {
            likeText.setText(savedFeedback.optString("like", ""));
            nameText.setText(savedFeedback.optString("name", ""));
            mailText.setText(savedFeedback.optString("mail", ""));
            improveText.setText(savedFeedback.optString("improve", ""));
            ratingBar.setRating((float) savedFeedback.optDouble("rating", 0.0));
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Try login
                final ProgressDialog dialog = ProgressDialog.show(view.getContext(),
                        getString(R.string.please_wait),
                        getString(R.string.feedback_process),
                        true);
                MyTFGApi api = new MyTFGApi(context);

                final int rating = (int) ratingBar.getRating();
                String like = likeText.getText().toString();
                String improve = improveText.getText().toString();
                String name = nameText.getText().toString();
                String mail = mailText.getText().toString();

                String version;
                try {
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    version = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException ex) {
                    version = "Unknown";
                }

                ApiParams params = new ApiParams();
                params.addParam("rating", "" + rating);
                params.addParam("good", like);
                params.addParam("bad", improve);
                params.addParam("os", "Android");
                params.addParam("os_version", Build.VERSION.RELEASE);
                params.addParam("app_version", version);
                params.addParam("name", name);
                params.addParam("mail", mail);

                api.call("api_app_feedback", params, new ApiCallback() {
                    @Override
                    public void callback(JSONObject result, int responseCode) {
                        dialog.dismiss();
                        String message;
                        switch (responseCode) {
                            case 200:
                                message = getString(R.string.feedback_success);
                                ratingBar.setRating(1.0f);
                                likeText.setText("");
                                improveText.setText("");
                                break;
                            case 400:
                                message = getString(R.string.feedback_400);
                                break;
                            case 500:
                                message = getString(R.string.feedback_500);
                                break;
                            default:
                                message = getString(R.string.feedback_error);
                                break;
                        }
                        context.getNavi().snackbar(message);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        // Save contents to preferences
        JSONObject savedFeedback = new JSONObject();
        try {
            savedFeedback.put("like", likeText.getText().toString());
            savedFeedback.put("improve", improveText.getText().toString());
            savedFeedback.put("rating", (double) ratingBar.getRating());
            savedFeedback.put("name", nameText.getText().toString());
            savedFeedback.put("mail", mailText.getText().toString());
            JsonFileManager.write(savedFeedback, "feedback", getContext());
        } catch (JSONException ignored) {}
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}

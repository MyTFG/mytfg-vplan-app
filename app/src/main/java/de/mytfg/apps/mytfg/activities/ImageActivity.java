package de.mytfg.apps.mytfg.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import de.mytfg.apps.mytfg.R;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";

    private ImageView photoView;
    private String image = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        photoView = (ImageView) findViewById(R.id.photo_view);

        Intent intent = getIntent();
        if (intent.hasExtra("image")) {
            image = intent.getStringExtra("image");
            ViewCompat.setTransitionName(photoView, intent.getStringExtra("transition_name"));
        } else {
            if(savedInstanceState != null) {
                image = savedInstanceState.getString("image");
                ViewCompat.setTransitionName(photoView, savedInstanceState.getString("transition_name"));
            }
        }

        Log.d(TAG, "Showing image " + image);
        Log.d(TAG, "Using transition name " + ViewCompat.getTransitionName(photoView));

        if(image != null) {
            Picasso.with(this)
                    .load(image)
                    .error(R.drawable.download_error)
                    .into(photoView);
        } else {
            photoView.setImageResource(R.drawable.download_error);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (image != null) {
            outState.putString("image", image);
        }
        outState.putString("transition_name", ViewCompat.getTransitionName(photoView));
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}

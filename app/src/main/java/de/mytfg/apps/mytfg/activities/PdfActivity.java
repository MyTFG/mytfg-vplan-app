package de.mytfg.apps.mytfg.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.tools.FileDownloader;

/**
 * Displays PDFs inside the App
 */

public class PdfActivity extends AppCompatActivity {
    private String url;
    private PDFView pdfView;
    private ProgressBar progressBar;
    private byte[] imageBytes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("PDF", "create");
        setContentView(R.layout.activity_pdf);

        // Hide Status Bar if possible
        if (Build.VERSION.SDK_INT > 15) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        url = null;

        Intent intent = getIntent();
        if (intent.hasExtra("pdf_url")) {
            url = intent.getStringExtra("pdf_url");
        } else if (savedInstanceState != null && savedInstanceState.containsKey("pdf_url")) {
            url = savedInstanceState.getString("pdf_url");
        }

        Log.d("PDF", "URL: " + url);
        if (url == null) {
            // Close Activity, if no URL is given
            this.finish();
        }

        progressBar = (ProgressBar) findViewById(R.id.pdfLoader);
        pdfView = (PDFView) findViewById(R.id.pdfView);

        progressBar.setVisibility(View.VISIBLE);
        pdfView.setVisibility(View.GONE);

        // Load the PDF
        final FileDownloader fileDownloader = new FileDownloader(this);
        fileDownloader.download(url, new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                if (success) {
                    imageBytes = fileDownloader.getResult();
                    display();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pdf_url", url);
    }

    private void display() {
        pdfView.fromBytes(imageBytes)
                .enableSwipe(true)
                .enableAntialiasing(true)
                .enableDoubletap(true)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Log.d("PDF", "loaded");
                        pdfView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        pdfView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Log.d("PDF", "Error");
                        t.printStackTrace();
                        // "Die" without error message?!
                        finish();
                    }
                })
                .load();
    }
}

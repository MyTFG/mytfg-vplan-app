package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import de.mytfg.apps.mytfg.api.SimpleCallback;
import de.mytfg.apps.mytfg.api.SuccessCallback;

/**
 * Downloads Files in the Background
 */

public class FileDownloader {
    private Context context;
    private SuccessCallback callback;
    private byte[] result;

    public FileDownloader(Context c) {
        context = c;
    }

    public void download(String uri, SuccessCallback callback) {
        this.callback = callback;
        new DownloadFile().execute(uri);
    }

    public byte[] getResult() {
        return result;
    }

    private class DownloadFile extends AsyncTask<String, String, byte[]> {

        @Override
        protected byte[] doInBackground(String... strings) {
            String url = strings[0];

            return download(url);
        }

        @Override
        protected void onPostExecute(byte[] res) {
            result = res;
            callback.callback(res.length > 0);
        }

        private byte[] download(String url) {
            try {
                URL u = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                int nRead;

                byte[] data =  new byte[16384];
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                return buffer.toByteArray();
            } catch (Exception ex) {
                ex.printStackTrace();
                return new byte[0];
            }
        }
    }

}

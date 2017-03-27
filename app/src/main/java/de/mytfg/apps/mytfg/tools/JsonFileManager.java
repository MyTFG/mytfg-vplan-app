package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class JsonFileManager {
    public static JSONObject read(String filename, Context context) {
        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                String ret = stringBuilder.toString();

                return new JSONObject(ret);
            }
        } catch (Exception ex) {
        }
        return new JSONObject();
    }

    public static boolean write(JSONObject data, String filename, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data.toString());
            outputStreamWriter.close();
            return true;
        }
        catch (Exception e) {
            Log.e("JSON-Manager", e.getMessage());
        }
        return false;
    }

    public static boolean clear(String filename, Context context) {
        Log.d("Delete File", filename);
        return context.deleteFile(filename);
    }
}

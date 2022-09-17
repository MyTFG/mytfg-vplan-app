package de.mytfg.apps.mytfg;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.annotation.RequiresApi;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;


/**
 * The Application class. Handles ACRA.
 */

@ReportsCrashes(
        formUri = "https://acra.lbader.de/acra.php",
        //formUriBasicAuthLogin = "your username", // optional
        //formUriBasicAuthPassword = "your password",  // optional
        reportType = org.acra.sender.HttpSender.Type.JSON,
        sendReportsAtShutdown = false
)

public class MytfgVplan extends Application {
    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        NotificationChannel allChannel = new NotificationChannel(
                "channel_mytfg", "MyTFG Channel", NotificationManager.IMPORTANCE_HIGH);


        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .createNotificationChannel(allChannel);
    }
}

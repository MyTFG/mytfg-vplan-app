package de.mytfg.apps.mytfg;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by lbader on 03.12.16.
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
    }
}

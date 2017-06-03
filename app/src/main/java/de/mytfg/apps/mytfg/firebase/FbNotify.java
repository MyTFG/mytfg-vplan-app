package de.mytfg.apps.mytfg.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.tools.Settings;

public class FbNotify {
    private static int nextId = 0;

    public static void notifyVplan(Context c, String title, String text, String day, int nextId, Bundle extras) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        if (nextId == -1) {
            nextId = FbNotify.nextId;
        }

        Intent resultIntent = new Intent(c, MainActivity.class);
        resultIntent.putExtra("type", "vplan_update");
        resultIntent.putExtra("tab", "today".equals(day) ? 0 : 1);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        c,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(), R.mipmap.tfg2_round);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.tfg)
                .addExtras(extras)
                .setLargeIcon(icon)
                .setColor(c.getResources().getColor(R.color.accent))
                .setAutoCancel(true)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentText(text);
        notificationManager.notify(nextId, mBuilder.build());

        FbNotify.nextId++;
    }
}

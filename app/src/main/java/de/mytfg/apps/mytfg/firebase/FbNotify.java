package de.mytfg.apps.mytfg.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;

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
                        nextId,
                        resultIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(), R.mipmap.tfg2_round);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c, "channel_mytfg")
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

    public static void notifyMessage(Context c, String title, String text, int id, Bundle extras) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(c, MainActivity.class);
        resultIntent.putExtra("type", "message");
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("text", text);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        c,
                        id,
                        resultIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(), R.mipmap.tfg2_round);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c, "channel_mytfg")
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
        notificationManager.notify(id, mBuilder.build());

        FbNotify.nextId++;
    }

    public static void notifyMyTFGNotification(Context c, String title, String text, String url, int id, Bundle extras) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(c, MainActivity.class);
        resultIntent.putExtra("type", "open-webview");
        resultIntent.putExtra("url", url);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        c,
                        id,
                        resultIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(), R.mipmap.tfg2_round);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c, "channel_mytfg")
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
        notificationManager.notify(id, mBuilder.build());

        FbNotify.nextId++;
    }
}

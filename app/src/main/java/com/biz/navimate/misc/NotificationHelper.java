package com.biz.navimate.misc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.biz.navimate.R;
import com.biz.navimate.activities.AppLoadActivity;
import com.biz.navimate.constants.Constants;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class NotificationHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NOTIFICATION_HELPER";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public static NotificationChannel channel = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //

    public static void Notify(Context context, int id)
    {
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Create Notification Channel(only for API 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channel == null)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel("Navimate", "Navimate", importance);
            channel.setDescription("Channel Description");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            nManager.createNotificationChannel(channel);
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tv_title, Constants.Notification.NOTIFICATION_TITLES[id]);
        remoteViews.setTextViewText(R.id.notif_message, Constants.Notification.NOTIFICATION_MESSAGES[id]);

        // Set pending intent
        Intent appLoadIntent = new Intent(context, AppLoadActivity.class);
        appLoadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent appLoadPendingIntent = PendingIntent.getActivity(     context,
                id,
                appLoadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set Notification properties
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.mipmap.status_bar_icon);
        nBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        nBuilder.setLights(Color.BLUE, Constants.Notification.ledOnMs, Constants.Notification.ledOffMs);
        nBuilder.setVibrate(Constants.Notification.Vibrate);
        nBuilder.setContent(remoteViews);
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setContentIntent(appLoadPendingIntent);

        // Launch notification
        nManager.notify(id, nBuilder.build());

        // Play notification sound
        RingtoneHelper.PlayNotificationSound(context);
    }
    // ----------------------- Private APIs ----------------------- //
}
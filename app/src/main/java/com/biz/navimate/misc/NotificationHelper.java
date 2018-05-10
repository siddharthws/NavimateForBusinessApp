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

/**
 * Created by Siddharth on 01-10-2017.
 */

public class NotificationHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NOTIFICATION_HELPER";

    // Notification IDs for different notification Types
    public static final int TYPE_TASK_UPDATE         = 1;
    public static final int TYPE_TEMPLATE_UPDATE     = 2;
    public static final int TYPE_LEAD_UPDATE         = 3;
    public static final int TYPE_ACCOUNT_ADDED       = 4;
    public static final int NOTIFICATION_ID_GM       = 5;
    private static final int ledOnMs = 2000;
    private static final int ledOffMs = 500;
    private static final long Vibrate[] = {0, 300, 200, 500};
    public static NotificationChannel channel = null;
    // Strign messages for each notification message
    private static final String[] NOTIFICATION_MESSAGES = {
            "",
            "Your tasks have been updated...",
            "Your templates have been updated...",
            "Your leads have been updated...",
            "You have been added to a new account...",
    };

    private static final String[] NOTIFICATION_TITLES = {
            "",
            "Task Updated",
            "Template Updated",
            "Leads Updated",
            "Added to New Account",
    };

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //

    public static void Notify(Context context, int type)
    {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tv_title, NOTIFICATION_TITLES[type]);
        remoteViews.setTextViewText(R.id.notif_message, NOTIFICATION_MESSAGES[type]);
        // Set common properties
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.mipmap.status_bar_icon);
        nBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        nBuilder.setLights(Color.BLUE, ledOnMs, ledOffMs);
        nBuilder.setVibrate(Vibrate);
        nBuilder.setContent(remoteViews);
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        //nBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.appicon));

        // Set pending intent
        Intent appLoadIntent = new Intent(context, AppLoadActivity.class);
        appLoadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent appLoadPendingIntent = PendingIntent.getActivity(     context,
                type,
                appLoadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(appLoadPendingIntent);
        // Launch notification
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(type, nBuilder.build());

        // Play notification sound
        RingtoneHelper.PlayNotificationSound(context);
    }

     public static void GMNotification(Context context)
     {
         NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

         //Create Notification Channel(only for API 26 and above)
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channel == null)
         {
             int importance = NotificationManager.IMPORTANCE_HIGH;
             channel = new NotificationChannel("Navimate", "Navimate", importance);
             channel.setDescription("Channel Description");
             channel.enableLights(true);
             channel.setLightColor(Color.BLUE);
             channel.enableVibration(true);
             notificationManager.createNotificationChannel(channel);
         }
         //Create RemoteView for custom notification layout
         RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
         //remoteViews.setTextViewText(R.id.notif_title, "Good Morning!");

         //Set Intent and Pending Intent
         Intent gmIntent = new Intent(context, AppLoadActivity.class);
         gmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         PendingIntent gmPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID_GM, gmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

         //Create Notification Compat Builder object and setting common properties of the notification with it
         NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
         builder.setSmallIcon(R.drawable.animation_clock);
         builder.setLights(Color.BLUE, ledOnMs, ledOffMs);
         builder.setVibrate(Vibrate);
         builder.setAutoCancel(true);
         builder.setContent(remoteViews);
         builder.setContentIntent(gmPendingIntent);
         builder.setPriority(NotificationCompat.PRIORITY_HIGH);

         //Launch Notification
         notificationManager.notify(NOTIFICATION_ID_GM, builder.build());
     }
     // ----------------------- Private APIs ----------------------- //
}
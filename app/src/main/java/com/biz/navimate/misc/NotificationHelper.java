package com.biz.navimate.misc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

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

    // Strign messages for each notification message
    private static final String[] NOTIFICATION_MESSAGES = {
            "",
            "Your tasks have been updated...",
            "Your templates have been updated...",
            "Your leads have been updated...",
            "You have been added to a new account...",
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

        // Set common properties
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.mipmap.status_bar_icon);
        nBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        // Set specific properties for notification
        nBuilder.setContentText(NOTIFICATION_MESSAGES[type]);
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

    // ----------------------- Private APIs ----------------------- //
}

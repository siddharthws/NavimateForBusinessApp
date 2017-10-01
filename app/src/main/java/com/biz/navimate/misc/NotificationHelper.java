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
    public static final int NOTIFICATION_ID_TASKS            = 1;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //

    public static void Notify(Context context)
    {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        // Set common properties
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.mipmap.status_bar_icon);
        nBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        // Set specific properties for notification
        nBuilder.setContentText("You have new tasks.");
        //nBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.appicon));

        // Set pending intent
        Intent appLoadIntent = new Intent(context, AppLoadActivity.class);
        appLoadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent appLoadPendingIntent = PendingIntent.getActivity(     context,
                NOTIFICATION_ID_TASKS,
                appLoadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(appLoadPendingIntent);

        // Launch notification
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID_TASKS, nBuilder.build());

        // Play notification sound
        RingtoneHelper.PlayNotificationSound(context);
    }

    // ----------------------- Private APIs ----------------------- //
}

package com.biz.navimate.firebase;

import com.biz.navimate.activities.HomescreenActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.NotificationHelper;
import com.biz.navimate.server.SyncDbTask;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class FcmMessagingService extends    FirebaseMessagingService
                                 implements IfaceServer.SyncTasks {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FCM_MESSAGING_SEVICE";

    // Notification Keys passed by server in FCM initData field
    public static final String KEY_NOTIFICATION_TYPE = "type";

    // FCM Notification Types
    public static final int TYPE_TASK_UPDATE    = 1;

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public FcmMessagingService() {
        super();
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Get and validate notification initData
        Map<String, String> dataMap = remoteMessage.getData();
        if (dataMap == null) {
            Dbg.error(TAG, "Empty initData field in notification");
            return;
        }

        // ParseToObject initData
        int notificationType = Integer.parseInt(dataMap.get(KEY_NOTIFICATION_TYPE));

        // Temp Hack to service new types of notification
        ServiceFcm(notificationType);

        return;
    }

    @Override
    public void onTaskCompleted() {
        // Update homescreen list
        HomescreenActivity.Refresh();

        // Send notification
        NotificationHelper.Notify(this);
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void ServiceFcm(int notificationType) {
        switch (notificationType) {
            case TYPE_TASK_UPDATE: {
                ServiceTaskUpdateNotification();
                break;
            }
            default: {
                Dbg.error(TAG, "Unknown FCM Message type");
            }
        }
    }

    private void ServiceTaskUpdateNotification() {
        if (App.IsInitialized()) {
            // sync DB from server
            SyncDbTask syncDb = new SyncDbTask(this, true);

            // Set listener for receiving result
            syncDb.SetListener(this);

            // Execute task
            syncDb.execute();
        }
    }
}

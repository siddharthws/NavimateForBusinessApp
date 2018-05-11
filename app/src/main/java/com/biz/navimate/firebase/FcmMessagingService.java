package com.biz.navimate.firebase;

import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.NotificationHelper;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.server.SyncDbTask;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class FcmMessagingService extends    FirebaseMessagingService {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FCM_MESSAGING_SEVICE";

    // Notification Keys passed by server in FCM initData field
    public static final String KEY_NOTIFICATION_TYPE = "type";

    // FCM Notification Types
    public static final int TYPE_TASK_UPDATE            = 1;
    public static final int TYPE_TEMPLATE_UPDATE        = 2;
    public static final int TYPE_LEAD_UPDATE            = 3;
    public static final int TYPE_ACCOUNT_ADDED          = 4;
    public static final int TYPE_ACCOUNT_REMOVED        = 5;

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

        // Service notifications
        switch (notificationType) {
            case TYPE_TASK_UPDATE: {
                ServiceTaskUpdate();
                break;
            }
            case TYPE_TEMPLATE_UPDATE: {
                ServiceTemplateUpdate();
                break;
            }
            case TYPE_LEAD_UPDATE: {
                ServiceLeadUpdate();
                break;
            }
            case TYPE_ACCOUNT_ADDED: {
                ServiceAccountAdded();
                break;
            }
            case TYPE_ACCOUNT_REMOVED: {
                ServiceAccountRemoved();
                break;
            }
        }

        return;
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Methods to service different types of notifications
    private void ServiceTaskUpdate() {
        // Sync Data if app is initialized
        if (App.IsInitialized()) {
            // sync DB from server
            SyncDbTask syncTask = new SyncDbTask(this, false);

            // Execute task
            syncTask.execute();
        }

        // Send notification to user
        NotificationHelper.Notify(this, Constants.Notification.ID_TASK_UPDATE);
    }

    private void ServiceTemplateUpdate() {
        // Sync Data if app is initialized
        if (App.IsInitialized()) {
            // sync DB from server
            SyncDbTask syncTask = new SyncDbTask(this, false);

            // Execute task
            syncTask.execute();
        }

        // Send notification to user
        NotificationHelper.Notify(this, Constants.Notification.ID_TEMPLATE_UPDATE);
    }

    private void ServiceLeadUpdate() {
        // Sync Data if app is initialized
        if (App.IsInitialized()) {
            // sync DB from server
            SyncDbTask syncTask = new SyncDbTask(this, false);

            // Execute task
            syncTask.execute();
        }

        // Send notification to user
        NotificationHelper.Notify(this, Constants.Notification.ID_LEAD_UPDATE);
    }

    private void ServiceAccountAdded() {
        if (App.IsInitialized()) {
            // Sync Data from server
            SyncDbTask syncTask = new SyncDbTask(this, false);
            syncTask.execute();
        }

        // Send notification to user
        NotificationHelper.Notify(this, Constants.Notification.ID_ACCOUNT_ADDED);
    }

    private void ServiceAccountRemoved() {
        // Init Database if it is not initialized
        if (!App.IsInitialized()) {
            DbHelper.Init(this);
        }

        // Clear all tables
        DbHelper.formTable.Clear();
        DbHelper.taskTable.Clear();
        DbHelper.leadTable.Clear();
        DbHelper.templateTable.Clear();
        DbHelper.fieldTable.Clear();

        // Clear Preferences
        Preferences.SetTaskSyncTime(this, 0);
    }
}

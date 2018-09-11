package com.biz.navimate.interfaces;

import com.biz.navimate.objects.ObjProduct;
import com.biz.navimate.objects.User;
import com.biz.navimate.objects.core.ObjNvm;
import com.biz.navimate.objects.core.ObjNvmCompact;

import java.util.ArrayList;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class IfaceServer {
    // GetProfile Task interface
    public interface GetProfile {
        void onProfileReceived(User user);
        void onProfileFailed();
    }

    // SyncTasks Task interface
    public interface SyncTasks {
        void onTaskCompleted();
    }

    // SyncForms Task interface
    public interface SyncForms {
        void onFormsSynced();
    }

    // OtpSms Task interface
    public interface OtpSms {
        void onSmsSuccess();
        void onSmsFailure();
    }

    // OtpSms Task interface
    public interface SubmitForm {
        void onFormSubmitted();
        void onFormSubmitFailed();
    }

    // Update FCM Task interface
    public interface UpdateFcm {
        void onFcmUpdated();
        void onFcmFailed();
    }

    // Update Name Task interface
    public interface UpdateName {
        void onNameUpdated();
        void onNameFailed();
    }

    // Register Task Interface
    public interface Register {
        void onRegisterSuccess();
    }

    // Upload Photo Task interface
    public interface UploadPhoto {
        void onPhotoUploaded(String fielname);
        void onPhotoUploadFailed();
    }

    // Check Updates Task interface
    public interface CheckUpdates {
        void exitApp();
        void onUpdateRequired();
        void onUpdateNotRequired();
    }

    // Check LocReport Sync Task interface
    public interface SyncLocReport {
        void onLocReportSynced();
        void onLocReportSyncFailed();
    }

    // Get Product Task interface
    public interface GetProduct {
        void onProductReceived(ObjProduct product);
        void onProductFailed();
    }

    // Get Product List Task interface
    public interface GetObjectList {
        void onObjectListSuccess(ArrayList<ObjNvmCompact> objects, int totalCount);
        void onObjectListFailed();
    }

    // Get Product List Task interface
    public interface GetObjectDetails {
        void onObjectDetailsSuccess(ObjNvm obj);
        void onObjectDetailsFailed();
    }
}

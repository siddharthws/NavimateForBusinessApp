package com.biz.navimate.interfaces;

import com.biz.navimate.objects.User;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class IfaceServer {
    // GetProfile Task interface
    public interface GetProfile {
        void onProfileReceived(User user);
        void onProfileFailed();
    }

    // GetTasks Task interface
    public interface GetTasks {
        void onTasksSuccess();
        void onTasksFailed();
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
        void onUpdateRequired();
        void onUpdateNotRequired();
    }
}

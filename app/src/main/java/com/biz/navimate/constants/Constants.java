package com.biz.navimate.constants;

/**
 * Created by Siddharth on 24-09-2017.
 */

public class Constants {
    // General App Constants
    public static class App {
        // Toggle debug mode
        public static final Boolean DEBUG        = true;
    }

    // Request codes for activity results
    public static class RequestCodes {
        public static final int INVALID         = -1;
        public static final int REGISTRATION    = 1;
        public static final int GPS             = 2;
        public static final int LEAD_PICKER     = 3;
        public static final int ZXING           = 4;
        public static final int PHOTO           = 5;
        public static final int SIGNATURE       = 6;
    }

    // Map related constants
    public class Extras {
        // Activity Result Extras
        public static final String LEAD_PICKER          = "leadPicker";
        public static final String SIGNATURE_IMAGE_PATH = "signature_image_path";
    }

    // User Preferences related constants
    public static class Preferences {
        // Shared Preferences Settings File
        public static final String PREF_FILE        = "user_prefs";

        // Shared Preferences Keys
        public static final String KEY_NAME         = "name";
        public static final String KEY_PHONE        = "phone";
        public static final String KEY_EMAIL        = "email";
        public static final String KEY_APP_ID       = "app_id";
        public static final String KEY_LATITUDE     = "latitude";
        public static final String KEY_LONGITUDE    = "longitude";
    }

    // Server related constants
    public static class Server {
        // URLs
        private static final String URL_BASE        = "http://192.168.1.10:8080";
        public static final String URL_GET_PROFILE  = URL_BASE + "/api/reps/profile";
        public static final String URL_GET_TASKS    = URL_BASE + "/api/reps/task";
        public static final String URL_OTP_SMS      = URL_BASE + "/api/reps/otp";
        public static final String URL_UPDATE_FCM   = URL_BASE + "/api/reps/fcm";
        public static final String URL_REGISTER     = URL_BASE + "/api/reps/register";
        public static final String URL_TRACK        = URL_BASE + "/api/track/data";
        public static final String URL_UPLOAD_PHOTO = URL_BASE + "/api/photos/upload";
        public static final String URL_CHECK_UPDATE = URL_BASE + "/api/app/update";

        // JSON Keys
        public static final String KEY_ID               = "id";
        public static final String KEY_NAME             = "name";
        public static final String KEY_PHONE            = "phoneNumber";
        public static final String KEY_EMAIL            = "email";
        public static final String KEY_MESSAGE          = "message";
        public static final String KEY_TASKS            = "tasks";
        public static final String KEY_LEAD             = "lead";
        public static final String KEY_TEMPLATE         = "template";
        public static final String KEY_TITLE            = "title";
        public static final String KEY_DESCRIPTION      = "description";
        public static final String KEY_ADDRESS          = "address";
        public static final String KEY_LATITUDE         = "latitude";
        public static final String KEY_LONGITUDE        = "longitude";
        public static final String KEY_DATA             = "data";
        public static final String KEY_TASK_ID          = "taskId";
        public static final String KEY_CLOSE_TASK       = "closeTask";
        public static final String KEY_FCM              = "fcmId";
        public static final String KEY_SPEED            = "speed";
        public static final String KEY_FILENAME         = "filename";
        public static final String KEY_VERSION_CODE     = "versionCode";
        public static final String KEY_UPDATE_REQUIRED  = "updateRequired";
    }

    // Map related constants
    public class Map {
        // Zoom levels
        public static final int MIN_ZOOM = 14;
    }
}

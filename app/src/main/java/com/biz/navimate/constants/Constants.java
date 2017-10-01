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
    }

    // Server related constants
    public static class Server {
        // URLs
        private static final String URL_BASE = "http://192.168.1.10:8080";
        public static final String URL_GET_PROFILE = URL_BASE + "/api/reps/profile";
        public static final String URL_GET_TASKS = URL_BASE + "/api/reps/task";
        public static final String URL_OTP_SMS = URL_BASE + "/api/reps/otp";

        // JSON Keys
        public static final String KEY_ID = "id";
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
    }

    // Map related constants
    public class Map {
        // Zoom levels
        public static final int MIN_ZOOM = 14;
    }
}

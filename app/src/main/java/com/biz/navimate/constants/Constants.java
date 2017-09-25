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

        // JSON Keys
        public static final String KEY_ID = "id";
        public static final String KEY_NAME    = "name";
        public static final String KEY_PHONE   = "phoneNumber";
        public static final String KEY_EMAIL   = "email";
    }
}

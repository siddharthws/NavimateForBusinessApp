package com.biz.navimate.constants;

/**
 * Created by Siddharth on 24-09-2017.
 */

public class Constants {
    // General App Constants
    public static class App {
        // Toggle debug mode
        public static final Boolean DEBUG        = false;
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
        public static final int PHOTO_EDITOR    = 7;
        public static final int PHOTO_CROP      = 8;
    }

    // Map related constants
    public class Extras {
        // Activity Result Extras
        public static final String LEAD_PICKER          = "leadPicker";
        public static final String IMAGE_NAME           = "imageName";
        public static final String SIGNATURE_IMAGE_PATH = "signature_image_path";
    }

    // User Preferences related constants
    public static class Preferences {
        // Shared Preferences Settings File
        public static final String PREF_FILE        = "user_prefs";

        // Shared Preferences Keys
        public static final String KEY_NAME                     = "name";
        public static final String KEY_PHONE                    = "phone";
        public static final String KEY_EMAIL                    = "email";
        public static final String KEY_APP_ID                   = "app_id";
        public static final String KEY_LATITUDE                 = "latitude";
        public static final String KEY_LONGITUDE                = "longitude";
        public static final String KEY_TIMESTAMP                = "timestamp";
        public static final String KEY_MAP_TYPE                 = "pref_map_type";
        public static final String KEY_MAP_TRAFFIC_OVERLAY      = "pref_map_traffic_overlay";
        public static final String KEY_START_TIME               = "startHr";
        public static final String KEY_END_TIME                 = "endHr";
        public static final String KEY_TASK_SYNC_TIME           = "taskSyncTime";
        public static final String KEY_LEAD_SYNC_TIME           = "leadSyncTime";
        public static final String KEY_TEMPLATE_SYNC_TIME       = "templateSyncTime";
    }

    // Template related constants
    public static class Template {
        // Template Types
        public static final int TYPE_FORM = 1;
        public static final int TYPE_LEAD = 2;
        public static final int TYPE_TASK = 3;

        // Field Types in templates
        public static final int FIELD_TYPE_NONE           = 0;
        public static final int FIELD_TYPE_TEXT           = 1;
        public static final int FIELD_TYPE_NUMBER         = 2;
        public static final int FIELD_TYPE_RADIOLIST      = 3;
        public static final int FIELD_TYPE_CHECKLIST      = 4;
        public static final int FIELD_TYPE_PHOTO          = 5;
        public static final int FIELD_TYPE_SIGN           = 6;
        public static final int FIELD_TYPE_LOCATION       = 7;
        public static final int FIELD_TYPE_CHECKBOX       = 8;
        public static final int FIELD_TYPE_DATE           = 9;
    }

    // Server related constants
    public static class Server {
        // URLs
        //private static final String URL_BASE            = "http://192.168.1.10:8080";     // Debug
        private static final String URL_BASE            = "https://biz.navimateapp.com";       // Release

        // Http URLs
        public static final String URL_GET_PROFILE      = URL_BASE + "/api/reps/profile";
        public static final String URL_OTP_SMS          = URL_BASE + "/api/reps/otp";
        public static final String URL_UPDATE_FCM       = URL_BASE + "/api/reps/fcm";
        public static final String URL_UPDATE_NAME      = URL_BASE + "/api/reps/name";
        public static final String URL_REGISTER         = URL_BASE + "/api/reps/register";
        public static final String URL_UPLOAD_PHOTO     = URL_BASE + "/api/photos/upload";
        public static final String URL_CHECK_UPDATE     = URL_BASE + "/api/app/update";
        public static final String URL_SYNC             = URL_BASE + "/api/reps/sync";
        public static final String URL_SYNC_FORMS       = URL_BASE + "/api/reps/sync/forms";
        public static final String URL_ADD_TASK         = URL_BASE + "/api/reps/addTask";
        public static final String URL_ACRA             = URL_BASE + "/api/app/acra";
        public static final String URL_APP_START        = URL_BASE + "/api/reps/appStart";
        public static final String URL_LOCATION_REPORT  = URL_BASE + "/api/reps/locationReport";

        // Stomp URLs
        public static final String URL_STOMP            = URL_BASE + "/ws-endpoint/websocket";

        // Sending channels
        public static final String URL_HEARTBEAT        = "/rxc/heart-beat";
        public static final String URL_TRACKING_UPDATE  = "/rxc/tracking-update";

        // Subsciprtion channels
        public static final String URL_START_TRACKING   = "/user/txc/start-tracking";
        public static final String URL_STOP_TRACKING    = "/user/txc/stop-tracking";

        // JSON Keys

        // ID Keys for different object types
        public static final String KEY_LEAD_ID          = "leadId";
        public static final String KEY_TASK_ID          = "taskId";
        public static final String KEY_TEMPLATE_ID      = "templateId";
        public static final String KEY_FIELD_ID         = "fieldId";
        public static final String KEY_FIELD_IDS        = "fieldIds";
        public static final String KEY_VALUE_IDS        = "valueIds";
        public static final String KEY_FORM_TEMPLATE_ID = "formTemplateId";
        public static final String KEY_DEFAULT_DATA_ID  = "defaultDataId";

        // Keys for data arrays sent by / to server
        public static final String KEY_SYNC_DATA        = "syncData";
        public static final String KEY_TASKS            = "tasks";
        public static final String KEY_LEADS            = "leads";
        public static final String KEY_LEAD             = "lead";
        public static final String KEY_TEMPLATES        = "templates";
        public static final String KEY_FORMS            = "forms";
        public static final String KEY_FIELDS           = "fields";
        public static final String KEY_DATA             = "data";
        public static final String KEY_TEMPLATE_DATA    = "templateData";
        public static final String KEY_VALUES           = "values";

        // Task related keys
        public static final String KEY_STATUS           = "status";

        // Lead related keys
        public static final String KEY_TITLE            = "title";
        public static final String KEY_DESCRIPTION      = "description";
        public static final String KEY_ADDRESS          = "address";
        public static final String KEY_LATITUDE         = "latitude";
        public static final String KEY_LONGITUDE        = "longitude";
        public static final String KEY_LAT              = "lat";
        public static final String KEY_LNG              = "lng";

        // Form related keys
        public static final String KEY_CLOSE_TASK       = "closeTask";
        public static final String KEY_TIMESTAMP        = "timestamp";

        // Field related keys
        public static final String KEY_TYPE             = "type";
        public static final String KEY_IS_MANDATORY     = "isMandatory";

        // Value related keys
        public static final String KEY_VALUE            = "value";

        // Misc Keys
        public static final String KEY_ID               = "id";
        public static final String KEY_VERSION          = "ver";
        public static final String KEY_NAME             = "name";
        public static final String KEY_PHONE            = "phoneNumber";
        public static final String KEY_EMAIL            = "email";
        public static final String KEY_VERSION_CODE     = "versionCode";
        public static final String KEY_UPDATE_REQUIRED  = "updateRequired";
        public static final String KEY_MANDATORY_UPDATE = "mandatoryUpdate";
        public static final String KEY_FCM              = "fcmId";
        public static final String KEY_SPEED            = "speed";
        public static final String KEY_MESSAGE          = "message";
        public static final String KEY_ERROR_CODE       = "errorCode";
        public static final String KEY_DATA_ID          = "dataId";
        public static final String KEY_REMOVED_IDS      = "removedIds";
        public static final String KEY_REPORT           = "report";
        public static final String KEY_REMOVE           = "remove";
        public static final String KEY_LAST_SYNC_TIME   = "lastSyncTime";
    }

    // Location related constants
    public class Location {
        // Initilization Error Codes
        public static final int ERROR_NO_CLIENTS                = 1;
        public static final int ERROR_NO_PERMISSION             = 2;
        public static final int ERROR_NO_GPS                    = 3;
        public static final int ERROR_UNAVAILABLE               = 4;
        public static final int ERROR_UNKNOWN                   = 5;
        public static final int ERROR_UPDATES_ERROR             = 6;
        public static final int ERROR_API_CLIENT                = 7;
        public static final int ERROR_CURRENT_LOC_UNAVAILABLE   = 8;

        // Client Tags
        public static final int CLIENT_TAG_MAP          = 1;
        public static final int CLIENT_TAG_TRACKER      = 2;
        public static final int CLIENT_TAG_INIT         = 3;
        public static final int CLIENT_TAG_SUBMIT_FORM  = 4;
        public static final int CLIENT_TAG_LOC_REPORT   = 5;
    }

    // Map related constants
    public class Map {
        // Zoom levels
        public static final int MIN_ZOOM = 14;
    }

    // Tracker Service related Constants
    public class Tracker {
        // Error codes
        public static final int ERROR_NONE              = 0;
        public static final int ERROR_IDLE              = 1;
        public static final int ERROR_WAITING           = 2;
        public static final int ERROR_NO_UPDATES        = 3;
        public static final int ERROR_NO_GPS            = 4;
        public static final int ERROR_NO_PERMISSION     = 5;
        public static final int ERROR_OFFLINE           = 6;
    }

    // Miscellaneous Constants
    public class Misc {
        // Invalid ID
        public static final long ID_INVALID = -1;
    }

    public class Date {
        // Date Formats
        public static final String FORMAT_BACKEND   = "yyyy-MM-dd HH:mm:ss";
        public static final String FORMAT_FRONTEND  = "dd-MM-yyyy";
    }


    public static class Notification {
        //Notification IDs
        public static final int ID_TASK_UPDATE         = 1;
        public static final int ID_TEMPLATE_UPDATE     = 2;
        public static final int ID_LEAD_UPDATE         = 3;
        public static final int ID_ACCOUNT_ADDED       = 4;
        public static final int ID_GOOD_MORNING        = 5;

        //Misc. Notification controls
        public static final int ledOnMs = 2000;
        public static final int ledOffMs = 500;
        public static final long Vibrate[] = {0, 300, 200, 500};

        //Notification Messages
        public static final String[] NOTIFICATION_MESSAGES = {
                "",
                "Tap here to open the App",
                "Tap here to open the App",
                "Tap here to open the App",
                "Tap here to open the App",
                "It's time to start work !"
        };

        //Notification Titles
        public static final String[] NOTIFICATION_TITLES = {
                "",
                "Task Updated",
                "Template Updated",
                "Leads Updated",
                "Added to New Account",
                "Good Morning"
        };
    }
}
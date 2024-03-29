package com.biz.navimate.constants;

import java.text.SimpleDateFormat;

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
        public static final int PHOTO_EDITOR    = 7;
        public static final int PHOTO_CROP      = 8;
        public static final int PHOTO_DRAW      = 9;
        public static final int FILE_PICKER     = 10;
        public static final int PLACE_PICKER    = 11;
        public static final int OBJECT_PICKER   = 12;
    }

    // Map related constants
    public class Extras {
        // Activity Result Extras
        public static final String LEAD_PICKER          = "leadPicker";
        public static final String PICKED_OBJECT        = "pickedObject";
        public static final String IMAGE_NAME           = "imageName";
        public static final String IMAGE_PATH           = "imagepath";
        public static final String SIGNATURE_IMAGE_PATH = "signature_image_path";
        public static final String TYPE                 = "extra_type";
        public static final String MODE                 = "mode";
        public static final String ID                   = "id";
        public static final String IS_EDITABLE          = "is_editable";
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
        public static final String KEY_IS_TRACKING              = "is_tracking";
    }

    // Database related constants
    public static class DB {
        // Column Titles
        public static final String COLUMN_ID                = "_id";
        public static final String COLUMN_SRV_ID            = "server_id";
        public static final String COLUMN_TEMPLATE_ID       = "template_id";
        public static final String COLUMN_OWNER_ID          = "owner_id";
        public static final String COLUMN_TASK_ID           = "task_id";
        public static final String COLUMN_PRODUCT_ID        = "product_id";
        public static final String COLUMN_PUBLIC_ID         = "public_id";
        public static final String COLUMN_LEAD_ID           = "lead_id";
        public static final String COLUMN_FORM_TEMPLATE_ID  = "form_template_id";
        public static final String COLUMN_FIELD_IDS         = "fieldIds";
        public static final String COLUMN_DIRTY             = "dirty";
        public static final String COLUMN_TITLE             = "title";
        public static final String COLUMN_ADDRESS           = "address";
        public static final String COLUMN_LATITUDE          = "latitude";
        public static final String COLUMN_LONGITUDE         = "longitude";
        public static final String COLUMN_VALUES            = "_values";
        public static final String COLUMN_VALUE             = "value";
        public static final String COLUMN_TYPE              = "type";
        public static final String COLUMN_IS_MANDATORY      = "isMandatory";
        public static final String COLUMN_CLOSE_TASK        = "close_task";
        public static final String COLUMN_TIMESTAMP         = "timestamp";
        public static final String COLUMN_STATUS            = "status";
        public static final String COLUMN_BATTERY           = "battery";
        public static final String COLUMN_SPEED             = "speed";
        public static final String COLUMN_NAME              = "name";
    }

    // Template related constants
    public static class Template {
        // Template Types
        public static final int TYPE_INVALID    = 0;
        public static final int TYPE_FORM       = 1;
        public static final int TYPE_LEAD       = 2;
        public static final int TYPE_TASK       = 3;
        public static final int TYPE_PRODUCT    = 4;

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
        public static final int FIELD_TYPE_FILE           = 10;
        public static final int FIELD_TYPE_PRODUCT        = 11;
        public static final int FIELD_TYPE_INVOICE        = 12;
    }

    // Server related constants
    public static class Server {
        // URLs
        //private static final String URL_BASE            = "http://192.168.1.10:8080";     // Debug
        private static final String URL_BASE            = "https://biz.navimateapp.com";       // Release

        // Http URLs
        public static final String URL_GET_PROFILE          = URL_BASE + "/api/reps/profile";
        public static final String URL_OTP_SMS              = URL_BASE + "/api/reps/otp";
        public static final String URL_UPDATE_FCM           = URL_BASE + "/api/reps/fcm";
        public static final String URL_UPDATE_NAME          = URL_BASE + "/api/reps/name";
        public static final String URL_REGISTER             = URL_BASE + "/api/reps/register";
        public static final String URL_UPLOAD_PHOTO         = URL_BASE + "/api/photos/upload";
        public static final String URL_UPLOAD_FILE          = URL_BASE + "/api/photos/uploadFile";
        public static final String URL_CHECK_UPDATE         = URL_BASE + "/api/app/update";
        public static final String URL_SYNC                 = URL_BASE + "/api/reps/sync";
        public static final String URL_SYNC_FORMS           = URL_BASE + "/api/reps/sync/forms";
        public static final String URL_ADD_TASK             = URL_BASE + "/api/reps/addTask";
        public static final String URL_EDIT_LEADS           = URL_BASE + "/api/reps/leads/edit";
        public static final String URL_ACRA                 = URL_BASE + "/api/app/acra";
        public static final String URL_APP_START            = URL_BASE + "/api/reps/appStart";
        public static final String URL_LOCATION_REPORT      = URL_BASE + "/api/reps/locationReport";
        public static final String URL_GET_PRODUCT          = URL_BASE + "/api/reps/product/id";
        public static final String URL_GET_OBJECT_LIST      = URL_BASE + "/api/reps/objects/search";
        public static final String URL_GET_OBJECT_DETAILS   = URL_BASE + "/api/reps/objects/details";
        public static final String URL_GET_FILE             = URL_BASE + "/api/files/get";

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
        public static final String KEY_PRODUCT_ID       = "productId";
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
        public static final String KEY_TASK             = "task";
        public static final String KEY_LEADS            = "leads";
        public static final String KEY_LEAD             = "lead";
        public static final String KEY_TEMPLATES        = "templates";
        public static final String KEY_FORMS            = "forms";
        public static final String KEY_FIELDS           = "fields";
        public static final String KEY_DATA             = "data";
        public static final String KEY_TEMPLATE_DATA    = "templateData";
        public static final String KEY_VALUES           = "values";
        public static final String KEY_OWNER            = "owner";

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
        public static final String KEY_SETTINGS         = "settings";
        public static final String KEY_B_MANDATORY      = "bMandatory";

        // Value related keys
        public static final String KEY_VALUE            = "value";

        // Product related keys
        public static final String KEY_PAGER            = "pager";
        public static final String KEY_RESULTS           = "results";
        public static final String KEY_TOTAL_COUNT      = "totalCount";
        public static final String KEY_COUNT            = "count";
        public static final String KEY_START            = "start";
        public static final String KEY_TEXT             = "text";

        // Misc Keys
        public static final String KEY_ID               = "id";
        public static final String KEY_IDS              = "ids";
        public static final String KEY_VERSION          = "ver";
        public static final String KEY_NAME             = "name";
        public static final String KEY_PHONE            = "phoneNumber";
        public static final String KEY_EMAIL            = "email";
        public static final String KEY_VERSION_CODE     = "versionCode";
        public static final String KEY_UPDATE_REQUIRED  = "updateRequired";
        public static final String KEY_MANDATORY_UPDATE = "mandatoryUpdate";
        public static final String KEY_FCM              = "fcmId";
        public static final String KEY_SPEED            = "speed";
        public static final String KEY_BATTERY          = "battery";
        public static final String KEY_MESSAGE          = "message";
        public static final String KEY_ERROR_CODE       = "errorCode";
        public static final String KEY_DATA_ID          = "dataId";
        public static final String KEY_REMOVED_IDS      = "removedIds";
        public static final String KEY_REPORT           = "report";
        public static final String KEY_REMOVE           = "remove";
        public static final String KEY_LAST_SYNC_TIME   = "lastSyncTime";
        public static final String KEY_PUBLIC_ID        = "publicId";
        public static final String KEY_SUBMIT_TIME      = "submitTime";
        public static final String KEY_OPTIONS          = "options";
        public static final String KEY_SELECTION        = "selection";
        public static final String KEY_FILE_NAME        = "filename";
        public static final String KEY_REP              = "rep";
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
        public static final int ERROR_TRACKING_DISABLED         = 9;

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
        public static final String FORMAT_LONG  = "d MMM yyyy - h:mm a";

        // Time values in milliseconds
        public static final int TIME_5_SEC  = 5 * 1000;
        public static final int TIME_30_SEC = 30 * 1000;
        public static final int TIME_1_MIN  = 60 * 1000;
        public static final int TIME_2_MIN  = 2 * 60 * 1000;
        public static final int TIME_5_MIN  = 5 * 60 * 1000;
        public static final int TIME_15_MIN = 15 * 60 * 1000;
        public static final int TIME_30_MIN = 30 * 60 * 1000;
        public static final int TIME_1_HR   = 60 * 60 * 1000;
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

    public static class Formatters {
        public static SimpleDateFormat DATE_BACKEND = new SimpleDateFormat(Date.FORMAT_BACKEND);
        public static SimpleDateFormat DATE_LONG    = new SimpleDateFormat(Date.FORMAT_LONG);
    }
}
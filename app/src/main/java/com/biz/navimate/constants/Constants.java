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
        public static final String KEY_MAP_TYPE                 = "pref_map_type";
        public static final String KEY_MAP_TRAFFIC_OVERLAY      = "pref_map_traffic_overlay";
    }

    // Template related constants
    public static class Template {
        // Field Types in templates
        public static final int FIELD_TYPE_NONE           = 0;
        public static final int FIELD_TYPE_TEXT           = 1;
        public static final int FIELD_TYPE_NUMBER         = 2;
        public static final int FIELD_TYPE_RADIOLIST      = 3;
        public static final int FIELD_TYPE_CHECKLIST      = 4;
        public static final int FIELD_TYPE_PHOTO          = 5;
        public static final int FIELD_TYPE_SIGN           = 6;
        public static final int FIELD_TYPE_LOCATION       = 7;
    }

    // Server related constants
    public static class Server {
        // URLs
        //private static final String URL_BASE            = "http://192.168.1.10:8080";     // Debug
        private static final String URL_BASE            = "http://34.214.114.8:8080";       // Release
        public static final String URL_GET_PROFILE      = URL_BASE + "/api/reps/profile";
        public static final String URL_OTP_SMS          = URL_BASE + "/api/reps/otp";
        public static final String URL_UPDATE_FCM       = URL_BASE + "/api/reps/fcm";
        public static final String URL_REGISTER         = URL_BASE + "/api/reps/register";
        public static final String URL_TRACK            = URL_BASE + "/api/track/data";
        public static final String URL_UPLOAD_PHOTO     = URL_BASE + "/api/photos/upload";
        public static final String URL_CHECK_UPDATE     = URL_BASE + "/api/app/update";
        public static final String URL_SYNC_TASKS       = URL_BASE + "/api/reps/sync/tasks";
        public static final String URL_SYNC_LEADS       = URL_BASE + "/api/reps/sync/leads";
        public static final String URL_SYNC_TEMPLATES   = URL_BASE + "/api/reps/sync/templates";
        public static final String URL_SYNC_FIELDS      = URL_BASE + "/api/reps/sync/fields";
        public static final String URL_SYNC_DATA        = URL_BASE + "/api/reps/sync/data";
        public static final String URL_SYNC_VALUES      = URL_BASE + "/api/reps/sync/values";
        public static final String URL_SYNC_FORMS       = URL_BASE + "/api/reps/sync/forms";

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
        public static final String KEY_TEMPLATES        = "templates";
        public static final String KEY_FORMS            = "forms";
        public static final String KEY_FIELDS           = "fields";
        public static final String KEY_DATA             = "data";
        public static final String KEY_VALUES           = "values";

        // Task related keys
        public static final String KEY_STATUS           = "status";

        // Lead related keys
        public static final String KEY_TITLE            = "title";
        public static final String KEY_DESCRIPTION      = "description";
        public static final String KEY_ADDRESS          = "address";
        public static final String KEY_LATITUDE         = "latitude";
        public static final String KEY_LONGITUDE        = "longitude";

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
        public static final String KEY_FCM              = "fcmId";
        public static final String KEY_SPEED            = "speed";
        public static final String KEY_MESSAGE          = "message";
    }

    // Map related constants
    public class Map {
        // Zoom levels
        public static final int MIN_ZOOM = 14;
    }

    // Miscellaneous Constants
    public class Misc {
        // Invalid ID
        public static final long ID_INVALID = -1;
    }
}

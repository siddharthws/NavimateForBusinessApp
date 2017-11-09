package com.biz.navimate.objects;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class FormField {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM_FIELD";

    // Field Types
    public static final String TYPE_INVALID        = "";
    public static final String TYPE_TEXT           = "text";
    public static final String TYPE_NUMBER         = "number";
    public static final String TYPE_RADIO_LIST     = "radioList";
    public static final String TYPE_CHECK_LIST     = "checkList";
    public static final String TYPE_PHOTO          = "photo";
    public static final String TYPE_SIGNATURE      = "signature";

    // JSON Fields
    public static final String JSON_KEY_TYPE                = "type";
    public static final String JSON_KEY_TITLE               = "title";
    public static final String JSON_KEY_VALUE               = "value";
    public static final String JSON_KEY_OPTIONS             = "options";
    public static final String JSON_KEY_SELECTION           = "selection";

    // ----------------------- Classes ----------------------- //
    // Base Class for all field types
    public static abstract class Base
    {
        // ----------------------- Globals ----------------------- //
        public String type = TYPE_INVALID;
        public String title = "";

        // ----------------------- Constructor ----------------------- //
        public Base(String type, String title)
        {
            this.type = type;
            this.title = title;
        }

        public Base(JSONObject json) throws JSONException
        {
            type = json.getString(JSON_KEY_TYPE);
            this.title = json.getString(JSON_KEY_TITLE);
        }

        public JSONObject toJson() throws JSONException
        {
            JSONObject json = new JSONObject();

            json.put(JSON_KEY_TYPE, type);
            json.put(JSON_KEY_TITLE, title);

            return json;
        }
    }

    // Text Form Field
    public static class Text extends Base
    {
        // Text to store
        public String data = "";

        public Text (String title, String data)
        {
            super(TYPE_TEXT, title);
            this.data = data;
        }

        // Create object from JSON
        public Text(JSONObject json) throws JSONException
        {
            // Init Super
            super(json);

            // Extract string data
            this.data = json.getString(JSON_KEY_VALUE);
        }

        // Convert object to JSON
        @Override
        public JSONObject toJson() throws JSONException
        {
            JSONObject json = super.toJson();

            json.put(JSON_KEY_VALUE, data);

            return json;
        }
    }

    // Number Form Field
    public static class Number extends Base
    {
        // Number to store
        public int data = 0;

        public Number (String title, int data)
        {
            super(TYPE_NUMBER, title);
            this.data = data;
        }

        // Create object from JSON
        public Number(JSONObject json) throws JSONException
        {
            // Init Super
            super(json);

            // Extract string data
            this.data = json.getInt(JSON_KEY_VALUE);
        }

        // Convert object to JSON
        @Override
        public JSONObject toJson() throws JSONException
        {
            JSONObject json = super.toJson();

            json.put(JSON_KEY_VALUE, data);

            return json;
        }
    }

    // Radio List Form Field
    public static class RadioList extends Base
    {
        // List of strings to store
        public ArrayList<String> options = null;
        public String selection = "";

        public RadioList (String title, ArrayList<String> options, String selection)
        {
            super(TYPE_RADIO_LIST, title);
            this.options = options;
            this.selection = selection;
        }

        // Create object from JSON
        public RadioList(JSONObject json) throws JSONException
        {
            // Init Super
            super(json);

            // Get Radio Data
            JSONObject radioData = json.getJSONObject(JSON_KEY_VALUE);

            // Get List of options and populate data
            JSONArray radioOptions = radioData.getJSONArray(JSON_KEY_OPTIONS);
            options = new ArrayList<>();
            for (int i = 0; i < radioOptions.length(); i++)
            {
                options.add(radioOptions.getString(i));
            }

            // Get selection index form json
            this.selection = radioData.getString(JSON_KEY_SELECTION);
        }

        // Convert object to JSON
        @Override
        public JSONObject toJson() throws JSONException
        {
            JSONObject json = super.toJson();

            // Feed all radio options into a JSON Array
            JSONArray radioOptions = new JSONArray();
            for (String option : options)
            {
                radioOptions.put(option);
            }

            // Create JSON Object for Radio List Data
            JSONObject radioData = new JSONObject();
            radioData.put(JSON_KEY_OPTIONS, radioOptions);
            radioData.put(JSON_KEY_SELECTION, selection);

            json.put(JSON_KEY_VALUE, radioData);

            return json;
        }
    }

    // Radio List Form Field
    public static class CheckList extends Base
    {
        // List of strings to store
        public ArrayList<String> options = null;
        public ArrayList<Boolean> selection = null;

        public CheckList (String title, ArrayList<String> options, ArrayList<Boolean> selection)
        {
            super(TYPE_CHECK_LIST, title);
            this.options = options;
            this.selection = selection;
        }

        // Create object from JSON
        public CheckList(JSONObject json) throws JSONException
        {
            // Init Super
            super(json);

            // Get Radio Data
            JSONObject data = json.getJSONObject(JSON_KEY_VALUE);

            // Get List of options and populate data
            JSONArray jsonOptions = data.getJSONArray(JSON_KEY_OPTIONS);
            options = new ArrayList<>();
            for (int i = 0; i < jsonOptions.length(); i++)
            {
                options.add(jsonOptions.getString(i));
            }

            // Get selection index form json
            JSONArray selectionJson = data.getJSONArray(JSON_KEY_SELECTION);
            selection = new ArrayList<>();
            for (int i = 0; i < selectionJson.length(); i++)
            {
                selection.add(selectionJson.getBoolean(i));
            }
        }

        // Convert object to JSON
        @Override
        public JSONObject toJson() throws JSONException
        {
            JSONObject json = super.toJson();

            // Feed all radio options into a JSON Array
            JSONArray optionsJson = new JSONArray();
            for (String option : options)
            {
                optionsJson.put(option);
            }

            // Feed all selection into a JSON Array
            JSONArray selectionJson = new JSONArray();
            for (Boolean option : selection)
            {
                selectionJson.put(option);
            }

            // Create JSON Object for Radio List Data
            JSONObject data = new JSONObject();
            data.put(JSON_KEY_OPTIONS, optionsJson);
            data.put(JSON_KEY_SELECTION, selectionJson);

            json.put(JSON_KEY_VALUE, data);

            return json;
        }
    }

    // Photo Form Field
    public static class Photo extends Base
    {
        // List of strings to store
        public String filename = null;
        public Bitmap photo = null;

        public Photo (String title, String filename, Bitmap photo)
        {
            super(TYPE_PHOTO, title);
            this.filename = filename;
            this.photo = photo;
        }

        // Create object from JSON
        public Photo(JSONObject json) throws JSONException
        {
            // Init Super
            super(json);

            // Get Radio Data
            filename = json.getString(JSON_KEY_VALUE);
        }

        // Convert object to JSON
        @Override
        public JSONObject toJson() throws JSONException
        {
            JSONObject json = super.toJson();
            json.put(JSON_KEY_VALUE, filename);
            return json;
        }
    }

    // Signature Form Field
    public static class Signature extends Base
    {
        // List of strings to store
        public String filename = null;
        public Bitmap signature = null;

        public Signature (String title, String filename, Bitmap signature)
        {
            super(TYPE_SIGNATURE, title);
            this.filename = filename;
            this.signature = signature;
        }

        // Create object from JSON
        public Signature(JSONObject json) throws JSONException
        {
            // Init Super
            super(json);

            // Get Radio Data
            filename = json.getString(JSON_KEY_VALUE);
        }

        // Convert object to JSON
        @Override
        public JSONObject toJson() throws JSONException
        {
            JSONObject json = super.toJson();
            json.put(JSON_KEY_VALUE, filename);
            return json;
        }
    }

    public static FormField.Base FromJson(JSONObject json) throws JSONException {
        String type = json.getString(JSON_KEY_TYPE);
        if (type.equals(TYPE_TEXT)) {
            return new Text(json);
        } else if (type.equals(TYPE_NUMBER)) {
            return new Number(json);
        } else if (type.equals(TYPE_RADIO_LIST)) {
            return new RadioList(json);
        } else if (type.equals(TYPE_CHECK_LIST)) {
            return new CheckList(json);
        } else if (type.equals(TYPE_PHOTO)) {
            return new Photo(json);
        } else if (type.equals(TYPE_SIGNATURE)) {
            return new Signature(json);
        }
        return null;
    }
}

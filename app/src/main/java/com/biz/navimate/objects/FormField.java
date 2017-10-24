package com.biz.navimate.objects;

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

    // JSON Fields
    public static final String JSON_KEY_TYPE                = "type";
    public static final String JSON_KEY_TITLE               = "title";
    public static final String JSON_KEY_VALUE               = "value";
    public static final String JSON_KEY_RADIO_OPTIONS       = "options";
    public static final String JSON_KEY_RADIO_SELECTION     = "selection";

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
            JSONArray radioOptions = radioData.getJSONArray(JSON_KEY_RADIO_OPTIONS);
            options = new ArrayList<>();
            for (int i = 0; i < radioOptions.length(); i++)
            {
                options.add(radioOptions.getString(i));
            }

            // Get selection index form json
            this.selection = radioData.getString(JSON_KEY_RADIO_SELECTION);
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
            radioData.put(JSON_KEY_RADIO_OPTIONS, radioOptions);
            radioData.put(JSON_KEY_RADIO_SELECTION, selection);

            json.put(JSON_KEY_VALUE, radioData);

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
        }
        return null;
    }
}

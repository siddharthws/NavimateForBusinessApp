package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 08-12-2017.
 */

public class FormEntry {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM_ENTRY";

    // JSON Fields
    public static final String JSON_KEY_OPTIONS             = "options";
    public static final String JSON_KEY_SELECTION           = "selection";

    // ----------------------- Classes ----------------------- //
    // Base Class for all field types
    public static abstract class Base
    {
        // ----------------------- Abstracts ----------------------- //
        public abstract String toString();

        // ----------------------- Globals ----------------------- //
        public Field field = null;

        // ----------------------- Constructor ----------------------- //
        public Base(Field field)
        {
            this.field = field;
        }
    }

    // Text Form Entry
    public static class Text extends Base
    {
        // Text to store
        public String text = "";

        public Text (Field field, String text)
        {
            super(field);
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    // Number Form Field
    public static class Number extends Base
    {
        // Number to store
        public Long number = 0L;

        public Number (Field field, Long number)
        {
            super(field);
            this.number = number;
        }

        // Convert object to JSON
        @Override
        public String toString()
        {
            return String.valueOf(number);
        }
    }

    // Checkbox Form Field
    public static class Checkbox extends Base
    {
        // Number to store
        public boolean bChecked = false;

        public Checkbox (Field field, boolean bChecked)
        {
            super(field);
            this.bChecked = bChecked;
        }

        // Convert object to JSON
        @Override
        public String toString()
        {
            return String.valueOf(bChecked);
        }
    }

    // Radio List Form Field
    public static class RadioList extends Base
    {
        // List of strings to store
        public ArrayList<String> options = null;
        public int selection = 0;

        public RadioList (Field field, ArrayList<String> options, int selection)
        {
            super(field);
            this.options = options;
            this.selection = selection;
        }

        // Convert object to JSON
        @Override
        public String toString()
        {
            JSONObject json = new JSONObject();

            // Feed all radio options into a JSON Array
            JSONArray radioOptions = new JSONArray(options);

            // Create JSON Object for Radio List Data
            JSONObject radioJson = new JSONObject();
            try {
                radioJson.put(JSON_KEY_OPTIONS, radioOptions);
                radioJson.put(JSON_KEY_SELECTION, selection);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return radioJson.toString();
        }
    }

    // Radio List Form Field
    public static class CheckList extends Base
    {
        // List of strings to store
        public ArrayList<String> options = null;
        public ArrayList<Boolean> selection = null;

        public CheckList (Field field, ArrayList<String> options, ArrayList<Boolean> selection)
        {
            super(field);
            this.options = options;
            this.selection = selection;
        }

        // Convert object to JSON
        @Override
        public String toString()
        {
            // Create JSON Object for Radio List Data
            JSONArray optionsJson = new JSONArray();
            try {
                // Feed all radio options into a JSON Array
                for (int i = 0; i < options.size(); i++) {
                    JSONObject optionJson = new JSONObject();
                    optionJson.put(Constants.Server.KEY_NAME, options.get(i));
                    optionJson.put("selection", selection.get(i));

                    optionsJson.put(optionJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return optionsJson.toString();
        }
    }

    // Photo Form Field
    public static class Photo extends Base
    {
        // List of strings to store
        public String filename = "";

        public Photo (Field field, String filename)
        {
            super(field);
            this.filename = filename;
        }

        // Convert object to JSON
        @Override
        public String toString()
        {
            return filename;
        }
    }

    // Signature Form Field
    public static class Signature extends Base
    {
        // List of strings to store
        public String filename = "";

        public Signature (Field field, String filename)
        {
            super(field);
            this.filename = filename;
        }

        // Convert object to JSON
        @Override
        public String toString()
        {
            return filename;
        }
    }

    public static Base Parse(Field field, String value) {
        switch (field.type) {
            case Constants.Template.FIELD_TYPE_TEXT : {
                return new Text(field, value);
            }
            case Constants.Template.FIELD_TYPE_NUMBER : {
                return new Number(field, Long.valueOf(value));
            }
            case Constants.Template.FIELD_TYPE_CHECKBOX : {
                return new Checkbox(field, Boolean.valueOf(value));
            }
            case Constants.Template.FIELD_TYPE_PHOTO : {
                return new Photo(field, value);
            }
            case Constants.Template.FIELD_TYPE_SIGN : {
                return new Signature(field, value);
            }
            case Constants.Template.FIELD_TYPE_RADIOLIST : {
                // Parse value in options and selection
                try {
                    JSONObject radioJson = new JSONObject(value);
                    JSONArray optionsJson = radioJson.getJSONArray(JSON_KEY_OPTIONS);
                    ArrayList<String> options = new ArrayList<>();
                    for (int i = 0; i < optionsJson.length(); i++) {
                        options.add(optionsJson.getString(i));
                    }
                    int selection = radioJson.getInt(JSON_KEY_SELECTION);
                    return new RadioList(field, options, selection);
                } catch (JSONException e) {
                    Dbg.error(TAG, "Error while converting radio list data");
                    Dbg.stack(e);
                }
            }
            case Constants.Template.FIELD_TYPE_CHECKLIST : {
                // Parse value in options and selection
                try {
                    JSONArray optionsJson = new JSONArray(value);
                    ArrayList<String> options = new ArrayList<>();
                    ArrayList<Boolean> selection = new ArrayList<>();
                    for (int i = 0; i < optionsJson.length(); i++) {
                        JSONObject optionJson = optionsJson.getJSONObject(i);
                        String name = optionJson.getString(Constants.Server.KEY_NAME);
                        Boolean bChecked = optionJson.getBoolean("selection");
                        options.add(name);
                        selection.add(bChecked);
                    }
                    return new CheckList(field, options, selection);
                } catch (JSONException e) {
                    Dbg.error(TAG, "Error while converting check list data");
                    Dbg.stack(e);
                }
            }
        }

        return null;
    }
}

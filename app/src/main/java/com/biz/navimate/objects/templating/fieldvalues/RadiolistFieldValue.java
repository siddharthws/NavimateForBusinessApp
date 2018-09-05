package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RadiolistFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "RADIOLIST_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public ArrayList<String> options = new ArrayList<>();
    public int selection = -1;

    // ----------------------- Constructor ----------------------- //
    public RadiolistFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        // Feed all radio options into a JSON Array
        JSONArray radioOptions = new JSONArray(options);

        // Create JSON Object for Radio List Data
        JSONObject radioJson = new JSONObject();
        try {
            radioJson.put(Constants.Server.KEY_OPTIONS, radioOptions);
            radioJson.put(Constants.Server.KEY_SELECTION, selection);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return radioJson.toString();
    }

    @Override
    public void fromString(String value) {
        try {
            // Clear options
            options.clear();

            // Populate options array
            JSONObject radioJson = new JSONObject(value);
            JSONArray optionsJson = radioJson.getJSONArray(Constants.Server.KEY_OPTIONS);
            for (int i = 0; i < optionsJson.length(); i++) {
                options.add(optionsJson.getString(i));
            }

            // Populate selection
            selection = radioJson.getInt(Constants.Server.KEY_SELECTION);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing radio list : " + value);
            Dbg.stack(e);
        }
    }

    @Override
    public String getError() {
        return "";
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}

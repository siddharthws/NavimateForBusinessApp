package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChecklistFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CHECKLIST_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public ArrayList<String> options = new ArrayList<>();
    public ArrayList<Boolean> selection = new ArrayList<>();

    // ----------------------- Constructor ----------------------- //
    public ChecklistFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        // Create JSON Object for Radio List Data
        JSONArray optionsJson = new JSONArray();
        try {
            // Feed all radio options into a JSON Array
            for (int i = 0; i < options.size(); i++) {
                JSONObject optionJson = new JSONObject();
                optionJson.put(Constants.Server.KEY_NAME, options.get(i));
                optionJson.put(Constants.Server.KEY_SELECTION, selection.get(i));

                optionsJson.put(optionJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return optionsJson.toString();
    }

    @Override
    public void fromString(String value) {
        try {
            // Clear current options and selection
            options.clear();
            selection.clear();

            // Add option and selections from JSON
            JSONArray optionsJson = new JSONArray(value);
            for (int i = 0; i < optionsJson.length(); i++) {
                JSONObject optionJson = optionsJson.getJSONObject(i);

                String name = optionJson.getString(Constants.Server.KEY_NAME);
                options.add(name);

                Boolean bChecked = optionJson.getBoolean(Constants.Server.KEY_SELECTION);
                selection.add(bChecked);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting check list data");
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

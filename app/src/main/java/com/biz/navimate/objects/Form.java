package com.biz.navimate.objects;

import com.biz.navimate.debug.Dbg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";

    // JSON Fields
    public static final String JSON_KEY_TYPE                = "type";
    public static final String JSON_KEY_TITLE               = "title";
    public static final String JSON_KEY_VALUE               = "value";
    public static final String JSON_KEY_RADIO_OPTIONS       = "options";
    public static final String JSON_KEY_RADIO_SELECTION     = "selection";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public int sales = 0;
    public String notes = "";
    public boolean bFailed = false, bWaiting = true, bDone = false;

    // ----------------------- Constructor ----------------------- //
    public Form () {}

    public Form (String name, JSONArray fields) {
        this.name = name;
        InitFields(fields);
    }

    private void InitFields(JSONArray fieldsJson)
    {
        try {
            for (int j = 0; j < fieldsJson.length(); j++) {
                JSONObject fieldJson = fieldsJson.getJSONObject(j);
                String typeString = fieldJson.getString(JSON_KEY_TYPE);
                if (typeString.equals("text")) {
                    notes = fieldJson.getString(JSON_KEY_VALUE);
                } else if (typeString.equals("number")) {
                    sales = fieldJson.getInt(JSON_KEY_VALUE);
                } else if (typeString.equals("radioList")) {
                    String selection = fieldJson.getString(JSON_KEY_RADIO_SELECTION);
                    if (selection.equalsIgnoreCase("waiting")) {
                        bWaiting = true;
                    } else if (selection.equalsIgnoreCase("done")) {
                        bDone = true;
                    } else if (selection.equalsIgnoreCase("failed")) {
                        bFailed = true;
                    }
                }
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "JSON Exception while parsing tasks");
            Dbg.stack(e);
        }
    }
}

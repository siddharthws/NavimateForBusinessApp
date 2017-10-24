package com.biz.navimate.objects;

import com.biz.navimate.debug.Dbg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<FormField.Base> fields = null;

    // ----------------------- Constructor ----------------------- //
    public Form (String name, JSONArray fields) {
        this.name = name;
        InitFields(fields);
    }

    public Form (String name, ArrayList<FormField.Base> fields) {
        this.name = name;
        this.fields = fields;
    }

    private void InitFields(JSONArray fieldsJson)
    {
        fields = new ArrayList<>();
        try {
            for (int j = 0; j < fieldsJson.length(); j++) {
                JSONObject fieldJson = fieldsJson.getJSONObject(j);
                fields.add(FormField.FromJson(fieldJson));
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "JSON Exception while parsing tasks");
            Dbg.stack(e);
        }
    }
}

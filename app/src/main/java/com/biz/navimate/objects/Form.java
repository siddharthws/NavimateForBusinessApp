package com.biz.navimate.objects;

import com.biz.navimate.debug.Dbg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form extends DbObject{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";
    public static final int APP_ID_INVALID = -1;

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<FormField.Base> fields = null;
    public int appId = APP_ID_INVALID;


    // ----------------------- Constructor ----------------------- //
    public Form (String name, JSONArray fields) {
        super(DbObject.TYPE_FORM,DbObject.DB_ID_INVALID);
        this.name = name;
        InitFields(fields);
    }

    public Form (String name, ArrayList<FormField.Base> fields) {
        super(DbObject.TYPE_FORM,DbObject.DB_ID_INVALID);
        this.name = name;
        this.fields = fields;
    }

    public Form() {}


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

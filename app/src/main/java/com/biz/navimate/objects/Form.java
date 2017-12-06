package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form extends DbObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<FormField.Base> fields = null;

    // ----------------------- Constructor ----------------------- //
    public Form (long id, long version, String name, ArrayList<FormField.Base> fields) {
        super(DbObject.TYPE_FORM, version, id);
        this.name = name;
        this.fields = fields;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Form FromJson(JSONObject templateJson) throws JSONException {
        long dbId           = templateJson.getLong(Constants.Server.KEY_ID);
        long version        = templateJson.getLong(Constants.Server.KEY_VERSION);
        JSONArray data      = templateJson.getJSONArray(Constants.Server.KEY_DATA);
        String name         = templateJson.getString(Constants.Server.KEY_NAME);

        return new Form(dbId, version, name, FormField.FromJsonArray(data));
    }
}

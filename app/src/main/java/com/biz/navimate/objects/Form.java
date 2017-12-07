package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<FormField.Base> fields = null;

    // ----------------------- Constructor ----------------------- //
    public Form (long dbId, long serverId, long version, String name, ArrayList<FormField.Base> fields) {
        super(DbObject.TYPE_FORM, dbId, serverId, version);
        this.name = name;
        this.fields = fields;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Form FromJson(JSONObject templateJson) throws JSONException {
        long serverId       = templateJson.getLong(Constants.Server.KEY_ID);
        long version        = templateJson.getLong(Constants.Server.KEY_VERSION);
        JSONArray data      = templateJson.getJSONArray(Constants.Server.KEY_DATA);
        String name         = templateJson.getString(Constants.Server.KEY_NAME);

        // Get DbId
        long dbId = Constants.Misc.ID_INVALID;
        Form existingForm = DbHelper.formTable.GetByServerId(serverId);
        if (existingForm != null) {
            dbId = existingForm.dbId;
        }

        return new Form(dbId, serverId, version, name, FormField.FromJsonArray(data));
    }
}

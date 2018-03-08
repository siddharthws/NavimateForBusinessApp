package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class Template extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TEMPLATE";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<Long> fieldIds = null;
    public int type = 0;

    // ----------------------- Constructor ----------------------- //
    public Template (long dbId, long serverId, long version, String name, int type, ArrayList<Long> fieldIds) {
        super(DbObject.TYPE_TEMPLATE, dbId, serverId, version);
        this.name = name;
        this.type = type;
        this.fieldIds = fieldIds;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Template FromJson(JSONObject templateJson) throws JSONException {
        long serverId               = templateJson.getLong(Constants.Server.KEY_ID);
        long version                = templateJson.getLong(Constants.Server.KEY_VERSION);
        String name                 = templateJson.getString(Constants.Server.KEY_NAME);
        int type                    = templateJson.getInt(Constants.Server.KEY_TYPE);
        JSONArray fieldIdsJson      = templateJson.getJSONArray(Constants.Server.KEY_FIELD_IDS);

        // Get field DB Ids array
        ArrayList<Long> fieldIds = new ArrayList<>();
        for (int i = 0; i < fieldIdsJson.length(); i++) {
            Long fieldServerId = fieldIdsJson.getLong(i);

            // Check if field exists
            Field field = DbHelper.fieldTable.GetByServerId(fieldServerId);
            if (field == null) {
                field = new Field(Constants.Misc.ID_INVALID, fieldServerId, Constants.Misc.ID_INVALID, "", Constants.Template.FIELD_TYPE_NONE, "",  false);
                DbHelper.fieldTable.Save(field);
            }

            // Add Field's DBId to field Ids array
            fieldIds.add(field.dbId);
        }

        // Get Template DbId
        long dbId = Constants.Misc.ID_INVALID;
        Template existingTemplate = DbHelper.templateTable.GetByServerId(serverId);
        if (existingTemplate != null) {
            dbId = existingTemplate.dbId;
        }

        return new Template(dbId, serverId, version, name, type, fieldIds);
    }
}

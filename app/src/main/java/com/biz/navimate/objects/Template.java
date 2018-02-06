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
    public Long defaultDataId = Constants.Misc.ID_INVALID;
    public ArrayList<Long> fieldIds = null;
    public int type = 0;

    // ----------------------- Constructor ----------------------- //
    public Template (long dbId, long serverId, long version, String name, int type, long defaultDataId, ArrayList<Long> fieldIds) {
        super(DbObject.TYPE_TEMPLATE, dbId, serverId, version);
        this.name = name;
        this.type = type;
        this.defaultDataId = defaultDataId;
        this.fieldIds = fieldIds;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Template FromJson(JSONObject templateJson) throws JSONException {
        long serverId               = templateJson.getLong(Constants.Server.KEY_ID);
        long version                = templateJson.getLong(Constants.Server.KEY_VERSION);
        String name                 = templateJson.getString(Constants.Server.KEY_NAME);
        int type                    = templateJson.getInt(Constants.Server.KEY_TYPE);
        long defaultDataServerId    = templateJson.getLong(Constants.Server.KEY_DEFAULT_DATA_ID);
        JSONArray fieldIdsJson      = templateJson.getJSONArray(Constants.Server.KEY_FIELD_IDS);

        // Get field DB Ids array
        ArrayList<Long> fieldIds = new ArrayList<>();
        for (int i = 0; i < fieldIdsJson.length(); i++) {
            Long fieldServerId = fieldIdsJson.getLong(i);

            // Check if field exists
            Field field = DbHelper.fieldTable.GetByServerId(fieldServerId);
            if (field == null) {
                field = new Field(Constants.Misc.ID_INVALID, fieldServerId, Constants.Misc.ID_INVALID, "", Constants.Template.FIELD_TYPE_NONE, false);
                DbHelper.fieldTable.Save(field);
            }

            // Add Field's DBId to field Ids array
            fieldIds.add(field.dbId);
        }

        // Get Default Data DbId
        Data data = DbHelper.dataTable.GetByServerId(defaultDataServerId);
        if (data == null) {
            data = new Data(Constants.Misc.ID_INVALID, defaultDataServerId, Constants.Misc.ID_INVALID, new ArrayList<Long>());
            DbHelper.dataTable.Save(data);
        }

        // Get Template DbId
        long dbId = Constants.Misc.ID_INVALID;
        Template existingTemplate = DbHelper.templateTable.GetByServerId(serverId);
        if (existingTemplate != null) {
            dbId = existingTemplate.dbId;
        }

        return new Template(dbId, serverId, version, name, type, data.dbId, fieldIds);
    }
}

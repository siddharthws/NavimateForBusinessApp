package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class Data extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DATA";

    // ----------------------- Globals ----------------------- //
    public ArrayList<Long> valueIds = null;

    // ----------------------- Constructor ----------------------- //
    public Data (long dbId, long serverId, long version, ArrayList<Long> valueIds) {
        super(DbObject.TYPE_DATA, dbId, serverId, version);
        this.valueIds = valueIds;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Data FromJson(JSONObject json) throws JSONException {
        long serverId               = json.getLong(Constants.Server.KEY_ID);
        long version                = json.getLong(Constants.Server.KEY_VERSION);
        JSONArray valuesJson        = json.getJSONArray(Constants.Server.KEY_VALUE_IDS);

        // Get value DB Ids array
        ArrayList<Long> valueIds = new ArrayList<>();
        for (int i = 0; i < valuesJson.length(); i++) {
            Long valueServerId = valuesJson.getLong(i);

            // Check if field exists
            Value value = DbHelper.valueTable.GetByServerId(valueServerId);
            if (value == null) {
                value = new Value(Constants.Misc.ID_INVALID, valueServerId, Constants.Misc.ID_INVALID, "", Constants.Misc.ID_INVALID);
                DbHelper.valueTable.Save(value);
            }

            // Add Field's DBId to field Ids array
            valueIds.add(value.dbId);
        }

        // Get Data DbId
        long dbId = Constants.Misc.ID_INVALID;
        Data existingData = DbHelper.dataTable.GetByServerId(serverId);
        if (existingData != null) {
            dbId = existingData.dbId;
        }

        return new Data(dbId, serverId, version, valueIds);
    }
}

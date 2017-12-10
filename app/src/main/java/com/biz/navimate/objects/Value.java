package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class Value extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "VALUE";

    // ----------------------- Globals ----------------------- //
    public String value = "";
    public Long fieldId = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public Value (long dbId, long serverId, long version, String value, long fieldId) {
        super(DbObject.TYPE_VALUE, dbId, serverId, version);
        this.value = value;
        this.fieldId = fieldId;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Value FromJson(JSONObject json) throws JSONException {
        long serverId               = json.getLong(Constants.Server.KEY_ID);
        long version                = json.getLong(Constants.Server.KEY_VERSION);
        String value                = json.getString(Constants.Server.KEY_VALUE);
        long fieldId                = json.getLong(Constants.Server.KEY_FIELD_ID);

        // Get Field from server ID
        Field field = DbHelper.fieldTable.GetByServerId(fieldId);

        // Get Data DbId
        long dbId = Constants.Misc.ID_INVALID;
        Value existingValue = DbHelper.valueTable.GetByServerId(serverId);
        if (existingValue != null) {
            dbId = existingValue.dbId;
        }

        return new Value(dbId, serverId, version, value, field.dbId);
    }
}

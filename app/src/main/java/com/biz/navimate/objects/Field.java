package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class Field extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FIELD";

    // ----------------------- Globals ----------------------- //
    public String title = "";
    public int type = Constants.Template.FIELD_TYPE_NONE;
    public boolean bMandatory = false;
    public String value = "";

    // ----------------------- Constructor ----------------------- //
    public Field (long dbId, long serverId, long version, String title, int type, String value, boolean bMandatory) {
        super(DbObject.TYPE_FIELD, dbId, serverId, version);
        this.title = title;
        this.type = type;
        this.bMandatory = bMandatory;
        this.value = value;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Field FromJson(JSONObject json) throws JSONException {
        long serverId               = json.getLong(Constants.Server.KEY_ID);
        long version                = json.getLong(Constants.Server.KEY_VERSION);
        String title                = json.getString(Constants.Server.KEY_TITLE);
        int type                    = json.getInt(Constants.Server.KEY_TYPE);
        String value                = json.getString(Constants.Server.KEY_VALUE);
        boolean bMandatory          = json.getBoolean(Constants.Server.KEY_IS_MANDATORY);

        // Get Data DbId
        long dbId = Constants.Misc.ID_INVALID;
        Field existingField = DbHelper.fieldTable.GetByServerId(serverId);
        if (existingField != null) {
            dbId = existingField.dbId;
        }

        return new Field(dbId, serverId, version, title, type, value, bMandatory);
    }
}

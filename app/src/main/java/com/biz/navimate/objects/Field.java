package com.biz.navimate.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.FieldTable;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.core.ObjDb;

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
    public Field (String title, int type, String value) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        this.title = title;
        this.type = type;
        this.value = value;
    }

    public Field (JSONObject json) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromJson(json);
    }

    public Field (Cursor cursor) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromCursor(cursor);
    }

    // ----------------------- Public APIs ----------------------- //
    //
    // Converter methods for JSON
    //
    public void fromJson(JSONObject json) {
        try {
            serverId             = json.getLong(Constants.Server.KEY_ID);
            title                = json.getString(Constants.Server.KEY_TITLE);
            type                 = json.getInt(Constants.Server.KEY_TYPE);
            value                = json.getString(Constants.Server.KEY_VALUE);
            bMandatory           = json.getJSONObject(Constants.Server.KEY_SETTINGS).getBoolean(Constants.Server.KEY_B_MANDATORY);
        } catch (Exception e) {
            Dbg.error(TAG, "Exception while converting from JSON");
            Dbg.stack(e);
        }
    }

    //
    // Converter methods for database
    //
    public void fromCursor(Cursor cursor) {
        super.fromDb(cursor);

        dbId                    = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_ID));
        serverId                = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_SRV_ID));
        title                   = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_TITLE));
        type                    = cursor.getInt     (cursor.getColumnIndex(Constants.DB.COLUMN_TYPE));
        value                   = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_VALUE));
        bMandatory              = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Constants.DB.COLUMN_IS_MANDATORY)));
    }

    public ContentValues toContentValues () {
        ContentValues cv = super.toDb();

        // Enter values into Database
        cv.put(Constants.DB.COLUMN_SRV_ID,         serverId);
        cv.put(Constants.DB.COLUMN_TITLE,          title);
        cv.put(Constants.DB.COLUMN_TYPE,           type);
        cv.put(Constants.DB.COLUMN_VALUE,          value);
        cv.put(Constants.DB.COLUMN_IS_MANDATORY,   String.valueOf(bMandatory));

        return cv;
    }
}

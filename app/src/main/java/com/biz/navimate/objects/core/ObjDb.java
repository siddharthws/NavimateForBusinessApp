package com.biz.navimate.objects.core;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;

/**
 * Created by Jagannath on 08-11-2017.
 */

public abstract class ObjDb {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_DB";

    // ----------------------- Globals ----------------------- //
    public long dbId = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public ObjDb() {}

    public ObjDb(ObjDb obj) {
        this.dbId = obj.dbId;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public boolean equals(Object object) {
        boolean bEqual = false;

        // Validate Object Instance
        if ((object != null) && (object instanceof ObjDb)) {
            // Cast object to compare
            ObjDb compareObject = (ObjDb) object;

            if (compareObject.dbId == dbId) {
                return true;
            }
        }

        return bEqual;
    }

    // ----------------------- Public APIs ----------------------- //
    // Database converter methods
    public void fromDb(Cursor cursor) {
        dbId = cursor.getLong(cursor.getColumnIndex(Constants.DB.COLUMN_ID));
    }

    public ContentValues toDb () {
        return new ContentValues();
    }
}

package com.biz.navimate.objects.core;

import com.biz.navimate.constants.Constants;

/**
 * Created by Jagannath on 08-11-2017.
 */

public class ObjDb {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DB_OBJECT";

    // ----------------------- Globals ----------------------- //
    public long     dbId        = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public ObjDb() {}

    @Override
    public boolean equals(Object object)
    {
        boolean bEqual = false;

        // Validate Object Instance
        if ((object != null) && (object instanceof ObjDb))
        {
            // Cast object to compare
            ObjDb compareObject = (ObjDb) object;

            if (compareObject.dbId == dbId) {
                return true;
            }
        }

        return bEqual;
    }
}

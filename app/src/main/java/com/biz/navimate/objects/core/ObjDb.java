package com.biz.navimate.objects.core;

import com.biz.navimate.constants.Constants;

/**
 * Created by Jagannath on 08-11-2017.
 */

public class ObjDb
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DB_OBJECT";

    // DB Object Types
    public static final int TYPE_INVALID                = 0;
    public static final int TYPE_LEAD                   = 1;
    public static final int TYPE_TASK                   = 2;
    public static final int TYPE_FORM                   = 3;
    public static final int TYPE_TEMPLATE               = 4;
    public static final int TYPE_DATA                   = 5;
    public static final int TYPE_FIELD                  = 6;
    public static final int TYPE_VALUE                  = 7;
    public static final int TYPE_LOCATION_REPORT        = 8;

    // ----------------------- Globals ----------------------- //
    public int      type        = TYPE_INVALID;
    public long     dbId        = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public ObjDb(int type,
                 long dbId)
    {
        this.type       = type;
        this.dbId       = dbId;
    }

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

package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;

/**
 * Created by Jagannath on 08-11-2017.
 */

public class DbObject
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

    // ----------------------- Globals ----------------------- //
    public int      type        = TYPE_INVALID;
    public long     dbId        = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public DbObject(int type,
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
        if ((object != null) && (object instanceof DbObject))
        {
            // Cast object to compare
            DbObject compareObject = (DbObject) object;

            if (compareObject.dbId == dbId) {
                return true;
            }
        }

        return bEqual;
    }
}

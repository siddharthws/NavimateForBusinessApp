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

    // ----------------------- Globals ----------------------- //
    public int      type        = TYPE_INVALID;
    public long     version     = Constants.Misc.ID_INVALID;
    public long     dbId        = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public DbObject()
    {
    }

    public DbObject(int type,
                    long version,
                    long dbId)
    {
        this.type       = type;
        this.version    = version;
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

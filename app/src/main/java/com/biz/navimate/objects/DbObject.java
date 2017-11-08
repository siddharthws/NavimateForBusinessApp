package com.biz.navimate.objects;

/**
 * Created by Jagannath on 08-11-2017.
 */

public class DbObject
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DB_OBJECT";

    public static final int DB_ID_INVALID = -1;

    // DB Object Types
    public static final int TYPE_INVALID                = 0;

    // ----------------------- Globals ----------------------- //
    public int      type        = TYPE_INVALID;
    public int      dbId        = DB_ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public DbObject()
    {
    }

    public DbObject(int type,
                    int dbId)
    {
        this.type       = type;
        this.dbId       = dbId;
    }
}

package com.biz.navimate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jagannath on 08-11-2017.
 */

public class DbHelper extends SQLiteOpenHelper
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DB_HELPER";

    // DB Properties
    private static final String  DATABASE_NAME                   = "NAVIMATE_BIZ_DATABASE";
    private static final int     DATABASE_VERSION                = 1;

    // ----------------------- Globals ----------------------- //
    private static DbHelper             dbHelper                = null;


    // ----------------------- Constructor ----------------------- //
    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onCreate(SQLiteDatabase db)
    {
       //Placeholder
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        //placeholder
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Init(Context context)
    {
        if (dbHelper == null)
        {
            dbHelper = new DbHelper(context);
        }
    }
}

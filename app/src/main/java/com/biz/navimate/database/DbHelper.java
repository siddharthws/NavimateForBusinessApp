package com.biz.navimate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.biz.navimate.debug.Dbg;

/**
 * Created by Jagannath on 08-11-2017.
 */

public class DbHelper extends SQLiteOpenHelper
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DB_HELPER";

    // DB Properties
    private static final String  DATABASE_NAME                   = "DB_HELPER";
    private static final int     DATABASE_VERSION                = 2;

    // ----------------------- Globals ----------------------- //
    private static DbHelper             dbHelper                = null;

    // Table Class Objects for different table implementations
    public static FormTable             formTable               = null;
    public static LeadTable             leadTable               = null;
    public static TaskTable             taskTable               = null;


    // ----------------------- Constructor ----------------------- //
    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        formTable              = new FormTable(this);
        leadTable              = new LeadTable(this);
        taskTable              = new TaskTable(this);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Dbg.info(TAG, "Creating database");

        db.execSQL(FormTable.CREATE_TABLE);
        db.execSQL(LeadTable.CREATE_TABLE);
        db.execSQL(TaskTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        Dbg.info(TAG, "Upgrading database");

       // Drop existing database
        db.execSQL("DROP TABLE IF EXISTS " + TaskTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LeadTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FormTable.TABLE_NAME);

        // Run onCreate Again
        onCreate(db);
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

package com.biz.navimate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Statics;

/**
 * Created by Jagannath on 08-11-2017.
 */

public class DbHelper extends SQLiteOpenHelper
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DB_HELPER";

    // DB Properties
    private static final String  DATABASE_NAME                   = "DB_HELPER";
    private static final int     DATABASE_VERSION                = 12;

    // ----------------------- Globals ----------------------- //
    private static DbHelper             dbHelper                = null;

    // Table Class Objects for different table implementations
    public static FormTable             formTable               = null;
    public static LeadTable             leadTable               = null;
    public static TaskTable             taskTable               = null;
    public static TemplateTable         templateTable           = null;
    public static FieldTable            fieldTable              = null;
    public static LocationReportTable   locationReportTable     = null;


    // ----------------------- Constructor ----------------------- //
    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        fieldTable             = new FieldTable(this);
        templateTable          = new TemplateTable(this);
        leadTable              = new LeadTable(this);
        taskTable              = new TaskTable(this);
        formTable              = new FormTable(this);
        locationReportTable    = new LocationReportTable(this);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Dbg.info(TAG, "Creating database");

        db.execSQL(FieldTable.CREATE_TABLE);
        db.execSQL(TemplateTable.CREATE_TABLE);
        db.execSQL(LeadTable.CREATE_TABLE);
        db.execSQL(TaskTable.CREATE_TABLE);
        db.execSQL(FormTable.CREATE_TABLE);
        db.execSQL(LocationReportTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Upgrade database
        if (oldVersion < 10) {
            // Reset database
            RemoveAll(db);
            onCreate(db);
        } else {
            switch (oldVersion) {
                case 10:
                    // Drop & recreate location report
                    db.execSQL("DROP TABLE IF EXISTS " + LocationReportTable.TABLE_NAME);
                    db.execSQL(LocationReportTable.CREATE_TABLE);
                case 11:
                    // Add Public ID to task with default value
                    db.execSQL( "ALTER TABLE " + TaskTable.TABLE_NAME +
                                " ADD COLUMN " + TaskTable.COLUMN_PUBLIC_ID + " TEXT DEFAULT '-'");
            }
        }

        // Set Upgrade Flag
        Statics.bDbUpgraded = true;
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Init(Context context)
    {
        if (dbHelper == null)
        {
            dbHelper = new DbHelper(context);
        }
    }

    private void RemoveAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationReportTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FormTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LeadTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TemplateTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FieldTable.TABLE_NAME);
    }
}

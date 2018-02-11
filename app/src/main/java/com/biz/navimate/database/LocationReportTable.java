package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.LocationReportObject;

/**
 * Created by Sai_Kameswari on 11-02-2018.
 */

public class LocationReportTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_REPORT_TABLE";

    // Table name
    public static final String TABLE_NAME       = "locationReport_table";

    // Columns
    public static final String COLUMN_LATITUDE        = "latitude";
    public static final String COLUMN_LONGITUDE       = "longitude";
    public static final String COLUMN_TIMESTAMP       = "timestamp";
    public static final String COLUMN_STATUS          = "status";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "   + TABLE_NAME + " (" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_LATITUDE         + " REAL," +
                    COLUMN_LONGITUDE        + " REAL," +
                    COLUMN_TIMESTAMP        + " INTEGER," +
                    COLUMN_STATUS           + " INTEGER)";

    // ----------------------- Constructor ----------------------- //
    public LocationReportTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_LATITUDE,
                                                    COLUMN_LONGITUDE,
                                                    COLUMN_TIMESTAMP,
                                                    COLUMN_STATUS});
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long   dbId                    = cursor.getLong   (cursor.getColumnIndex(COLUMN_ID));
        double latitude                = cursor.getDouble (cursor.getColumnIndex(COLUMN_LATITUDE));
        double longitude               = cursor.getDouble (cursor.getColumnIndex(COLUMN_LONGITUDE));
        long timeStamp                 = cursor.getLong   (cursor.getColumnIndex(COLUMN_TIMESTAMP));
        int status                     = cursor.getInt    (cursor.getColumnIndex(COLUMN_STATUS));

        return new LocationReportObject(dbId, latitude, longitude, timeStamp, status);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        LocationReportObject locationReportObject = (LocationReportObject) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_LATITUDE,         locationReportObject.latitude);
        dbEntry.put(COLUMN_LONGITUDE,        locationReportObject.longitude);
        dbEntry.put(COLUMN_TIMESTAMP,        locationReportObject.timestamp);
        dbEntry.put(COLUMN_STATUS,           locationReportObject.status);

        return dbEntry;
    }
}

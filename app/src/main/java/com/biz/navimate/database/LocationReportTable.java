package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.core.ObjDb;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.LocationReportObject;

import java.util.ArrayList;

/**
 * Created by Sai_Kameswari on 11-02-2018.
 */

public class LocationReportTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_REPORT_TABLE";

    // Table name
    public static final String TABLE_NAME       = "locationReport_table";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "   + TABLE_NAME + " (" +
                    Constants.DB.COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Constants.DB.COLUMN_LATITUDE         + " REAL," +
                    Constants.DB.COLUMN_LONGITUDE        + " REAL," +
                    Constants.DB.COLUMN_TIMESTAMP        + " INTEGER," +
                    Constants.DB.COLUMN_STATUS           + " INTEGER," +
                    Constants.DB.COLUMN_BATTERY          + " INTEGER," +
                    Constants.DB.COLUMN_SPEED            + " REAL)";

    // ----------------------- Constructor ----------------------- //
    public LocationReportTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   Constants.DB.COLUMN_ID,
                                                    Constants.DB.COLUMN_LATITUDE,
                                                    Constants.DB.COLUMN_LONGITUDE,
                                                    Constants.DB.COLUMN_TIMESTAMP,
                                                    Constants.DB.COLUMN_STATUS,
                                                    Constants.DB.COLUMN_BATTERY,
                                                    Constants.DB.COLUMN_SPEED});
    }

    // ----------------------- Public APIs ----------------------- //
    public LocationReportObject GetLatest() {
        if (cache.size() > 0) {
            return (LocationReportObject) cache.get(cache.size() - 1);
        }

        return null;
    }

    public ArrayList<LocationReportObject> GetReportsToSync() {
        ArrayList<LocationReportObject> syncList = new ArrayList<>();

        for (int i = 1; i < cache.size(); i++) {
            syncList.add((LocationReportObject) cache.get(i));
        }

        return syncList;
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected ObjDb ParseToObject(Cursor cursor)
    {
        long   dbId                    = cursor.getLong   (cursor.getColumnIndex(Constants.DB.COLUMN_ID));
        double latitude                = cursor.getDouble (cursor.getColumnIndex(Constants.DB.COLUMN_LATITUDE));
        double longitude               = cursor.getDouble (cursor.getColumnIndex(Constants.DB.COLUMN_LONGITUDE));
        long timeStamp                 = cursor.getLong   (cursor.getColumnIndex(Constants.DB.COLUMN_TIMESTAMP));
        int status                     = cursor.getInt    (cursor.getColumnIndex(Constants.DB.COLUMN_STATUS));
        int battery                    = cursor.getInt    (cursor.getColumnIndex(Constants.DB.COLUMN_BATTERY));
        float speed                    = cursor.getFloat  (cursor.getColumnIndex(Constants.DB.COLUMN_SPEED));

        return new LocationReportObject(dbId, latitude, longitude, timeStamp, status, battery, speed);
    }

    @Override
    protected ContentValues ParseToContent(ObjDb dbItem)
    {
        LocationReportObject locationReportObject = (LocationReportObject) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(Constants.DB.COLUMN_LATITUDE,         locationReportObject.latitude);
        dbEntry.put(Constants.DB.COLUMN_LONGITUDE,        locationReportObject.longitude);
        dbEntry.put(Constants.DB.COLUMN_TIMESTAMP,        locationReportObject.timestamp);
        dbEntry.put(Constants.DB.COLUMN_STATUS,           locationReportObject.status);
        dbEntry.put(Constants.DB.COLUMN_SPEED,            locationReportObject.speed);
        dbEntry.put(Constants.DB.COLUMN_BATTERY,          locationReportObject.battery);

        return dbEntry;
    }
}

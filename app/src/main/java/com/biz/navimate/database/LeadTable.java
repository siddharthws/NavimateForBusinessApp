package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Task;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class LeadTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD_TABlE";

    // Table name
    public static final String TABLE_NAME             = "lead_table";

    // Columns
    public static final String COLUMN_TITLE            = "title";
    public static final String COLUMN_DESCRIPTION      = "description";
    public static final String COLUMN_PHONE            = "phone";
    public static final String COLUMN_EMAIL            = "email";
    public static final String COLUMN_ADDRESS          = "address";
    public static final String COLUMN_LATITUDE         = "latitude";
    public static final String COLUMN_LONGITUDE        = "longitude";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID             + " INTEGER PRIMARY KEY," +
                    COLUMN_VERSION        + " INTEGER," +
                    COLUMN_TITLE          + " TEXT," +
                    COLUMN_DESCRIPTION    + " TEXT," +
                    COLUMN_PHONE          + " TEXT," +
                    COLUMN_EMAIL          + " TEXT," +
                    COLUMN_ADDRESS        + " TEXT," +
                    COLUMN_LATITUDE       + " REAL," +
                    COLUMN_LONGITUDE      + " REAL)";

    // ----------------------- Constructor ----------------------- //
    public LeadTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_TITLE,
                                                    COLUMN_DESCRIPTION,
                                                    COLUMN_PHONE,
                                                    COLUMN_EMAIL,
                                                    COLUMN_ADDRESS,
                                                    COLUMN_LATITUDE,
                                                    COLUMN_LONGITUDE});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Lead> GetLeadsToSync() {
        // Get list of open tasks
        ArrayList<Task> openTasks = DbHelper.taskTable.GetOpenTasks();

        // Create list of leads in open tasks
        ArrayList<Lead> leads = new ArrayList<>();
        for (Task task : openTasks) {
            // Get Task lead
            Lead lead = (Lead) GetById(task.leadId);

            // Add unique leads to array
            if (!leads.contains(lead)) {
                leads.add(lead);
            }
        }
        return leads;
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long dbId               = cursor.getLong    (cursor.getColumnIndex(COLUMN_ID));
        long version            = cursor.getLong    (cursor.getColumnIndex(COLUMN_VERSION));
        String title            = cursor.getString  (cursor.getColumnIndex(COLUMN_TITLE));
        String description      = cursor.getString  (cursor.getColumnIndex(COLUMN_DESCRIPTION));
        String phone            = cursor.getString  (cursor.getColumnIndex(COLUMN_PHONE));
        String email            = cursor.getString  (cursor.getColumnIndex(COLUMN_EMAIL));
        String address          = cursor.getString  (cursor.getColumnIndex(COLUMN_ADDRESS));
        double latitude         = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LATITUDE));
        double longitude        = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LONGITUDE));

        return new Lead(dbId, version, title, description, phone, email, address, new LatLng(latitude, longitude));
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Lead lead = (Lead) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_ID,              lead.dbId);
        dbEntry.put(COLUMN_VERSION,         lead.version);
        dbEntry.put(COLUMN_TITLE,           lead.title);
        dbEntry.put(COLUMN_DESCRIPTION,     lead.description);
        dbEntry.put(COLUMN_PHONE,           lead.phone);
        dbEntry.put(COLUMN_EMAIL,           lead.email);
        dbEntry.put(COLUMN_ADDRESS,         lead.address);
        dbEntry.put(COLUMN_LATITUDE,        lead.position.latitude);
        dbEntry.put(COLUMN_LONGITUDE,       lead.position.longitude);

        return dbEntry;
    }
}

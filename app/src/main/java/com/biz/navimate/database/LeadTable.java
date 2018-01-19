package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

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
    public static final String COLUMN_SRV_ID           = "server_id";
    public static final String COLUMN_VERSION          = "version";
    public static final String COLUMN_TITLE            = "title";
    public static final String COLUMN_ADDRESS          = "address";
    public static final String COLUMN_LATITUDE         = "latitude";
    public static final String COLUMN_LONGITUDE        = "longitude";
    public static final String COLUMN_TEMPLATE_ID      = "template_id";
    public static final String COLUMN_DATA_ID          = "data_id";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID         + " INTEGER," +
                    COLUMN_VERSION        + " INTEGER," +
                    COLUMN_TITLE          + " TEXT," +
                    COLUMN_TEMPLATE_ID    + " INTEGER," +
                    COLUMN_DATA_ID        + " INTEGER," +
                    COLUMN_ADDRESS        + " TEXT," +
                    COLUMN_LATITUDE       + " REAL," +
                    COLUMN_LONGITUDE      + " REAL)";

    // ----------------------- Constructor ----------------------- //
    public LeadTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_TITLE,
                                                    COLUMN_DATA_ID,
                                                    COLUMN_TEMPLATE_ID,
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

    // API to get object by serverId
    public Lead GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
            Lead lead = (Lead) dbItem;
            if (lead.serverId == serverId) {
                return lead;
            }
        }

        return null;
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long   dbId             = cursor.getLong    (cursor.getColumnIndex(COLUMN_ID));
        long   serverId         = cursor.getLong    (cursor.getColumnIndex(COLUMN_SRV_ID));
        long   version          = cursor.getLong    (cursor.getColumnIndex(COLUMN_VERSION));
        String title            = cursor.getString  (cursor.getColumnIndex(COLUMN_TITLE));
        long   templateId       = cursor.getLong  (cursor.getColumnIndex(COLUMN_TEMPLATE_ID));
        long   dataId           = cursor.getLong  (cursor.getColumnIndex(COLUMN_DATA_ID));
        String address          = cursor.getString  (cursor.getColumnIndex(COLUMN_ADDRESS));
        double latitude         = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LATITUDE));
        double longitude        = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LONGITUDE));

        return new Lead(dbId, serverId, version, title, templateId, dataId, address, new LatLng(latitude, longitude));
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Lead lead = (Lead) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,          lead.serverId);
        dbEntry.put(COLUMN_VERSION,         lead.version);
        dbEntry.put(COLUMN_TITLE,           lead.title);
        dbEntry.put(COLUMN_ADDRESS,         lead.address);
        dbEntry.put(COLUMN_LATITUDE,        lead.position.latitude);
        dbEntry.put(COLUMN_LONGITUDE,       lead.position.longitude);
        dbEntry.put(COLUMN_DATA_ID,         lead.dataId);
        dbEntry.put(COLUMN_TEMPLATE_ID,     lead.templateId);

        return dbEntry;
    }
}

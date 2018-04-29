package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String COLUMN_VALUES           = "_values";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID         + " TEXT," +
                    COLUMN_VERSION        + " INTEGER," +
                    COLUMN_TITLE          + " TEXT," +
                    COLUMN_ADDRESS        + " TEXT," +
                    COLUMN_LATITUDE       + " REAL," +
                    COLUMN_LONGITUDE      + " REAL," +
                    COLUMN_TEMPLATE_ID    + " INTEGER," +
                    COLUMN_VALUES         + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public LeadTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_TITLE,
                                                    COLUMN_ADDRESS,
                                                    COLUMN_LATITUDE,
                                                    COLUMN_LONGITUDE,
                                                    COLUMN_TEMPLATE_ID,
                                                    COLUMN_VALUES});
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
    public Lead GetByServerId(String serverId) {
        for (DbObject dbItem : cache) {
            Lead lead = (Lead) dbItem;
            if (lead.textServerId.equals(serverId)) {
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
        String serverId         = cursor.getString  (cursor.getColumnIndex(COLUMN_SRV_ID));
        String title            = cursor.getString  (cursor.getColumnIndex(COLUMN_TITLE));
        String address          = cursor.getString  (cursor.getColumnIndex(COLUMN_ADDRESS));
        double latitude         = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LATITUDE));
        double longitude        = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LONGITUDE));

        long   templateId       = cursor.getLong    (cursor.getColumnIndex(COLUMN_TEMPLATE_ID));
        Template template       = (Template) DbHelper.templateTable.GetById(templateId);
        if (template == null) {
            template = new Template();
        }

        // Parse values into Form Entry objects
        ArrayList<FormEntry.Base> values = new ArrayList<>();
        try {
            JSONArray valuesJson    = new JSONArray(cursor.getString(cursor.getColumnIndex(COLUMN_VALUES)));
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);

                // Get field and string value
                Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
                Field field = (Field) DbHelper.fieldTable.GetById(fieldId);
                String value = valueJson.getString(Constants.Server.KEY_VALUE);

                // Add form entry object
                values.add(FormEntry.Parse(field, value));
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing task values");
            Dbg.stack(e);
        }

        return new Lead(dbId, serverId, title, address, new LatLng(latitude, longitude), template, values);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Lead lead = (Lead) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,          lead.textServerId);
        dbEntry.put(COLUMN_VERSION,         lead.version);
        dbEntry.put(COLUMN_TITLE,           lead.title);
        dbEntry.put(COLUMN_ADDRESS,         lead.address);
        dbEntry.put(COLUMN_LATITUDE,        lead.position.latitude);
        dbEntry.put(COLUMN_LONGITUDE,       lead.position.longitude);
        dbEntry.put(COLUMN_TEMPLATE_ID,     lead.template.dbId);

        // Prepare JSON Array for values
        JSONArray valuesJson = new JSONArray();
        try {
            for(FormEntry.Base value : lead.values) {
                JSONObject valueJson = new JSONObject();
                valueJson.put(Constants.Server.KEY_FIELD_ID, value.field.dbId);
                valueJson.put(Constants.Server.KEY_VALUE, value.toString());
                valuesJson.put(valueJson);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "JSON Exception while converting to DB string");
            Dbg.stack(e);
        }
        dbEntry.put(COLUMN_VALUES,          valuesJson.toString());

        return dbEntry;
    }
}

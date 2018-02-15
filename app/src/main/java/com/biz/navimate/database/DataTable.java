package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Siddharth on 07-12-2017.
 */

public class DataTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DATA_TABlE";

    // Table name
    public static final String TABLE_NAME       = "data_table";

    // Columns
    public static final String COLUMN_SRV_ID        = "server_id";
    public static final String COLUMN_VERSION       = "version";
    public static final String COLUMN_VALUE_IDS     = "valueIds";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "   + TABLE_NAME + " (" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID           + " INTEGER," +
                    COLUMN_VERSION          + " INTEGER," +
                    COLUMN_VALUE_IDS        + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public DataTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                COLUMN_SRV_ID,
                COLUMN_VERSION,
                COLUMN_VALUE_IDS});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Data> GetDataToSync() {
        ArrayList<Data> datas = new ArrayList<>();

        // Create list of default data in all open task templates
        ArrayList<Template> unsyncedTemplates = DbHelper.templateTable.GetTemplatesToSync();
        for (Template template : unsyncedTemplates) {
            // Get Template's default data
            Data data = (Data) DbHelper.dataTable.GetById(template.defaultDataId);
            datas.add(data);
        }

        // Add task templated data objects
        ArrayList<Task> tasks = DbHelper.taskTable.GetOpenTasks();
        for (Task task : tasks) {
            // Get Task's templated data
            Data data = (Data) DbHelper.dataTable.GetById(task.dataId);
            datas.add(data);
        }

        // Add lead templated data objects
        ArrayList<Lead> leads = DbHelper.leadTable.GetLeadsToSync();
        for (Lead lead : leads) {
            // Get Lead's templated data
            Data data = (Data) DbHelper.dataTable.GetById(lead.dataId);
            datas.add(data);
        }

        return datas;
    }

    // API to get object by serverId
    public Data GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
            Data data = (Data) dbItem;
            if (data.serverId == serverId) {
                return data;
            }
        }

        return null;
    }

    // API to remove a data object
    public void Remove(Data data) {
        // Remove all values in this data object
        for (Long valueId : data.valueIds) {
            DbHelper.valueTable.Remove(valueId);
        }

        // Remove Data Object
        Remove(data.dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long   dbId                    = cursor.getLong    (cursor.getColumnIndex(COLUMN_ID));
        long   serverId                = cursor.getLong    (cursor.getColumnIndex(COLUMN_SRV_ID));
        long version                   = cursor.getLong    (cursor.getColumnIndex(COLUMN_VERSION));
        String valuesString            = cursor.getString  (cursor.getColumnIndex(COLUMN_VALUE_IDS));

        // Create Field IDs array
        ArrayList<Long> valueIds = new ArrayList<>();
        try
        {
            JSONArray valuesJson = new JSONArray(valuesString);
            for (int i = 0; i < valuesJson.length(); i++) {
                valueIds.add(valuesJson.getLong(i));
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting JSON Array to Form Filed");
            Dbg.stack(e);
        }

        return new Data (dbId, serverId, version, valueIds);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Data data = (Data) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,         data.serverId);
        dbEntry.put(COLUMN_VERSION,        data.version);
        JSONArray valuesJson = new JSONArray(data.valueIds);
        dbEntry.put(COLUMN_VALUE_IDS, valuesJson.toString());

        return dbEntry;
    }
}

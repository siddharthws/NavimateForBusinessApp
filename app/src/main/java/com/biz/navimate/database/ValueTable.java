package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.Value;

import java.util.ArrayList;

/**
 * Created by Siddharth on 07-12-2017.
 */

public class ValueTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "VALUE_TABlE";

    // Table name
    public static final String TABLE_NAME       = "value_table";

    // Columns
    public static final String COLUMN_SRV_ID        = "server_id";
    public static final String COLUMN_VERSION       = "version";
    public static final String COLUMN_VALUE         = "value";
    public static final String COLUMN_FIELD_ID      = "fieldId";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "   + TABLE_NAME + " (" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID           + " INTEGER," +
                    COLUMN_VERSION          + " INTEGER," +
                    COLUMN_VALUE            + " TEXT," +
                    COLUMN_FIELD_ID         + " INTEGER)";

    // ----------------------- Constructor ----------------------- //
    public ValueTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_VALUE,
                                                    COLUMN_FIELD_ID});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Value> GetValuesToSync() {
        // Get list of open tasks
        ArrayList<Template> unsyncedTemplates = DbHelper.templateTable.GetTemplatesToSync();

        // Create list of form templates in open tasks
        ArrayList<Value> values = new ArrayList<>();
        for (Template template : unsyncedTemplates) {
            // Get Template's default data
            Data data = (Data) DbHelper.dataTable.GetById(template.defaultDataId);

            // Add all fields in this template
            for (Long valueId : data.valueIds) {
                values.add((Value) GetById(valueId));
            }
        }

        return values;
    }

    // API to get object by serverId
    public Value GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
            Value value = (Value) dbItem;
            if (value.serverId == serverId) {
                return value;
            }
        }

        return null;
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long   dbId                    = cursor.getLong    (cursor.getColumnIndex(COLUMN_ID));
        long   serverId                = cursor.getLong    (cursor.getColumnIndex(COLUMN_SRV_ID));
        long version                   = cursor.getLong    (cursor.getColumnIndex(COLUMN_VERSION));
        String value                   = cursor.getString  (cursor.getColumnIndex(COLUMN_VALUE));
        long fieldId                   = cursor.getLong    (cursor.getColumnIndex(COLUMN_FIELD_ID));

        return new Value (dbId, serverId, version, value, fieldId);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Value value = (Value) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,         value.serverId);
        dbEntry.put(COLUMN_VERSION,        value.version);
        dbEntry.put(COLUMN_VALUE,          value.value);
        dbEntry.put(COLUMN_FIELD_ID,       value.fieldId);

        return dbEntry;
    }
}

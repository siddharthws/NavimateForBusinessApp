package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;

import java.util.ArrayList;

/**
 * Created by Siddharth on 07-12-2017.
 */

public class FieldTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FIELD_TABlE";

    // Table name
    public static final String TABLE_NAME       = "field_table";

    // Columns
    public static final String COLUMN_SRV_ID        = "server_id";
    public static final String COLUMN_VERSION       = "version";
    public static final String COLUMN_TITLE         = "title";
    public static final String COLUMN_TYPE          = "type";
    public static final String COLUMN_VALUE         = "value";
    public static final String COLUMN_IS_MANDATORY  = "isMandatory";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "   + TABLE_NAME + " (" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID           + " INTEGER," +
                    COLUMN_VERSION          + " INTEGER," +
                    COLUMN_TITLE            + " TEXT," +
                    COLUMN_TYPE             + " INTEGER," +
                    COLUMN_VALUE            + " TEXT," +
                    COLUMN_IS_MANDATORY     + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public FieldTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_TITLE,
                                                    COLUMN_TYPE,
                                                    COLUMN_VALUE,
                                                    COLUMN_IS_MANDATORY});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Field> GetFieldsToSync() {
        // Get list of open tasks
        ArrayList<Template> unsyncedTemplates = DbHelper.templateTable.GetTemplatesToSync();

        // Create list of form templates in open tasks
        ArrayList<Field> fields = new ArrayList<>();
        for (Template template : unsyncedTemplates) {
            // Add all fields in this template
            for (Long fieldId : template.fieldIds) {
                fields.add((Field) GetById(fieldId));
            }
        }

        return fields;
    }

    // API to get object by serverId
    public Field GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
            Field field = (Field) dbItem;
            if (field.serverId == serverId) {
                return field;
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
        String title                   = cursor.getString  (cursor.getColumnIndex(COLUMN_TITLE));
        int type                       = cursor.getInt     (cursor.getColumnIndex(COLUMN_TYPE));
        String value                   = cursor.getString  (cursor.getColumnIndex(COLUMN_VALUE));
        boolean bMandatory             = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_IS_MANDATORY)));

        return new Field (dbId, serverId, version, title, type, value, bMandatory);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Field field = (Field) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,         field.serverId);
        dbEntry.put(COLUMN_VERSION,        field.version);
        dbEntry.put(COLUMN_TITLE,          field.title);
        dbEntry.put(COLUMN_TYPE,           field.type);
        dbEntry.put(COLUMN_VALUE,          field.value);
        dbEntry.put(COLUMN_IS_MANDATORY,   field.bMandatory);

        return dbEntry;
    }
}

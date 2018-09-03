package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.core.ObjDb;
import com.biz.navimate.objects.Field;
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
    public static final String COLUMN_TITLE         = "title";
    public static final String COLUMN_TYPE          = "type";
    public static final String COLUMN_VALUE         = "value";
    public static final String COLUMN_IS_MANDATORY  = "isMandatory";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "   + TABLE_NAME + " (" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID           + " INTEGER," +
                    COLUMN_TITLE            + " TEXT," +
                    COLUMN_TYPE             + " INTEGER," +
                    COLUMN_VALUE            + " TEXT," +
                    COLUMN_IS_MANDATORY     + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public FieldTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
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
            fields.addAll(template.fields);
        }

        return fields;
    }

    // API to get object by serverId
    public Field GetByServerId(long serverId) {
        for (ObjDb dbItem : cache) {
            Field field = (Field) dbItem;
            if (field.serverId == serverId) {
                return field;
            }
        }

        return null;
    }

    // API to remove a field
    public void Remove(long dbId) {
        // Remove Field
        RemoveById(dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected ObjDb ParseToObject(Cursor cursor) {
        return new Field(cursor);
    }

    @Override
    protected ContentValues ParseToContent(ObjDb dbItem) {
        return  ((Field) dbItem).toContentValues();
    }
}

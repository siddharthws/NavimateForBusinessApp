package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Template;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Siddharth on 07-12-2017.
 */

public class TemplateTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TEMPLATE_TABlE";

    // Table name
    public static final String TABLE_NAME       = "template_table";

    // Columns
    public static final String COLUMN_SRV_ID    = "server_id";
    public static final String COLUMN_VERSION   = "version";
    public static final String COLUMN_NAME      = "name";
    public static final String COLUMN_TYPE      = "type";
    public static final String COLUMN_DATA_ID   = "dataId";
    public static final String COLUMN_FIELD_IDS = "fieldIds";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID        + " INTEGER," +
                    COLUMN_VERSION       + " INTEGER," +
                    COLUMN_NAME          + " TEXT," +
                    COLUMN_TYPE          + " INTEGER," +
                    COLUMN_FIELD_IDS     + " TEXT," +
                    COLUMN_DATA_ID       + " INTEGER)";

    // ----------------------- Constructor ----------------------- //
    public TemplateTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_NAME,
                                                    COLUMN_TYPE,
                                                    COLUMN_DATA_ID,
                                                    COLUMN_FIELD_IDS});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Template> GetTemplatesToSync() {
        ArrayList<Template> templates = new ArrayList<>();

        // Sync all templates
        templates.addAll((CopyOnWriteArrayList<Template>) GetAll());

        return templates;
    }

    // API to get object by serverId
    public Template GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
            Template template = (Template) dbItem;
            if (template.serverId == serverId) {
                return template;
            }
        }

        return null;
    }

    // API to remove a template
    public void Remove(Template template) {
        // Remove all forms with this template
        ArrayList<Form> forms = DbHelper.formTable.GetByTemplate(template);
        for (Form form : forms) {
            DbHelper.formTable.Remove(form);
        }

        // Remove Default Data Objects
        Data data = (Data) DbHelper.dataTable.GetById(template.defaultDataId);
        DbHelper.dataTable.Remove(data);

        // Remove all fields
        for (Long fieldId : template.fieldIds) {
            DbHelper.fieldTable.Remove(fieldId);
        }

        // Remove template
        Remove(template.dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long   dbId                    = cursor.getLong    (cursor.getColumnIndex(COLUMN_ID));
        long   serverId                = cursor.getLong    (cursor.getColumnIndex(COLUMN_SRV_ID));
        long version                   = cursor.getLong    (cursor.getColumnIndex(COLUMN_VERSION));
        String name                    = cursor.getString  (cursor.getColumnIndex(COLUMN_NAME));
        int type                       = cursor.getInt     (cursor.getColumnIndex(COLUMN_TYPE));
        long defaultDataId             = cursor.getLong    (cursor.getColumnIndex(COLUMN_DATA_ID));
        String fieldsString            = cursor.getString  (cursor.getColumnIndex(COLUMN_FIELD_IDS));

        // Create Field IDs array
        ArrayList<Long> fieldIds = new ArrayList<>();
        try
        {
            JSONArray fieldsJson = new JSONArray(fieldsString);
            for (int i = 0; i < fieldsJson.length(); i++) {
                fieldIds.add(fieldsJson.getLong(i));
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting JSON Array to Form Filed");
            Dbg.stack(e);
        }

        return new Template (dbId, serverId, version, name, type, defaultDataId, fieldIds);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Template template = (Template) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,         template.serverId);
        dbEntry.put(COLUMN_VERSION,        template.version);
        dbEntry.put(COLUMN_NAME,           template.name);
        dbEntry.put(COLUMN_TYPE,           template.type);
        dbEntry.put(COLUMN_DATA_ID,        template.defaultDataId);
        JSONArray fields = new JSONArray(template.fieldIds);
        dbEntry.put(COLUMN_FIELD_IDS, fields.toString());

        return dbEntry;
    }
}

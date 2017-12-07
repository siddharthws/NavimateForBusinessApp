package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.FormField;
import com.biz.navimate.objects.Task;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Sai_Kameswari on 08-11-2017.
 */

public class FormTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM_TABlE";

    // Table name
    public static final String TABLE_NAME             = "form_table";

    // Columns
    public static final String COLUMN_SRV_ID    = "server_id";
    public static final String COLUMN_VERSION   = "version";
    public static final String COLUMN_NAME      = "name";
    public static final String COLUMN_DATA      = "data";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID        + " INTEGER," +
                    COLUMN_VERSION       + " INTEGER," +
                    COLUMN_NAME          + " TEXT," +
                    COLUMN_DATA          + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public FormTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_NAME,
                                                    COLUMN_DATA});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Form> GetFormTemplatesToSync() {
        // Get list of open tasks
        ArrayList<Task> openTasks = DbHelper.taskTable.GetOpenTasks();

        // Create list of form templates in open tasks
        ArrayList<Form> forms = new ArrayList<>();
        for (Task task : openTasks) {
            // Get Task form template
            Form form = (Form) GetById(task.formTemplateId);

            // Add to array
            if (!forms.contains(form)) {
                forms.add(form);
            }
        }

        return forms;
    }

    // API to get object by serverId
    public Form GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
            Form form = (Form) dbItem;
            if (form.serverId == serverId) {
                return form;
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
        String name                    = cursor.getString  (cursor.getColumnIndex(COLUMN_NAME));
        String fields                  = cursor.getString  (cursor.getColumnIndex(COLUMN_DATA));

        JSONArray fieldsJson = new JSONArray();
        try
        {
            fieldsJson  =   new JSONArray(fields);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting JSON Array to Form Filed");
            Dbg.stack(e);
        }

        return new Form (dbId, serverId, version, name, FormField.FromJsonArray(fieldsJson));
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Form form = (Form) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,         form.serverId);
        dbEntry.put(COLUMN_VERSION,        form.version);
        dbEntry.put(COLUMN_NAME,           form.name);

        JSONArray fields = new JSONArray();
        try
        {
            for (FormField.Base field : form.fields) {
                fields.put(field.toJson());
            }

        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting fields to String");
            Dbg.stack(e);
        }
        dbEntry.put(COLUMN_DATA,          fields.toString());

        return dbEntry;
    }
}

package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Sai_Kameswari on 08-11-2017.
 */

public class FormTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM_TABlE";

    // Table name
    public static final String TABLE_NAME             = "form_table";

    // Columns
    public static final String COLUMN_SRV_ID        = "server_id";
    public static final String COLUMN_VERSION       = "version";
    public static final String COLUMN_TEMPLATE_ID   = "template_id";
    public static final String COLUMN_TASK_ID       = "task_id";
    public static final String COLUMN_DATA_ID       = "data_id";
    public static final String COLUMN_CLOSE_TASK    = "close_task";
    public static final String COLUMN_LATITUDE      = "latitude";
    public static final String COLUMN_LONGITUDE     = "longitude";
    public static final String COLUMN_TIMESTAMP     = "timestamp";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID        + " INTEGER," +
                    COLUMN_VERSION       + " INTEGER," +
                    COLUMN_TEMPLATE_ID   + " INTEGER," +
                    COLUMN_TASK_ID       + " INTEGER," +
                    COLUMN_DATA_ID       + " INTEGER," +
                    COLUMN_CLOSE_TASK    + " TEXT," +
                    COLUMN_LATITUDE      + " REAL," +
                    COLUMN_LONGITUDE     + " REAL," +
                    COLUMN_TIMESTAMP     + " INTEGER)";

    // ----------------------- Constructor ----------------------- //
    public FormTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_TEMPLATE_ID,
                                                    COLUMN_TASK_ID,
                                                    COLUMN_DATA_ID,
                                                    COLUMN_CLOSE_TASK,
                                                    COLUMN_LATITUDE,
                                                    COLUMN_LONGITUDE,
                                                    COLUMN_TIMESTAMP});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Form> GetUnsyncedForms() {
        ArrayList<Form> forms = new ArrayList<>();

        // Create list of forms that have not been sent to server
        for (Form form : (CopyOnWriteArrayList<Form>) GetAll()) {
            if (form.serverId == Constants.Misc.ID_INVALID) {
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

    public ArrayList<Form> GetByTemplate(Template template) {
        ArrayList<Form> forms = new ArrayList<>();

        // Create list of forms that have not been sent to server
        for (Form form : (CopyOnWriteArrayList<Form>) GetAll()) {
            if (form.templateId == template.dbId) {
                forms.add(form);
            }
        }

        return forms;
    }

    public ArrayList<Form> GetByTask(Task task) {
        ArrayList<Form> forms = new ArrayList<>();

        // Create list of forms that have not been sent to server
        for (Form form : (CopyOnWriteArrayList<Form>) GetAll()) {
            if (form.task.dbId == task.dbId) {
                forms.add(form);
            }
        }

        return forms;
    }

    // API to remove a template
    public void Remove(Form form) {
        // Remove Form
        RemoveById(form.dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long   dbId                    = cursor.getLong    (cursor.getColumnIndex(COLUMN_ID));
        long   serverId                = cursor.getLong    (cursor.getColumnIndex(COLUMN_SRV_ID));
        long version                   = cursor.getLong    (cursor.getColumnIndex(COLUMN_VERSION));
        Long templateId                = cursor.getLong    (cursor.getColumnIndex(COLUMN_TEMPLATE_ID));
        Long taskId                    = cursor.getLong    (cursor.getColumnIndex(COLUMN_TASK_ID));
        Long dataId                    = cursor.getLong    (cursor.getColumnIndex(COLUMN_DATA_ID));
        Boolean bCloseTask             = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_CLOSE_TASK)));
        double latitude                = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LATITUDE));
        double longitude               = cursor.getDouble  (cursor.getColumnIndex(COLUMN_LONGITUDE));
        Long timestamp                 = cursor.getLong    (cursor.getColumnIndex(COLUMN_TIMESTAMP));

        return new Form (dbId, serverId, version, templateId, taskId, dataId, bCloseTask, new LatLng(latitude, longitude), timestamp);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Form form = (Form) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_SRV_ID,         form.serverId);
        dbEntry.put(COLUMN_VERSION,        form.version);
        dbEntry.put(COLUMN_TEMPLATE_ID,    form.templateId);
        dbEntry.put(COLUMN_TASK_ID,        form.taskId);
        dbEntry.put(COLUMN_DATA_ID,        form.dataId);
        dbEntry.put(COLUMN_CLOSE_TASK,     form.bCloseTask);
        dbEntry.put(COLUMN_LATITUDE,       form.latlng.latitude);
        dbEntry.put(COLUMN_LONGITUDE,      form.latlng.longitude);
        dbEntry.put(COLUMN_TIMESTAMP,      form.timestamp);

        return dbEntry;
    }
}

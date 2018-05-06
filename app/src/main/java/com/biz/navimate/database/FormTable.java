package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;

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
    public static final String COLUMN_TEMPLATE_ID   = "template_id";
    public static final String COLUMN_TASK_ID       = "task_id";
    public static final String COLUMN_CLOSE_TASK    = "close_task";
    public static final String COLUMN_LATITUDE      = "latitude";
    public static final String COLUMN_LONGITUDE     = "longitude";
    public static final String COLUMN_TIMESTAMP     = "timestamp";
    public static final String COLUMN_VALUES        = "_values";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID        + " INTEGER," +
                    COLUMN_TEMPLATE_ID   + " INTEGER," +
                    COLUMN_TASK_ID       + " INTEGER," +
                    COLUMN_VALUES        + " TEXT," +
                    COLUMN_CLOSE_TASK    + " TEXT," +
                    COLUMN_LATITUDE      + " REAL," +
                    COLUMN_LONGITUDE     + " REAL," +
                    COLUMN_TIMESTAMP     + " INTEGER)";

    // ----------------------- Constructor ----------------------- //
    public FormTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_TEMPLATE_ID,
                                                    COLUMN_TASK_ID,
                                                    COLUMN_VALUES,
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
            if (form.template.dbId == template.dbId) {
                forms.add(form);
            }
        }

        return forms;
    }

    public ArrayList<Form> GetByTask(Task task) {
        ArrayList<Form> forms = new ArrayList<>();

        // Create list of forms that have not been sent to server
        for (Form form : (CopyOnWriteArrayList<Form>) GetAll()) {
            if (form.task != null && form.task.dbId == task.dbId) {
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
    protected DbObject ParseToObject(Cursor cursor) {
        return new Form(cursor);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem) {
        return  ((Form) dbItem).toContentValues();
    }
}

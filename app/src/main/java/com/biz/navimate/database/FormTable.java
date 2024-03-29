package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.core.ObjForm;
import com.biz.navimate.objects.core.ObjDb;
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

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    Constants.DB.COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Constants.DB.COLUMN_DIRTY          + " TEXT," +
                    Constants.DB.COLUMN_SRV_ID        + " TEXT," +
                    Constants.DB.COLUMN_TEMPLATE_ID   + " INTEGER," +
                    Constants.DB.COLUMN_TASK_ID       + " INTEGER," +
                    Constants.DB.COLUMN_VALUES        + " TEXT," +
                    Constants.DB.COLUMN_CLOSE_TASK    + " TEXT," +
                    Constants.DB.COLUMN_LATITUDE      + " REAL," +
                    Constants.DB.COLUMN_LONGITUDE     + " REAL," +
                    Constants.DB.COLUMN_OWNER_ID      + " INTEGER," +
                    Constants.DB.COLUMN_TIMESTAMP     + " INTEGER)";

    // ----------------------- Constructor ----------------------- //
    public FormTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   Constants.DB.COLUMN_ID,
                                                    Constants.DB.COLUMN_DIRTY,
                                                    Constants.DB.COLUMN_SRV_ID,
                                                    Constants.DB.COLUMN_TEMPLATE_ID,
                                                    Constants.DB.COLUMN_TASK_ID,
                                                    Constants.DB.COLUMN_VALUES,
                                                    Constants.DB.COLUMN_CLOSE_TASK,
                                                    Constants.DB.COLUMN_LATITUDE,
                                                    Constants.DB.COLUMN_LONGITUDE,
                                                    Constants.DB.COLUMN_OWNER_ID,
                                                    Constants.DB.COLUMN_TIMESTAMP});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get dirty objects
    public ArrayList<ObjForm> GetDirty() {
        ArrayList<ObjForm> objs = new ArrayList<>();
        for (ObjDb dbItem : cache) {
            if (((ObjForm) dbItem).bDirty) {
                objs.add((ObjForm) dbItem);
            }
        }

        return objs;
    }

    // API to get object by serverId
    public ObjForm GetByServerId(String serverId) {
        for (ObjDb dbItem : cache) {
            ObjForm form = (ObjForm) dbItem;
            if (form.serverId.equals(serverId)) {
                return form;
            }
        }

        return null;
    }

    public ArrayList<ObjForm> GetByTemplate(Template template) {
        ArrayList<ObjForm> forms = new ArrayList<>();

        // Create list of forms that have not been sent to server
        for (ObjForm form : (CopyOnWriteArrayList<ObjForm>) GetAll()) {
            if (form.template.dbId == template.dbId) {
                forms.add(form);
            }
        }

        return forms;
    }

    public ArrayList<ObjForm> GetByTask(Task task) {
        ArrayList<ObjForm> forms = new ArrayList<>();

        // Create list of forms that have not been sent to server
        for (ObjForm form : (CopyOnWriteArrayList<ObjForm>) GetAll()) {
            if (form.task != null && form.task.dbId == task.dbId) {
                forms.add(form);
            }
        }

        return forms;
    }

    // API to remove a template
    public void Remove(ObjForm form) {
        // Remove Form
        RemoveById(form.dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected ObjDb ParseToObject(Cursor cursor) {
        return new ObjForm(cursor);
    }

    @Override
    protected ContentValues ParseToContent(ObjDb dbItem) {
        return  ((ObjForm) dbItem).toDb();
    }
}

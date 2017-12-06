package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Task;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class TaskTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TASK_TABlE";

    // Table name
    public static final String TABLE_NAME             = "task_table";

    // Columns
    public static final String COLUMN_LEAD_ID            = "lead_id";
    public static final String COLUMN_FORM_TEMPLATE_ID   = "form_template_id";
    public static final String COLUMN_STATUS             = "status";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY," +
                    COLUMN_VERSION          + " INTEGER," +
                    COLUMN_LEAD_ID          + " INTEGER," +
                    COLUMN_FORM_TEMPLATE_ID + " INTEGER," +
                    COLUMN_STATUS           + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public TaskTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_VERSION,
                                                    COLUMN_LEAD_ID,
                                                    COLUMN_FORM_TEMPLATE_ID,
                                                    COLUMN_STATUS});
    }

    // ----------------------- Private APIs ----------------------- //
    public ArrayList<Task> GetOpenTasks() {
        ArrayList<Task> openTasks = new ArrayList<>();
        for (Task task : (CopyOnWriteArrayList<Task>) DbHelper.taskTable.GetAll())
        {
            // Ignore closed task
            if (task.status == Task.TaskStatus.OPEN) {
                openTasks.add(task);
            }
        }

        return openTasks;
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        long   dbId                  = cursor.getLong    (cursor.getColumnIndex(COLUMN_ID));
        long   version               = cursor.getLong    (cursor.getColumnIndex(COLUMN_VERSION));
        long   leadId                = cursor.getLong    (cursor.getColumnIndex(COLUMN_LEAD_ID));
        long   formTemplateId        = cursor.getLong    (cursor.getColumnIndex(COLUMN_FORM_TEMPLATE_ID));
        Task.TaskStatus status       = Task.TaskStatus.valueOf(cursor.getString  (cursor.getColumnIndex(COLUMN_STATUS)));

        return new Task(dbId, version, leadId, formTemplateId, status);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Task task = (Task) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_ID,                task.dbId);
        dbEntry.put(COLUMN_VERSION,           task.version);
        dbEntry.put(COLUMN_LEAD_ID,           task.leadId);
        dbEntry.put(COLUMN_FORM_TEMPLATE_ID,  task.formTemplateId);
        dbEntry.put(COLUMN_STATUS,            task.status.name());

        return dbEntry;
    }
}

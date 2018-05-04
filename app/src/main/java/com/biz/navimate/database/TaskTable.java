package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;

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
    public static final String COLUMN_SRV_ID             = "server_id";
    public static final String COLUMN_LEAD_ID            = "lead_id";
    public static final String COLUMN_FORM_TEMPLATE_ID   = "form_template_id";
    public static final String COLUMN_STATUS             = "status";
    public static final String COLUMN_TEMPLATE_ID        = "template_id";
    public static final String COLUMN_VALUES             = "_values";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID           + " INTEGER," +
                    COLUMN_LEAD_ID          + " INTEGER," +
                    COLUMN_FORM_TEMPLATE_ID + " INTEGER," +
                    COLUMN_TEMPLATE_ID      + " INTEGER," +
                    COLUMN_VALUES           + " TEXT," +
                    COLUMN_STATUS           + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public TaskTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_LEAD_ID,
                                                    COLUMN_FORM_TEMPLATE_ID,
                                                    COLUMN_TEMPLATE_ID,
                                                    COLUMN_VALUES,
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

    public ArrayList<Task> GetClosedTasks() {
        ArrayList<Task> closedTasks = new ArrayList<>();
        for (Task task : (CopyOnWriteArrayList<Task>) DbHelper.taskTable.GetAll())
        {
            // Ignore closed task
            if (task.status == Task.TaskStatus.CLOSED) {
                closedTasks.add(task);
            }
        }

        return closedTasks;
    }

    // API to get object by serverId
    public Task GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
            Task task = (Task) dbItem;
            if (task.serverId == serverId) {
                return task;
            }
        }

        return null;
    }

    // API to get object by lead
    public ArrayList<Task> GetByLead(Lead lead) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (DbObject dbItem : cache) {
            Task task = (Task) dbItem;
            if (task.lead.equals(lead)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    // API to get object by template
    public ArrayList<Task> GetByTemplate(Template template) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (DbObject dbItem : cache) {
            Task task = (Task) dbItem;
            if (task.template.equals(template)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    // API to get object by form template
    public ArrayList<Task> GetByFormTemplate(Template template) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (DbObject dbItem : cache) {
            Task task = (Task) dbItem;
            if (task.formTemplate.equals(template)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    // API to remove task and associated objects
    public void Remove(Task task) {
        // Remove all associated forms
        ArrayList<Form> forms = DbHelper.formTable.GetByTask(task);
        for (Form form : forms) {
            DbHelper.formTable.Remove(form);
        }

        // Remove task object
        RemoveById(task.dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor) {
        return new Task(cursor);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem) {
        return  ((Task) dbItem).toContentValues();
    }
}

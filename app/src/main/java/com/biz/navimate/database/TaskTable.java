package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.core.ObjDb;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.core.ObjLead;
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

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    Constants.DB.COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Constants.DB.COLUMN_SRV_ID           + " TEXT," +
                    Constants.DB.COLUMN_PUBLIC_ID        + " TEXT," +
                    Constants.DB.COLUMN_LEAD_ID          + " INTEGER," +
                    Constants.DB.COLUMN_FORM_TEMPLATE_ID + " INTEGER," +
                    Constants.DB.COLUMN_TEMPLATE_ID      + " INTEGER," +
                    Constants.DB.COLUMN_VALUES           + " TEXT," +
                    Constants.DB.COLUMN_STATUS           + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public TaskTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   Constants.DB.COLUMN_ID,
                                                    Constants.DB.COLUMN_SRV_ID,
                                                    Constants.DB.COLUMN_PUBLIC_ID,
                                                    Constants.DB.COLUMN_LEAD_ID,
                                                    Constants.DB.COLUMN_FORM_TEMPLATE_ID,
                                                    Constants.DB.COLUMN_TEMPLATE_ID,
                                                    Constants.DB.COLUMN_VALUES,
                                                    Constants.DB.COLUMN_STATUS});
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
    public Task GetByServerId(String textServerId) {
        for (ObjDb dbItem : cache) {
            Task task = (Task) dbItem;
            if (task.textServerId.equals(textServerId)) {
                return task;
            }
        }

        return null;
    }

    // API to get object by lead
    public ArrayList<Task> GetByLead(ObjLead lead) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (ObjDb dbItem : cache) {
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
        for (ObjDb dbItem : cache) {
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
        for (ObjDb dbItem : cache) {
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
    protected ObjDb ParseToObject(Cursor cursor) {
        return new Task(cursor);
    }

    @Override
    protected ContentValues ParseToContent(ObjDb dbItem) {
        return  ((Task) dbItem).toContentValues();
    }
}

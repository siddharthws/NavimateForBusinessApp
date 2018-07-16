package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;

import java.util.ArrayList;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class LeadTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD_TABlE";

    // Table name
    public static final String TABLE_NAME             = "lead_table";

    // Columns
    public static final String COLUMN_SRV_ID           = "server_id";
    public static final String COLUMN_TITLE            = "title";
    public static final String COLUMN_ADDRESS          = "address";
    public static final String COLUMN_LATITUDE         = "latitude";
    public static final String COLUMN_LONGITUDE        = "longitude";
    public static final String COLUMN_TEMPLATE_ID      = "template_id";
    public static final String COLUMN_VALUES           = "_values";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID         + " TEXT," +
                    COLUMN_TITLE          + " TEXT," +
                    COLUMN_ADDRESS        + " TEXT," +
                    COLUMN_LATITUDE       + " REAL," +
                    COLUMN_LONGITUDE      + " REAL," +
                    COLUMN_TEMPLATE_ID    + " INTEGER," +
                    COLUMN_VALUES         + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public LeadTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_TITLE,
                                                    COLUMN_ADDRESS,
                                                    COLUMN_LATITUDE,
                                                    COLUMN_LONGITUDE,
                                                    COLUMN_TEMPLATE_ID,
                                                    COLUMN_VALUES});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Lead> GetLeadsToSync() {
        // Get list of open tasks
        ArrayList<Task> openTasks = DbHelper.taskTable.GetOpenTasks();

        // Create list of leads in open tasks
        ArrayList<Lead> leads = new ArrayList<>();
        for (Task task : openTasks) {
            // Add unique leads to array
            if (!leads.contains(task.lead)) {
                leads.add(task.lead);
            }
        }
        return leads;
    }

    // API to get object by serverId
    public Lead GetByServerId(String textServerId) {
        for (DbObject dbItem : cache) {
            Lead lead = (Lead) dbItem;
            if (lead.textServerId.equals(textServerId)) {
                return lead;
            }
        }

        return null;
    }

    // API to get object by template
    public ArrayList<Lead> GetByTemplate(Template template) {
        ArrayList<Lead> leads = new ArrayList<>();
        for (DbObject dbItem : cache) {
            Lead lead = (Lead) dbItem;
            if (lead.template.equals(template)) {
                leads.add(lead);
            }
        }

        return leads;
    }

    // API to remove a lead
    public void Remove(Lead lead) {
        // Remove all associated tasks
        ArrayList<Task> tasks = DbHelper.taskTable.GetByLead(lead);
        for (Task task : tasks) {
            DbHelper.taskTable.Remove(task);
        }

        // Remove this lead
        RemoveById(lead.dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor) {
        return new Lead(cursor);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem) {
        return  ((Lead) dbItem).toContentValues();
    }
}

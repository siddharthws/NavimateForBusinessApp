package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.core.ObjLead;
import com.biz.navimate.objects.core.ObjDb;
import com.biz.navimate.constants.Constants;
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

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    Constants.DB.COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Constants.DB.COLUMN_SRV_ID         + " TEXT," +
                    Constants.DB.COLUMN_TITLE          + " TEXT," +
                    Constants.DB.COLUMN_ADDRESS        + " TEXT," +
                    Constants.DB.COLUMN_LATITUDE       + " REAL," +
                    Constants.DB.COLUMN_LONGITUDE      + " REAL," +
                    Constants.DB.COLUMN_OWNER_ID       + " INTEGER," +
                    Constants.DB.COLUMN_TEMPLATE_ID    + " INTEGER," +
                    Constants.DB.COLUMN_VALUES         + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public LeadTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   Constants.DB.COLUMN_ID,
                                                    Constants.DB.COLUMN_SRV_ID,
                                                    Constants.DB.COLUMN_TITLE,
                                                    Constants.DB.COLUMN_ADDRESS,
                                                    Constants.DB.COLUMN_LATITUDE,
                                                    Constants.DB.COLUMN_LONGITUDE,
                                                    Constants.DB.COLUMN_OWNER_ID,
                                                    Constants.DB.COLUMN_TEMPLATE_ID,
                                                    Constants.DB.COLUMN_VALUES});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<ObjLead> GetLeadsToSync() {
        // Get list of open tasks
        ArrayList<Task> openTasks = DbHelper.taskTable.GetOpenTasks();

        // Create list of leads in open tasks
        ArrayList<ObjLead> leads = new ArrayList<>();
        for (Task task : openTasks) {
            // Add unique leads to array
            if (!leads.contains(task.lead)) {
                leads.add(task.lead);
            }
        }
        return leads;
    }

    // API to get object by serverId
    public ObjLead GetByServerId(String serverId) {
        for (ObjDb dbItem : cache) {
            ObjLead lead = (ObjLead) dbItem;
            if (lead.serverId.equals(serverId)) {
                return lead;
            }
        }

        return null;
    }

    // API to get object by template
    public ArrayList<ObjLead> GetByTemplate(Template template) {
        ArrayList<ObjLead> leads = new ArrayList<>();
        for (ObjDb dbItem : cache) {
            ObjLead lead = (ObjLead) dbItem;
            if (lead.template.equals(template)) {
                leads.add(lead);
            }
        }

        return leads;
    }

    // API to remove a lead
    public void Remove(ObjLead lead) {
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
    protected ObjDb ParseToObject(Cursor cursor) {
        return new ObjLead(cursor);
    }

    @Override
    protected ContentValues ParseToContent(ObjDb dbItem) {
        return  ((ObjLead) dbItem).toDb();
    }
}

package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.core.ObjForm;
import com.biz.navimate.objects.core.ObjLead;
import com.biz.navimate.objects.core.ObjDb;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;

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

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    Constants.DB.COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Constants.DB.COLUMN_SRV_ID        + " INTEGER," +
                    Constants.DB.COLUMN_NAME          + " TEXT," +
                    Constants.DB.COLUMN_TYPE          + " INTEGER," +
                    Constants.DB.COLUMN_FIELD_IDS     + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public TemplateTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   Constants.DB.COLUMN_ID,
                                                    Constants.DB.COLUMN_SRV_ID,
                                                    Constants.DB.COLUMN_NAME,
                                                    Constants.DB.COLUMN_TYPE,
                                                    Constants.DB.COLUMN_FIELD_IDS});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Template> GetTemplatesToSync() {
        ArrayList<Template> templates = new ArrayList<>();

        // Sync all templates
        templates.addAll((CopyOnWriteArrayList<Template>) GetAll());

        return templates;
    }

    // API to get object by type
    public ArrayList<Template> GetByType(int type) {
        ArrayList<Template> templates = new ArrayList<>();

        for (ObjDb dbItem : cache) {
            Template template = (Template) dbItem;
            if (template.type == type) {
                templates.add(template);
            }
        }

        return templates;
    }

    // API to get object by serverId
    public Template GetByServerId(long serverId) {
        for (ObjDb dbItem : cache) {
            Template template = (Template) dbItem;
            if (template.serverId == serverId) {
                return template;
            }
        }

        return null;
    }

    // API to remove a template
    public void Remove(Template template) {
        // Remove objects associated with this template
        switch (template.type) {
            case Constants.Template.TYPE_FORM: {
                // Remove all forms with this template
                ArrayList<ObjForm> forms = DbHelper.formTable.GetByTemplate(template);
                for (ObjForm form : forms) {
                    DbHelper.formTable.Remove(form);
                }

                // Remove all tasks with this form template
                ArrayList<Task> tasks = DbHelper.taskTable.GetByFormTemplate(template);
                for (Task task : tasks) {
                    DbHelper.taskTable.Remove(task);
                }
                break;
            }
            case Constants.Template.TYPE_LEAD: {
                // Remove all leads with this template
                ArrayList<ObjLead> leads = DbHelper.leadTable.GetByTemplate(template);
                for (ObjLead lead : leads) {
                    DbHelper.leadTable.Remove(lead);
                }
                break;
            }
            case Constants.Template.TYPE_TASK: {
                // Remove all tasks with this template
                ArrayList<Task> tasks = DbHelper.taskTable.GetByTemplate(template);
                for (Task task : tasks) {
                    DbHelper.taskTable.Remove(task);
                }
                break;
            }
        }

        // Remove all fields
        for (Field field : template.fields) {
            DbHelper.fieldTable.Remove(field.dbId);
        }

        // Remove template
        RemoveById(template.dbId);
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected ObjDb ParseToObject(Cursor cursor) {
        return new Template(cursor);
    }

    @Override
    protected ContentValues ParseToContent(ObjDb dbItem) {
        return  ((Template) dbItem).toContentValues();
    }
}

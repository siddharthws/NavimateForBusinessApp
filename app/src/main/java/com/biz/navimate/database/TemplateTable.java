package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;

import org.json.JSONArray;
import org.json.JSONException;

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

    // Columns
    public static final String COLUMN_SRV_ID    = "server_id";
    public static final String COLUMN_NAME      = "name";
    public static final String COLUMN_TYPE      = "type";
    public static final String COLUMN_FIELD_IDS = "fieldIds";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID        + " INTEGER," +
                    COLUMN_NAME          + " TEXT," +
                    COLUMN_TYPE          + " INTEGER," +
                    COLUMN_FIELD_IDS     + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public TemplateTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_SRV_ID,
                                                    COLUMN_NAME,
                                                    COLUMN_TYPE,
                                                    COLUMN_FIELD_IDS});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get valid objects for syncing
    public ArrayList<Template> GetTemplatesToSync() {
        ArrayList<Template> templates = new ArrayList<>();

        // Sync all templates
        templates.addAll((CopyOnWriteArrayList<Template>) GetAll());

        return templates;
    }

    // API to get object by serverId
    public Template GetByServerId(long serverId) {
        for (DbObject dbItem : cache) {
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
                ArrayList<Form> forms = DbHelper.formTable.GetByTemplate(template);
                for (Form form : forms) {
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
                ArrayList<Lead> leads = DbHelper.leadTable.GetByTemplate(template);
                for (Lead lead : leads) {
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
    protected DbObject ParseToObject(Cursor cursor) {
        return new Template(cursor);
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem) {
        return  ((Template) dbItem).toContentValues();
    }
}

package com.biz.navimate.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.database.TaskTable;
import com.biz.navimate.debug.Dbg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Task extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TASK";

    public static enum TaskStatus {
        OPEN(1), CLOSED(2);
        private int value;

        private static Map<Integer, TaskStatus> map = new HashMap<Integer, TaskStatus>();

        static {
            for (TaskStatus status : TaskStatus.values()) {
                map.put(status.value, status);
            }
        }

        private TaskStatus(final int value) { this.value = value; }

        public static TaskStatus valueOf(int value) {
            return map.get(value);
        }
    }

    // ----------------------- Globals ----------------------- //
    public String textServerId                  = "";
    public String publicId                      = "";
    public Lead lead                            = null;
    public Template formTemplate                = null;
    public Template template                    = null;
    public ArrayList<FormEntry.Base> values     = new ArrayList<>();
    public TaskStatus status                    = TaskStatus.OPEN;

    // ----------------------- Constructor ----------------------- //
    public Task (JSONObject json) {
        super(DbObject.TYPE_FIELD, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromJson(json);
    }

    public Task (Cursor cursor) {
        super(DbObject.TYPE_FIELD, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromCursor(cursor);
    }

    // ----------------------- Public APIs ----------------------- //
    //
    // Converter methods for JSON
    //
    public void fromJson(JSONObject json) {
        try {
            textServerId = json.getString(Constants.Server.KEY_ID);
            publicId     = json.getString(Constants.Server.KEY_PUBLIC_ID);
            status       = TaskStatus.valueOf(json.getInt(Constants.Server.KEY_STATUS));

            // Get Local Lead Object
            String leadId = json.getJSONObject(Constants.Server.KEY_LEAD).getString(Constants.Server.KEY_ID);
            lead = DbHelper.leadTable.GetByServerId(leadId);

            // Get Local Form Template Object
            long formTemplateId     = json.getLong(Constants.Server.KEY_FORM_TEMPLATE_ID);
            formTemplate = DbHelper.templateTable.GetByServerId(formTemplateId);

            // Get Local Template Object
            long templateId         = json.getLong(Constants.Server.KEY_TEMPLATE_ID);
            template = DbHelper.templateTable.GetByServerId(templateId);

            // Parse values into Form Entry objects
            JSONArray valuesJson    = json.getJSONArray(Constants.Server.KEY_VALUES);
            values = new ArrayList<>();
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);

                // Get field and string value
                Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
                Field field = (Field) DbHelper.fieldTable.GetByServerId(fieldId);
                String value = valueJson.getString(Constants.Server.KEY_VALUE);

                // Add form entry object
                values.add(FormEntry.Parse(field, value));
            }
        } catch (Exception e) {
            Dbg.error(TAG, "Exception whiel parsing from JSON");
            Dbg.stack(e);
        }
    }

    //
    // Converter methods for database
    //
    public void fromCursor(Cursor cursor)
    {
        dbId                = cursor.getLong    (cursor.getColumnIndex(TaskTable.COLUMN_ID));
        publicId            = cursor.getString  (cursor.getColumnIndex(TaskTable.COLUMN_PUBLIC_ID));
        textServerId        = cursor.getString  (cursor.getColumnIndex(TaskTable.COLUMN_SRV_ID));
        status              = Task.TaskStatus.valueOf(cursor.getString  (cursor.getColumnIndex(TaskTable.COLUMN_STATUS)));

        long   leadId                = cursor.getLong    (cursor.getColumnIndex(TaskTable.COLUMN_LEAD_ID));
        lead = (Lead) DbHelper.leadTable.GetById(leadId);

        long   formTemplateId        = cursor.getLong    (cursor.getColumnIndex(TaskTable.COLUMN_FORM_TEMPLATE_ID));
        formTemplate = (Template) DbHelper.templateTable.GetById(formTemplateId);

        long   templateId        = cursor.getLong    (cursor.getColumnIndex(TaskTable.COLUMN_TEMPLATE_ID));
        template = (Template) DbHelper.templateTable.GetById(templateId);

        // Parse values into Form Entry objects
        values = new ArrayList<>();
        try {
            JSONArray valuesJson    = new JSONArray(cursor.getString(cursor.getColumnIndex(TaskTable.COLUMN_VALUES)));
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);

                // Get field and string value
                Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
                Field field = (Field) DbHelper.fieldTable.GetById(fieldId);
                String value = valueJson.getString(Constants.Server.KEY_VALUE);

                // Add form entry object
                values.add(FormEntry.Parse(field, value));
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing task values");
            Dbg.stack(e);
        }
    }

    public ContentValues toContentValues () {
        ContentValues cv = new ContentValues();

        // Enter values into Database
        cv.put(TaskTable.COLUMN_SRV_ID,            textServerId);
        cv.put(TaskTable.COLUMN_PUBLIC_ID,         publicId);
        cv.put(TaskTable.COLUMN_LEAD_ID,           lead.dbId);
        cv.put(TaskTable.COLUMN_FORM_TEMPLATE_ID,  formTemplate.dbId);
        cv.put(TaskTable.COLUMN_TEMPLATE_ID,       template.dbId);
        cv.put(TaskTable.COLUMN_STATUS,            status.name());

        // Prepare JSON Array for values
        JSONArray valuesJson = new JSONArray();
        try {
            for(FormEntry.Base value : values) {
                JSONObject valueJson = new JSONObject();
                valueJson.put(Constants.Server.KEY_FIELD_ID, value.field.dbId);
                valueJson.put(Constants.Server.KEY_VALUE, value.toString());
                valuesJson.put(valueJson);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "JSON Exception while converting to DB string");
            Dbg.stack(e);
        }
        cv.put(TaskTable.COLUMN_VALUES,          valuesJson.toString());

        return cv;
    }
}

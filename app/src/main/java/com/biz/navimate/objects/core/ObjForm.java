package com.biz.navimate.objects.core;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.database.FormTable;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.ServerObject;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.core.ObjDb;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class ObjForm extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_FORM";

    // ----------------------- Globals ----------------------- //
    public String textServerId                  = "";
    public Boolean bCloseTask                   = false;
    public Long timestamp                       = 0L;
    public LatLng latlng                        = new LatLng(0, 0);
    public Task task                            = null;
    public Template template                    = null;
    public ArrayList<FormEntry.Base> values     = new ArrayList<>();

    // ----------------------- Constructor ----------------------- //
    public ObjForm() {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
    }

    public ObjForm(Task task) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        this.task = task;
        this.template = task.formTemplate;
        this.bCloseTask = task.status == Task.TaskStatus.OPEN ? false : true;
    }

    public ObjForm(JSONObject json) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromJson(json);
    }

    public ObjForm(Cursor cursor) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromCursor(cursor);
    }

    // ----------------------- Public APIs ----------------------- //
    //
    // Converter methods for JSON
    //
    public void fromJson(JSONObject formJson) {
        try {
            textServerId        = formJson.getString(Constants.Server.KEY_ID);

            String status = formJson.getString(Constants.Server.KEY_STATUS);
            bCloseTask          = ((status != null) && (status.equals(Task.TaskStatus.CLOSED.name())));

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.Date.FORMAT_BACKEND);
            timestamp           = sdf.parse(formJson.getString(Constants.Server.KEY_SUBMIT_TIME)).getTime();

            double latitude     = formJson.getDouble(Constants.Server.KEY_LAT);
            double longitude    = formJson.getDouble(Constants.Server.KEY_LNG);
            latlng = new LatLng(latitude, longitude);

            // Get Local Template Object
            long templateId         = formJson.getLong(Constants.Server.KEY_TEMPLATE_ID);
            template = DbHelper.templateTable.GetByServerId(templateId);

            // Get Local Template Object
            JSONObject taskJson = formJson.getJSONObject(Constants.Server.KEY_TASK);
            if (taskJson != null) {
                String taskId   = taskJson.getString(Constants.Server.KEY_ID);
                task            = DbHelper.taskTable.GetByServerId(taskId);
            }

            // Parse values into Form Entry objects
            JSONArray valuesJson    = formJson.getJSONArray(Constants.Server.KEY_VALUES);
            values = new ArrayList<>();
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);

                // Get field and string value
                Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
                Field field = DbHelper.fieldTable.GetByServerId(fieldId);
                String value = valueJson.getString(Constants.Server.KEY_VALUE);

                // Add form entry object
                values.add(FormEntry.Parse(field, value));
            }
        } catch (Exception e) {
            Dbg.error(TAG, "Exception while converting from JSON");
            Dbg.stack(e);
        }
    }

    public JSONObject toJson() {
        JSONObject formJson = new JSONObject();

        try {
            // Add form properties
            formJson.put(Constants.Server.KEY_LATITUDE, latlng.latitude);
            formJson.put(Constants.Server.KEY_LONGITUDE, latlng.longitude);
            formJson.put(Constants.Server.KEY_TIMESTAMP, timestamp);
            formJson.put(Constants.Server.KEY_TASK_ID, task != null ? task.textServerId : Constants.Misc.ID_INVALID);
            formJson.put(Constants.Server.KEY_CLOSE_TASK, bCloseTask);
            formJson.put(Constants.Server.KEY_TEMPLATE_ID, template.serverId);

            // Create Values Array
            JSONArray valuesJson = new JSONArray();
            for (FormEntry.Base value : values) {
                // Create Value JSON
                JSONObject valueJson = new JSONObject();
                valueJson.put(Constants.Server.KEY_FIELD_ID, value.field.serverId);
                valueJson.put(Constants.Server.KEY_VALUE, value.toString());

                // Add to Values JSON Array
                valuesJson.put(valueJson);
            }
            formJson.put(Constants.Server.KEY_VALUES, valuesJson);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting sync data in object");
            Dbg.stack(e);
        }

        return formJson;
    }

    //
    // Converter methods for database
    //
    public void fromCursor(Cursor cursor) {
        super.fromDb(cursor);

        textServerId            = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_SRV_ID));
        bCloseTask              = Boolean.valueOf   (cursor.getString(cursor.getColumnIndex(Constants.DB.COLUMN_CLOSE_TASK)));
        timestamp               = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_TIMESTAMP));
        double latitude         = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LATITUDE));
        double longitude        = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LONGITUDE));
        latlng = new LatLng(latitude, longitude);


        long   templateId        = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_TEMPLATE_ID));
        template = (Template) DbHelper.templateTable.GetById(templateId);

        Long taskId = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_TASK_ID));
        task = (Task) DbHelper.taskTable.GetById(taskId);

        // Parse values into Form Entry objects
        values = new ArrayList<>();
        try {
            JSONArray valuesJson    = new JSONArray(cursor.getString(cursor.getColumnIndex(Constants.DB.COLUMN_VALUES)));
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
        ContentValues cv = super.toDb();

        // Enter values into Database
        cv.put(Constants.DB.COLUMN_SRV_ID,         textServerId);
        cv.put(Constants.DB.COLUMN_TEMPLATE_ID,    template.dbId);
        cv.put(Constants.DB.COLUMN_TASK_ID,        task != null ? task.dbId : Constants.Misc.ID_INVALID);
        cv.put(Constants.DB.COLUMN_CLOSE_TASK,     bCloseTask);
        cv.put(Constants.DB.COLUMN_LATITUDE,       latlng.latitude);
        cv.put(Constants.DB.COLUMN_LONGITUDE,      latlng.longitude);
        cv.put(Constants.DB.COLUMN_TIMESTAMP,      timestamp);

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
        cv.put(Constants.DB.COLUMN_VALUES,          valuesJson.toString());

        return cv;
    }
}

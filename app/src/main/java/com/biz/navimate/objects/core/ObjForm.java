package com.biz.navimate.objects.core;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Task;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class ObjForm extends ObjNvm {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_FORM";

    // ----------------------- Globals ----------------------- //
    public Boolean bCloseTask                   = false;
    public Long timestamp                       = 0L;
    public LatLng latlng                        = new LatLng(0, 0);
    public Task task                            = null;
    public long ownerId                         = 0L;

    // ----------------------- Constructor ----------------------- //
    public ObjForm() {
        super(Constants.Template.TYPE_FORM);
    }

    public ObjForm(Task task) {
        super(Constants.Template.TYPE_FORM);
        this.task = task;
        this.SetTemplate(task.formTemplate);
        this.bCloseTask = task.status == Task.TaskStatus.OPEN ? false : true;
    }

    public ObjForm(JSONObject json) {
        super(Constants.Template.TYPE_FORM);
        fromServer(json);
    }

    public ObjForm(Cursor cursor) {
        super(Constants.Template.TYPE_FORM);
        fromDb(cursor);
    }

    // ----------------------- Overrides ----------------------- //
    // Converter methods for JSON
    @Override
    public void fromServer(JSONObject json) {
        super.fromServer(json);

        try {
            String status = json.getString(Constants.Server.KEY_STATUS);
            bCloseTask          = ((status != null) && (status.equals(Task.TaskStatus.CLOSED.name())));

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.Date.FORMAT_BACKEND);
            timestamp           = sdf.parse(json.getString(Constants.Server.KEY_SUBMIT_TIME)).getTime();

            double latitude     = json.getDouble(Constants.Server.KEY_LAT);
            double longitude    = json.getDouble(Constants.Server.KEY_LNG);
            latlng = new LatLng(latitude, longitude);

            ownerId             = json.getJSONObject(Constants.Server.KEY_REP).getLong(Constants.Server.KEY_ID);

            // Get Local Template Object
            JSONObject taskJson = json.getJSONObject(Constants.Server.KEY_TASK);
            if (taskJson != null) {
                String taskId   = taskJson.getString(Constants.Server.KEY_ID);
                task            = DbHelper.taskTable.GetByServerId(taskId);
            }
        } catch (Exception e) {
            Dbg.error(TAG, "Exception while converting from JSON");
            Dbg.stack(e);
        }
    }

    @Override
    public JSONObject toServer() {
        JSONObject json = super.toServer();

        try {
            // Add form properties
            json.put(Constants.Server.KEY_LATITUDE, latlng.latitude);
            json.put(Constants.Server.KEY_LONGITUDE, latlng.longitude);
            json.put(Constants.Server.KEY_TIMESTAMP, timestamp);
            json.put(Constants.Server.KEY_TASK_ID, task != null ? task.textServerId : Constants.Misc.ID_INVALID);
            json.put(Constants.Server.KEY_CLOSE_TASK, bCloseTask);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting sync data in object");
            Dbg.stack(e);
        }

        return json;
    }

    // Converter methods for database
    @Override
    public void fromDb(Cursor cursor) {
        super.fromDb(cursor);

        bCloseTask              = Boolean.valueOf   (cursor.getString(cursor.getColumnIndex(Constants.DB.COLUMN_CLOSE_TASK)));
        timestamp               = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_TIMESTAMP));
        ownerId                 = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_OWNER_ID));

        double latitude         = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LATITUDE));
        double longitude        = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LONGITUDE));
        latlng = new LatLng(latitude, longitude);

        Long taskId = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_TASK_ID));
        task = (Task) DbHelper.taskTable.GetById(taskId);
    }

    @Override
    public ContentValues toDb() {
        ContentValues cv = super.toDb();

        // Enter values into Database
        cv.put(Constants.DB.COLUMN_TASK_ID,        task != null ? task.dbId : Constants.Misc.ID_INVALID);
        cv.put(Constants.DB.COLUMN_CLOSE_TASK,     bCloseTask);
        cv.put(Constants.DB.COLUMN_LATITUDE,       latlng.latitude);
        cv.put(Constants.DB.COLUMN_LONGITUDE,      latlng.longitude);
        cv.put(Constants.DB.COLUMN_OWNER_ID,       ownerId);
        cv.put(Constants.DB.COLUMN_TIMESTAMP,      timestamp);

        return cv;
    }

    // ----------------------- Public APIs ----------------------- //
    public boolean isOwned() {
        return ownerId == 0 || ownerId == Preferences.GetUser().appId;
    }
}

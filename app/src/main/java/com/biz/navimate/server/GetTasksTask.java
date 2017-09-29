package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.views.RlDialog;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class GetTasksTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_TASKS_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetTasks listener = null;
    public void SetListener(IfaceServer.GetTasks listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public GetTasksTask(Context parentContext)
    {
        super(parentContext, Constants.Server.URL_GET_TASKS);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Call Super
        super.doInBackground(params);

        // Parse Result
        if (IsResponseValid()) {
            // Parse JSON into tasks
            ArrayList<Task> tasks = new ArrayList<>();
            try {
                JSONArray tasksJson = responseJson.getJSONArray(Constants.Server.KEY_TASKS);
                for (int i = 0; i < tasksJson.length(); i++) {
                    JSONObject taskJson = tasksJson.getJSONObject(i);

                    // Task ID
                    int id = taskJson.getInt(Constants.Server.KEY_ID);

                    // Lead
                    JSONObject leadJson = taskJson.getJSONObject(Constants.Server.KEY_LEAD);
                    String title = leadJson.getString(Constants.Server.KEY_TITLE);
                    String description = leadJson.getString(Constants.Server.KEY_DESCRIPTION);
                    String phone = leadJson.getString(Constants.Server.KEY_PHONE);
                    String email = leadJson.getString(Constants.Server.KEY_EMAIL);
                    String address = leadJson.getString(Constants.Server.KEY_ADDRESS);
                    Double lat = leadJson.getDouble(Constants.Server.KEY_LATITUDE);
                    Double lng = leadJson.getDouble(Constants.Server.KEY_LONGITUDE);
                    Lead lead = new Lead(id, title, description, phone, email, address, new LatLng(lat, lng));

                    // Template
                    JSONObject templateJson = taskJson.getJSONObject(Constants.Server.KEY_TEMPLATE);
                    String name = templateJson.getString(Constants.Server.KEY_NAME);
                    JSONArray fieldsJson = templateJson.getJSONArray(Constants.Server.KEY_DATA);
                    Form template = new Form(name, fieldsJson);

                    // Add to tasks
                    tasks.add(new Task(id, lead, template));
                }

                // Set current Tasks if non-zero result received
                if (tasks.size() > 0) {
                    Statics.SetCurrentTasks(tasks);
                }
            } catch (JSONException e) {
                Dbg.error(TAG, "JSON Exception while parsing tasks");
                Dbg.stack(e);
            }
        } else {
            Dbg.error(TAG, "Task result not valid");
        }

        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {
        // Hide dialog
        RlDialog.Hide();

        if (IsResponseValid())
        {
            // Call listener
            if (listener != null)
            {
                listener.onTasksSuccess();
            }
        }
        else
        {
            // Call listener
            if (listener != null)
            {
                listener.onTasksFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

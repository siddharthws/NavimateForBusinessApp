package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.HomescreenActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.FormField;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.views.RlDialog;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class SubmitFormTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SUBMIT_FORM_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.SubmitForm listener = null;
    public void SetListener(IfaceServer.SubmitForm listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Form form;
    private int taskId;
    private boolean bCloseTask = false;
    private LatLng location = null;

    // ----------------------- Constructor ----------------------- //
    public SubmitFormTask(Context parentContext, Form form, int taskId, LatLng location, boolean bCloseTask)
    {
        super(parentContext, Constants.Server.URL_GET_TASKS);
        this.form = form;
        this.taskId = taskId;
        this.bCloseTask = bCloseTask;
        this.location = location;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute()
    {
        RlDialog.Show(new Dialog.Waiting("Submitting Form..."));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();

        try
        {
            requestJson.put(Constants.Server.KEY_TASK_ID, taskId);
            JSONArray fields = new JSONArray();
            for (FormField.Base field : form.fields) {
                fields.put(field.toJson());
            }
            requestJson.put(Constants.Server.KEY_DATA, fields);
            requestJson.put(Constants.Server.KEY_CLOSE_TASK, bCloseTask);
            requestJson.put(Constants.Server.KEY_LATITUDE, location.latitude);
            requestJson.put(Constants.Server.KEY_LONGITUDE, location.longitude);
        }
        catch (JSONException e)
        {
            Dbg.error(TAG, "Error while putting initData in JSON");
            return null;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        super.doInBackground(params);

        if (IsResponseValid())
        {
            //Store Data of Form in Database
            DbHelper.formTable.Add(form);
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
            // Toast success
            Dbg.Toast(parentContext, "Form submitted succesfully...", Toast.LENGTH_SHORT);

            // Update List if task was to be closed
            if (bCloseTask) {
                Statics.RemoveFromTasks(taskId);

                // Update homescreen list if it's showing
                BaseActivity currentActivity = App.GetCurrentActivity();
                if ((currentActivity != null) && (currentActivity.getClass().equals(HomescreenActivity.class))) {
                    ((HomescreenActivity) currentActivity).onTasksSuccess();
                }
            }

            // Call listener
            if (listener != null)
            {
                listener.onFormSubmitted();
            }
        }
        else
        {
            // Toast error
            Dbg.Toast(parentContext, "Unable to submit form !!!", Toast.LENGTH_SHORT);

            // Re-launch Submit form dialog with the filled form
            RlDialog.Show(new Dialog.SubmitForm(form, taskId, bCloseTask));

            // Call listener
            if (listener != null)
            {
                listener.onFormSubmitFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

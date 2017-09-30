package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Task;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;
import org.json.JSONObject;

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

    // ----------------------- Constructor ----------------------- //
    public SubmitFormTask(Context parentContext, Form form, int taskId, boolean bCloseTask)
    {
        super(parentContext, Constants.Server.URL_GET_TASKS);
        this.form = form;
        this.taskId = taskId;
        this.bCloseTask = bCloseTask;
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
        requestJson = new JSONObject();

        try
        {
            requestJson.put(Constants.Server.KEY_TASK_ID, taskId);
            requestJson.put(Constants.Server.KEY_DATA, form.GetFieldsJson());
            requestJson.put(Constants.Server.KEY_CLOSE_TASK, bCloseTask);
        }
        catch (JSONException e)
        {
            Dbg.error(TAG, "Error while putting initData in JSON");
            return null;
        }

        // Call Super
        super.doInBackground(params);

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

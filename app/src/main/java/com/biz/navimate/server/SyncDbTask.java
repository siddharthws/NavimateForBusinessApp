package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ServerObject;
import com.biz.navimate.objects.Task;
import com.biz.navimate.views.RlDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class SyncDbTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SYNC_DB_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.SyncTasks listener = null;
    public void SetListener(IfaceServer.SyncTasks listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private boolean bDialog = false;

    // ----------------------- Constructor ----------------------- //
    public SyncDbTask(Context parentContext, boolean bDialog) {
        super(parentContext, "");
        this.bDialog = bDialog;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute () {
        // Show waiting dialog if required
        if (bDialog) {
            RlDialog.Show(new Dialog.Waiting("Syncing Tasks..."));
        }
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Sync Tasks
        SyncTasks();

        // Sync Leads
        SyncLeads();

        // Sync Templates
        SyncTemplates();

        return null;
    }

    @Override
    public void onPostExecute (Void result) {
        // Dismiss waiting dialog if required
        if (bDialog) {
            RlDialog.Hide();
        }

        // Call listener
        if (listener != null) {
            listener.onTaskCompleted();
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Sync entry point functions
    private void SyncTasks () {
        // Get list of all open tasks
        ArrayList<Task> openTasks = DbHelper.taskTable.GetOpenTasks();

        // Create version objects
        JSONArray syncData = GetSyncData(openTasks);

        // Send to server
        PostToServer(syncData, Constants.Server.URL_SYNC_TASKS);

        // Parse response
        if (IsResponseValid()) {
            ParseTaskResponse();
        } else {
            Dbg.error(TAG, "Error while getting tasks form server");
        }
    }

    private void SyncLeads () {
        // Get list of all open tasks
        ArrayList<Lead> unsyncedLeads = DbHelper.leadTable.GetLeadsToSync();

        // Create version objects
        JSONArray syncData = GetSyncData(unsyncedLeads);

        // Send to server
        PostToServer(syncData, Constants.Server.URL_SYNC_LEADS);

        // Parse response
        if (IsResponseValid()) {
            ParseLeadResponse();
        } else {
            Dbg.error(TAG, "Error while getting leads form server");
        }
    }

    private void SyncTemplates () {
        // Get list of all open tasks
        ArrayList<Form> unsyncedTemplates = DbHelper.formTable.GetFormTemplatesToSync();

        // Create version objects
        JSONArray syncData = GetSyncData(unsyncedTemplates);

        // Send to server
        PostToServer(syncData, Constants.Server.URL_SYNC_TEMPLATES);

        // Parse response
        if (IsResponseValid()) {
            ParseTemplateResponse();
        } else {
            Dbg.error(TAG, "Error while getting templates form server");
        }
    }

    // Response parsing functions
    private void ParseTaskResponse() {
        try {
            // Get tasks Json array
            JSONArray tasksJson = responseJson.getJSONArray(Constants.Server.KEY_TASKS);

            // Parse each JSON Object to task object
            ArrayList<Task> tasks = new ArrayList<>();
            ArrayList<Lead> leads = new ArrayList<>();
            ArrayList<Form> forms = new ArrayList<>();
            for (int i = 0; i < tasksJson.length(); i++) {
                // Parse task JSOn to Taks object
                JSONObject taskJson = tasksJson.getJSONObject(i);
                Task task = Task.FromJson(taskJson);
                tasks.add(task);
            }

            // Save all new tasks / leads / templates in database
            for (Task task : tasks) {
                DbHelper.taskTable.Save(task);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting sync data in object");
            Dbg.stack(e);
        }
    }

    private void ParseLeadResponse() {
        try {
            // Get tasks Json array
            JSONArray leadsJson = responseJson.getJSONArray(Constants.Server.KEY_LEADS);

            // Parse each JSON Object to task object
            ArrayList<Lead> leads = new ArrayList<>();
            for (int i = 0; i < leadsJson.length(); i++) {
                JSONObject leadJson = leadsJson.getJSONObject(i);
                leads.add(Lead.FromJson(leadJson));
            }

            // Save all new tasks in database
            for (Lead lead : leads) {
                DbHelper.leadTable.Save(lead);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting sync data in object");
            Dbg.stack(e);
        }
    }

    private void ParseTemplateResponse() {
        try {
            // Get tasks Json array
            JSONArray templatesJson = responseJson.getJSONArray(Constants.Server.KEY_TEMPLATES);

            // Parse each JSON Object to task object
            ArrayList<Form> forms = new ArrayList<>();
            for (int i = 0; i < templatesJson.length(); i++) {
                JSONObject templateJson = templatesJson.getJSONObject(i);
                forms.add(Form.FromJson(templateJson));
            }

            // Save all new forms in database
            for (Form form : forms) {
                DbHelper.formTable.Save(form);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting sync data in object");
            Dbg.stack(e);
        }
    }

    // API to post request to server
    private void PostToServer(JSONArray syncData, String url) {
        // Set URL
        this.url = url;

        // Create request JSON
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Constants.Server.KEY_SYNC_DATA, syncData);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting initData in JSON");
            Dbg.stack(e);
        }

        // Update Okhttp request builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call super to send request
        super.doInBackground();
    }

    // API to get server recognizable sync data from db objects
    private JSONArray GetSyncData(ArrayList<? extends ServerObject> items) {
        JSONArray syncData = new JSONArray();

        // Iterate through db items and add version and id
        for (ServerObject item : items) {
            // Create sync object
            JSONObject syncObject = new JSONObject();
            try {
                syncObject.put(Constants.Server.KEY_ID, item.serverId);
                syncObject.put(Constants.Server.KEY_VERSION, item.version);
            } catch (JSONException e) {
                Dbg.error(TAG, "Error while putting sync data in object");
                Dbg.stack(e);
            }

            // Add to array
            syncData.put(syncObject);
        }

        return syncData;
    }
}

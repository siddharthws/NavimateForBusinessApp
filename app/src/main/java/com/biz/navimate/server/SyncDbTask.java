package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.activities.HomescreenActivity;
import com.biz.navimate.activities.TaskActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.core.ObjForm;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.core.ObjLead;
import com.biz.navimate.views.RlDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

/**
 * Created by Siddharth on 11-04-2018.
 */

public class SyncDbTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SYNC_TASK";

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
        super(parentContext, Constants.Server.URL_SYNC);
        this.bDialog = bDialog;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute () {
        // Show waiting dialog if required
        if (bDialog) {
            RlDialog.Show(new Dialog.Waiting("Syncing..."));
        }
    }

    @Override
    public Void doInBackground (Void... params) {
        try {
            // Prepare JSON for request
            JSONObject json = GetRequestJson();

            // Prepare Request Builder
            reqBuilder.method("POST", RequestBody.create(JSON, json.toString()));

            // Send request to server
            super.doInBackground();

            // Parse response
            if (IsResponseValid()) {
                ParseResponse();
            }

            // Send dirty leads to server
            SyncDirtyLeads();
        }catch(JSONException e) {
            Dbg.error(TAG, "Exception while parsing data for task");
            Dbg.stack(e);
        }

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

        // Refresh Homescreen Activity
        HomescreenActivity.RefreshHome();

        // Refresh Task Activity
        TaskActivity.RefreshList();
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private JSONObject GetRequestJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(Constants.Server.KEY_LAST_SYNC_TIME, Preferences.GetTaskSyncTime());

        return json;
    }

    private void ParseResponse() throws JSONException {
        // Parse Templates
        ParseTemplates(responseJson.getJSONObject(Constants.Server.KEY_TEMPLATES));

        // Parse leads
        ParseLeads(responseJson.getJSONObject(Constants.Server.KEY_LEADS));

        // Parse Tasks
        ParseTasks(responseJson.getJSONObject(Constants.Server.KEY_TASKS));

        // Parse Forms
        ParseForms(responseJson.getJSONObject(Constants.Server.KEY_FORMS));

        // Update Sync Time
        Preferences.SetTaskSyncTime(parentContext, System.currentTimeMillis());
    }

    private void ParseTemplates(JSONObject json) throws JSONException {
        // Save objects from response
        JSONArray templatesJson = json.getJSONArray(Constants.Server.KEY_TEMPLATES);
        for (int i = 0; i < templatesJson.length(); i++) {
            // Parse JSON into template object
            JSONObject templateJson = templatesJson.getJSONObject(i);

            // Get object by server ID or create new
            Template template = DbHelper.templateTable.GetByServerId(templateJson.getLong(Constants.Server.KEY_ID));
            if (template != null) {
                template.fromJson(templateJson);
            } else {
                template = new Template(templateJson);
            }

            // Save template object in DB
            DbHelper.templateTable.Save(template);
        }

        // Remove objects as per response
        JSONArray removeIds = json.getJSONArray(Constants.Server.KEY_REMOVE);
        for (int i = 0; i < removeIds.length(); i++) {
            Template template = DbHelper.templateTable.GetByServerId(removeIds.getLong(i));
            if (template != null) {
                DbHelper.templateTable.Remove(template);
            }
        }
    }

    private void ParseLeads(JSONObject json) throws JSONException {
        // Save objects from response
        JSONArray leadsJson = json.getJSONArray(Constants.Server.KEY_LEADS);
        for (int i = 0; i < leadsJson.length(); i++) {
            // Parse JSON into object
            JSONObject leadJson = leadsJson.getJSONObject(i);

            // Get object by server ID or create new
            ObjLead lead = DbHelper.leadTable.GetByServerId(leadJson.getString(Constants.Server.KEY_ID));
            if (lead != null) {
                lead.fromServer(leadJson);
            } else {
                lead = new ObjLead(leadJson);
            }

            // Save object in DB
            DbHelper.leadTable.Save(lead);
        }

        // Remove objects as per response
        JSONArray removeIds = json.getJSONArray(Constants.Server.KEY_REMOVE);
        for (int i = 0; i < removeIds.length(); i++) {
            ObjLead lead = DbHelper.leadTable.GetByServerId(removeIds.getString(i));
            if (lead != null) {
                DbHelper.leadTable.Remove(lead);
            }
        }
    }

    private void ParseTasks(JSONObject json) throws JSONException {
        // Save objects from response
        JSONArray tasksJson = json.getJSONArray(Constants.Server.KEY_TASKS);
        for (int i = 0; i < tasksJson.length(); i++) {
            // Parse JSON into object
            JSONObject taskJson = tasksJson.getJSONObject(i);

            // Get object by server ID or create new
            Task task = DbHelper.taskTable.GetByServerId(taskJson.getString(Constants.Server.KEY_ID));
            if (task != null) {
                task.fromJson(taskJson);
            } else {
                task = new Task(taskJson);
            }

            // Save object in DB
            DbHelper.taskTable.Save(task);
        }

        // Remove objects as per response
        JSONArray removeIds = json.getJSONArray(Constants.Server.KEY_REMOVE);
        for (int i = 0; i < removeIds.length(); i++) {
            Task task = DbHelper.taskTable.GetByServerId(removeIds.getString(i));
            if (task != null) {
                DbHelper.taskTable.Remove(task);
            }
        }
    }

    private void ParseForms(JSONObject json) throws JSONException {
        // Save objects from response
        JSONArray formsJson = json.getJSONArray(Constants.Server.KEY_FORMS);
        for (int i = 0; i < formsJson.length(); i++) {
            // Parse JSON into object
            JSONObject formJson = formsJson.getJSONObject(i);

            // Get object by server ID or create new
            ObjForm form = DbHelper.formTable.GetByServerId(formJson.getString(Constants.Server.KEY_ID));
            if (form != null) {
                form.fromJson(formJson);
            } else {
                form = new ObjForm(formJson);
            }

            // Save object in DB
            DbHelper.formTable.Save(form);
        }
    }

    private void SyncDirtyLeads() {
        // Get all dirty leads
        ArrayList<ObjLead> leads = DbHelper.leadTable.GetDirty();

        // Prep lead JSON
        JSONObject json = new JSONObject();
        try {
            JSONArray leadsJson = new JSONArray();
            for (ObjLead lead : leads) {
                leadsJson.put(lead.toServer());
            }

            json.put(Constants.Server.KEY_LEADS, leadsJson);
        } catch (JSONException e) {
            Dbg.error(TAG, "Could not parse leads into JSON");
            return;
        }

        // Execute server request
        this.url = Constants.Server.URL_EDIT_LEADS;
        reqBuilder.method("POST", RequestBody.create(JSON, json.toString()));
        super.doInBackground();

        // Parse response
        if (IsResponseValid()) {
            try {
                JSONArray ids = responseJson.getJSONArray(Constants.Server.KEY_IDS);
                for (int i = 0; i < ids.length(); i++) {
                    ObjLead lead = leads.get(i);
                    lead.serverId = ids.getString(i);
                    lead.bDirty = false;
                    DbHelper.leadTable.Save(lead);
                }
            } catch (JSONException e) {
                Dbg.error(TAG, "Could not parse lead IDs from response JSON");
                return;
            }
        }
    }
}

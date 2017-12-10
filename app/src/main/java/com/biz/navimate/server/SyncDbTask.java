package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ServerObject;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.Value;
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

        // Sync Templates
        SyncFields();

        // Sync Templates
        SyncDatas();

        // Sync Templates
        SyncValues();

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
        ArrayList<Template> unsyncedTemplates = DbHelper.templateTable.GetTemplatesToSync();

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

    private void SyncFields () {
        // Get list of all open tasks
        ArrayList<Field> unsyncedFields = DbHelper.fieldTable.GetFieldsToSync();

        // Create version objects
        JSONArray syncData = GetSyncData(unsyncedFields);

        // Send to server
        PostToServer(syncData, Constants.Server.URL_SYNC_FIELDS);

        // Parse response
        if (IsResponseValid()) {
            ParseFieldResponse();
        } else {
            Dbg.error(TAG, "Error while getting fields form server");
        }
    }

    private void SyncDatas () {
        // Get list of all open tasks
        ArrayList<Data> unsyncedData = DbHelper.dataTable.GetDataToSync();

        // Create version objects
        JSONArray syncData = GetSyncData(unsyncedData);

        // Send to server
        PostToServer(syncData, Constants.Server.URL_SYNC_DATA);

        // Parse response
        if (IsResponseValid()) {
            ParseDataResponse();
        } else {
            Dbg.error(TAG, "Error while getting data from server");
        }
    }

    private void SyncValues () {
        // Get list of all open tasks
        ArrayList<Value> unsyncedValues = DbHelper.valueTable.GetValuesToSync();

        // Create version objects
        JSONArray syncData = GetSyncData(unsyncedValues);

        // Send to server
        PostToServer(syncData, Constants.Server.URL_SYNC_VALUES);

        // Parse response
        if (IsResponseValid()) {
            ParseValueResponse();
        } else {
            Dbg.error(TAG, "Error while getting values from server");
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
            Dbg.error(TAG, "Error while parsing task response");
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
            Dbg.error(TAG, "Error while parsing lead response");
            Dbg.stack(e);
        }
    }

    private void ParseTemplateResponse() {
        try {
            // Get tasks Json array
            JSONArray templatesJson = responseJson.getJSONArray(Constants.Server.KEY_TEMPLATES);

            // Parse each JSON Object to task object
            ArrayList<Template> templates = new ArrayList<>();
            for (int i = 0; i < templatesJson.length(); i++) {
                JSONObject templateJson = templatesJson.getJSONObject(i);
                templates.add(Template.FromJson(templateJson));
            }

            // Save all new templates in database
            for (Template template : templates) {
                DbHelper.templateTable.Save(template);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing template response");
            Dbg.stack(e);
        }
    }

    private void ParseFieldResponse() {
        try {
            // Get tasks Json array
            JSONArray fieldsJson = responseJson.getJSONArray(Constants.Server.KEY_FIELDS);

            // Parse each JSON Object to Field object
            ArrayList<Field> fields = new ArrayList<>();
            for (int i = 0; i < fieldsJson.length(); i++) {
                JSONObject fieldJson = fieldsJson.getJSONObject(i);
                fields.add(Field.FromJson(fieldJson));
            }

            // Save all new forms in database
            for (Field field : fields) {
                DbHelper.fieldTable.Save(field);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing field response");
            Dbg.stack(e);
        }
    }

    private void ParseDataResponse() {
        try {
            // Get tasks Json array
            JSONArray datasJson = responseJson.getJSONArray(Constants.Server.KEY_DATA);

            // Parse each JSON Object to Field object
            ArrayList<Data> datas = new ArrayList<>();
            for (int i = 0; i < datasJson.length(); i++) {
                JSONObject dataJson = datasJson.getJSONObject(i);
                datas.add(Data.FromJson(dataJson));
            }

            // Save all new forms in database
            for (Data data : datas) {
                DbHelper.dataTable.Save(data);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing data response");
            Dbg.stack(e);
        }
    }

    private void ParseValueResponse() {
        try {
            // Get tasks Json array
            JSONArray valuesJson = responseJson.getJSONArray(Constants.Server.KEY_VALUES);

            // Parse each JSON Object to Field object
            ArrayList<Value> values = new ArrayList<>();
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);
                values.add(Value.FromJson(valueJson));
            }

            // Save all new forms in database
            for (Value value : values) {
                DbHelper.valueTable.Save(value);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing value response");
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

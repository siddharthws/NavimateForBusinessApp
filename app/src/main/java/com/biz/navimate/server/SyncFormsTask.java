package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.activities.HomescreenActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.Value;
import com.biz.navimate.views.RlDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Siddharth on 08-12-2017.
 */

public class SyncFormsTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SYNC_FORMS_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.SyncForms listener = null;
    public void SetListener(IfaceServer.SyncForms listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private int unsyncedCountBefore = 0;
    private ArrayList<Form> unsyncedForms = new ArrayList<>();
    private boolean bDialog = false;

    // ----------------------- Constructor ----------------------- //
    public SyncFormsTask(Context parentContext, boolean bDialog) {
        super(parentContext, "");
        this.bDialog = bDialog;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute () {
        // Show waiting dialog if required
        if (bDialog) {
            RlDialog.Show(new Dialog.Waiting("Syncing Forms..."));
        }

        // Save no. of unsynced forms
        unsyncedCountBefore = DbHelper.formTable.GetUnsyncedForms().size();
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Sync Tasks
        SyncForms();

        return null;
    }

    @Override
    public void onPostExecute (Void result) {
        // Dismiss waiting dialog if required
        if (bDialog) {
            RlDialog.Hide();
        }

        // Show toast
        int unsyncedFormsCount = DbHelper.formTable.GetUnsyncedForms().size();
        if (unsyncedFormsCount == 0) {
            Dbg.Toast(parentContext, "All forms have been synced...", Toast.LENGTH_LONG);
        } else if (unsyncedFormsCount == unsyncedCountBefore) {
            Dbg.Toast(parentContext, "No forms could be synced !!!", Toast.LENGTH_LONG);
        } else {
            Dbg.Toast(parentContext, "Some forms could not be synced !!!", Toast.LENGTH_LONG);
        }

        // Refresh Homescreen
        HomescreenActivity.Refresh();

        // Call listener
        if (listener != null) {
            listener.onFormsSynced();
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Sync entry point functions
    private void SyncForms () {
        // Get list of all unsynced forms
        unsyncedForms = DbHelper.formTable.GetUnsyncedForms();

        // Try syncing images for forms
        JSONArray formsJson = new JSONArray();
        for (Form form : unsyncedForms) {
            // Get images in this form
            ArrayList<String> images = GetFormImages(form);

            // Sync images with server
            boolean bImagesSynced = true;
            for (String image : images) {
                bImagesSynced = SyncImage(image);

                if (!bImagesSynced) {
                    break;
                }
            }


            if (!bImagesSynced) {
                // Skip this form submission if image sync failed
                continue;
            }

            // Add form Json to sync data
            formsJson.put(GetFormJson(form));
        }

        // Sync all form data with server
        if (formsJson.length() > 0) {
            PostToServer(formsJson, Constants.Server.URL_SYNC_FORMS);
        }

        // Parse response
        if (IsResponseValid()) {
            ParseFormResponse();
        } else {
            Dbg.error(TAG, "Error while getting tasks form server");
        }
    }

    private ArrayList<String> GetFormImages(Form form) {
        ArrayList<String> images = new ArrayList<>();

        // Get Form data
        Data data = (Data) DbHelper.dataTable.GetById(form.dataId);

        // Iterate through values
        for (Long valueId : data.valueIds) {
            // Get Value
            Value value = (Value) DbHelper.valueTable.GetById(valueId);

            // Get field
            Field field = (Field) DbHelper.fieldTable.GetById(value.fieldId);

            // Check if field type is image
            if (((field.type == Constants.Template.FIELD_TYPE_PHOTO) ||
                (field.type == Constants.Template.FIELD_TYPE_SIGN)) &&
                (value.value.length() > 0)) {
                images.add(value.value);
            }
        }

        return images;
    }

    private boolean SyncImage(String image) {
        // Set URL for uploading images
        this.url = Constants.Server.URL_UPLOAD_PHOTO;

        // Set response to null
        responseJson = null;

        // Get Image Path
        String imagePath = Statics.GetAbsolutePath(parentContext, image);

        // Check if image exists
        if (!new File(imagePath).exists()) {
            return false;
        }

        // Create Multipart request body
        MultipartRequestBody fileBody = new MultipartRequestBody(imagePath);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("uploadedFile", image, fileBody).build();
        reqBuilder.method("POST", requestBody);

        // Send Request
        super.doInBackground();

        // Validate response
        if (!IsResponseValid()) {
            return false;
        }

        return true;
    }

    // Response parsing functions
    private void ParseFormResponse() {
        try {
            // Get forms Json array
            JSONArray formsJson = responseJson.getJSONArray(Constants.Server.KEY_FORMS);
            for (int i = 0; i < formsJson.length(); i++) {
                // Get objects
                JSONObject formJson = formsJson.getJSONObject(i);
                JSONObject dataJson = formJson.getJSONObject(Constants.Server.KEY_DATA);
                JSONArray valuesJson = dataJson.getJSONArray(Constants.Server.KEY_VALUES);
                Form form = unsyncedForms.get(i);
                Data data = (Data) DbHelper.dataTable.GetById(form.dataId);

                // Update server Id and version of form object
                form.serverId = formJson.getLong(Constants.Server.KEY_ID);
                form.version = formJson.getLong(Constants.Server.KEY_VERSION);
                DbHelper.formTable.Save(form);

                // Update server Id and version of data object
                data.serverId = dataJson.getLong(Constants.Server.KEY_ID);
                data.version = dataJson.getLong(Constants.Server.KEY_VERSION);
                DbHelper.dataTable.Save(data);

                // Update server Id and Version of each value in the data
                for (int j = 0; j < valuesJson.length(); j++) {
                    JSONObject valueJson = valuesJson.getJSONObject(j);
                    Value value = (Value) DbHelper.valueTable.GetById(data.valueIds.get(j));

                    value.serverId = valueJson.getLong(Constants.Server.KEY_ID);
                    value.version = valueJson.getLong(Constants.Server.KEY_VERSION);
                    DbHelper.valueTable.Save(value);
                }
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing task response");
            Dbg.stack(e);
        }
    }

    // API to post request to server
    private void PostToServer(JSONArray syncData, String url) {
        // Set URL
        this.url = url;

        // Set response JSOn to null
        responseJson = null;

        // Create request JSON
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Constants.Server.KEY_FORMS, syncData);
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
    private JSONObject GetFormJson(Form form) {
        // Get template, task & data for this form
        long taskServerId = Constants.Misc.ID_INVALID;
        if (form.taskId != Constants.Misc.ID_INVALID) {
            Task task = (Task) DbHelper.taskTable.GetById(form.taskId);
            taskServerId = task.serverId;
        }
        Template template = (Template) DbHelper.templateTable.GetById(form.templateId);
        Data data = (Data) DbHelper.dataTable.GetById(form.dataId);

        // Create form Json
        JSONObject formJson = new JSONObject();
        try {
            // Create Values Array
            JSONArray valuesJson = new JSONArray();
            for (Long valueId : data.valueIds) {
                // Get Value and Field
                Value value = (Value) DbHelper.valueTable.GetById(valueId);
                Field field = (Field) DbHelper.fieldTable.GetById(value.fieldId);

                // Create Value JSON
                JSONObject valueJson = new JSONObject();
                valueJson.put(Constants.Server.KEY_FIELD_ID, field.serverId);
                valueJson.put(Constants.Server.KEY_VALUE, value.value);

                // Add to Values JSON Array
                valuesJson.put(valueJson);
            }

            // Create Data JSON
            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.Server.KEY_TEMPLATE_ID, template.serverId);
            dataJson.put(Constants.Server.KEY_VALUES, valuesJson);

            // Create Form Json
            formJson.put(Constants.Server.KEY_TASK_ID, taskServerId);
            formJson.put(Constants.Server.KEY_LATITUDE, form.latlng.latitude);
            formJson.put(Constants.Server.KEY_LONGITUDE, form.latlng.longitude);
            formJson.put(Constants.Server.KEY_TIMESTAMP, form.timestamp);
            formJson.put(Constants.Server.KEY_CLOSE_TASK, form.bCloseTask);
            formJson.put(Constants.Server.KEY_DATA, dataJson);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting sync data in object");
            Dbg.stack(e);
        }

        return formJson;
    }
}

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
    private ArrayList<Form> unsyncedForms = new ArrayList<>();
    private ArrayList<String> unsyncedImages = new ArrayList<>();
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

        // Call listener
        if (listener != null) {
            listener.onFormsSynced();
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Sync entry point functions
    private void SyncForms () {
        // Get list of all open tasks
        unsyncedForms = DbHelper.formTable.GetUnsyncedForms();

        // Create forms JSON Array
        JSONArray formsJson = GetFormsJson(unsyncedForms);

        // Send Images to server
        UploadImages();

        // Send to server
        PostToServer(formsJson, Constants.Server.URL_SYNC_FORMS);

        // Parse response
        if (IsResponseValid()) {
            ParseFormResponse();
        } else {
            Dbg.error(TAG, "Error while getting tasks form server");
        }
    }

    private void UploadImages() {
        // Set URL for images
        this.url = Constants.Server.URL_UPLOAD_PHOTO;

        // Send images one by one
        for (String image : unsyncedImages) {
            // Get Image Path
            String imagePath = Statics.GetAbsolutePath(parentContext, image);

            // Check if image exists
            if (!new File(imagePath).exists()) {
                continue;
            }

            // Create Multipart request body
            MultipartRequestBody fileBody = new MultipartRequestBody(imagePath);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("uploadedFile", image, fileBody).build();
            reqBuilder.method("POST", requestBody);

            // Send Request
            super.doInBackground();
        }
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
    private JSONArray GetFormsJson(ArrayList<Form> forms) {
        JSONArray formsJson = new JSONArray();

        // Iterate through db items and add version and id
        for (Form form : forms) {
            // Get template, task & data for this form
            Task task = (Task) DbHelper.taskTable.GetById(form.taskId);
            Template template = (Template) DbHelper.templateTable.GetById(task.formTemplateId);
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

                    // Add pending image uploads for Phot / Sign fields
                    if (((field.type == Constants.Template.FIELD_TYPE_PHOTO) ||
                         (field.type == Constants.Template.FIELD_TYPE_SIGN)) &&
                        (value.value.length() > 0)) {
                        unsyncedImages.add(value.value);
                    }

                    // Add to Values JSON Array
                    valuesJson.put(valueJson);
                }

                // Create Data JSON
                JSONObject dataJson = new JSONObject();
                dataJson.put(Constants.Server.KEY_TEMPLATE_ID, template.serverId);
                dataJson.put(Constants.Server.KEY_VALUES, valuesJson);

                // Create Form Json
                formJson.put(Constants.Server.KEY_TASK_ID, task.serverId);
                formJson.put(Constants.Server.KEY_LATITUDE, form.latlng.latitude);
                formJson.put(Constants.Server.KEY_LONGITUDE, form.latlng.longitude);
                formJson.put(Constants.Server.KEY_TIMESTAMP, form.timestamp);
                formJson.put(Constants.Server.KEY_CLOSE_TASK, form.bCloseTask);
                formJson.put(Constants.Server.KEY_DATA, dataJson);

                // Add to forms JSON Array
                formsJson.put(formJson);
            } catch (JSONException e) {
                Dbg.error(TAG, "Error while putting sync data in object");
                Dbg.stack(e);
            }
        }

        return formsJson;
    }
}

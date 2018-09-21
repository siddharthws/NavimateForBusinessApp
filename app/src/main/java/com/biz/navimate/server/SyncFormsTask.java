package com.biz.navimate.server;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.biz.navimate.activities.HomescreenActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.core.ObjForm;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
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
    private ArrayList<ObjForm> unsyncedForms = new ArrayList<>();
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
        unsyncedCountBefore = DbHelper.formTable.GetDirty().size();
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

        // Refresh Homescreen
        HomescreenActivity.RefreshHome();

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
        unsyncedForms = DbHelper.formTable.GetDirty();

        // Try syncing images for forms
        JSONArray formsJson = new JSONArray();
        for (ObjForm form : unsyncedForms) {
            // Sync images with server
            ArrayList<String> images = GetFormImages(form);
            boolean bImagesSynced = true;
            for (String image : images) {
                bImagesSynced = SyncImage(image);
                if (!bImagesSynced) {break;}
            }

            // Sync files with server
            boolean bFilesSynced = true;
            if (bImagesSynced) {
                ArrayList<String> files = GetFormFiles(form);
                for (String file : files) {
                    bFilesSynced = SyncFile(file);
                    if (!bFilesSynced) {break;}
                }
            }

            // Sync invoices with server
            boolean bInvoicesSynced = true;
            if (bInvoicesSynced) {
                ArrayList<Object> invoices = GetFormInvoices(form);
                for (Object invoice : invoices) {
                    bInvoicesSynced = SyncInvoice(invoice);
                    if (!bInvoicesSynced) {break;}
                }
            }

            if (!bImagesSynced || !bFilesSynced || !bInvoicesSynced) {
                // Skip this form submission if image sync failed
                continue;
            }

            // Add form Json to sync data
            formsJson.put(form.toServer());
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

    private ArrayList<String> GetFormImages(ObjForm form) {
        ArrayList<String> images = new ArrayList<>();

        // Iterate through values
        for (FieldValue value : form.values) {
            if (((value.field.type == Constants.Template.FIELD_TYPE_PHOTO) ||
                (value.field.type == Constants.Template.FIELD_TYPE_SIGN)) &&
                (value.toString().length() > 0)) {
                images.add(value.toString());
            }
        }

        return images;
    }

    private ArrayList<String> GetFormFiles(ObjForm form) {
        ArrayList<String> files = new ArrayList<>();

        // Iterate through values
        for (FieldValue value : form.values) {
            if ((value.field.type == Constants.Template.FIELD_TYPE_FILE) &&
                    (value.toString().length() > 0)) {
                files.add(value.toString());
            }
        }

        return files;
    }

    private ArrayList<Object> GetFormInvoices(ObjForm form) {
        // Placeholder
        return new ArrayList<>();
    }

    private boolean SyncImage(String image) {
        // Set URL for uploading images
        this.url = Constants.Server.URL_UPLOAD_PHOTO;

        // Set response to null
        responseJson = null;

        // Get Image Path
        String imagePath = Statics.GetAbsolutePath(parentContext, image, Environment.DIRECTORY_PICTURES);

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

    private boolean SyncFile(String filename) {
        // Set URL for uploading images
        this.url = Constants.Server.URL_UPLOAD_FILE;

        // Set response to null
        responseJson = null;

        // Get Image Path
        String absPath = Statics.GetAbsolutePath(parentContext, filename, Environment.DIRECTORY_DOCUMENTS);

        // Check if image exists
        File file = new File(absPath);
        if (!new File(absPath).exists()) {
            return false;
        }

        // Create Multipart request body
        MultipartRequestBody fileBody = new MultipartRequestBody(absPath);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("uploadedFile", filename, fileBody).build();
        reqBuilder.method("POST", requestBody);

        // Send Request
        super.doInBackground();

        // Validate response
        if (!IsResponseValid()) {
            return false;
        }

        // Delete File since succesfully synced
        file.delete();

        return true;
    }

    private boolean SyncInvoice(Object invoice) {
        // Placeholder
        return true;
    }

    // Response parsing functions
    private void ParseFormResponse() {
        try {
            // Get forms Json array
            JSONArray formsJson = responseJson.getJSONArray(Constants.Server.KEY_FORMS);
            for (int i = 0; i < formsJson.length(); i++) {
                // Update server Id of form object
                ObjForm form = unsyncedForms.get(i);
                form.serverId = formsJson.getString(i);
                form.bDirty = false;
                DbHelper.formTable.Save(form);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing response");
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
}

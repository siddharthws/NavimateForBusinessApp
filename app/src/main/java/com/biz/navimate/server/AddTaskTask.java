package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.views.RlDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

/**
 * Created by Sai_Kameswari on 06-02-2018.
 */

public class AddTaskTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ADD_TASK_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Globals ----------------------- //
    private long leadId;
    private long formTemplateId;
    private long templateId;
    private ArrayList<FormEntry.Base> entries;

    // ----------------------- Constructor ----------------------- //
    public AddTaskTask(Context parentContext, long leadId, long formTemplateId, long templateId, ArrayList<FormEntry.Base> entries)
    {
        super(parentContext, Constants.Server.URL_ADD_TASK);
        this.leadId = leadId;
        this.formTemplateId = formTemplateId;
        this.templateId = templateId;
        this.entries=entries;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute ()
    {
        RlDialog.Show(new Dialog.Waiting("Adding Task..."));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        try
        {
            requestJson.put(Constants.Server.KEY_LEAD_ID, leadId);
            requestJson.put(Constants.Server.KEY_FORM_TEMPLATE_ID, formTemplateId);
            requestJson.put(Constants.Server.KEY_TEMPLATE_ID, templateId);

            JSONArray valuesJson = new JSONArray();
            for (FormEntry.Base entry : entries) {
                // Get Value and Field
                Field field = (Field) DbHelper.fieldTable.GetById(entry.field.dbId);

                // Create Value JSON
                JSONObject valueJson = new JSONObject();
                valueJson.put(Constants.Server.KEY_FIELD_ID, field.serverId);
                valueJson.put(Constants.Server.KEY_VALUE, entry.toString());

                // Add to Values JSON Array
                valuesJson.put(valueJson);
            }
            // Create Data JSON
            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.Server.KEY_TEMPLATE_ID, templateId);
            dataJson.put(Constants.Server.KEY_VALUES, valuesJson);

            requestJson.put(Constants.Server.KEY_TEMPLATE_DATA, dataJson);
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

        return null;
    }
    @Override
    public void onPostExecute (Void result)
    {
        RlDialog.Hide();
        if (IsResponseValid())
        {
            Dbg.Toast(parentContext,"Task Created Successfully", Toast.LENGTH_SHORT);
            SyncDbTask syncTask = new SyncDbTask(parentContext, false);
            syncTask.execute();
        }
        else
        {
            Dbg.Toast(parentContext,"Task Creation Failed", Toast.LENGTH_SHORT);
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
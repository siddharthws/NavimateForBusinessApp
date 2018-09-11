package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.core.ObjNvm;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class GetObjectDetailsTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_OBJECT_DETAILS_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetObjectDetails listener = null;
    public void SetListener(IfaceServer.GetObjectDetails listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private int type = Constants.Template.TYPE_INVALID;
    private String id = "";

    private ObjNvm obj = null;

    // ----------------------- Constructor ----------------------- //
    public GetObjectDetailsTask(Context parentContext, int type, String id) {
        super(parentContext, Constants.Server.URL_GET_OBJECT_DETAILS);
        this.type       = type;
        this.id         = id;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute() {}

    @Override
    public Void doInBackground (Void... params) {
        // Try getting object from database
        GetFromDb();

        // If object not found, try getting from server
        if (obj == null) { GetFromServer(); }

        return null;
    }

    @Override
    public void onPostExecute (Void result) {
        if (listener != null) {
            if (obj != null) {
                listener.onObjectDetailsSuccess(obj);
            } else {
                listener.onObjectDetailsFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void GetFromDb() {
        obj = DbHelper.GetNvmObject(type, id);
    }

    private void GetFromServer() {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Constants.Server.KEY_TYPE, type);
            requestJson.put(Constants.Server.KEY_ID, id);
        }
        catch (JSONException e) {
            Dbg.error(TAG, "Error while preparing request");
            return;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        super.doInBackground();

        // Parse and save object in database
        if (IsResponseValid()) {
            obj = ObjNvm.newInstance(type, responseJson);
            DbHelper.SaveNvmObject(type, obj);
        }
    }
}
package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.core.ObjNvmCompact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class GetObjectListTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_OBJECT_LIST_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetObjectList listener = null;
    public void SetListener(IfaceServer.GetObjectList listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private int type = Constants.Template.TYPE_INVALID;
    private  String text   = "";

    private int startIndex = 0;
    private int count = 0;
    private int totalCount = 0;

    ArrayList<ObjNvmCompact> objects = null;

    // ----------------------- Constructor ----------------------- //
    public GetObjectListTask(Context parentContext, int type, int startIndex, int count, String text) {
        super(parentContext, Constants.Server.URL_GET_OBJECT_LIST);
        this.type       = type;
        this.startIndex = startIndex;
        this.count      = count;
        this.text       = text;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute() {}

    @Override
    public Void doInBackground (Void... params) {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Constants.Server.KEY_TYPE, type);
            requestJson.put(Constants.Server.KEY_TEXT, text);

            JSONObject pager = new JSONObject();
            pager.put(Constants.Server.KEY_START, startIndex);
            pager.put(Constants.Server.KEY_COUNT, count);
            requestJson.put(Constants.Server.KEY_PAGER, pager);
        }
        catch (JSONException e) {
            Dbg.error(TAG, "Error while preparing request");
            return null;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        super.doInBackground(params);

        // Save Product in Database
        if (IsResponseValid()) {
            try{
                objects = new ArrayList<>();
                JSONArray objectsJson = responseJson.getJSONArray(Constants.Server.KEY_RESULTS);
                this.totalCount = responseJson.getInt(Constants.Server.KEY_TOTAL_COUNT);

                // Parse json data to array
                for(int i =0 ; i < objectsJson.length(); i++){
                    JSONObject obj = objectsJson.getJSONObject(i);
                    String id = obj.getString(Constants.Server.KEY_ID);
                    String name = obj.getString(Constants.Server.KEY_NAME);

                    objects.add(new ObjNvmCompact(id, name));
                }
            } catch (JSONException e) {
                Dbg.error(TAG, "Could not parse result json");
                Dbg.stack(e);
            }
        }
        return null;
    }

    @Override
    public void onPostExecute (Void result) {
        if (objects != null) {
            // Call listener
            if (listener != null) {
                listener.onObjectListSuccess(objects, totalCount);
            }
        } else {
            Dbg.Toast(parentContext, "Could not get results...", Toast.LENGTH_SHORT);

            // Call listener
            if (listener != null) {
                listener.onObjectListFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
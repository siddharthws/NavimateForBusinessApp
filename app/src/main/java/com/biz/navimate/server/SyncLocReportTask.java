package com.biz.navimate.server;

import android.content.Context;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.LocationReportObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.CopyOnWriteArrayList;
import okhttp3.RequestBody;

/**
 * Created by Sai_Kameswari on 01-03-2018.
 */

public class SyncLocReportTask extends BaseServerTask {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SYNC_LOC_REPORT";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.SyncLocReport listener = null;
    public void SetListener(IfaceServer.SyncLocReport listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public SyncLocReportTask(Context parentContext)
    {
        super(parentContext, Constants.Server.URL_LOCATION_REPORT);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        try
        {
            requestJson.put(Constants.Server.KEY_REPORT, getReportJsonArray());
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

        // If successful, deleete all records in DB
        if (IsResponseValid()) {
            // Remove all entries except for last
            LocationReportObject latest = DbHelper.locationReportTable.GetLatest();
            DbHelper.locationReportTable.Clear();
            DbHelper.locationReportTable.Save(latest);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        if (IsResponseValid())
        {
            // Call listener
            if (listener != null)
            {
                listener.onLocReportSynced();
            }
        }
        else
        {
            Dbg.error(TAG, "Response not valid");

            // Call listener
            if (listener != null)
            {
                listener.onLocReportSyncFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private JSONArray getReportJsonArray()
    {
        JSONArray reportJsonArray = new JSONArray();

        // Get all records in Loc report Table
       for(LocationReportObject reportObj : DbHelper.locationReportTable.GetReportsToSync()) {
           try {
               // Create JSOn Obejct for this record
               JSONObject recordJson = new JSONObject();
               recordJson.put(Constants.Server.KEY_LATITUDE, reportObj.latitude);
               recordJson.put(Constants.Server.KEY_LONGITUDE, reportObj.longitude);
               recordJson.put(Constants.Server.KEY_TIMESTAMP, reportObj.timestamp);
               recordJson.put(Constants.Server.KEY_STATUS, reportObj.status);
               recordJson.put(Constants.Server.KEY_SPEED, reportObj.speed);
               recordJson.put(Constants.Server.KEY_BATTERY, reportObj.battery);

               //Put record in JsonArray
               reportJsonArray.put(recordJson);
           }
           catch (JSONException e) {
               Dbg.error(TAG, "Error while putting creating report JSON");
               return null;
           }
       }

       return reportJsonArray;
    }
}
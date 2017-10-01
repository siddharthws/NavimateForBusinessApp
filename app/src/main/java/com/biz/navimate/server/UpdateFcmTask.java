package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class UpdateFcmTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "UPDATE_FCM_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.UpdateFcm listener = null;
    public void SetListener(IfaceServer.UpdateFcm listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private String fcmId = "";

    // ----------------------- Constructor ----------------------- //
    public UpdateFcmTask(Context parentContext, String fcmId)
    {
        super(parentContext, Constants.Server.URL_UPDATE_FCM);
        this.fcmId = fcmId;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        requestJson = new JSONObject();

        try
        {
            requestJson.put(Constants.Server.KEY_FCM, fcmId);
        }
        catch (JSONException e)
        {
            Dbg.error(TAG, "Error while putting initData in JSON");
            return null;
        }

        // Call Super
        super.doInBackground(params);

        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {

        if (IsResponseValid())
        {
            // Call listener
            if (listener != null)
            {
                listener.onFcmUpdated();
            }
        }
        else
        {
            // Call listener
            if (listener != null)
            {
                listener.onFcmFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

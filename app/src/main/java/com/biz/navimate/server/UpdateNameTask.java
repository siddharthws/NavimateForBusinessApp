package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.User;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

/**
 * Created by Siddharth on 08-03-2018.
 */

public class UpdateNameTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "UPDATE_NAME_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.UpdateName listener = null;
    public void SetListener(IfaceServer.UpdateName listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private String name = "";

    // ----------------------- Constructor ----------------------- //
    public UpdateNameTask(Context parentContext, String name)
    {
        super(parentContext, Constants.Server.URL_UPDATE_NAME);
        this.name = name;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute ()
    {
        // Show waiting dialog
        RlDialog.Show(new Dialog.Waiting("Updating name..."));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();

        try
        {
            requestJson.put(Constants.Server.KEY_NAME, name);
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

        // Update name in preferences
        if (IsResponseValid()) {
            User user = Preferences.GetUser();
            user.name = name;
            Preferences.SetUser(parentContext, user);
        }

        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {
        // Hide dialog
        RlDialog.Hide();

        if (IsResponseValid())
        {
            // Call listener
            if (listener != null)
            {
                listener.onNameUpdated();
            }
        }
        else
        {
            // Call listener
            if (listener != null)
            {
                listener.onNameFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

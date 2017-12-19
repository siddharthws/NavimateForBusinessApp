package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

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
 * Created by Siddharth on 05-11-2017.
 */

public class RegisterTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "REGISTER_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.Register listener = null;
    public void SetListener(IfaceServer.Register listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private String name, phoneNumber = "";

    // ----------------------- Constructor ----------------------- //
    public RegisterTask(Context parentContext, String phoneNumber, String name)
    {
        super(parentContext, Constants.Server.URL_REGISTER);
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute() {
        // Show waiting dialog
        RlDialog.Show(new Dialog.Waiting("Registering..."));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();

        try
        {
            requestJson.put(Constants.Server.KEY_PHONE, phoneNumber);
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

        // Register user in preferences if server returned OK
        if (IsResponseValid()) {
            try {
                int appId = responseJson.getInt(Constants.Server.KEY_ID);

                // Register the user in preferences
                Preferences.SetUser(parentContext, new User(name, phoneNumber, "", appId));

            } catch (JSONException e) {
                Dbg.error(TAG, "Unable to extract App ID from registration response");
            }
        }

        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {
        RlDialog.Hide();

        if (IsResponseValid())
        {
            Dbg.Toast(parentContext, "Registration complete...", Toast.LENGTH_SHORT);

            // Call listener
            if (listener != null)
            {
                listener.onRegisterSuccess();
            }
        }
        else
        {
            Dbg.Toast(parentContext, "Unable to register...", Toast.LENGTH_SHORT);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

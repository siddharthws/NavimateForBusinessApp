package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.User;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class GetProfileTask extends BaseServerTask
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_PROFILE_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetProfile listener = null;
    public void SetListener(IfaceServer.GetProfile listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private String phoneNo = "";

    // ----------------------- Constructor ----------------------- //
    public GetProfileTask(Context parentContext, String phoneNo)
    {
        super(parentContext, Constants.Server.URL_GET_PROFILE);
        this.phoneNo = phoneNo;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute()
    {
        RlDialog.Show(new Dialog.Waiting("Getting account information..."));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        try
        {
            requestJson.put(Constants.Server.KEY_PHONE, phoneNo);
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
        // Hide dialog
        RlDialog.Hide();

        if (IsResponseValid())
        {
            try {
                // Create user object from data received
                User profile = new User(responseJson.getString(Constants.Server.KEY_NAME),
                                        responseJson.getString(Constants.Server.KEY_PHONE),
                                        responseJson.getString(Constants.Server.KEY_EMAIL),
                                        responseJson.getInt(Constants.Server.KEY_ID));

                // Call listener
                if (listener != null)
                {
                    listener.onProfileReceived(profile);
                }
            } catch (JSONException e) {
                Dbg.error(TAG, "JSON exception while reading response data");
                Dbg.stack(e);
            }
        }
        else
        {
            // Show error toast
            Dbg.Toast(parentContext, "Phone number not registered...", Toast.LENGTH_SHORT);

            // Call listener
            if (listener != null)
            {
                listener.onProfileFailed();
            }
        }
    }

    @Override
    protected boolean IsResponseValid()
    {
        // Check base response
        if (!super.IsResponseValid())
        {
            Dbg.error(TAG, "Super not valid");
            return false;
        }

        // Check if name is available
        if (!responseJson.has(Constants.Server.KEY_NAME))
        {
            Dbg.error(TAG, "Name not valid");
            return false;
        }

        // Check if name is available
        if (!responseJson.has(Constants.Server.KEY_ID))
        {
            Dbg.error(TAG, "App Id not present");
            return false;
        } else {
            try {
                Dbg.error(TAG, responseJson.toString());
                if (responseJson.getInt(Constants.Server.KEY_ID) == User.INVALID_ID) {
                    Dbg.error(TAG, "App Id not valid");
                    return false;
                }
            } catch (JSONException e) {
                Dbg.error(TAG, "JSON exception while reading response data");
                Dbg.stack(e);
                return false;
            }
        }

        return true;
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

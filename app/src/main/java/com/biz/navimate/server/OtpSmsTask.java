package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class OtpSmsTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OTP_SMS_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.OtpSms listener = null;
    public void SetListener(IfaceServer.OtpSms listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private String phoneNo = "", message = "";

    // ----------------------- Constructor ----------------------- //
    public OtpSmsTask(Context parentContext, String phoneNo, String message)
    {
        super(parentContext, Constants.Server.URL_OTP_SMS);
        this.phoneNo = phoneNo;
        this.message = message;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        requestJson = new JSONObject();

        try
        {
            requestJson.put(Constants.Server.KEY_PHONE, phoneNo);
            requestJson.put(Constants.Server.KEY_MESSAGE, message);
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
                listener.onSmsSuccess();
            }
        }
        else
        {
            // Call listener
            if (listener != null)
            {
                listener.onSmsFailure();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

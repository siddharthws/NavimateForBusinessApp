package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

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
    public void onPreExecute()
    {
        RlDialog.Show(new Dialog.Waiting("Sending Verification SMS..."));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();

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
            // Call listener
            if (listener != null)
            {
                listener.onSmsSuccess();
            }
        }
        else
        {
            // Toast error
            Dbg.Toast(parentContext, "Unable to send SMS !!!", Toast.LENGTH_SHORT);

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

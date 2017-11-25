package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceDialog;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

/**
 * Created by Siddharth on 23-11-2017.
 */

public class CheckUpdatesTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CHECK_UPDATES_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.CheckUpdates listener = null;
    public void SetListener(IfaceServer.CheckUpdates listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private boolean bUpdateRequired = false;

    // ----------------------- Constructor ----------------------- //
    public CheckUpdatesTask(Context parentContext)
    {
        super(parentContext, Constants.Server.URL_CHECK_UPDATE);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Get current version code
        int currentVersionCode = Preferences.GetVersion().code;

        // Init Request JSON
        JSONObject requestJson = new JSONObject();

        try
        {
            requestJson.put(Constants.Server.KEY_VERSION_CODE, currentVersionCode);
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

        if (IsResponseValid()) {
            try {
                bUpdateRequired = responseJson.getBoolean(Constants.Server.KEY_UPDATE_REQUIRED);
            } catch (JSONException e) {
                Dbg.stack(e);
            }
        } else {
            Dbg.error(TAG, "Response not valid");
        }

        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {
        if (IsResponseValid())
        {
            if (bUpdateRequired) {
                // Confirm from user if he wants to update
                RlDialog.Show(new Dialog.Confirm("Your App is Out-of-Date. Would you like to update now ?", new IfaceDialog.Confirm() {
                    @Override
                    public void onConfirmYesClick() {
                        // Call listener
                        if (listener != null)
                        {
                            listener.onUpdateRequired();
                        }
                    }

                    @Override
                    public void onConfirmNoClick() {
                        // Call listener
                        if (listener != null)
                        {
                            listener.onUpdateNotRequired();
                        }
                    }
                }));
            } else {
                // Update not required. App is up to date
                if (listener != null)
                {
                    listener.onUpdateNotRequired();
                }
            }
        }
        else
        {
            // Toast error
            Dbg.Toast(parentContext, "Unable to check for updates !!!", Toast.LENGTH_SHORT);

            // Call listener
            if (listener != null)
            {
                listener.onUpdateNotRequired();
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
        if (!responseJson.has(Constants.Server.KEY_UPDATE_REQUIRED))
        {
            Dbg.error(TAG, "Check update result not found");
            return false;
        }

        return true;
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

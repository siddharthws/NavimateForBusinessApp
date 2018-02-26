package com.biz.navimate.server;

import android.content.Context;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.AccountSettings;

/**
 * Created by Sai_Kameswari on 25-02-2018.
 */

public class GetWorkingHoursTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_WORKING_HOURS_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public GetWorkingHoursTask(Context parentContext)
    {
        super(parentContext, Constants.Server.URL_APP_START);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Call Super
        super.doInBackground(params);

        //check response validity
        if (IsResponseValid()) {
            try {
                int startHr = responseJson.getInt(Constants.Preferences.KEY_START_TIME);
                int endHr = responseJson.getInt(Constants.Preferences.KEY_END_TIME);
                Preferences.SetAcountSettings(parentContext, new AccountSettings(startHr, endHr));
            } catch (Exception e) {
                Dbg.stack(e);
            }
        } else {
            Dbg.error(TAG, "Response not valid");
        }

        return null;
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
        if (!responseJson.has(Constants.Preferences.KEY_START_TIME) || (!responseJson.has(Constants.Preferences.KEY_END_TIME)))
        {
            Dbg.error(TAG, "Working hours not fetched from server");
            return false;
        }

        return true;
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

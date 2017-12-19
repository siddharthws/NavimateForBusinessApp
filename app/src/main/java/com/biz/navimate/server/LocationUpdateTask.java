package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.services.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

/**
 * Created by Siddharth on 28-10-2017.
 */

public class LocationUpdateTask extends BaseServerTask
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_UPDATE_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public LocationUpdateTask(Context parentContext)
    {
        super(parentContext, Constants.Server.URL_TRACK);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        try
        {
            // Put location
            LocationObj currentLocation  = LocationService.cache.GetLocation();
            requestJson.put(Constants.Server.KEY_LATITUDE, currentLocation.latlng.latitude);
            requestJson.put(Constants.Server.KEY_LONGITUDE, currentLocation.latlng.longitude);

            // Put speed
            requestJson.put(Constants.Server.KEY_SPEED, LocationService.cache.GetSpeed());
        }
        catch (JSONException e)
        {
            Dbg.error(TAG, "Error while putting initData in JSON");
            return null;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        return super.doInBackground(params);
    }

    public boolean executeSync()
    {
        // Perform background task
        doInBackground();

        return IsResponseValid();
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

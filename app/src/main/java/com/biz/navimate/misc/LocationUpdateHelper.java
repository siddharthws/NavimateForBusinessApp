package com.biz.navimate.misc;

import android.content.Context;

import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.google.android.gms.common.api.Status;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationUpdateHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_UPDATE_HELPER";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // interface used to convey results of location update
    public interface LocationInitInterface
    {
        void onLocationInitError(int errorCode, Status status);
        void onLocationInitSuccess(LocationObj location);
    }
    private LocationInitInterface listener = null;
    public void SetInitListener(LocationInitInterface listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context context = null;

    // ----------------------- Constructor ----------------------- //
    public LocationUpdateHelper(Context context)
    {
        this.context = context;
    }

    // ----------------------- Overrides ----------------------- //

    // ----------------------- Public APIs ----------------------- //
    // APIs to Add / Remove Location Clients
    // Each client provides a LocationUpdateObject depending on what type of service it wants
    // Start() API queries the arbiter for which type of location update should be started
    public void AddClient(int tag, LocationUpdate serviceObject)
    {
        // Place holder
    }

    public void RemoveClient(int tag)
    {
        // Placeholder
    }

    // APIs to start / stop location updates
    public void Start()
    {
        // Placeholder
    }

    public void Stop()
    {
        // Place holder
    }

    // API to check if location is updating correctly
    // Cecks this based on time stamp of last location and the currently enabled service
    public boolean IsUpdating()
    {
        boolean bUpdating = false;

        // Placeholder

        return bUpdating;
    }

    // ----------------------- Private APIs ----------------------- //
    // API to get the location service object based on all the added clients
    // Process is called Location Service Arbitration
    private LocationUpdateHelper LocationServiceArbiter()
    {
        // Place holder
        return null;
    }

    // Error / Success reporting to listener
    private void ReportError(int errorCode, Status status)
    {
        if (listener != null)
        {
            listener.onLocationInitError(errorCode, status);
        }
    }

    private void ReportSuccess(LocationObj locationObject)
    {
        if (listener != null)
        {
            listener.onLocationInitSuccess(locationObject);
        }
    }
}

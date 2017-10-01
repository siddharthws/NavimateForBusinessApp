package com.biz.navimate.misc;

import android.content.Context;

import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.google.android.gms.common.api.Status;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationUpdateHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_UPDATE_HELPER";

    // Client Tags
    public static final int CLIENT_TAG_MAP      = 1;

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
    // Global Location Clients alng with required update object for the client
    private static HashMap<Integer, LocationUpdate> clients = null;

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
        // Init clients if null
        if (clients == null)
        {
            clients = new HashMap<>();
        }

        // Add to clients
        clients.put(tag, serviceObject);

        // Restart Update Logic
        Start();
    }

    public void RemoveClient(int tag)
    {
        if (clients != null)
        {
            // Remove from clients
            clients.remove(tag);

            // Restart Update Logic
            Start();
        }
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
    private LocationUpdate LocationServiceArbiter()
    {
        LocationUpdate requiredUpdates = null;

        if ((clients != null) && (clients.size() > 0))
        {
            Iterator<Integer> tagIter = clients.keySet().iterator();
            while (tagIter.hasNext())
            {
                LocationUpdate clientUpdates = clients.get(tagIter.next());
                if ((requiredUpdates == null) || (clientUpdates.interval < requiredUpdates.interval))
                {
                    requiredUpdates = clientUpdates;
                }
            }
        }

        return requiredUpdates;
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

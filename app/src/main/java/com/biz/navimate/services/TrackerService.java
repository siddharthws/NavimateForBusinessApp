package com.biz.navimate.services;

import android.content.Context;
import android.os.SystemClock;

import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.server.LocationUpdateTask;

/**
 * Created by Siddharth on 19-01-2017.
 */

public class TrackerService     extends     BaseService
                                implements  Runnable
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TRACKER_SERVICE";

    private static final int   UPDATE_INTERVAL_MS           = 5000;
    private static final int   MAX_UPDATE_PERIOD_S          = 60;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static TrackerService service   = null;

    private LocationUpdateTask locationUpdateTask = null;
    private long lastUpdateTimeMs = 0L;
    private LocationObj locCache            = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void Init(){
        // Placeholder
    }

    @Override
    public void StickyServiceJob()
    {
        service = this;

        // Init Tracking Task if required
        if (locationUpdateTask == null) {
            locationUpdateTask = new LocationUpdateTask(this);
        }

        // Send location update depending on last update
        boolean bSuccess = true;
        if (locCache == null) {
            // Send location first time
            locCache = LocationCache.instance.GetLocation();
            bSuccess = locationUpdateTask.executeSync();
            lastUpdateTimeMs = System.currentTimeMillis();
        } else {
            // Send location only if it has updated
            LocationObj currentLoc = LocationCache.instance.GetLocation();
            int elapsedTimeS = (int) ((System.currentTimeMillis() - lastUpdateTimeMs) / 1000);
            if ((currentLoc.latlng.latitude != locCache.latlng.latitude) ||
                (currentLoc.latlng.longitude != locCache.latlng.longitude) ||
                (elapsedTimeS > MAX_UPDATE_PERIOD_S)) {
                locCache = currentLoc;
                bSuccess = locationUpdateTask.executeSync();
                lastUpdateTimeMs = System.currentTimeMillis();
            }
        }

        // Stop service if error returned from server
        if (!bSuccess) {
            // Remove Location Client
            new LocationUpdateHelper(this).RemoveClient(LocationUpdateHelper.CLIENT_TAG_TRACKER);

            // Stop Service
            TrackerService.StopService();
            return;
        }

        // Sleep this thread
        Sleep(UPDATE_INTERVAL_MS);
    }

    // ----------------------- Public APIs ----------------------- //
    // APIs to start / stop / check status of the service
    public static void StartService(Context context)
    {
        if (!IsRunning())
        {
            // Add tracker client to receive fast updates
            new LocationUpdateHelper(context).AddClient(LocationUpdateHelper.CLIENT_TAG_TRACKER, LocationUpdate.FAST);

            // Start Service
            StartService(context, TrackerService.class);
        }
    }

    public static void StopService()
    {
        // Stop service
        if (service != null)
        {
            StopService(service);
        }
    }

    public static boolean IsRunning()
    {
        return IsRunning(service);
    }

    // ----------------------- Private APIs ----------------------- //
}

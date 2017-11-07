package com.biz.navimate.services;

import android.content.Context;
import android.os.SystemClock;

import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.server.TrackingTask;

/**
 * Created by Siddharth on 19-01-2017.
 */

public class TrackerService     extends     BaseService
                                implements  Runnable
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TRACKER_SERVICE";

    private static final int   UPDATE_INTERVAL_MS           = 5000;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static TrackerService service   = null;

    private LocationObj locCache            = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void StickyServiceJob()
    {
        service = this;

        // Sleep this thread
        long nextRunTimeMs = System.currentTimeMillis() + UPDATE_INTERVAL_MS;
        while ((System.currentTimeMillis() < nextRunTimeMs) && (!bStopTask))
        {
            SystemClock.sleep(1000);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // APIs to start / stop / check status of the service
    public static void StartService(Context context)
    {
        // Placeholder
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

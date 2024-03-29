package com.biz.navimate.objects;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationUpdate {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_UPDATE_OBEJCT";

    // Update Intervals
    private static final int UPDATE_INTERVAL_VERY_FAST          = 5000;
    private static final int UPDATE_INTERVAL_FAST               = 30000;
    private static final int UPDATE_INTERVAL_MODERATE           = 60000;
    private static final int UPDATE_INTERVAL_SLOW               = 120000;

    // Expiry Times
    private static final int EXPIRY_INTERVAL_VERY_FAST_MS       = 15000;
    private static final int EXPIRY_INTERVAL_FAST_MS            = 90000;
    private static final int EXPIRY_INTERVAL_MODERATE_MS        = 180000;
    private static final int EXPIRY_INTERVAL_SLOW_MS            = 360000;

    // Static Objects for different types of services
    public static final LocationUpdate V_FAST            = new LocationUpdate(UPDATE_INTERVAL_VERY_FAST, EXPIRY_INTERVAL_VERY_FAST_MS, LocationRequest.PRIORITY_HIGH_ACCURACY);
    public static final LocationUpdate FAST              = new LocationUpdate(UPDATE_INTERVAL_FAST, EXPIRY_INTERVAL_FAST_MS, LocationRequest.PRIORITY_HIGH_ACCURACY);
    public static final LocationUpdate MODERATE          = new LocationUpdate(UPDATE_INTERVAL_MODERATE, EXPIRY_INTERVAL_MODERATE_MS, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    public static final LocationUpdate SLOW              = new LocationUpdate(UPDATE_INTERVAL_SLOW, EXPIRY_INTERVAL_SLOW_MS, LocationRequest.PRIORITY_LOW_POWER);

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public int interval = 0, expiry = 0, priority = 0;
    public LocationRequest locationRequest = null;

    // ----------------------- Constructor ----------------------- //

    public LocationUpdate(int interval, int expiry, int priority)
    {
        this.interval    = interval;
        this.expiry      = expiry;
        this.priority    = priority;

        // Initialize Location Request
        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(UPDATE_INTERVAL_VERY_FAST);
        locationRequest.setPriority(priority);
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

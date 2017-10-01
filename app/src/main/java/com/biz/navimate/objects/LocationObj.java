package com.biz.navimate.objects;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationObj {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_OBJECT";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public LatLng latlng = null;
    public long   timestamp = 0;
    public float  accuracy = 0.0f;

    // ----------------------- Constructor ----------------------- //
    public LocationObj(double lat, double lng, long timestamp, float accuracy)
    {
        this.latlng = new LatLng(lat, lng);
        this.timestamp  = timestamp;
        this.accuracy   = accuracy;
    }

    public LocationObj(Location location)
    {
        this.latlng = new LatLng(location.getLatitude(), location.getLongitude());
        this.accuracy   = location.getAccuracy();
        this.timestamp  = location.getTime();
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

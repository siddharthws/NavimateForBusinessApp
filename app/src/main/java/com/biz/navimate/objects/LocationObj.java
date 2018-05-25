package com.biz.navimate.objects;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationObj {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_OBJECT";

    // Movement Types
    public static final int STANDING    = 1;
    public static final int WALKING     = 2;
    public static final int DRIVING     = 3;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public LatLng latlng = null;
    public long   timestamp = 0;
    public float  accuracy = 0.0f;
    public float  speed = 0.0f;

    // ----------------------- Constructor ----------------------- //
    public LocationObj(double lat, double lng, long timestamp, float accuracy, float speed)
    {
        this.latlng = new LatLng(lat, lng);
        this.timestamp  = timestamp;
        this.accuracy   = accuracy;
        this.speed      = speed;
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public int GetMovement() {
        // get type of movement from speed
        if (speed > 18) {
            return DRIVING;
        } else if (speed >= 1) {
            return WALKING;
        } else {
            return STANDING;
        }
    }

    // ----------------------- Private APIs ----------------------- //
}

package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class Camera {
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "CAMERA";

    // Macros to define types of Marker Objects
    public static final int CAM_UPDATE_INVALID          = 0;
    public static final int CAM_UPDATE_LOCATION         = 1;
    public static final int CAM_UPDATE_BOUNDS           = 2;

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Classes ---------------------------//
    // Base properties common to all camera updates
    public static abstract class Base
    {
        public int      type        = CAM_UPDATE_INVALID;
        public boolean  bAnimate    = false;

        public Base(int type, boolean bAnimate)
        {
            this.type       = type;
            this.bAnimate   = bAnimate;
        }
    }

    // Class to update camera position to a single location
    public static class Location extends Base
    {
        // Location to center the map on
        public LatLng location = null;

        // Zoom Level
        public float zoom = 0f;

        public Location(LatLng location, int zoom, boolean bAnimate)
        {
            super(CAM_UPDATE_LOCATION, bAnimate);
            this.location = location;
            this.zoom = zoom;
        }
    }

    // Class to update camera position such that given bounds are shown
    public static class Bounds extends Base
    {
        // Location to center the map on
        public ArrayList<LatLng> bounds = null;

        public Bounds(ArrayList<LatLng> bounds, boolean bAnimate)
        {
            super(CAM_UPDATE_BOUNDS, bAnimate);
            this.bounds = bounds;
        }
    }
}

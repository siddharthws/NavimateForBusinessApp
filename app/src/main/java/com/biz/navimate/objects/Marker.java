package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class Marker {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "MARKER";

    // Macros to define types of Marker Objects
    public static final int MARKER_TYPE_INVALID          = 0;

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Classes ---------------------------//
    // Base properties common to all markers
    // This class should be inherited by other specialized classes for each marker type
    public static abstract class Base
    {
        public int      type        = MARKER_TYPE_INVALID;
        public LatLng   position    = null;

        public Base(int type, LatLng position)
        {
            this.type       = type;
            this.position   = position;
        }
    }

    // Specialized Marker classes
}

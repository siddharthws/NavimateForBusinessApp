package com.biz.navimate.objects;

import com.biz.navimate.misc.IconGen;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class MarkerObj {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "MARKER";

    // Macros to define types of MarkerObj Objects
    public static final int MARKER_TYPE_INVALID          = 0;
    public static final int MARKER_TYPE_TASK             = 1;

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Classes ---------------------------//
    // Base properties common to all markers
    // This class should be inherited by other specialized classes for each marker type
    public static abstract class Base
    {
        public int      type        = MARKER_TYPE_INVALID;
        public LatLng   position    = null;

        // MarkerObj Object maintained returned by google maps when marker is added to map
        public Marker marker      = null;

        public Base(int type, LatLng position)
        {
            this.type       = type;
            this.position   = position;
        }

        // Abstracts
        // API to provide marker options to populate UI of marker. Should be implemented by derived classes.
        public abstract MarkerOptions GetMarkerOptions();
    }

    // Specialized MarkerObj classes

    // Task Marker Object. Maintains Task data
    public static class Task extends Base
    {
        public com.biz.navimate.objects.Task task = null;

        public Task(com.biz.navimate.objects.Task task)
        {
            super(MARKER_TYPE_TASK, task.lead.position);
            this.task = task;
        }

        // API to get Marker Options to populate UI
        @Override
        public MarkerOptions GetMarkerOptions()
        {
            // Init marker options and assign parameters
            MarkerOptions markerOpt = new MarkerOptions()
                                            .position(position)
                                            .icon(BitmapDescriptorFactory.fromBitmap(IconGen.GetMarkerIcon(task.lead.title)));

            return markerOpt;
        }
    }
}

package com.biz.navimate.objects;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.biz.navimate.R;
import com.biz.navimate.misc.IconGen;
import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.runnables.GrowCircleRunnable;
import com.biz.navimate.runnables.MarkerMoveRunnable;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
    public static final int MARKER_TYPE_CURRENT_LOCATION = 2;

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

    // Current Location Marker & Accuracy circle
    public static class CurrentLocation     extends     Base
    {
        public Circle accuracyCircle = null;

        private Context context = null;
        private LocationUpdateHelper locationUpdateHelper = null;

        // Runnables for updating the marker
        private MarkerMoveRunnable moveRunnable = null;
        private GrowCircleRunnable circleRunnable = null;

        private Boolean             bEnabled = false;
        private BitmapDescriptor enabledIcon = null, disabledIcon = null;

        public CurrentLocation(Context context)
        {
            super(MARKER_TYPE_CURRENT_LOCATION, new LatLng(0, 0));

            this.context = context;
            locationUpdateHelper = new LocationUpdateHelper(context);

            // Init Icons
            enabledIcon = BitmapDescriptorFactory.fromBitmap(IconGen.GetScaledIcon(context, R.mipmap.icon_current_location_marker_enabled, 20, 20));
            disabledIcon = BitmapDescriptorFactory.fromBitmap(IconGen.GetScaledIcon(context, R.mipmap.icon_current_location_marker_disabled, 20, 20));
        }

        // API to get Marker Options to populate UI
        @Override
        public MarkerOptions GetMarkerOptions()
        {
            // Init marker options and assign parameters
            MarkerOptions markerOpt = new MarkerOptions()
                    .position(new LatLng(0, 0))
                    .icon(disabledIcon)
                    .anchor(0.5f, 0.5f)
                    .zIndex(3);;

            return markerOpt;
        }

        // API to get accuracy circle ptions
        public CircleOptions GetCircleOptions()
        {
            // Init marker options and assign parameters
            CircleOptions circleOpt = new CircleOptions()
                    .center(new LatLng(0, 0))
                    .fillColor(ContextCompat.getColor(context, R.color.GMAP_BLUE_SEMI_TRANSPARENT))
                    .strokeWidth(1)
                    .strokeColor(ContextCompat.getColor(context, R.color.GMAP_BLUE));

            return circleOpt;
        }

        public void Refresh()
        {
            if (marker == null)
            {
                return;
            }

            LocationObj currentLocation = LocationCache.instance.GetLocation();

            if (Statics.IsPositionValid(currentLocation.latlng))
            {
                // Update marker position
                Move(currentLocation.latlng);
            }

            // Update accuracy circle only if location is updating
            if (locationUpdateHelper.IsUpdating())
            {
                GrowCircle(currentLocation.accuracy);
            }
            else
            {
                GrowCircle(0);
            }

            // Update marker icon based on Location Update status
            if (locationUpdateHelper.IsUpdating())
            {
                // Update Marker to blue if required
                if (!bEnabled)
                {
                    marker.setIcon(enabledIcon);
                    bEnabled = true;
                }
            }
            else
            {
                // Update marker to grey if required
                if (bEnabled)
                {
                    marker.setIcon(disabledIcon);
                    bEnabled = false;
                }

                // Remove Accuracy circle
                accuracyCircle.setRadius(0);
            }
        }

        private void Move(LatLng newPosition)
        {
            // Check if move is required
            if (position.equals(newPosition))
            {
                return;
            }

            // Reset current position
            position = newPosition;

            // Start runnable for transition
            if (moveRunnable == null)
            {
                moveRunnable = new MarkerMoveRunnable(this);
            }
            moveRunnable.Reset(position);
            moveRunnable.Post(0);
        }

        private void GrowCircle (double finalRadius)
        {
            // Start runnable for transition
            if (circleRunnable == null)
            {
                circleRunnable = new GrowCircleRunnable(accuracyCircle);
            }

            // Check if radius update is required
            if (accuracyCircle.getRadius() == finalRadius)
            {
                return;
            }

            // Set radius
            circleRunnable.Reset(finalRadius);

            // Post runnable
            circleRunnable.Post(0);
        }
    }
}

package com.biz.navimate.runnables;

import android.view.animation.LinearInterpolator;

import com.biz.navimate.objects.MarkerObj;
import com.biz.navimate.objects.Statics;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class MarkerMoveRunnable extends BaseRunnable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "MARKER_MOVE_RUNNABLE";

    private static final int ANIMATION_INTERVAL_MS      = 1500;
    private static final int RUN_INTERVAL_MS            = 10;

    // Macro for Marker smooth transition animation duration
    private static final int MIN_DISTANCE_FOR_SMOOTH_MOVEMENT_M       = 0;
    private static final int MAX_DISTANCE_FOR_SMOOTH_MOVEMENT_M       = 150;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // Movement properties
    private MarkerObj.Base  marker = null;
    private LatLng startLatLng = null;
    private LatLng endLatLng = null;
    private long startTime = 0L;
    private LinearInterpolator animInterpolator = null;

    // ----------------------- Constructor ----------------------- //
    public MarkerMoveRunnable(MarkerObj.Base marker)
    {
        super();
        this.marker = marker;
        animInterpolator = new LinearInterpolator();
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void PerformTask()
    {
        // Get final LatLng for currentl cycle based on time and interpolation
        long elapsedTime = System.currentTimeMillis() - startTime;
        float t = 0.0f;

        // Move marker smoothly / abruptly based on distance to move
        int distanceToMove = Statics.GetDistanceBetweenCoordinates(marker.marker.getPosition(), endLatLng);
        if ((distanceToMove > MIN_DISTANCE_FOR_SMOOTH_MOVEMENT_M) && (distanceToMove < MAX_DISTANCE_FOR_SMOOTH_MOVEMENT_M))
        {
            // move smoothly
            t = animInterpolator.getInterpolation((float) elapsedTime / ANIMATION_INTERVAL_MS);;
        }
        else
        {
            // Move abruptly
            t = 1.0f;
        }

        double lat, lng;
        if (t < 1.0)
        {
            lat = t * endLatLng.latitude + (1 - t)*startLatLng.latitude;
            lng = t * endLatLng.longitude + (1 - t)*startLatLng.longitude;
        }
        else
        {
            lat = endLatLng.latitude;
            lng = endLatLng.longitude;
        }

        // Move marker
        LatLng markerPosition = new LatLng(lat, lng);
        marker.marker.setPosition(markerPosition);

        // Move the accuracy circle if applicable
        if (marker.type == MarkerObj.MARKER_TYPE_CURRENT_LOCATION)
        {
            MarkerObj.CurrentLocation clMarker = (MarkerObj.CurrentLocation) marker;
            clMarker.accuracyCircle.setCenter(markerPosition);
        }

        // Re-schedule callback
        if (t < 1.0)
        {
            Post(RUN_INTERVAL_MS);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public void Reset(LatLng finalPosition)
    {
        startLatLng = marker.marker.getPosition();
        endLatLng = finalPosition;
        startTime = System.currentTimeMillis();
    }

    // ----------------------- Private APIs ----------------------- //
}

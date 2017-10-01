package com.biz.navimate.runnables;

import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.model.Circle;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class GrowCircleRunnable extends BaseRunnable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GROW_CIRCLE_RUNNABLE";

    private static final int ANIMATION_INTERVAL_MS      = 300;
    private static final int RUN_INTERVAL_MS            = 10;

    // Macro for Marker smooth transition animation duration
    private static final int MIN_DISTANCE_FOR_SMOOTH_GROW_M       = 1;
    private static final int MAX_DISTANCE_FOR_SMOOTH_GROW_M       = 150;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // Movement properties
    private Circle circle = null;
    private double startRadius = 0;
    private double endRadius = 0;
    private long startTime = 0L;
    private LinearInterpolator animInterpolator = null;

    // ----------------------- Constructor ----------------------- //
    public GrowCircleRunnable(Circle circle)
    {
        super();
        this.circle = circle;
        animInterpolator = new LinearInterpolator();
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void PerformTask()
    {
        // Get final LatLng for currentl cycle based on time and interpolation
        long elapsedTime = System.currentTimeMillis() - startTime;
        float t = 0.0f;

        // Check if radius is valid
        double growMargin = Math.abs(endRadius - startRadius);
        if ((growMargin > MIN_DISTANCE_FOR_SMOOTH_GROW_M) && (growMargin < MAX_DISTANCE_FOR_SMOOTH_GROW_M) && (elapsedTime < ANIMATION_INTERVAL_MS))
        {
            // find interposlation
            t = animInterpolator.getInterpolation((float) elapsedTime / ANIMATION_INTERVAL_MS);
        }
        else
        {
            t = 1.0f;
        }

        // Update circle
        circle.setRadius((t * endRadius) + ((1 - t)*startRadius));

        // Re-schedule callback if more adjustment is required
        if (t < 1.0)
        {
            Post(RUN_INTERVAL_MS);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public void Reset(double radius)
    {
        this.startRadius = circle.getRadius();
        this.endRadius = radius;
        this.startTime = System.currentTimeMillis();
    }

    // ----------------------- Private APIs ----------------------- //
}

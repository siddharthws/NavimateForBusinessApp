package com.biz.navimate.interpolators;

import android.view.animation.Interpolator;

/**
 * Base Interpolator class for interpolating animations from 0 to 1.
 * Interpolations help introducing bouncy / colliding / accelerated effects int he animations.
 */

public abstract class BaseInterpolator implements Interpolator
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_INTERPOLATOR";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private boolean bIn = true;

    // ----------------------- Constructor ----------------------- //
    public  BaseInterpolator(boolean bIn)
    {
        this.bIn = bIn;
    }

    // ----------------------- Abstracts ----------------------- //
    public abstract float GetIn(float t);
    public abstract float GetOut(float t);

    // ----------------------- Overrides ----------------------- //
    @Override
    public float getInterpolation(float ratio)
    {
        if (bIn)
        {
            return GetIn(ratio);
        }
        else
        {
            return GetOut(ratio);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Helper APIs
    protected float In(float t, double strength)
    {
        return (float) Math.pow(t, strength);
    }

    protected float Out(float t, double strength)
    {
        return (1.0f - In(1.0f - t, strength));
    }
}

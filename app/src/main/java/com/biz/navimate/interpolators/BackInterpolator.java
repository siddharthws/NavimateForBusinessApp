package com.biz.navimate.interpolators;

/**
 * Created by Siddharth on 01-06-2017.
 */

public class BackInterpolator extends BaseInterpolator
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BACK_INTERPOLATOR";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public BackInterpolator(boolean bIn)
    {
        super(bIn);
    }

    // ----------------------- Overrides ----------------------- //
    public float GetOut(float t)
    {
        float result;

        if (t <= 0.6f)
        {
            // Get interval
            float interval = 0.6f;

            // Normalize t
            t *=  (1.0f / interval);

            // Play cubic out
            result = Out(t, 2);

            // Normalize to match target height
            result *= 1.1f;
        }
        else if (t <= 0.8f)
        {
            // Get interval
            float interval = 0.8f - 0.6f;

            // Normalize t
            t -= 0.6f;
            t *=  (1.0f / interval);

            // Play cubic in
            result = In(t, 2);

            // Invert
            result = 1.0f - result;

            // Normalize to match target height
            result *= 0.05f;

            // Add offset
            result = result + 1.05f;
        }
        else
        {
            // Get interval
            float interval = 1.0f - 0.8f;

            // Normalize t
            t -= 0.8f;
            t *=  (1.0f / interval);

            // Play cubic out
            result = Out(t, 2);

            // Invert
            result = 1.0f - result;

            // Normalize to match target height
            result *= 0.05f;

            // Add offset
            result += 1.0f;
        }

        return result;
    }

    public float GetIn(float t)
    {
        return (1.0f - GetOut(1.0f - t));
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

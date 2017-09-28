package com.biz.navimate.objects;

import android.graphics.Point;
import android.os.Looper;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class Statics {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "STATICS";

    // ----------------------- Globals ----------------------- //
    // Screen size
    public static Point SCREEN_SIZE = null;

    // Screen Density
    public static float SCREEN_DENSITY = 0f;

    // ----------------------- Public APIs ----------------------- //
    // Check if this thread is UI thread
    public static boolean IsOnUiThread()
    {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    // Convert Dip into Pixels
    public static int GetPxFromDip(int dip)
    {
        int px = (int) Math.ceil(dip * SCREEN_DENSITY);
        return px;
    }
}

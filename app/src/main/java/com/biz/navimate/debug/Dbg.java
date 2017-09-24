package com.biz.navimate.debug;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;

/**
 * Created by Siddharth on 03-11-2016.
 */

public class Dbg
{
    // ----------------------- Constants ----------------------- //
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // Logging APIs
    public static void info (String TAG,
                             String msg)
    {
        if (Constants.App.DEBUG)
        {
            Log.i(TAG, msg);
        }
    }

    public static void error (String TAG,
                              String msg)
    {
        if (Constants.App.DEBUG)
        {
            Log.e(TAG, msg);
        }
    }

    public static void stack (Exception e)
    {
        if (Constants.App.DEBUG)
        {
            e.printStackTrace();
        }
    }

    // Toasting APIs
    public static void Toast (Context context,
                              String msg,
                              int duration)
    {
        if (context != null)
        {
            Toast.makeText(context, msg, duration).show();
        }
    }

    // ----------------------- Private APIs ----------------------- //
}

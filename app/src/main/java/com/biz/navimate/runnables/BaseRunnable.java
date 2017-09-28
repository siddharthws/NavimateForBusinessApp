package com.biz.navimate.runnables;

import android.os.Handler;

/**
 * Created by Siddharth on 28-09-2017.
 */

public abstract class BaseRunnable implements Runnable
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_RUNNABLE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // general runnable properties
    private Handler handler     = null;
    private Boolean bPosted     = false;

    // ----------------------- Constructor ----------------------- //
    public BaseRunnable()
    {
        handler = new Handler();
    }

    // ----------------------- Abstracts ----------------------- //
    protected abstract void PerformTask();

    // ----------------------- Overrides ----------------------- //
    // Runnable Overrides
    @Override
    public void run()
    {
        bPosted = false;

        // Perform Task (implemented by inherited class)\
        PerformTask();
    }

    // ----------------------- Public APIs ----------------------- //
    // API to post the runnable
    public void Post(int delayMs)
    {
        // Remove existing callbacks
        Unpost();

        // post
        if (delayMs == 0)
        {
            handler.post(this);
        }
        else
        {
            handler.postDelayed(this, delayMs);
        }
        bPosted = true;
    }

    // API to remove any further posts of this runnable
    // Note : If there is an execution of the runnable currently running, it will not be cancelled
    public void Unpost()
    {
        if (bPosted)
        {
            handler.removeCallbacks(this);
            bPosted = false;
        }
    }

    // ----------------------- Private APIs ----------------------- //
}

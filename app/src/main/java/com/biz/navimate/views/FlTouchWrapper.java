package com.biz.navimate.views;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Generic Touch Wrapper for wrapping non touchable views
 */

public class FlTouchWrapper extends FrameLayout
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FL_TOUCH_WRAPPER";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceTouchListener
    {
        void onTouch();
    }
    private IfaceTouchListener listener = null;
    public void SetTouchListener (IfaceTouchListener listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public FlTouchWrapper(Context context)
    {
        super(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                if (listener != null)
                {
                    listener.onTouch();
                }
                break;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

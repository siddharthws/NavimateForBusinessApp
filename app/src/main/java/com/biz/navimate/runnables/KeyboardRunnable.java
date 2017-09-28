package com.biz.navimate.runnables;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class KeyboardRunnable extends BaseRunnable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "KEYBOARD_RUNNABLE";

    // Runnable Interval
    private static final int INTERVAL_MS = 100;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private Context context         = null;
    private View targetView         = null;

    // ----------------------- Constructor ----------------------- //
    public KeyboardRunnable(Context context, View targetView)
    {
        super();
        this.context        = context;
        this.targetView     = targetView;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void PerformTask()
    {
        // Validate Params
        if ((context == null) || (targetView == null))
        {
            Dbg.error(TAG, "Invalid Params");
            return;
        }

        // Get Input Method Manager
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check view is focusable
        if (!(targetView.isFocusable() && targetView.isFocusableInTouchMode()))
        {
            Dbg.error(TAG, "Non focusable view");
            return;
        }
        // Try focusing
        else if (!targetView.requestFocus())
        {
            Dbg.error(TAG, "Cannot focus on view");
            Post(INTERVAL_MS);
        }
        // Check if Imm is active with this view
        else if (!imm.isActive(targetView))
        {
            Dbg.error(TAG, "IMM is not active");
            Post(INTERVAL_MS);
        }
        // Show Keyboard
        else if (!imm.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT))
        {
            Dbg.error(TAG, "Unable to show keyboard");
            Post(INTERVAL_MS);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Hide(Context context)
    {
        if (context != null)
        {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (App.GetCurrentActivity() != null)
            {
                imm.hideSoftInputFromWindow(App.GetCurrentActivity().findViewById(android.R.id.content).getWindowToken(), 0);
            }
        }
        else
        {
            Dbg.error(TAG, "Invalid params to hide keyboard");
        }
    }

    // ----------------------- Private APIs ----------------------- //
}

package com.biz.navimate.runnables;

import android.widget.Button;

/**
 * Created by Siddharth on 19-04-2017.
 */

public class VerifyPhoneResendRunnable extends BaseRunnable
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "VERIFY_PHONE_RESEND_RUNNABLE";

    private static final int NUM_SECONDS = 60;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // helpers
    private Button btnResend = null;
    private long startTimeMs = 0L;

    // ----------------------- Constructor ----------------------- //
    public VerifyPhoneResendRunnable(Button btnResend)
    {
        super();
        this.btnResend = btnResend;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void Post(int delayMs)
    {
        this.startTimeMs = System.currentTimeMillis();
        super.Post(delayMs);
    }

    @Override
    public void PerformTask()
    {
        // Check seconds remaing
        long currentTimeMs = System.currentTimeMillis();
        long elapsedTimeMs = currentTimeMs - startTimeMs;
        long remainingTimeS = NUM_SECONDS - (elapsedTimeMs / 1000);

        // Check if time is up
        if (remainingTimeS <= 0)
        {
            // Enable Button
            btnResend.setEnabled(true);

            // Set Button Text
            btnResend.setText("RESEND");
        }
        else
        {
            // Disable Button
            btnResend.setEnabled(false);

            // Set Button Text
            btnResend.setText("RESEND (" + remainingTimeS + ")");

            // Post runnable
            super.Post(1000);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}

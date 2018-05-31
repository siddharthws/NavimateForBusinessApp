package com.biz.navimate.views.buttons;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

import com.biz.navimate.R;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.services.LocationService;

public class IbTrackingCtrl extends AppCompatImageButton implements View.OnClickListener {
    // ----------------------- Globals ----------------------- //
    private LocationUpdateRunnable locUpdateRunnable = null;

    // ----------------------- Constructors ----------------------- //
    public IbTrackingCtrl(Context context)
    {
        super(context);
        InitView(context);
    }

    public IbTrackingCtrl(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        // Get tracking status
        boolean bTracking = Preferences.GetTracking();

        // Toggle Status
        if (bTracking) {
            // Add setting to preferences
            Preferences.SetTracking(getContext(), false);

            // Interrupt Location Service so that updates get disabled
            LocationService.Interrupt();
        } else {
            // Add setting to preferences
            Preferences.SetTracking(getContext(), true);

            // Execute Location Update runnable
            locUpdateRunnable.Post(0);
        }

        // Reset UI
        Reset();
    }

    // ----------------------- Private APIs ----------------------- //
    public void Reset() {
        // Get tracking status
        boolean bTracking = Preferences.GetTracking();

        // Set background and image as per tracking status
        if (bTracking) {
            this.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_green));
            this.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_eye_white));
        } else {
            this.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_red));
            this.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_eye_cross_white));
        }
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context)
    {
        // Init Location update runnable
        locUpdateRunnable = new LocationUpdateRunnable(context);

        // Reset UI
        Reset();

        // Set click listener
        setOnClickListener(this);
    }
}

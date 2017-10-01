package com.biz.navimate.runnables;

import android.content.Context;

import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.objects.LocationObj;
import com.google.android.gms.common.api.Status;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationUpdateRunnable extends     BaseRunnable
                                    implements  LocationUpdateHelper.LocationInitInterface {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_UPDATE_RUNNABLE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceRunnableLocationInit {
        void onLocationInitSuccess(LocationObj location);
    }

    private IfaceRunnableLocationInit initListener = null;

    public void SetInitListener(IfaceRunnableLocationInit listener) {
        this.initListener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private LocationUpdateHelper locationUpdateHelper = null;

    // ----------------------- Constructor ----------------------- //
    public LocationUpdateRunnable(Context context) {
        super();

        // Initialize Update Helper class
        locationUpdateHelper = new LocationUpdateHelper(context);
        locationUpdateHelper.SetInitListener(this);
    }

    // ----------------------- Overrides ----------------------- //
    // Perform Runnable task here
    @Override
    protected void PerformTask() {
        // Placeholder
    }

    // Location Update Listeners
    @Override
    public void onLocationInitSuccess(LocationObj location) {
        // Placeholder
    }

    @Override
    public void onLocationInitError(int errorCode, Status status) {
        // Placeholder
    }

// ----------------------- Public APIs ----------------------- //
// ----------------------- Private APIs ----------------------- //
}
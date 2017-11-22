package com.biz.navimate.runnables;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.widget.Toast;

import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfacePermission;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.views.RlDialog;
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
        void onLocationInitError();
    }

    private IfaceRunnableLocationInit initListener = null;

    public void SetInitListener(IfaceRunnableLocationInit listener) {
        this.initListener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context context = null;
    private LocationUpdateHelper locationUpdateHelper = null;

    // ----------------------- Constructor ----------------------- //
    public LocationUpdateRunnable(Context context) {
        super();

        this.context = context;

        // Initialize Update Helper class
        locationUpdateHelper = new LocationUpdateHelper(context);
        locationUpdateHelper.SetInitListener(this);
    }

    // ----------------------- Overrides ----------------------- //
    // Perform Runnable task here
    @Override
    protected void PerformTask() {
        if (locationUpdateHelper.IsUpdating())
        {
            if (initListener != null)
            {
                initListener.onLocationInitSuccess(LocationCache.instance.GetLocation());
            }
        }
        else
        {
            // Start Location Updates
            locationUpdateHelper.Start();

            // Show waiting dialog
            Dialog.Waiting dialogData = new Dialog.Waiting("Getting your location...");
            RlDialog.Show(dialogData);
        }
    }

    // Location Update Listeners
    @Override
    public void onLocationInitSuccess(LocationObj location) {
        // Hide dialog box
        RlDialog.Hide();

        // Call SetAlarm Success Listener
        if (initListener != null)
        {
            initListener.onLocationInitSuccess(location);
        }
    }

    @Override
    public void onLocationInitError(int errorCode, Status status) {
        // Hide previously started dialog box
        RlDialog.Hide();

        // Service Error
        switch (errorCode)
        {
            case LocationUpdateHelper.ERROR_API_CLIENT:
            case LocationUpdateHelper.ERROR_UNAVAILABLE:
            case LocationUpdateHelper.ERROR_UNKNOWN_SETTINGS_ERROR:
            case LocationUpdateHelper.ERROR_UPDATES_ERROR:
            case LocationUpdateHelper.ERROR_UPDATES_UNAVAILABLE:
            {
                Dialog.Alert dialogData = new Dialog.Alert("Failed to get location : " + errorCode);
                RlDialog.Show(dialogData);

                // Call Init Erorr Listener
                if (initListener != null)
                {
                    initListener.onLocationInitError();
                }
                break;
            }
            case LocationUpdateHelper.ERROR_RESOLUTION_REQUIRED:
            {
                BaseActivity currentActivity = App.GetCurrentActivity();
                if (currentActivity != null)
                {
                    currentActivity.SetGpsResultListener(new IfaceResult.ResultGps()
                    {
                        @Override
                        public void onGpsEnableSuccess()
                        {
                            // Try again
                            Post(0);
                        }

                        @Override
                        public void onGpsEnableFailure()
                        {
                            Dbg.error(TAG, "Failed to enable GPS");
                        }
                    });
                    if (status.hasResolution())
                    {
                        // start resolution
                        try
                        {
                            Dbg.info(TAG, "Started Resolution for result");
                            status.startResolutionForResult(currentActivity, Constants.RequestCodes.GPS);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            Dbg.error(TAG, "Exception while trying to resolve result");
                            Dbg.stack(e);
                        }
                    }
                    else
                    {
                        Dbg.error(TAG, "Status does not have resolution");
                    }
                }
                else
                {
                    Dbg.error(TAG, "Current activity is null. Cannot ask for permissions");
                }
                break;
            }
            case LocationUpdateHelper.ERROR_PERMISSION_REQUIRED:
            {
                BaseActivity currentActivity = App.GetCurrentActivity();
                if (currentActivity != null)
                {
                    currentActivity.SetLocationPermissionListener(new IfacePermission.Location()
                    {
                        @Override
                        public void onLocationPermissionSuccess()
                        {
                            // Try again
                            Post(0);
                        }

                        @Override
                        public void onLocationPermissionFailure()
                        {
                            Dbg.error(TAG, "Failed to get location permission");
                        }
                    });
                    currentActivity.RequestPermission(new String[] {Manifest.permission.ACCESS_FINE_LOCATION});
                }
                else
                {
                    Dbg.error(TAG, "Current activity is null. Cannot ask for permissions");
                }
                break;
            }
            case LocationUpdateHelper.ERROR_CURRENT_LOC_UNAVAILABLE:
            {
                // Show error toast
                Dbg.Toast(context, "Your location is being enabled...", Toast.LENGTH_SHORT);

                // Call Init Error Listener
                if (initListener != null)
                {
                    initListener.onLocationInitError();
                }
                break;
            }
            default:
            {
                Dbg.error(TAG, "Unserviceable SetAlarm Error");
                break;
            }
        }
    }

// ----------------------- Public APIs ----------------------- //
// ----------------------- Private APIs ----------------------- //
}
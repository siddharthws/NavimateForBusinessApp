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
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.services.LocationService;
import com.biz.navimate.views.RlDialog;
import com.google.android.gms.common.api.Status;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationUpdateRunnable extends     BaseRunnable
                                    implements  LocationService.IfaceLocationInit {
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

    // ----------------------- Constructor ----------------------- //
    public LocationUpdateRunnable(Context context) {
        super();

        this.context = context;
    }

    // ----------------------- Overrides ----------------------- //
    // Perform Runnable task here
    @Override
    protected void PerformTask() {
        if (LocationService.IsUpdating()) {
            if (initListener != null)
            {
                initListener.onLocationInitSuccess(LocationService.cache.GetLocation());
            }
        }
        else {
            // Add Listener for Location Service
            LocationService.AddInitListener(this);

            // Show waiting dialog
            RlDialog.Show(new Dialog.Waiting("Getting your location..."));
        }
    }

    // Location Update Listeners
    @Override
    public void onLocationSuccess(LocationObj location) {
        // Run code on UI Thread
        BaseActivity cActivity = App.GetCurrentActivity();
        if (cActivity != null) {
            cActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Hide dialog box
                    RlDialog.Hide();

                    // Call SetAlarm Success Listener
                    if (initListener != null)
                    {
                        initListener.onLocationInitSuccess(location);
                    }
                }
            });
        }

        // Remove Location service Listener
        LocationService.RemoveInitListener(this);
    }

    @Override
    public void onLocationError(int errorCode, Status status) {
        // Run code on UI Thread
        BaseActivity cActivity = App.GetCurrentActivity();
        if (cActivity != null) {
            cActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Hide previously started dialog box
                    RlDialog.Hide();

                    // Service Error
                    switch (errorCode)
                    {
                        case Constants.Location.ERROR_API_CLIENT:
                        case Constants.Location.ERROR_UNAVAILABLE:
                        case Constants.Location.ERROR_UNKNOWN:
                        case Constants.Location.ERROR_UPDATES_ERROR:
                        case Constants.Location.ERROR_NO_CLIENTS: {
                            Dialog.Alert dialogData = new Dialog.Alert("Failed to get location : " + errorCode);
                            RlDialog.Show(dialogData);

                            // Call Init Error Listener
                            if (initListener != null)
                            {
                                initListener.onLocationInitError();
                            }
                            break;
                        }
                        case Constants.Location.ERROR_NO_GPS: {
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
                                        // Call Init Erorr Listener
                                        if (initListener != null)
                                        {
                                            initListener.onLocationInitError();
                                        }
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

                                // Call Init Error Listener
                                if (initListener != null)
                                {
                                    initListener.onLocationInitError();
                                }
                            }
                            break;
                        }
                        case Constants.Location.ERROR_NO_PERMISSION: {
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
                                        // Call Init Error Listener
                                        if (initListener != null)
                                        {
                                            initListener.onLocationInitError();
                                        }
                                    }
                                });
                                currentActivity.RequestPermission(new String[] {Manifest.permission.ACCESS_FINE_LOCATION});
                            }
                            else
                            {
                                Dbg.error(TAG, "Current activity is null. Cannot ask for permissions");

                                // Call Init Error Listener
                                if (initListener != null)
                                {
                                    initListener.onLocationInitError();
                                }
                            }
                            break;
                        }
                        case Constants.Location.ERROR_CURRENT_LOC_UNAVAILABLE: {
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
                            Dbg.error(TAG, "Unserviceable Init Error");

                            // Call Init Error Listener
                            if (initListener != null)
                            {
                                initListener.onLocationInitError();
                            }
                            break;
                        }
                    }
                }
            });
        }

        // Remove Location service Listener
        LocationService.RemoveInitListener(this);
    }

// ----------------------- Public APIs ----------------------- //
// ----------------------- Private APIs ----------------------- //
}
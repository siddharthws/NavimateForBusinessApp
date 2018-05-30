package com.biz.navimate.misc;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.services.LocationService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.concurrent.TimeUnit;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationUpdateHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_UPDATE_HELPER";

    // Timeout for Result Callbacks
    private static final int RESULT_CB_TIMEOUT_MS = 10000;

    // ----------------------- Classes ---------------------------//
    private class WaitForUpdate extends AsyncTask<Void, Void, Boolean> {
        private static final long WAIT_TIME_MS = 10000;

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean bSuccess = false;
            long startTimeMs = System.currentTimeMillis();

            while (!bSuccess && ((System.currentTimeMillis() - startTimeMs) < WAIT_TIME_MS)) {
                // Sleep for 1 second
                SystemClock.sleep(1000);

                // Check location status
                bSuccess = LocationService.IsUpdating();
            }

            return bSuccess;
        }

        @Override
        public void onPostExecute(Boolean bSuccess) {
            if (bSuccess) {
                ReportSuccess(LocationService.cache.GetLocation());
            } else {
                ReportError(Constants.Location.ERROR_CURRENT_LOC_UNAVAILABLE, null);
            }
        }
    }

    // ----------------------- Interfaces ----------------------- //
    // interface used to convey results of location update
    public interface LocationInitInterface
    {
        void onLocationInitError(int errorCode, Status status);
        void onLocationInitSuccess(LocationObj location);
    }
    private LocationInitInterface listener = null;
    public void SetInitListener(LocationInitInterface listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context context = null;
    private LocationUpdate requiredUpdate = null;

    // ----------------------- Constructor ----------------------- //
    public LocationUpdateHelper(Context context)
    {
        this.context = context;
    }

    // ----------------------- Overrides ----------------------- //

    // ----------------------- Public APIs ----------------------- //

    // APIs to start / stop location updates
    public void Start(LocationUpdate requiredUpdate) {
        // Update currentUpdateObject cache
        this.requiredUpdate = requiredUpdate;

        // Send Location Settings request
        SendLocationSettingsRequest();
    }

    public void Stop()
    {
        if (GoogleApiClientHolder.instance.apiClient.isConnected())
        {
            PendingResult<Status> result = LocationServices.FusedLocationApi.removeLocationUpdates(GoogleApiClientHolder.instance.apiClient, LocationService.cache);

            // Set result callback
            result.setResultCallback(new ResultCallback<Status>()
            {
                @Override
                public void onResult(@NonNull Status status)
                {
                    if (!status.isSuccess())
                    {
                        // report probelm to listener if any
                        Dbg.error(TAG, "Error stopping updates. Ignoring this");
                    }
                }
            }, RESULT_CB_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        }
    }

    // ----------------------- Private APIs ----------------------- //
    // APIs to initialize location updates
    private void SendLocationSettingsRequest()
    {
        Dbg.info(TAG, "Sending Location Settings Request");

        // Create Location Settings Request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(requiredUpdate.locationRequest);
        builder.setAlwaysShow(true);

        // Start Location Settings Request
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(GoogleApiClientHolder.instance.apiClient, builder.build());

        // Set result callback
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
            {
                LocationSettingsResult(locationSettingsResult);
            }
        }, RESULT_CB_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    private void LocationSettingsResult(LocationSettingsResult result)
    {
        Dbg.info(TAG, "Location Settings Result Received");

        Status status = result.getStatus();
        int errorCode;

        // Start Location Updates if result was success
        if (status.getStatusCode() == LocationSettingsStatusCodes.SUCCESS)
        {
            StartLocationUpdates();
            return;
        }
        else
        {
            // Check Location status code
            switch (status.getStatusCode())
            {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                {
                    Dbg.error(TAG, "location service resolution required ");
                    errorCode = Constants.Location.ERROR_NO_GPS;
                    break;
                }

                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                {
                    Dbg.error(TAG, "Location service unavailable");
                    errorCode = Constants.Location.ERROR_UNAVAILABLE;
                    break;
                }

                default:
                {
                    Dbg.error(TAG, "Location Settings Init failed with status code : " + status.getStatusMessage() + " : " + status.getStatusCode());
                    errorCode = Constants.Location.ERROR_UNKNOWN;
                    break;
                }
            }
        }

        ReportError(errorCode, status);
    }

    private void StartLocationUpdates()
    {
        Dbg.info(TAG, "Sending Location Updates Request");

        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int permissionCheck = ContextCompat.checkSelfPermission(context, locationPermission);
        GoogleApiClient apiClient = GoogleApiClientHolder.instance.apiClient;

        if (!Preferences.GetTracking()) {
            ReportError(Constants.Location.ERROR_TRACKING_DISABLED, null);
        } else if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
            ReportError(Constants.Location.ERROR_NO_PERMISSION, null);
        }
        else if (!apiClient.isConnected()) {
            ReportError(Constants.Location.ERROR_API_CLIENT, null);
        }
        else {
            // Request Location Updates
            PendingResult<Status> result = LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, requiredUpdate.locationRequest, LocationService.cache);

            // Set result callback
            result.setResultCallback(new ResultCallback<Status>()
            {
                @Override
                public void onResult(@NonNull Status status)
                {
                    LocationUpdateResult(status);
                }
            }, RESULT_CB_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        }
    }

    private void LocationUpdateResult(final Status status)
    {
        Dbg.info(TAG, "Location Update Result Received");

        if (!status.isSuccess()) {
            Dbg.error(TAG, "Request Location Update failed " + status.getStatusMessage());
            ReportError(Constants.Location.ERROR_UPDATES_ERROR, status);
        } else {
            // Wait for location to be available
            WaitForUpdate waitTask = new WaitForUpdate();
            waitTask.execute();
        }
    }

    // Error / Success reporting to listener
    private void ReportError(int errorCode, Status status)
    {
        if (listener != null)
        {
            listener.onLocationInitError(errorCode, status);
        }
    }

    private void ReportSuccess(LocationObj locationObject)
    {
        if (listener != null)
        {
            listener.onLocationInitSuccess(locationObject);
        }
    }
}

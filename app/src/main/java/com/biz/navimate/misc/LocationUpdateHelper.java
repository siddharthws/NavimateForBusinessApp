package com.biz.navimate.misc;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationUpdateHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_UPDATE_HELPER";

    // Erro codes for location initialization
    public static final int ERROR_RESOLUTION_REQUIRED           = 1;
    public static final int ERROR_UNAVAILABLE                   = 2;
    public static final int ERROR_UNKNOWN_SETTINGS_ERROR        = 3;
    public static final int ERROR_UPDATES_UNAVAILABLE           = 4;
    public static final int ERROR_UPDATES_ERROR                 = 5;
    public static final int ERROR_PERMISSION_REQUIRED           = 6;
    public static final int ERROR_API_CLIENT                    = 7;

    // Timeout for Result Callbacks
    private static final int RESULT_CB_TIMEOUT_MS = 10000;

    // Client Tags
    public static final int CLIENT_TAG_MAP      = 1;

    // ----------------------- Classes ---------------------------//
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
    // Global Location Clients alng with required update object for the client
    private static HashMap<Integer, LocationUpdate> clients = null;

    // Current Update objetc as per which location is being initialized
    private static LocationUpdate currentUpdate               = null;

    private Context context = null;

    // ----------------------- Constructor ----------------------- //
    public LocationUpdateHelper(Context context)
    {
        this.context = context;
    }

    // ----------------------- Overrides ----------------------- //

    // ----------------------- Public APIs ----------------------- //
    // APIs to Add / Remove Location Clients
    // Each client provides a LocationUpdateObject depending on what type of service it wants
    // Start() API queries the arbiter for which type of location update should be started
    public void AddClient(int tag, LocationUpdate serviceObject)
    {
        // Init clients if null
        if (clients == null)
        {
            clients = new HashMap<>();
        }

        // Add to clients
        clients.put(tag, serviceObject);

        // Restart Update Logic
        Start();
    }

    public void RemoveClient(int tag)
    {
        if (clients != null)
        {
            // Remove from clients
            clients.remove(tag);

            // Restart Update Logic
            Start();
        }
    }

    // APIs to start / stop location updates
    public void Start()
    {
        // Get Required Update Object from the arbiter
        LocationUpdate requiredUpdate = LocationServiceArbiter();

        // Check if no updates are required
        if (requiredUpdate == null)
        {
            // Check if currently any updates are running
            if (currentUpdate != null)
            {
                // Stop Updates
                Stop();
            }

            return;
        }

        // If required and current updates are equal and location is already updating, report success
        if (requiredUpdate.equals(currentUpdate) && IsUpdating())
        {
            ReportSuccess(LocationCache.instance.GetLocation());
            return;
        }

        // Update currentUpdateObject cache
        currentUpdate = requiredUpdate;

        // Send Location Settings request
        SendLocationSettingsRequest();
    }

    public void Stop()
    {
        if (GoogleApiClientHolder.instance.apiClient.isConnected())
        {
            PendingResult<Status> result = LocationServices.FusedLocationApi.removeLocationUpdates(GoogleApiClientHolder.instance.apiClient, LocationCache.instance);

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

    // API to check if location is updating correctly
    // Cecks this based on time stamp of last location and the currently enabled service
    public boolean IsUpdating()
    {
        boolean bUpdating = false;

        // Get Last Known Location
        LocationObj lastKnownLocation = LocationCache.instance.GetLocation();

        // Check time signature
        if (lastKnownLocation != null)
        {
            if (currentUpdate != null)
            {
                long lastUpdateTime = lastKnownLocation.timestamp;
                if ((System.currentTimeMillis() - lastUpdateTime) < currentUpdate.expiry)
                {
                    bUpdating = true;
                }
            }
        }

        return bUpdating;
    }

    // ----------------------- Private APIs ----------------------- //
    // API to get the location service object based on all the added clients
    // Process is called Location Service Arbitration
    private LocationUpdate LocationServiceArbiter()
    {
        LocationUpdate requiredUpdates = null;

        if ((clients != null) && (clients.size() > 0))
        {
            Iterator<Integer> tagIter = clients.keySet().iterator();
            while (tagIter.hasNext())
            {
                LocationUpdate clientUpdates = clients.get(tagIter.next());
                if ((requiredUpdates == null) || (clientUpdates.interval < requiredUpdates.interval))
                {
                    requiredUpdates = clientUpdates;
                }
            }
        }

        return requiredUpdates;
    }

    // APIs to initialize location updates
    private void SendLocationSettingsRequest()
    {
        Dbg.info(TAG, "Sending Location Settings Request");

        // Create Location Settings Request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(currentUpdate.locationRequest);
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
                    errorCode = ERROR_RESOLUTION_REQUIRED;
                    break;
                }

                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                {
                    Dbg.error(TAG, "Location service unavailable");
                    errorCode = ERROR_UNAVAILABLE;
                    break;
                }

                default:
                {
                    Dbg.error(TAG, "Location Settings Init failed with status code : " + status.getStatusMessage() + " : " + status.getStatusCode());
                    errorCode = ERROR_UNKNOWN_SETTINGS_ERROR;
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

        if (permissionCheck != PermissionChecker.PERMISSION_GRANTED)
        {
            ReportError(ERROR_PERMISSION_REQUIRED, null);
        }
        else if (!apiClient.isConnected())
        {
            ReportError(ERROR_API_CLIENT, null);
        }
        else
        {
            // Request Location Updates
            PendingResult<Status> result = LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, currentUpdate.locationRequest, LocationCache.instance);

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

    private void LocationUpdateResult(Status status)
    {
        Dbg.info(TAG, "Location Update Result Received");

        if (!status.isSuccess())
        {
            Dbg.error(TAG, "Request Location Update failed " + status.getStatusMessage());
            ReportError(ERROR_UPDATES_ERROR, status);
        }
        else
        {
            // Check if Location is available through Last Location API
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED)
            {
                if (IsUpdating())
                {
                    ReportSuccess(LocationCache.instance.GetLocation());
                }
                else
                {
                    Location lastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(GoogleApiClientHolder.instance.apiClient);
                    if (lastKnowLocation != null) {
                        LocationCache.instance.onLocationChanged(lastKnowLocation);
                        if (IsUpdating()) {
                            ReportSuccess(LocationCache.instance.GetLocation());
                        } else {
                            ReportError(ERROR_UPDATES_ERROR, status);
                        }
                    } else {
                        ReportError(ERROR_UPDATES_ERROR, status);
                    }
                }
            }
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

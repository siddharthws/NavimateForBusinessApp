package com.biz.navimate.services;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationReportObject;
import com.biz.navimate.objects.LocationUpdate;

/**
 * Created by Sai_Kameswari on 17-02-2018.
 */

public class LocReportService extends BaseService {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOC_REPORT_SERVICE";

    // Macros for time intervals
    private static final int   TIME_MS_60_S            = 60 * 1000; // 60 second interval

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static LocReportService service   = null;
    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void Init(){service = this;}

    @Override
    public void StickyServiceJob()
    {
        //Location Report Logic
        checkLocationStatus();

        // Getinterval for next sleep
        long interval = GetSleepInterval();

        // Sleep
        Sleep(interval);
    }

    @Override
    public void Destroy(){}

    // ----------------------- Public APIs ----------------------- //
    // APIs to start / stop the service
    public static void StartService(Context context) {
        if (!IsRunning()) {
            // Add tracker client to receive slow updates
            LocationService.AddClient(service, Constants.Location.CLIENT_TAG_LOC_REPORT, LocationUpdate.SLOW);

            // Start Service
            StartService(context, LocReportService.class);
        }
    }

    public static void StopService() {
        // Stop service
        if (IsRunning()) {
            // Remove tracker client
            LocationService.RemoveClient(Constants.Location.CLIENT_TAG_LOC_REPORT);

            // Stop this service
            StopService(service);
        }
    }

    public static boolean IsRunning()
    {
        return IsRunning(service);
    }

    // ----------------------- Private APIs ----------------------- //
    // API to get sleep interval as per current params
    private long GetSleepInterval() {
        return TIME_MS_60_S;
    }

    // API to perform tracking logic
    private void checkLocationStatus() {
        if (LocationService.IsUpdating()) {
            saveLocReport(Constants.Tracker.ERROR_NONE);
        } else if (!LocationService.IsGpsEnabled(this)) {
            // Send GPS error to server
            saveLocReport(Constants.Tracker.ERROR_NO_GPS);
        } else if (!LocationService.IsLocationPermissionGranted(this)) {
            // Send Permission error to server
            saveLocReport(Constants.Tracker.ERROR_NO_PERMISSION);
        } else {
            // Save Location not updating error to server
            saveLocReport(Constants.Tracker.ERROR_NO_UPDATES);
        }
    }

    // API to fetch Lat Long Details and store in local DB
    private void saveLocReport(int statusCode) {
        // Get current location
        LocationObj currentLoc = LocationService.cache.GetLocation();
        double latitude  = 0;
        double longitude = 0;

        //check if status is invalid or valid
        if(statusCode == Constants.Tracker.ERROR_NONE)
        {
            latitude = currentLoc.latlng.latitude;
            longitude = currentLoc.latlng.longitude;
        }

        //create a LocReportObject
        LocationReportObject locationReportObject = new LocationReportObject(
                                                        Constants.Misc.ID_INVALID,
                                                        latitude,
                                                        longitude,
                                                        System.currentTimeMillis(),
                                                        statusCode);

        //Save the LocReportObject in DB
        DbHelper.locationReportTable.Save(locationReportObject);
    }
}
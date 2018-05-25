package com.biz.navimate.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationReportObject;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.server.SyncLocReportTask;
import java.util.Calendar;

/**
 * Created by Sai_Kameswari on 17-02-2018.
 */

public class LocReportService   extends     BaseService
                                implements  IfaceServer.SyncLocReport {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOC_REPORT_SERVICE";

    // Macros for time intervals
    private static final int   TIME_MS_60_S            = 60 * 1000; // 60 second interval
    private static final int   TIME_MS_15_M            = 15 * 60 * 1000; // 15 Minute interval

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static LocReportService service   = null;
    private long lastSyncTime = 0L;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void Init(){ service = this; }

    @Override
    public void StickyServiceJob() {
        //check if current time is between working hours
        if (isWorkingHours()) {
            //Location Report Logic
            checkLocationStatus();
        }

        //check if last sync time is expired
        if (isSyncTimeExpired()) {
            if (DbHelper.locationReportTable.GetAll().size() > 0) {
                // Trigger Sync task
                SyncLocReportTask syncLocReportTask = new SyncLocReportTask(this);
                syncLocReportTask.SetListener(this);
                syncLocReportTask.execute();
            } else {
                // Nothing to sync. Just Update Last Sync time
                lastSyncTime = System.currentTimeMillis();
            }
        }

        // Sleep
        Sleep(TIME_MS_60_S);
    }

    @Override
    public void Destroy(){}

    // Listeners for Location report success / failure
    @Override
    public void onLocReportSynced() {
        // Update Sync time
        lastSyncTime = System.currentTimeMillis();
    }

    @Override
    public void onLocReportSyncFailed() {
        // Do Nothing. Report will try syncing again on next iteration
    }

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
        double latitude  = 0;
        double longitude = 0;
        float speed = 0.0f;

        //check if status is invalid or valid
        if(statusCode == Constants.Tracker.ERROR_NONE) {
            LocationObj currentLoc = LocationService.cache.GetLocation();
            latitude = currentLoc.latlng.latitude;
            longitude = currentLoc.latlng.longitude;
            speed = currentLoc.speed;
        }

        // Get battery info
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int battery = Math.round(level / (float)scale);

        //create a LocReportObject
        LocationReportObject locationReportObject = new LocationReportObject(
                                                        Constants.Misc.ID_INVALID,
                                                        latitude,
                                                        longitude,
                                                        System.currentTimeMillis(),
                                                        statusCode,
                                                        battery,
                                                        speed);

        //Save the LocReportObject in DB
        DbHelper.locationReportTable.Save(locationReportObject);
    }

    //API to check if the current time is in between working hours
    private boolean isWorkingHours() {
        //Get working hours from Preferences
        int startHr = Preferences.GetAccountSettings().startTime;
        int endHr = Preferences.GetAccountSettings().endTime;

        //Get current time in Hour format(0-23)
        Calendar rightNow = Calendar.getInstance();
        int currentHr = rightNow.get(Calendar.HOUR_OF_DAY);

        //check if currrent time is between working hours
        if(currentHr >= startHr && currentHr < endHr) {return true;}
        return false;
    }

    //API to check if the sync time with server is expired
    private boolean isSyncTimeExpired() {
        // Get elapsed time
        long currentTime = System.currentTimeMillis();
        long elapsedTimeMs = currentTime - lastSyncTime;

        // Check if elapsed time is more than required sync time (15 minutes)
        return (elapsedTimeMs > TIME_MS_15_M);
    }
}

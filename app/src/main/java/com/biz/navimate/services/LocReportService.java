package com.biz.navimate.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.interfaces.IfaceLocation;
import com.biz.navimate.misc.InternetHelper;
import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationReportObject;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.server.SyncLocReportTask;
import java.util.Calendar;

/**
 * Created by Sai_Kameswari on 17-02-2018.
 */

public class LocReportService   extends     BaseService
                                implements  IfaceServer.SyncLocReport, IfaceLocation.MovementUpdate {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOC_REPORT_SERVICE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static LocReportService service   = null;
    private long lastSyncTime = 0L;
    private LocationUpdate lastLocUpdate = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void Init(){
        service = this;

        // Add Movement Update listener
        LocationCache.AddMovementListener(this);
    }

    @Override
    public void StickyServiceJob() {
        //check if current time is between working hours
        if (isWorkingHours()) {
            //Location Report Logic
            checkLocationStatus();
        }

        // Check if report syncing is required
        if (shouldSync()) {
            // Trigger Sync task
            SyncLocReportTask syncLocReportTask = new SyncLocReportTask(this);
            syncLocReportTask.SetListener(this);
            syncLocReportTask.execute();
        }

        // Sleep for given interval
        Sleep(GetInterval());
    }

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

    @Override
    public void onMovementUpdated() {
        bInterruptSleep = true;
    }

    // ----------------------- Public APIs ----------------------- //
    // APIs to start / stop the service
    public static void StartService(Context context) {
        if (!IsRunning()) {
            // Add tracker client to receive slow updates
            LocationService.AddClient(service, Constants.Location.CLIENT_TAG_LOC_REPORT, LocationUpdate.FAST);

            // Start Service
            StartService(context, LocReportService.class);
        }
    }

    public static void StopService() {
        // Stop service
        if (IsRunning()) {
            service.Destroy();

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

        // Add other values for submission with OK Status code
        if(statusCode == Constants.Tracker.ERROR_NONE) {
            LocationObj currentLoc = LocationService.cache.GetLocation();
            latitude = Statics.round(currentLoc.latlng.latitude, 5);
            longitude = Statics.round(currentLoc.latlng.longitude, 5);
            speed = currentLoc.speed;
        }

        // Ignore update if last submission contains the same values as this one and is not very new
        LocationReportObject latestReport = DbHelper.locationReportTable.GetLatest();
        if (latestReport != null &&
            latestReport.status == statusCode &&
            latestReport.latitude == latitude &&
            latestReport.longitude == longitude &&
            System.currentTimeMillis() - latestReport.timestamp < Constants.Date.TIME_1_HR) {
            return;
        }

        // Get battery info
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int battery = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

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

    //API to check if report syncing is required
    private boolean shouldSync() {
        boolean bSync = false;

        // Check Internet
        if (InternetHelper.IsInternetAvailable(this)) {
            // Get number of records and time since last sync
            int recordsCount = DbHelper.locationReportTable.GetReportsToSync().size();

            // Check if there are any records to sync
            if (recordsCount > 0) {
                // Get elapsed time since last sync
                long currentTime = System.currentTimeMillis();
                long elapsedTimeMs = currentTime - lastSyncTime;

                // Sync if too many unsynced records or too much time has passed since last sync
                if (recordsCount >= 50 || elapsedTimeMs > Constants.Date.TIME_1_HR) {
                    bSync = true;
                }
            }
        }

        return bSync;
    }

    private long GetInterval() {
        long interval = Constants.Date.TIME_1_MIN;

        // Gte last saved location report object
        LocationReportObject locReport = DbHelper.locationReportTable.GetLatest();
        LocationObj latestLoc = LocationService.cache.GetLocation();

        // Set 1 minute interval for invalid status
        if (locReport != null && locReport.status == Constants.Tracker.ERROR_NONE) {
            // Change period if user is going too slow or fast
            switch (latestLoc.GetMovement()) {
                case LocationObj.STANDING: {
                    interval = Constants.Date.TIME_2_MIN;
                    break;
                }
                case LocationObj.DRIVING: {
                    interval = Constants.Date.TIME_30_SEC;
                    break;
                }
            }
        }

        return interval;
    }

    private void Destroy() {
        // Save Offline status to report
        saveLocReport(Constants.Tracker.ERROR_OFFLINE);

        // Remove Location Client
        LocationService.RemoveClient(Constants.Location.CLIENT_TAG_LOC_REPORT);

        // Remove Movement Udpate Listener
        LocationCache.RemoveMovementListener(this);
    }
}

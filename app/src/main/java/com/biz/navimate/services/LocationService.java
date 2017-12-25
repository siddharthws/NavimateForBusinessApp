package com.biz.navimate.services;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.objects.Statics;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Siddharth on 18-12-2017.
 */

public class LocationService extends BaseService implements LocationUpdateHelper.LocationInitInterface {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_SERVICE";

    private static final long UPDATE_INTERVAL_MS     = 10 * 1000; // 10 seconds
    private static final long PREF_SAVE_INTERVAL_MS  = 60 * 1000; // 60 seconds

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceLocationInit {
        void onLocationError(int errorCode, Status status);
        void onLocationSuccess(LocationObj location);
    }
    private static ArrayList<IfaceLocationInit> initListeners = new ArrayList<>();
    public static void AddInitListener(IfaceLocationInit listener) {
        if (!initListeners.contains(listener)) {
            initListeners.add(listener);
        }

        // Interrupt sleep
        if (service != null) {
            service.bInterruptSleep = true;
        }
    }
    public static void RemoveInitListener(IfaceLocationInit listener) {
        if (initListeners.contains(listener)) {
            initListeners.remove(listener);
        }
    }

    // ----------------------- Globals ----------------------- //
    // Service Object
    private static LocationService service = null;

    // Global Location Clients alng with required update object for the client
    private static HashMap<Integer, LocationUpdate> clients = new HashMap<>();

    // Location Cache
    public static LocationCache cache = null;

    // Location Init Helper for initializing location when not available
    private LocationUpdateHelper locUpdateHelper = null;

    // Flag for ongoing location update
    private boolean bOngoingUpdate = false;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void Init() {
        // Set Service
        service = this;

        // Init Cache
        if (cache == null) {
            cache = new LocationCache();
        }

        // Init Location Update Helper
        locUpdateHelper = new LocationUpdateHelper(this);
        locUpdateHelper.SetInitListener(this);
    }

    @Override
    public void StickyServiceJob() {
        // Location Init State Machine
        if (IsUpdating()) {
            // Save in preferences if old location has expired
            if ((System.currentTimeMillis() - Preferences.GetLocation().timestamp) > PREF_SAVE_INTERVAL_MS) {
                Preferences.SetLocation(this, cache.GetLocation());
            }

            // Stop Updates if no clients have requested
            if (clients.size() == 0) {
                // Stop Location Updates
                locUpdateHelper.Stop();
            }

            // Report success
            ReportSuccess(LocationService.cache.GetLocation());
        } else if (clients.size() == 0) {
            // No clients added for location service. Report error
            ReportError(Constants.Location.ERROR_NO_CLIENTS, null);
        } else if (!bOngoingUpdate) {
            // Start updates if they are not ongoing
            StartUpdates();
        }

        // Sleep for sometime
        Sleep(UPDATE_INTERVAL_MS);
    }

    @Override
    public void Destroy(){
        // Stop location updates
        locUpdateHelper.Stop();
    }

    @Override
    public void onLocationInitError(int errorCode, Status status) {
        // Report success
        ReportError(errorCode, status);

        // Mark Update cmplete
        bOngoingUpdate = false;
    }

    @Override
    public void onLocationInitSuccess(LocationObj location) {
        // Report success
        ReportSuccess(location);

        // Mark Update complete
        bOngoingUpdate = false;
    }

    // ----------------------- Public APIs ----------------------- //
    // APIs to start / stop / check status of the service
    public static void StartService(Context context) {
        if (!IsRunning()) {
            // Start Service
            StartService(context, LocationService.class);
        }
    }

    public static void StopService() {
        // Stop service
        if (IsRunning()) {
            // Stop this service
            StopService(service);
        }
    }

    public static boolean IsRunning()
    {
        return IsRunning(service);
    }

    // APIs to Add / Remove Location Clients
    // Each client provides a LocationUpdate object depending on what type of service it wants
    // Arbiter decides which LocationUpdate object needs to be used depending on different clients
    public static void AddClient(Context context, int tag, LocationUpdate serviceObject) {
        // Add to clients
        clients.put(tag, serviceObject);

        // Interrupt sleep to re run init logic
        service.bInterruptSleep = true;
    }

    public static void RemoveClient(int tag) {
        // Remove from clients
        clients.remove(tag);

        // Interrupt sleep to re run init logic
        service.bInterruptSleep = true;
    }

    // API to check if location is updating correctly
    // Checks this based on time stamp of last location and current client requirements
    public static boolean IsUpdating() {
        boolean bUpdating = false;

        // Check if service is running
        if (IsRunning()) {
            // Check if any clients have requested location
            if (clients.size() > 0) {
                // Check if last known location is valid
                LocationObj lastKnownLocation = cache.GetLocation();
                if (Statics.IsPositionValid(lastKnownLocation.latlng)) {
                    // Get time since last update
                    long lastUpdateTime = lastKnownLocation.timestamp;
                    long elapsedTime = System.currentTimeMillis() - lastUpdateTime;

                    // Get current update required as per arbiter
                    LocationUpdate requiredUpdate = service.LocationServiceArbiter();

                    // Check if location is expired
                    bUpdating = elapsedTime < requiredUpdate.expiry;
                }
            }
        }

        return bUpdating;
    }

    // ----------------------- Private APIs ----------------------- //
    // API to start location updates
    private void StartUpdates() {
        // Get Required Update Object from the arbiter
        LocationUpdate requiredUpdate = LocationServiceArbiter();

        // Start updates depending on required update type
        locUpdateHelper.Start(requiredUpdate);

        // Set Ongoing update flag
        bOngoingUpdate = true;
    }

    // API to get the location service object based on all the added clients
    // Process is called Location Service Arbitration
    private LocationUpdate LocationServiceArbiter() {
        LocationUpdate requiredUpdates = null;

        if (clients.size() > 0) {
            // Iterate through all clients
            Iterator<Integer> tagIter = clients.keySet().iterator();
            while (tagIter.hasNext()) {
                // Get Update required by this client
                LocationUpdate clientUpdates = clients.get(tagIter.next());

                // Save Update with least interval
                if ((requiredUpdates == null) ||
                    (clientUpdates.interval < requiredUpdates.interval)) {
                    requiredUpdates = clientUpdates;
                }
            }
        }

        return requiredUpdates;
    }

    // APis to report error / success to listeners
    private void ReportSuccess(LocationObj location) {
        // Send success to all listeners
        for (IfaceLocationInit listener : initListeners) {
            listener.onLocationSuccess(location);
        }
    }

    private void ReportError(int errorCode, Status status) {
        // Send error to all listeners
        for (IfaceLocationInit listener : initListeners) {
            listener.onLocationError(errorCode, status);
        }
    }

    // Miscellaneous Helper APIs
    // API to get GPS status
    public static boolean IsGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // API to check Location permission
    public static boolean IsLocationPermissionGranted(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionCheck == PermissionChecker.PERMISSION_GRANTED;
    }

    // API to check if 2 locations are approximately same (5 decimal digit rounding and checking)
    public static boolean IsApproxLocations(LatLng loc1, LatLng loc2) {
        // Get rounded Lat/Lng
        double rndLat1 = (double) Math.round(loc1.latitude * 100000d) / 100000d;
        double rndLat2 = (double) Math.round(loc2.latitude * 100000d) / 100000d;
        double rndLng1 = (double) Math.round(loc1.longitude * 100000d) / 100000d;
        double rndLng2 = (double) Math.round(loc2.longitude * 100000d) / 100000d;

        // Compare and return
        return ((rndLat1 == rndLat2) && (rndLng1 == rndLng2));
    }
}

package com.biz.navimate.misc;

import android.location.Location;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceLocation;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.Statics;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class LocationCache implements LocationListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CURRENT_LOCATION_CACHE";

    private static final int CACHE_SIZE                      = 20;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // movement update listeners
    private static ArrayList<IfaceLocation.MovementUpdate> listeners = new ArrayList<>();

    // Cache of Locations
    private ArrayList<LocationObj> cache = null;

    // ----------------------- Constructor ----------------------- //
    public LocationCache()
    {
        // Init Cache
        cache   = new ArrayList<>();

        // Feed first lat lng into cache using preferences
        AddToCache(Preferences.GetLocation());
    }

    // ----------------------- Overrides ----------------------- //

    @Override
    public void onLocationChanged(Location location)
    {
        // Ignore if tracking is disabled
        if (!Preferences.GetTracking()) {
            return;
        }

        // Check if new location is better
        if (!isBetterLocation(location, GetLocation()))
        {
            Dbg.info(TAG, "Location is worse than previous one. Ignoring");
            return;
        }

        // Create local Location Object
        float speed = GetSpeedForNewLocation(location);
        LocationObj locObj = new LocationObj(location.getLatitude(), location.getLongitude(), location.getTime(), location.getAccuracy(), speed);

        // Add Location to cache
        AddToCache(locObj);

        // Call movement listeners if movement type got updated
        if (cache.size() < 2 || cache.get(0).GetMovement() != cache.get(1).GetMovement()) {
            for (IfaceLocation.MovementUpdate listener : listeners) {
                listener.onMovementUpdated();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Methods to add and remove movement listeners
    public static void AddMovementListener (IfaceLocation.MovementUpdate listener) {
        listeners.add(listener);
    }
    public static void RemoveMovementListener (IfaceLocation.MovementUpdate listener) {
        listeners.remove(listener);
    }

    // APIs to get latest location related data
    public LocationObj GetLocation()
    {
        return GetLocationAtIndex(0);
    }

    // ----------------------- Private APIs ----------------------- //
    // APi to add a Location Object to cache
    private void AddToCache(LocationObj location)
    {
        // Add to cache
        cache.add(0, location);

        // Remove oldest entries outside max cache size
        while (cache.size() > CACHE_SIZE)
        {
            cache.remove(cache.size() - 1);
        }
    }

    // API to get a location at specified index from cache
    private LocationObj GetLocationAtIndex(int index)
    {
        if (cache != null)
        {
            if (cache.size() > index)
            {
                return cache.get(index);
            }
        }

        return null;
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, LocationObj currentLocation) {
        if (currentLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentLocation.timestamp;
        boolean isSignificantlyNewer = timeDelta > Constants.Date.TIME_2_MIN;
        boolean isSignificantlyOlder = timeDelta < -Constants.Date.TIME_2_MIN;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentLocation.accuracy);
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate) {
            return true;
        }

        // Return false for very less accurate data
        return false;
    }

    // Speed calculator
    private float GetSpeedForNewLocation(Location location) {
        // Return 0 if no valid values available in cache or if last known location is too old
        if (cache.size() == 0 ||
            location.getTime() - GetLocationAtIndex(0).timestamp > Constants.Date.TIME_30_MIN) {
            return 0;
        }

        // Calculate speed by averaging last few speeds
        float totalSpeed = 0;
        int count = 0;

        // Current segment speed
        float segmentSpeed = GetSegmentSpeed(   new LatLng( location.getLatitude(),
                                                            location.getLongitude()),
                                                cache.get(0).latlng,
                                                location.getTime(),
                                                cache.get(0).timestamp);
        totalSpeed += segmentSpeed;
        count++;

        // Past segment speeds
        LocationObj segmentLoc = null;
        switch (cache.size()) {
            case 3:
                // Add speed from segment between second and third location of cache
                segmentLoc = cache.get(1);
                if (segmentLoc.GetMovement() != LocationObj.STANDING) {
                    totalSpeed +=  segmentLoc.speed;
                    count++;
                }
            case 2:
                // Add speed from segment between first and second location of cache
                segmentLoc = cache.get(0);
                if (segmentLoc.GetMovement() != LocationObj.STANDING) {
                    totalSpeed +=  segmentLoc.speed;
                    count++;
                }
        }

        // Get average speed
        float speed = totalSpeed / (float) count;

        // Round to 2 decimal places
        speed = (float) Statics.round(speed, 2);
        return speed;
    }

    // Method to get speed between location points in km/hr
    private float GetSegmentSpeed(LatLng start, LatLng end, long startTime, long endTime) {
        // get time difference
        long timeDeltaS = Math.abs(endTime - startTime) / 1000;
        if (timeDeltaS == 0) {
            return 0.0f;
        }

        // Get distance
        long distance = 0;
        if (Statics.IsPositionValid(start) && Statics.IsPositionValid(end)) {
            distance = Statics.GetDistanceBetweenCoordinates(start, end);
        }

        // Get speed
        float speed = ((float) distance) / ((float) timeDeltaS);

        // Convert to km/hr
        speed = ((speed * 18.0f) / 5.0f);
        return speed;
    }
}

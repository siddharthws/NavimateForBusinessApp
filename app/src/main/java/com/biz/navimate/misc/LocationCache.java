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
    private float GetSpeedForNewLocation(Location location)
    {
        float speed = 0;

        // Return 0 if no valid values available in cache
        if (cache.size() == 0 || (location.getTime() - GetLocationAtIndex(0).timestamp > 15*60*1000)) {
            return 0;
        }

        // Add speeds for last 4 segments
        int speedCacheSize = Math.min(3, cache.size());

        // Calculate Speed by averaging last 3 values
        for (int i = 0; i < speedCacheSize - 1; i++)
        {
            // Get start and end of segment
            LocationObj segmentStart = GetLocationAtIndex(i);
            LocationObj segmentEnd = GetLocationAtIndex(i+1);

            // Get distance and time to cover the segment
            int    segmentDistanceM = Statics.GetDistanceBetweenCoordinates(segmentStart.latlng, segmentEnd.latlng);
            long   segmentTimeS = (Math.abs(segmentEnd.timestamp - segmentStart.timestamp)) / 1000;

            // Get speed adn add to total
            double segmentSpeed = (((double) segmentDistanceM) / (double) segmentTimeS);
            speed += segmentSpeed;
        }

        // Add speed for current segment
        LatLng segmentStart = new LatLng(location.getLatitude(), location.getLongitude());
        LocationObj segmentEnd = GetLocationAtIndex(0);
        if (Statics.IsPositionValid(segmentStart) && Statics.IsPositionValid(segmentEnd.latlng) && (segmentEnd.timestamp < location.getTime()))
        {
            int    segmentDistanceM = Statics.GetDistanceBetweenCoordinates(segmentStart, segmentEnd.latlng);
            long   segmentTimeS = (Math.abs(segmentEnd.timestamp - location.getTime())) / 1000;

            double segmentSpeed = (((double) segmentDistanceM) / (double) segmentTimeS);
            speed += segmentSpeed;
            speedCacheSize++;
        }

        // Average out
        speed = speed / (float) speedCacheSize;

        // Convert to km/hr
        speed = ((speed * 18.0f) / 5.0f);

        // Round to 2 decimal places
        speed = (float) Statics.round(speed, 2);

        return speed;
    }
}

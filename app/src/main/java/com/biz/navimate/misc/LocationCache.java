package com.biz.navimate.misc;

import android.location.Location;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.Statics;
import com.google.android.gms.location.LocationListener;

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
        // Create location object form newly received location
        LocationObj newLocation = new LocationObj(location);

        // Add Location to cache
        AddToCache(newLocation);
    }

    // ----------------------- Public APIs ----------------------- //
    // APIs to get latest location related data
    public LocationObj GetLocation()
    {
        return GetLocationAtIndex(0);
    }

    public float GetSpeed()
    {
        float speed = 0;
        int speedCacheSize = 2;

        // Calculate Speed by averaging last 2 values
        for (int i = 1; i <= speedCacheSize; i++)
        {
            if (cache.size() > i)
            {
                LocationObj segmentStart = GetLocationAtIndex(i);
                LocationObj segmentEnd = GetLocationAtIndex(i-1);

                // Calculate only for valid values
                if (Statics.IsPositionValid(segmentStart.latlng) && Statics.IsPositionValid(segmentEnd.latlng))
                {
                    int    segmentDistanceM = Statics.GetDistanceBetweenCoordinates(segmentStart.latlng, segmentEnd.latlng);
                    long   segmentTimeS = (segmentEnd.timestamp - segmentStart.timestamp) / 1000;
                    if (segmentTimeS < 0)
                    {
                        segmentTimeS = 1;
                    }

                    double segmentSpeed = (((double) segmentDistanceM) / (double) segmentTimeS);
                    speed += segmentSpeed;
                }
            }
        }

        // Average out
        speed = speed / (float) speedCacheSize;

        // Convert to km/hr
        speed = ((speed * 18.0f) / 5.0f);

        return speed;
    }

    // ----------------------- Private APIs ----------------------- //
    // APi to add a Location Object to cache
    private void AddToCache(LocationObj location)
    {
        // Check if new location is better
        if (!IsLocationBetterThanCurrent(location))
        {
            Dbg.info(TAG, "Location is worse than previous one. Ignoring");
            return;
        }

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

    // Location Filter
    private boolean IsLocationBetterThanCurrent(LocationObj location)
    {
        // Place Holder. Add Location Filters here
        return true;
    }
}

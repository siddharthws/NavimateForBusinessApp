package com.biz.navimate.maps;

import com.biz.navimate.objects.MarkerObj;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class MarkerHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "MARKER_HELPER";

    // ----------------------- Interfaces ----------------------- //

    // ----------------------- Globals ----------------------- //
    // Map Instance on which markers take effect
    private GoogleMap map                       = null;

    // MarkerObj Cache
    private ArrayList<MarkerObj.Base> cache     = null;

    // ----------------------- Constructor ----------------------- //
    public MarkerHelper()
    {
        // Init cache
        cache = new ArrayList<>();
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // API used to add markers on map
    public void Add(MarkerObj.Base marker)
    {
        // Don't add if marker is already present in cache
        if (!cache.contains(marker))
        {
            // Add object to cache
            cache.add(marker);

            // Show on map if map is available
            if (map != null)
            {
                // Get Marker Options from Marker Object
                MarkerOptions markerOpt = marker.GetMarkerOptions();

                // Add marker to map and store the marker object returned by map
                marker.marker = map.addMarker(markerOpt);
            }
        }
    }

    // API used to remove markers from map
    public void Remove(MarkerObj.Base marker)
    {
        // Check if this marekr presetn in cache
        if (cache.contains(marker))
        {
            // Remove marker from map
            if (marker.marker != null)
            {
                marker.marker.remove();
            }

            // Remove object from cache
            cache.remove(marker);
        }
    }

    // API to clear all markers from map
    public void Clear()
    {
        // Remove all markers from map
        for (MarkerObj.Base markerObj : cache)
        {
            if (markerObj.marker != null)
            {
                markerObj.marker.remove();
            }
        }

        // Clear Cahce
        cache.clear();
    }

    // API to Load map. This will be called when map load has completed.
    // All markers in cache are added to map when map is loaded for first time
    public void LoadMap(GoogleMap map)
    {
        // Assign map object
        this.map = map;

        // Show all markers in cache on the map
        for (MarkerObj.Base markerObj : cache)
        {
            // Get Marker Options from Marker Object
            MarkerOptions markerOpt = markerObj.GetMarkerOptions();

            // Add marker to map and store the marker object returned by map
            markerObj.marker = map.addMarker(markerOpt);
        }
    }

    // ----------------------- Private APIs ----------------------- //
}

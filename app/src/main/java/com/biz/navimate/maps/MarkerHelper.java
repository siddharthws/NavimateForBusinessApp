package com.biz.navimate.maps;

import com.biz.navimate.objects.Marker;
import com.google.android.gms.maps.GoogleMap;

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

    // Marker Cache
    private ArrayList<Marker.Base> cache     = null;

    // ----------------------- Constructor ----------------------- //
    public MarkerHelper()
    {
        // Init cache
        cache = new ArrayList<>();
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // API used to add markers on map
    public void Add(Marker.Base marker)
    {
        // Placeholder
    }

    // API used to remove markers from map
    public void Remove(Marker.Base marker)
    {
        // Placeholder
    }

    // API to clear all markers from map
    public void Clear()
    {
        // Placeholder
    }

    // API to Load map. This will be called when map load has completed.
    // All markers in cache are added to map when map is loaded for first time
    public void LoadMap(GoogleMap map)
    {
        // Placeholder
    }

    // ----------------------- Private APIs ----------------------- //
}

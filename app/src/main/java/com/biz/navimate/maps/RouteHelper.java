package com.biz.navimate.maps;

import com.biz.navimate.objects.Route;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class RouteHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ROUTE_HELPER";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private GoogleMap   map             = null;

    private ArrayList<Route.Base> cache = null;

    // ----------------------- Constructor ----------------------- //
    public RouteHelper()
    {
        cache = new ArrayList<>();
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public void Add(Route.Base route)
    {
        // Don't add if route is already present in cache
        if (!cache.contains(route))
        {
            // Add object to cache
            cache.add(route);

            // Show on map if map is available
            if (map != null)
            {
                // Get Polyline Options from Route Object
                PolylineOptions polyOpt = route.GetPolylineOptions();

                // Add polyline to map and store the polyline object returned by map
                route.polyline = map.addPolyline(polyOpt);
            }
        }
    }

    public void Remove(Route.Base route)
    {
        // Check if this route is present in cache
        if (cache.contains(route))
        {
            // Remove marker from map
            if (route.polyline != null)
            {
                route.polyline.remove();
            }

            // Remove object from cache
            cache.remove(route);
        }
    }

    public void Clear()
    {
        // Remove all polylines from map
        for (Route.Base routeObj : cache)
        {
            if (routeObj.polyline != null)
            {
                routeObj.polyline.remove();
            }
        }

        // Clear Cahce
        cache.clear();
    }

    public void LoadMap(GoogleMap map)
    {
        // Assign map object
        this.map = map;

        // Show all markers in cache on the map
        for (Route.Base route : cache)
        {
            // Get Polyline Options from Route Object
            PolylineOptions polyOpt = route.GetPolylineOptions();

            // Add polyline to map and store the polyline object returned by map
            route.polyline = map.addPolyline(polyOpt);
        }
    }

    // ----------------------- Private APIs ----------------------- //

    public Route.Base GetRouteObjectFromPoly(Polyline polyline)
    {
        // Find marker in cache
        for (Route.Base route : cache)
        {
            if (polyline.equals(route.polyline))
            {
                return route;
            }
        }

        return null;
    }
}

package com.biz.navimate.maps;

import com.biz.navimate.objects.Route;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

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
        // Place Holder
    }

    public void Remove(Route.Base route)
    {
        // Placeholder
    }

    public void Clear()
    {
        // Placeholder
    }

    public void LoadMap(GoogleMap map)
    {
        // Placeholder
    }

    // ----------------------- Private APIs ----------------------- //

    public Route.Base GetRouteObjectFromPoly(Polyline polyline)
    {
        // Place Holder

        return null;
    }
}

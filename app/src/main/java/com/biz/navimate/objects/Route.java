package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class Route {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ROUTE";

    // ----------------------- Classes ---------------------------//
    // Class to define a single step in a route
    public static class Step
    {
        // Start and end of this step
        public LatLng start = null;
        public LatLng end = null;

        // Constructor
        public Step(LatLng start, LatLng end)
        {
            this.start = start;
            this.end = end;
        }
    }

    // Base class for different type of route objects
    // Provides APIs which any route related operation may need.
    // Inherited classes must implement the APIs as per the route structure
    public static abstract class Base
    {
        // Globals
        public Polyline polyline        = null;

        // Abstracts
        // Distance / Duration getter
        public abstract int GetDurationS();
        public abstract int GetDistanceM();

        // API to get alll points on this route
        public abstract ArrayList<LatLng> GetAllPoints();

        // checkpoint getter
        public abstract ArrayList<LatLng> GetCheckpoints();

        // API to check if the route contains the given polyline
        public abstract boolean hasPolyline(Polyline polyline);

        // Equals
        public abstract boolean equals(Object object);

        // Public APIs
        // API to provide polyline options to populate UI of route on map. Should be implemented by derived classes.
        public PolylineOptions GetPolylineOptions()
        {
            // Init polyline options
            PolylineOptions polyOptions = new PolylineOptions();

            // Add points on the route
            polyOptions.addAll(GetAllPoints());

            // Init width of polyline
            polyOptions.width(15);

            return polyOptions;
        }
    }

    // A single route is a collection of position on map in a sequential order
    // starting from a source and ending at a destination
    public static class Single extends Base
    {
        // List of steps in this route
        public ArrayList<Step> steps    = null;

        // Distance / Duration of route
        private int durationS = 0;
        private int distanceM = 0;

        // Constructor
        public Single(ArrayList<Step> steps, int durationS, int distanceM)
        {
            this.steps = steps;
            this.durationS = durationS;
            this.distanceM = distanceM;
        }

        // Overrides
        @Override
        public int GetDurationS()
        {
            return durationS;
        }

        @Override
        public int GetDistanceM()
        {
            return distanceM;
        }

        @Override
        public ArrayList<LatLng> GetAllPoints()
        {
            ArrayList<LatLng> points = new ArrayList<>();

            if ((steps != null) && (steps.size() > 0))
            {
                // Add start of first step
                points.add(steps.get(0).start);

                // Add end of all steps
                for (Step step :steps)
                {
                    // Add Start & end
                    points.add(step.end);
                }
            }

            return points;
        }

        @Override
        public ArrayList<LatLng> GetCheckpoints()
        {
            ArrayList<LatLng> checkpoints = new ArrayList<>();

            if ((steps != null) && (steps.size() > 0))
            {
                // Add Start
                checkpoints.add(steps.get(0).start);

                // Add End
                checkpoints.add(steps.get(steps.size() - 1).end);
            }

            return checkpoints;
        }

        @Override
        public boolean hasPolyline(Polyline polyline)
        {
            if ((this.polyline != null) && (this.polyline.equals(polyline)))
            {
                return true;
            }

            return false;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean bEqual = false;

            // Validate Object Instance
            if ((object != null) && (object instanceof Single))
            {
                // Cast object to compare
                Single compareObject = (Single) object;

                // Get Checkpoints
                ArrayList<LatLng> compareCheckpoints = compareObject.GetCheckpoints();
                ArrayList<LatLng> currentCheckpoints = GetCheckpoints();

                // Ensure there are equal and a valid number of checkpoints in both objects
                if ((compareCheckpoints.size() == currentCheckpoints.size()) && (compareCheckpoints.size() > 0))
                {
                    // Mark bEqual as true
                    bEqual = true;

                    // Compare each checkpoint
                    for (int i = 0; i < compareCheckpoints.size(); i++)
                    {
                        LatLng compareCheckpoint = compareCheckpoints.get(i);
                        LatLng currentCheckpoint = currentCheckpoints.get(i);

                        if ((compareCheckpoint.longitude != currentCheckpoint.longitude) || (compareCheckpoint.latitude != currentCheckpoint.latitude))
                        {
                            bEqual = false;
                            break;
                        }
                    }
                }
            }

            return bEqual;
        }
    }

    // A multi route is a collection of single routes.
    // A multi route has the ability to maintain waypoints. I.e. the number of checkpoints.
    // In a single route the checkpoints are limited to 2 (start and end)
    // Multi route can contain more than 2 checkpoints
    public static class Multi extends Base
    {
        // List of routes in this route
        public ArrayList<Single> routes = null;

        // Constructor
        public Multi(ArrayList<Single> routes)
        {
            this.routes = routes;
        }

        // Overrides
        @Override
        public int GetDurationS()
        {
            // Add duration of all routes
            int durationS = 0;

            for (Single route : routes)
            {
                durationS += route.durationS;
            }

            return durationS;
        }

        @Override
        public int GetDistanceM()
        {
            // Add distance of all routes
            int distanceM = 0;

            for (Single route : routes)
            {
                distanceM += route.distanceM;
            }

            return distanceM;
        }

        @Override
        public ArrayList<LatLng> GetAllPoints()
        {
            ArrayList<LatLng> points = new ArrayList<>();

            if ((routes != null) && (routes.size() > 0))
            {
                // Add first point of first route
                points.add(routes.get(0).GetAllPoints().get(0));

                // Add all points of consecutive routes
                for (Single route : routes)
                {
                    ArrayList<LatLng> routePoints = route.GetAllPoints();

                    // Remove first point in this route since it was already added for previous route
                    routePoints.remove(0);

                    // Add to multi route points
                    points.addAll(routePoints);

                }
            }

            return points;
        }

        @Override
        public ArrayList<LatLng> GetCheckpoints()
        {
            ArrayList<LatLng> places = new ArrayList<>();

            if ((routes != null) && (routes.size() > 0))
            {
                // Add start of first route
                places.add(routes.get(0).GetCheckpoints().get(0));

                // Add end of all routes
                for (Single route : routes)
                {
                    places.add(route.GetCheckpoints().get(1));
                }
            }

            return places;
        }

        @Override
        public boolean hasPolyline(Polyline polyline)
        {
            for (Single route : routes)
            {
                if (route.hasPolyline(polyline))
                {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean bEqual = false;

            // Validate Object Instance
            if ((object != null) && (object instanceof Multi))
            {
                // Cast object to compare
                Multi compareObject = (Multi) object;

                // Get Checkpoints
                ArrayList<LatLng> compareCheckpoints = compareObject.GetCheckpoints();
                ArrayList<LatLng> currentCheckpoints = GetCheckpoints();

                // Ensure there are equal and a valid number of checkpoints in both objects
                if ((compareCheckpoints.size() == currentCheckpoints.size()) && (compareCheckpoints.size() > 0))
                {
                    // Mark bEqual as true
                    bEqual = true;

                    // Compare each checkpoint
                    for (int i = 0; i < compareCheckpoints.size(); i++)
                    {
                        LatLng compareCheckpoint = compareCheckpoints.get(i);
                        LatLng currentCheckpoint = currentCheckpoints.get(i);

                        if ((compareCheckpoint.longitude != currentCheckpoint.longitude) || (compareCheckpoint.latitude != currentCheckpoint.latitude))
                        {
                            bEqual = false;
                            break;
                        }
                    }
                }
            }

            return bEqual;
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
